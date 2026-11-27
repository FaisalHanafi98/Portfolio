package com.faisal.portfolio.analytics;

import java.time.Instant;
import java.util.List;

public record AnalyticsSummaryDto(
        Instant generatedAt,
        Instant lastRecordedAt,
        int windowDays,
        int rawRetentionDays,
        int aggregateRetentionMonths,
        int minTopicCellCount,
        long totalEvents,
        List<ProjectFunnelMetricDto> projectFunnel,
        List<TopicMetricDto> topics,
        int suppressedTopicCellCount
) {
    public record ProjectFunnelMetricDto(
            String projectSlug,
            long caseStudyOpens,
            long meaningfulReads,
            Double conversionRatePercent,
            long depth25Count,
            long depth50Count,
            long depth75Count,
            long depth100Count
    ) {
    }

    public record TopicMetricDto(
            String topicLabel,
            long eventCount
    ) {
    }
}
