package com.faisal.portfolio.status;

import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.distribution.HistogramSnapshot;
import io.micrometer.core.instrument.distribution.ValueAtPercentile;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Component
public class ApiLatencyTracker {

    public static final String TIMER_NAME = "portfolio.api.latency";
    public static final String ROUTE_TIMER_NAME = "portfolio.api.route.requests";
    public static final String OTHER_ROUTE = "OTHER";

    public static final Set<String> ALLOWED_ROUTES = Set.of(
            "/api/v1/health",
            "/api/v1/projects",
            "/api/v1/projects/{slug}",
            "/api/v1/skills",
            "/api/v1/experience",
            "/api/v1/status",
            "/api/v1/analytics/events"
    );

    private static final int WINDOW_BUCKETS = 60;

    private final MeterRegistry meterRegistry;
    private final Clock clock;
    private final StatusProperties properties;
    private final Timer aggregateTimer;
    private final Map<String, Timer> routeTimers = new ConcurrentHashMap<>();
    private final long windowMillis;
    private final long bucketMillis;
    private final long[] bucketIndices = new long[WINDOW_BUCKETS];
    private final long[] bucketCounts = new long[WINDOW_BUCKETS];

    public ApiLatencyTracker(MeterRegistry meterRegistry, StatusProperties properties) {
        this.meterRegistry = meterRegistry;
        this.clock = meterRegistry.config().clock();
        this.properties = properties;
        this.windowMillis = properties.getWindowSeconds() * 1000L;
        this.bucketMillis = Math.max(1000L, windowMillis / WINDOW_BUCKETS);
        Duration expiry = Duration.ofSeconds(properties.getWindowSeconds());

        this.aggregateTimer = Timer.builder(TIMER_NAME)
                .description("Bounded latency distribution for public portfolio API routes")
                .publishPercentiles(0.95)
                .serviceLevelObjectives(
                        Duration.ofMillis(10),
                        Duration.ofMillis(25),
                        Duration.ofMillis(50),
                        Duration.ofMillis(100),
                        Duration.ofMillis(250),
                        Duration.ofMillis(500),
                        Duration.ofSeconds(1),
                        Duration.ofMillis(2500)
                )
                .minimumExpectedValue(Duration.ofMillis(1))
                .maximumExpectedValue(Duration.ofSeconds(10))
                .distributionStatisticExpiry(expiry)
                .distributionStatisticBufferLength(5)
                .register(meterRegistry);
    }

    public static String normalizeRoute(String routePattern) {
        if (routePattern != null && ALLOWED_ROUTES.contains(routePattern)) {
            return routePattern;
        }
        return OTHER_ROUTE;
    }

    public void recordRequest(String routePattern, long durationNanos) {
        long boundedNanos = Math.max(0L, durationNanos);
        String route = normalizeRoute(routePattern);

        aggregateTimer.record(boundedNanos, TimeUnit.NANOSECONDS);
        routeTimers.computeIfAbsent(route, this::registerRouteTimer)
                .record(boundedNanos, TimeUnit.NANOSECONDS);
        incrementWindowBucket(clock.wallTime());
    }

    public StatusSnapshotDto.TrafficMetricsDto snapshot() {
        long requestCount = sumWindowBuckets(clock.wallTime());
        int minSamples = properties.getMinSamples();
        int windowSeconds = properties.getWindowSeconds();

        if (requestCount < minSamples) {
            return new StatusSnapshotDto.TrafficMetricsDto(windowSeconds, requestCount, minSamples, "insufficient_data", null);
        }

        HistogramSnapshot snapshot = aggregateTimer.takeSnapshot();
        Double p95Ms = extractP95Millis(snapshot);
        if (p95Ms == null) {
            return new StatusSnapshotDto.TrafficMetricsDto(windowSeconds, requestCount, minSamples, "insufficient_data", null);
        }

        return new StatusSnapshotDto.TrafficMetricsDto(windowSeconds, requestCount, minSamples, "measured", p95Ms);
    }

    private Timer registerRouteTimer(String route) {
        return Timer.builder(ROUTE_TIMER_NAME)
                .tag("route", route)
                .serviceLevelObjectives(
                        Duration.ofMillis(10),
                        Duration.ofMillis(50),
                        Duration.ofMillis(250),
                        Duration.ofSeconds(1)
                )
                .minimumExpectedValue(Duration.ofMillis(1))
                .maximumExpectedValue(Duration.ofSeconds(10))
                .distributionStatisticExpiry(Duration.ofSeconds(properties.getWindowSeconds()))
                .distributionStatisticBufferLength(5)
                .register(meterRegistry);
    }

    private synchronized void incrementWindowBucket(long nowMs) {
        long bucketIdx = nowMs / bucketMillis;
        int slot = (int) Math.floorMod(bucketIdx, WINDOW_BUCKETS);
        if (bucketIndices[slot] != bucketIdx) {
            bucketIndices[slot] = bucketIdx;
            bucketCounts[slot] = 0L;
        }
        bucketCounts[slot]++;
    }

    private synchronized long sumWindowBuckets(long nowMs) {
        long currentBucket = nowMs / bucketMillis;
        long oldestValidBucket = Math.max(0L, (nowMs - windowMillis + 1) / bucketMillis);
        long total = 0L;
        for (int i = 0; i < WINDOW_BUCKETS; i++) {
            long slotBucket = bucketIndices[i];
            if (slotBucket >= oldestValidBucket && slotBucket <= currentBucket) {
                total += bucketCounts[i];
            }
        }
        return total;
    }

    private static Double extractP95Millis(HistogramSnapshot snapshot) {
        for (ValueAtPercentile value : snapshot.percentileValues()) {
            if (Math.abs(value.percentile() - 0.95) < 0.005) {
                double ms = value.value(TimeUnit.MILLISECONDS);
                if (Double.isFinite(ms) && ms >= 0.0) {
                    return Math.round(ms * 100.0) / 100.0;
                }
            }
        }
        return null;
    }
}
