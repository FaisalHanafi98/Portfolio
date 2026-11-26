package com.faisal.portfolio.status;

import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.config.MeterFilter;
import io.micrometer.core.instrument.distribution.DistributionStatisticConfig;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
@EnableConfigurationProperties(StatusProperties.class)
public class StatusMetricsConfig {

    private static final int MAX_URI_TAGS = 16;

    @Bean
    public ApiMetricsFilter apiMetricsFilter(ApiLatencyTracker tracker) {
        return new ApiMetricsFilter(tracker);
    }

    @Bean
    public MeterFilter httpServerUriTagLimit() {
        return MeterFilter.maximumAllowableTags("http.server.requests", "uri", MAX_URI_TAGS, MeterFilter.deny());
    }

    @Bean
    public MeterFilter portfolioRouteTagLimit() {
        return MeterFilter.maximumAllowableTags(
                ApiLatencyTracker.ROUTE_TIMER_NAME, "route", MAX_URI_TAGS, MeterFilter.deny());
    }

    @Bean
    public MeterFilter httpServerBoundedDistribution(StatusProperties properties) {
        Duration expiry = Duration.ofSeconds(properties.getWindowSeconds());
        return new MeterFilter() {
            @Override
            public DistributionStatisticConfig configure(Meter.Id id, DistributionStatisticConfig config) {
                if (!"http.server.requests".equals(id.getName())) {
                    return config;
                }
                return DistributionStatisticConfig.builder()
                        .percentilesHistogram(false)
                        .expiry(expiry)
                        .bufferLength(5)
                        .minimumExpectedValue((double) Duration.ofMillis(1).toNanos())
                        .maximumExpectedValue((double) Duration.ofSeconds(10).toNanos())
                        .serviceLevelObjectives(
                                (double) Duration.ofMillis(10).toNanos(),
                                (double) Duration.ofMillis(25).toNanos(),
                                (double) Duration.ofMillis(50).toNanos(),
                                (double) Duration.ofMillis(100).toNanos(),
                                (double) Duration.ofMillis(250).toNanos(),
                                (double) Duration.ofMillis(500).toNanos(),
                                (double) Duration.ofSeconds(1).toNanos(),
                                (double) Duration.ofMillis(2500).toNanos()
                        )
                        .build()
                        .merge(config);
            }
        };
    }
}
