package com.faisal.portfolio.analytics;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional(readOnly = true)
public class AnalyticsSummaryService {

    private static final List<String> ORDERED_SHOWCASE_SLUGS = List.of(
            "creams",
            "sen2nal",
            "baiti",
            "auto-recruit",
            "ringgitsense"
    );

    private final AnalyticsDailyAggregateRepository aggregateRepository;
    private final AnalyticsProperties properties;
    private final Clock clock;

    public AnalyticsSummaryService(
            AnalyticsDailyAggregateRepository aggregateRepository,
            AnalyticsProperties properties,
            Clock clock
    ) {
        this.aggregateRepository = aggregateRepository;
        this.properties = properties;
        this.clock = clock;
    }

    public AnalyticsSummaryDto getSummary(int requestedWindowDays) {
        int windowDays = Math.clamp(requestedWindowDays, 1, 90);
        Instant now = clock.instant();
        LocalDate sinceDate = LocalDate.ofInstant(now, ZoneOffset.UTC).minusDays(windowDays - 1L);

        List<AnalyticsDailyAggregate> rows =
                aggregateRepository.findByBucketDateGreaterThanEqualOrderByBucketDateDesc(sinceDate);

        Map<String, FunnelAccumulator> funnelBySlug = new LinkedHashMap<>();
        for (String slug : ORDERED_SHOWCASE_SLUGS) {
            funnelBySlug.put(slug, new FunnelAccumulator());
        }

        Map<String, Long> topicTotals = new LinkedHashMap<>();
        long totalEvents = 0L;
        Instant lastRecordedAt = null;

        for (AnalyticsDailyAggregate row : rows) {
            long count = row.getEventCount();
            totalEvents += count;

            Instant rowUpdated = row.getUpdatedAt();
            if (rowUpdated != null && (lastRecordedAt == null || rowUpdated.isAfter(lastRecordedAt))) {
                lastRecordedAt = rowUpdated;
            }

            FunnelAccumulator acc = funnelBySlug.get(row.getProjectSlug());
            if (acc != null) {
                switch (row.getEventName()) {
                    case "case_study_open" -> acc.opens += count;
                    case "meaningful_read" -> acc.meaningfulReads += count;
                    case "read_depth" -> acc.addDepth(row.getDepthBucket(), count);
                    default -> {
                    }
                }
            }

            String topic = row.getTopicLabel();
            if (topic != null && !AnalyticsIngestionService.DEFAULT_DIMENSION.equals(topic)) {
                topicTotals.merge(topic, count, Long::sum);
            }
        }

        List<AnalyticsSummaryDto.ProjectFunnelMetricDto> projectFunnel = funnelBySlug.entrySet().stream()
                .map(entry -> entry.getValue().toDto(entry.getKey()))
                .toList();

        int minTopicCount = properties.getMinTopicCellCount();
        List<AnalyticsSummaryDto.TopicMetricDto> visibleTopics = new ArrayList<>();
        int suppressedTopicCellCount = 0;

        for (Map.Entry<String, Long> entry : topicTotals.entrySet()) {
            if (entry.getValue() >= minTopicCount) {
                visibleTopics.add(new AnalyticsSummaryDto.TopicMetricDto(entry.getKey(), entry.getValue()));
            } else if (entry.getValue() > 0) {
                suppressedTopicCellCount++;
            }
        }

        visibleTopics.sort(Comparator.comparingLong(AnalyticsSummaryDto.TopicMetricDto::eventCount).reversed()
                .thenComparing(AnalyticsSummaryDto.TopicMetricDto::topicLabel));

        return new AnalyticsSummaryDto(
                now,
                lastRecordedAt,
                windowDays,
                properties.getRawRetentionDays(),
                properties.getAggregateRetentionMonths(),
                minTopicCount,
                totalEvents,
                projectFunnel,
                List.copyOf(visibleTopics),
                suppressedTopicCellCount
        );
    }

    private static final class FunnelAccumulator {
        long opens;
        long meaningfulReads;
        long depth25;
        long depth50;
        long depth75;
        long depth100;

        void addDepth(String bucket, long count) {
            if (bucket == null) return;
            switch (bucket) {
                case "25" -> depth25 += count;
                case "50" -> depth50 += count;
                case "75" -> depth75 += count;
                case "100" -> depth100 += count;
                default -> {
                }
            }
        }

        AnalyticsSummaryDto.ProjectFunnelMetricDto toDto(String slug) {
            Double rate = opens > 0
                    ? Math.round((meaningfulReads * 10_000.0) / opens) / 100.0
                    : null;
            return new AnalyticsSummaryDto.ProjectFunnelMetricDto(
                    slug,
                    opens,
                    meaningfulReads,
                    rate,
                    depth25,
                    depth50,
                    depth75,
                    depth100
            );
        }
    }
}
