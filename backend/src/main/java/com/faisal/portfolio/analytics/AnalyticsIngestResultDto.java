package com.faisal.portfolio.analytics;

import java.time.Instant;

public record AnalyticsIngestResultDto(
        String status,
        boolean deduplicated,
        Instant recordedAt
) {
}
