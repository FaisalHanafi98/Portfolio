package com.faisal.portfolio.analytics;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;

@Service
public class AnalyticsRetentionService {

    private static final Logger log = LoggerFactory.getLogger(AnalyticsRetentionService.class);

    private final AnalyticsEventRepository eventRepository;
    private final AnalyticsDailyAggregateRepository aggregateRepository;
    private final AnalyticsProperties properties;
    private final Clock clock;

    public AnalyticsRetentionService(
            AnalyticsEventRepository eventRepository,
            AnalyticsDailyAggregateRepository aggregateRepository,
            AnalyticsProperties properties,
            Clock clock
    ) {
        this.eventRepository = eventRepository;
        this.aggregateRepository = aggregateRepository;
        this.properties = properties;
        this.clock = clock;
    }

    @Scheduled(cron = "${portfolio.analytics.retention-cron:0 15 3 * * *}", zone = "UTC")
    @Transactional
    public void scheduledPurge() {
        purgeExpiredRecords();
    }

    @Transactional
    public RetentionPurgeResult purgeExpiredRecords() {
        Instant now = clock.instant();
        Instant rawCutoff = now.minus(properties.getRawRetentionDays(), ChronoUnit.DAYS);
        LocalDate aggCutoff = LocalDate.ofInstant(now, ZoneOffset.UTC)
                .minusMonths(properties.getAggregateRetentionMonths());

        long deletedRaw = eventRepository.deleteByOccurredAtBefore(rawCutoff);
        long deletedAgg = aggregateRepository.deleteByBucketDateBefore(aggCutoff);

        log.info("Analytics retention purge removed {} raw events and {} daily aggregates", deletedRaw, deletedAgg);
        return new RetentionPurgeResult(deletedRaw, deletedAgg);
    }

    public record RetentionPurgeResult(long deletedRawEvents, long deletedDailyAggregates) {
    }
}
