package com.faisal.portfolio.analytics;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Set;

@Service
public class AnalyticsIngestionService {

    public static final int SUPPORTED_SCHEMA_VERSION = 1;
    public static final String SITE_SCOPE_SLUG = "__site__";
    public static final String DEFAULT_DIMENSION = "none";

    public static final Set<String> ALLOWED_EVENTS = Set.of(
            "page_view",
            "case_study_open",
            "meaningful_read",
            "read_depth",
            "section_view",
            "topic_category"
    );

    public static final Set<String> ALLOWED_PROJECTS = Set.of(
            SITE_SCOPE_SLUG,
            "creams",
            "sen2nal",
            "baiti",
            "auto-recruit",
            "ringgitsense"
    );

    public static final Set<String> ALLOWED_SECTIONS = Set.of(
            DEFAULT_DIMENSION,
            "hero",
            "about",
            "projects",
            "skills",
            "experience",
            "contact",
            "status",
            "analytics",
            "problem",
            "solution",
            "architecture",
            "constraints",
            "overview"
    );

    public static final Set<String> ALLOWED_DEPTH_BUCKETS = Set.of(
            DEFAULT_DIMENSION,
            "25",
            "50",
            "75",
            "100",
            "meaningful_30s"
    );

    public static final Set<String> ALLOWED_TOPICS = Set.of(
            DEFAULT_DIMENSION,
            "software_engineering",
            "data_analytics",
            "devops",
            "ai_retrieval"
    );

    private final AnalyticsEventRepository eventRepository;
    private final AnalyticsDailyAggregateRepository aggregateRepository;
    private final TransactionTemplate newTxTemplate;
    private final Clock clock;

    public AnalyticsIngestionService(
            AnalyticsEventRepository eventRepository,
            AnalyticsDailyAggregateRepository aggregateRepository,
            PlatformTransactionManager transactionManager,
            Clock clock
    ) {
        this.eventRepository = eventRepository;
        this.aggregateRepository = aggregateRepository;
        this.newTxTemplate = new TransactionTemplate(transactionManager);
        this.newTxTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        this.clock = clock;
    }

    public AnalyticsIngestResultDto ingest(AnalyticsEventRequest request) {
        if (request.schemaVersion() == null || request.schemaVersion() != SUPPORTED_SCHEMA_VERSION) {
            throw new IllegalArgumentException("Unsupported schemaVersion");
        }
        String eventName = requireAllowlisted(request.eventName(), ALLOWED_EVENTS, "eventName");
        String projectSlug = normalizeAllowlisted(request.projectSlug(), ALLOWED_PROJECTS, SITE_SCOPE_SLUG, "projectSlug");
        String sectionKey = normalizeAllowlisted(request.sectionKey(), ALLOWED_SECTIONS, DEFAULT_DIMENSION, "sectionKey");
        String depthBucket = normalizeAllowlisted(request.depthBucket(), ALLOWED_DEPTH_BUCKETS, DEFAULT_DIMENSION, "depthBucket");
        String topicLabel = normalizeAllowlisted(request.topicLabel(), ALLOWED_TOPICS, DEFAULT_DIMENSION, "topicLabel");

        Instant serverNow = clock.instant();
        String eventId = request.eventId().trim();

        boolean inserted;
        try {
            inserted = Boolean.TRUE.equals(newTxTemplate.execute(status -> {
                if (eventRepository.existsByEventId(eventId)) {
                    return Boolean.FALSE;
                }
                eventRepository.saveAndFlush(new AnalyticsEvent(
                        eventId,
                        SUPPORTED_SCHEMA_VERSION,
                        eventName,
                        projectSlug,
                        sectionKey,
                        depthBucket,
                        topicLabel,
                        serverNow
                ));
                return Boolean.TRUE;
            }));
        } catch (DataIntegrityViolationException duplicateRace) {
            inserted = false;
        }

        if (!inserted) {
            return new AnalyticsIngestResultDto("accepted", true, serverNow);
        }

        LocalDate bucketDate = LocalDate.ofInstant(serverNow, ZoneOffset.UTC);
        incrementDailyBucket(bucketDate, projectSlug, eventName, depthBucket, topicLabel, serverNow);
        return new AnalyticsIngestResultDto("accepted", false, serverNow);
    }

    private void incrementDailyBucket(
            LocalDate bucketDate,
            String projectSlug,
            String eventName,
            String depthBucket,
            String topicLabel,
            Instant updatedAt
    ) {
        Boolean done = newTxTemplate.execute(status -> {
            int updated = aggregateRepository.incrementExistingBucket(
                    bucketDate, projectSlug, eventName, depthBucket, topicLabel, updatedAt
            );
            return updated > 0;
        });
        if (Boolean.TRUE.equals(done)) {
            return;
        }

        try {
            newTxTemplate.executeWithoutResult(status -> aggregateRepository.saveAndFlush(
                    new AnalyticsDailyAggregate(
                            bucketDate, projectSlug, eventName, depthBucket, topicLabel, 1L, updatedAt
                    )
            ));
        } catch (DataIntegrityViolationException concurrentBucketInsert) {
            newTxTemplate.executeWithoutResult(status -> aggregateRepository.incrementExistingBucket(
                    bucketDate, projectSlug, eventName, depthBucket, topicLabel, updatedAt
            ));
        }
    }

    private static String requireAllowlisted(String value, Set<String> allowlist, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Missing required analytics field: " + field);
        }
        String trimmed = value.trim();
        if (!allowlist.contains(trimmed)) {
            throw new IllegalArgumentException("Disallowed value for analytics field: " + field);
        }
        return trimmed;
    }

    private static String normalizeAllowlisted(String value, Set<String> allowlist, String defaultVal, String field) {
        if (value == null || value.isBlank()) {
            return defaultVal;
        }
        String trimmed = value.trim();
        if (!allowlist.contains(trimmed)) {
            throw new IllegalArgumentException("Disallowed value for analytics field: " + field);
        }
        return trimmed;
    }
}
