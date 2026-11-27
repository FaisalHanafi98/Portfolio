package com.faisal.portfolio.analytics;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.dao.DataIntegrityViolationException;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.time.Instant;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
class AnalyticsSchemaRepositoryTest {

    @Autowired
    private AnalyticsEventRepository eventRepository;

    @Autowired
    private AnalyticsDailyAggregateRepository aggregateRepository;

    @Autowired
    private DataSource dataSource;

    @Test
    void v2MigrationCreatesPrivacySafeAnalyticsTablesWithoutPiiOrPromptColumns() throws Exception {
        assertThat(new ClassPathResource("db/migration/V2__first_party_analytics_schema.sql").exists())
                .isTrue();

        Set<String> eventColumns = new HashSet<>();
        try (Connection conn = dataSource.getConnection();
             ResultSet rs = conn.getMetaData().getColumns(null, null, "ANALYTICS_EVENTS", null)) {
            while (rs.next()) {
                eventColumns.add(rs.getString("COLUMN_NAME").toLowerCase());
            }
        }
        if (eventColumns.isEmpty()) {
            try (Connection conn = dataSource.getConnection();
                 ResultSet rs = conn.getMetaData().getColumns(null, null, "analytics_events", null)) {
                while (rs.next()) {
                    eventColumns.add(rs.getString("COLUMN_NAME").toLowerCase());
                }
            }
        }

        assertThat(eventColumns).contains(
                "id", "event_id", "schema_version", "event_name",
                "project_slug", "section_key", "depth_bucket", "topic_label", "occurred_at"
        );
        assertThat(eventColumns).doesNotContain(
                "ip", "ip_address", "client_ip", "url", "raw_url", "query_string",
                "prompt", "prompt_text", "jd_text", "user_agent", "referrer"
        );
    }

    @Test
    void eventRepositoryEnforcesUniqueEventIdAndRetentionCleanup() {
        Instant now = Instant.parse("2026-11-27T12:00:00Z");
        AnalyticsEvent first = new AnalyticsEvent(
                "evt_1234567890abcd", 1, "case_study_open", "creams", "projects", "none", "none", now
        );
        eventRepository.saveAndFlush(first);

        AnalyticsEvent duplicate = new AnalyticsEvent(
                "evt_1234567890abcd", 1, "case_study_open", "creams", "projects", "none", "none", now
        );
        assertThatThrownBy(() -> eventRepository.saveAndFlush(duplicate))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void dailyAggregateRepositoryStoresCoarseBucketsAndCleansOldDates() {
        LocalDate oldDay = LocalDate.of(2025, 10, 1);
        LocalDate recentDay = LocalDate.of(2026, 11, 27);
        Instant now = Instant.parse("2026-11-27T12:00:00Z");

        aggregateRepository.saveAndFlush(new AnalyticsDailyAggregate(
                oldDay, "creams", "case_study_open", "none", "none", 3L, now
        ));
        aggregateRepository.saveAndFlush(new AnalyticsDailyAggregate(
                recentDay, "creams", "meaningful_read", "75", "none", 2L, now
        ));

        long deleted = aggregateRepository.deleteByBucketDateBefore(LocalDate.of(2025, 11, 27));
        assertThat(deleted).isEqualTo(1L);
        assertThat(aggregateRepository.findByBucketDateGreaterThanEqualOrderByBucketDateDesc(LocalDate.of(2026, 1, 1)))
                .hasSize(1);
    }
}
