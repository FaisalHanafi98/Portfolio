package com.faisal.portfolio.status;

import com.faisal.portfolio.PortfolioApplication;
import io.micrometer.core.instrument.MeterRegistry;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = PortfolioApplication.class)
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class StatusControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MeterRegistry meterRegistry;

    @Test
    void getStatus_returnsSnapshotWithInsufficientDataBeforeMinSamples() throws Exception {
        mockMvc.perform(get("/api/v1/status"))
                .andExpect(status().isOk())
                .andExpect(header().string("Cache-Control", containsString("no-store")))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("healthy"))
                .andExpect(jsonPath("$.data.uptimeSeconds", greaterThanOrEqualTo(0)))
                .andExpect(jsonPath("$.data.revision").value("local-dev"))
                .andExpect(jsonPath("$.data.database.status").value("up"))
                .andExpect(jsonPath("$.data.database.latencyMs", greaterThanOrEqualTo(0)))
                .andExpect(jsonPath("$.data.traffic.windowSeconds").value(300))
                .andExpect(jsonPath("$.data.traffic.minSamples").value(20))
                .andExpect(jsonPath("$.data.traffic.p95Status").value("insufficient_data"))
                .andExpect(jsonPath("$.data.traffic.p95Ms").value(nullValue()));
    }

    @Test
    void getStatus_reportsMeasuredP95AndKeepsRouteTagsBoundedAcrossArbitraryPaths() throws Exception {
        for (int i = 0; i < 10; i++) {
            mockMvc.perform(get("/api/v1/projects")).andExpect(status().isOk());
        }
        for (int i = 0; i < 5; i++) {
            mockMvc.perform(get("/api/v1/projects/nonexistent-slug-" + i)).andExpect(status().isNotFound());
        }
        for (int i = 0; i < 5; i++) {
            mockMvc.perform(get("/api/v1/unknown-probe-" + i)).andExpect(status().isNotFound());
        }

        mockMvc.perform(get("/api/v1/status"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.traffic.requestCount", greaterThanOrEqualTo(20)))
                .andExpect(jsonPath("$.data.traffic.p95Status").value("measured"))
                .andExpect(jsonPath("$.data.traffic.p95Ms", greaterThanOrEqualTo(0.0)));

        List<String> routeTags = meterRegistry.find(ApiLatencyTracker.ROUTE_TIMER_NAME).timers().stream()
                .map(timer -> timer.getId().getTag("route"))
                .distinct()
                .sorted()
                .toList();

        assertThat(routeTags).containsExactly(
                "/api/v1/projects",
                "/api/v1/projects/{slug}",
                "/api/v1/status",
                ApiLatencyTracker.OTHER_ROUTE
        );

        mockMvc.perform(get("/api/v1/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("ok"));
    }
}
