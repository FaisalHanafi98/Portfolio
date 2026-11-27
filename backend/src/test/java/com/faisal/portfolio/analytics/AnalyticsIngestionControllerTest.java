package com.faisal.portfolio.analytics;

import com.faisal.portfolio.PortfolioApplication;
import io.micrometer.core.instrument.MeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(
        classes = PortfolioApplication.class,
        properties = {
                "portfolio.status.github.scheduling-enabled=false",
                "portfolio.analytics.rate-limit-per-minute=30"
        }
)
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class AnalyticsIngestionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AnalyticsEventRepository eventRepository;

    @Autowired
    private AnalyticsDailyAggregateRepository aggregateRepository;

    @Autowired
    private MeterRegistry meterRegistry;

    @BeforeEach
    void cleanAnalyticsTables() {
        eventRepository.deleteAll();
        aggregateRepository.deleteAll();
    }

    @Test
    void validEventPersistsOnceAndDeduplicatesRetriesAndIgnoresClientClockSkew() throws Exception {
        String body = """
                {
                  "eventId": "evt-retry-dedup-001",
                  "schemaVersion": 1,
                  "eventName": "meaningful_read",
                  "projectSlug": "creams",
                  "sectionKey": "architecture",
                  "depthBucket": "75",
                  "topicLabel": "software_engineering",
                  "clientOccurredAt": "2020-01-01T00:00:00Z"
                }
                """;

        mockMvc.perform(post("/api/v1/analytics/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isAccepted())
                .andExpect(header().string("Cache-Control", containsString("no-store")))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.deduplicated").value(false));

        mockMvc.perform(post("/api/v1/analytics/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.deduplicated").value(true));

        assertThat(eventRepository.count()).isEqualTo(1L);
        var aggregates = aggregateRepository.findAll();
        assertThat(aggregates).hasSize(1);
        assertThat(aggregates.getFirst().getEventCount()).isEqualTo(1L);
        assertThat(aggregates.getFirst().getBucketDate()).isEqualTo(LocalDate.now(ZoneOffset.UTC));

        assertThat(meterRegistry.find("portfolio.api.route.requests").tag("route", "/api/v1/analytics/events").timer())
                .isNotNull();
    }

    @Test
    void rejectsDisallowedValuesUnknownPiiFieldsAndOversizedPayloads() throws Exception {
        String unknownPiiField = """
                {
                  "eventId": "evt-bad-field-001",
                  "schemaVersion": 1,
                  "eventName": "case_study_open",
                  "projectSlug": "creams",
                  "ip": "203.0.113.5",
                  "prompt": "raw user text"
                }
                """;

        mockMvc.perform(post("/api/v1/analytics/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(unknownPiiField))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error", not(containsString("UnrecognizedPropertyException"))));

        String badAllowlist = """
                {
                  "eventId": "evt-bad-slug-001",
                  "schemaVersion": 1,
                  "eventName": "case_study_open",
                  "projectSlug": "secret-private-repo"
                }
                """;

        mockMvc.perform(post("/api/v1/analytics/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(badAllowlist))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));

        String oversized = "{\"eventId\":\"evt-oversized-001\",\"schemaVersion\":1,\"eventName\":\"page_view\",\"projectSlug\":\""
                + "a".repeat(2500) + "\"}";

        mockMvc.perform(post("/api/v1/analytics/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(oversized))
                .andExpect(status().isPayloadTooLarge());

        assertThat(eventRepository.count()).isZero();
    }

    @Test
    void concurrentLoadMaintainsExactAggregateCountsAndBoundsLatency() throws Exception {
        try (var pool = Executors.newFixedThreadPool(4)) {
            List<Callable<Integer>> tasks = new ArrayList<>();
            for (int i = 0; i < 16; i++) {
                final String eventId = String.format("evt-concurrent-%04d", i % 10);
                tasks.add(() -> mockMvc.perform(post("/api/v1/analytics/events")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                          "eventId": "%s",
                                          "schemaVersion": 1,
                                          "eventName": "case_study_open",
                                          "projectSlug": "sen2nal",
                                          "sectionKey": "projects"
                                        }
                                        """.formatted(eventId)))
                        .andReturn()
                        .getResponse()
                        .getStatus());
            }

            List<Future<Integer>> results = pool.invokeAll(tasks);
            for (Future<Integer> f : results) {
                int statusCode = f.get(5, TimeUnit.SECONDS);
                assertThat(statusCode).isIn(200, 202);
            }
        }

        assertThat(eventRepository.count()).isEqualTo(10L);
        var aggregates = aggregateRepository.findAll();
        assertThat(aggregates).hasSize(1);
        assertThat(aggregates.getFirst().getEventCount()).isEqualTo(10L);

        var timer = meterRegistry.find("portfolio.api.route.requests")
                .tag("route", "/api/v1/analytics/events")
                .timer();
        assertThat(timer).isNotNull();
        assertThat(timer.count()).isGreaterThanOrEqualTo(16L);
    }

    @Test
    void rateLimitReturns429WithRetryAfterWhenBurstExceedsAllowance() throws Exception {
        for (int i = 0; i < 30; i++) {
            String ok = """
                    {
                      "eventId": "evt-ratelimit-%04d",
                      "schemaVersion": 1,
                      "eventName": "page_view",
                      "projectSlug": "__site__"
                    }
                    """.formatted(i);
            mockMvc.perform(post("/api/v1/analytics/events")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(ok))
                    .andExpect(status().isAccepted());
        }

        String blocked = """
                {
                  "eventId": "evt-ratelimit-0099",
                  "schemaVersion": 1,
                  "eventName": "page_view",
                  "projectSlug": "__site__"
                }
                """;

        mockMvc.perform(post("/api/v1/analytics/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(blocked))
                .andExpect(status().isTooManyRequests())
                .andExpect(header().exists("Retry-After"))
                .andExpect(jsonPath("$.success").value(false));

        assertThat(eventRepository.count()).isEqualTo(30L);
    }
}
