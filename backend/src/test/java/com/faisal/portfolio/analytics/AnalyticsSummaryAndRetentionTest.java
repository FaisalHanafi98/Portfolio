package com.faisal.portfolio.analytics;

import com.faisal.portfolio.PortfolioApplication;
import io.micrometer.core.instrument.MeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(
        classes = PortfolioApplication.class,
        properties = {
                "portfolio.status.github.scheduling-enabled=false",
                "portfolio.analytics.min-topic-cell-count=5",
                "portfolio.analytics.raw-retention-days=30",
                "portfolio.analytics.aggregate-retention-months=12"
        }
)
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class AnalyticsSummaryAndRetentionTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AnalyticsEventRepository eventRepository;

    @Autowired
    private AnalyticsDailyAggregateRepository aggregateRepository;

    @Autowired
    private AnalyticsRetentionService retentionService;

    @Autowired
    private MeterRegistry meterRegistry;

    @BeforeEach
    void resetTables() {
        eventRepository.deleteAll();
        aggregateRepository.deleteAll();
    }

    @Test
    void summaryReconcilesDenominatorsConversionRatesAndSuppressesSparseTopics() throws Exception {
        Instant now = Instant.now();
        LocalDate today = LocalDate.ofInstant(now, ZoneOffset.UTC);

        aggregateRepository.save(new AnalyticsDailyAggregate(today, "creams", "case_study_open", "none", "none", 4L, now));
        aggregateRepository.save(new AnalyticsDailyAggregate(today, "creams", "meaningful_read", "meaningful_30s", "none", 3L, now));
        aggregateRepository.save(new AnalyticsDailyAggregate(today, "creams", "read_depth", "75", "none", 2L, now));
        aggregateRepository.save(new AnalyticsDailyAggregate(today, "__site__", "topic_category", "none", "software_engineering", 5L, now));
        aggregateRepository.save(new AnalyticsDailyAggregate(today, "__site__", "topic_category", "none", "devops", 4L, now));

        mockMvc.perform(get("/api/v1/analytics/summary"))
                .andExpect(status().isOk())
                .andExpect(header().string("Cache-Control", containsString("no-store")))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.totalEvents").value(18))
                .andExpect(jsonPath("$.data.rawRetentionDays").value(30))
                .andExpect(jsonPath("$.data.aggregateRetentionMonths").value(12))
                .andExpect(jsonPath("$.data.minTopicCellCount").value(5))
                .andExpect(jsonPath("$.data.suppressedTopicCellCount").value(1))
                .andExpect(jsonPath("$.data.projectFunnel[0].projectSlug").value("creams"))
                .andExpect(jsonPath("$.data.projectFunnel[0].caseStudyOpens").value(4))
                .andExpect(jsonPath("$.data.projectFunnel[0].meaningfulReads").value(3))
                .andExpect(jsonPath("$.data.projectFunnel[0].conversionRatePercent").value(75.0))
                .andExpect(jsonPath("$.data.projectFunnel[0].depth75Count").value(2))
                .andExpect(jsonPath("$.data.topics[*].topicLabel", hasItem("software_engineering")))
                .andExpect(jsonPath("$.data.topics[*].topicLabel", not(hasItem("devops"))));

        assertThat(meterRegistry.find("portfolio.api.route.requests").tag("route", "/api/v1/analytics/summary").timer())
                .isNotNull();
    }

    @Test
    void retentionPurgeDeletesExpiredRawEventsAndOldDailyAggregatesOnly() {
        Instant now = Instant.now();
        Instant staleRaw = now.minus(31, ChronoUnit.DAYS);
        Instant freshRaw = now.minus(29, ChronoUnit.DAYS);
        LocalDate staleAggDate = LocalDate.ofInstant(now, ZoneOffset.UTC).minusMonths(13);
        LocalDate freshAggDate = LocalDate.ofInstant(now, ZoneOffset.UTC).minusMonths(11);

        eventRepository.save(new AnalyticsEvent("evt-old-raw-0001", 1, "page_view", "__site__", "none", "none", "none", staleRaw));
        eventRepository.save(new AnalyticsEvent("evt-fresh-raw-01", 1, "page_view", "__site__", "none", "none", "none", freshRaw));

        aggregateRepository.save(new AnalyticsDailyAggregate(staleAggDate, "sen2nal", "case_study_open", "none", "none", 2L, staleRaw));
        aggregateRepository.save(new AnalyticsDailyAggregate(freshAggDate, "sen2nal", "case_study_open", "none", "none", 5L, freshRaw));

        var result = retentionService.purgeExpiredRecords();
        assertThat(result.deletedRawEvents()).isEqualTo(1L);
        assertThat(result.deletedDailyAggregates()).isEqualTo(1L);
        assertThat(eventRepository.count()).isEqualTo(1L);
        assertThat(aggregateRepository.count()).isEqualTo(1L);
    }
}
