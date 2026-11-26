package com.faisal.portfolio.status;

import java.time.Instant;

public record StatusSnapshotDto(
        String status,
        Instant checkedAt,
        long uptimeSeconds,
        Instant startedAt,
        String revision,
        DatabaseStatusDto database,
        TrafficMetricsDto traffic
) {
    public record DatabaseStatusDto(String status, Long latencyMs) {}

    public record TrafficMetricsDto(
            int windowSeconds,
            long requestCount,
            int minSamples,
            String p95Status,
            Double p95Ms
    ) {}
}
