package com.faisal.portfolio.status;

import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.MockClock;
import io.micrometer.core.instrument.simple.SimpleConfig;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

class ApiLatencyTrackerTest {

    @Test
    void returnsInsufficientDataBelowThresholdThenMeasuredThenExpiresAfterFiveMinutes() {
        MockClock clock = new MockClock();
        SimpleMeterRegistry registry = new SimpleMeterRegistry(SimpleConfig.DEFAULT, clock);
        StatusProperties properties = new StatusProperties();
        properties.setWindowSeconds(300);
        properties.setMinSamples(20);

        ApiLatencyTracker tracker = new ApiLatencyTracker(registry, properties);

        for (int i = 0; i < 19; i++) {
            tracker.recordRequest("/api/v1/projects", TimeUnit.MILLISECONDS.toNanos(12));
        }

        StatusSnapshotDto.TrafficMetricsDto beforeThreshold = tracker.snapshot();
        assertThat(beforeThreshold.requestCount()).isEqualTo(19L);
        assertThat(beforeThreshold.p95Status()).isEqualTo("insufficient_data");
        assertThat(beforeThreshold.p95Ms()).isNull();

        tracker.recordRequest("/api/v1/projects/{slug}", TimeUnit.MILLISECONDS.toNanos(45));
        StatusSnapshotDto.TrafficMetricsDto measured = tracker.snapshot();
        assertThat(measured.windowSeconds()).isEqualTo(300);
        assertThat(measured.requestCount()).isEqualTo(20L);
        assertThat(measured.minSamples()).isEqualTo(20);
        assertThat(measured.p95Status()).isEqualTo("measured");
        assertThat(measured.p95Ms()).isNotNull().isGreaterThan(0.0);

        clock.add(Duration.ofMinutes(6));
        StatusSnapshotDto.TrafficMetricsDto expired = tracker.snapshot();
        assertThat(expired.requestCount()).isZero();
        assertThat(expired.p95Status()).isEqualTo("insufficient_data");
        assertThat(expired.p95Ms()).isNull();
    }

    @Test
    void normalizesUnknownPathsToBoundedOtherLabelWithoutCardinalityExplosion() {
        MockClock clock = new MockClock();
        SimpleMeterRegistry registry = new SimpleMeterRegistry(SimpleConfig.DEFAULT, clock);
        StatusProperties properties = new StatusProperties();
        ApiLatencyTracker tracker = new ApiLatencyTracker(registry, properties);

        for (int i = 0; i < 50; i++) {
            tracker.recordRequest("/api/v1/arbitrary-" + i, TimeUnit.MILLISECONDS.toNanos(5));
        }
        tracker.recordRequest("/api/v1/projects/{slug}", TimeUnit.MILLISECONDS.toNanos(8));

        List<String> routeTags = registry.find(ApiLatencyTracker.ROUTE_TIMER_NAME).timers().stream()
                .map(timer -> timer.getId().getTag("route"))
                .distinct()
                .sorted()
                .toList();

        assertThat(routeTags).containsExactly("/api/v1/projects/{slug}", ApiLatencyTracker.OTHER_ROUTE);
        assertThat(registry.find(ApiLatencyTracker.ROUTE_TIMER_NAME).timers())
                .extracting(Meter::getId)
                .hasSize(2);
    }
}
