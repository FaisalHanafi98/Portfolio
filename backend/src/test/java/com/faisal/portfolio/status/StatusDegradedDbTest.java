package com.faisal.portfolio.status;

import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class StatusDegradedDbTest {

    @SuppressWarnings("unchecked")
    private static <T> ObjectProvider<T> emptyProvider() {
        ObjectProvider<T> provider = Mockito.mock(ObjectProvider.class);
        when(provider.getIfAvailable()).thenReturn(null);
        return provider;
    }

    @Test
    void slowDatabaseConnectionBoundsResponseTimeAndMarksDegraded() {
        DataSource slowDataSource = Mockito.mock(DataSource.class);
        try {
            when(slowDataSource.getConnection()).thenAnswer(invocation -> {
                Thread.sleep(2500);
                return null;
            });
        } catch (SQLException ignored) {
            // mock setup only
        }

        StatusProperties properties = new StatusProperties();
        properties.setDbTimeoutMs(150L);
        properties.setRevision("sha-test");

        StatusService service = new StatusService(
                slowDataSource,
                new ApiLatencyTracker(new SimpleMeterRegistry(), properties),
                properties,
                emptyProvider(),
                emptyProvider()
        );

        try {
            long startNanos = System.nanoTime();
            StatusSnapshotDto snapshot = service.getSnapshot();
            long elapsedMs = Duration.ofNanos(System.nanoTime() - startNanos).toMillis();

            assertThat(elapsedMs).isLessThan(1000L);
            assertThat(snapshot.status()).isEqualTo("degraded");
            assertThat(snapshot.database().status()).isEqualTo("degraded");
            assertThat(snapshot.database().latencyMs()).isNull();
            assertThat(snapshot.revision()).isEqualTo("sha-test");
        } finally {
            service.shutdown();
        }
    }

    @Test
    void failingDatabaseRedactsJdbcUrlsHostnamesAndExceptionsInHttpResponse() throws Exception {
        DataSource failingDataSource = Mockito.mock(DataSource.class);
        when(failingDataSource.getConnection()).thenThrow(
                new SQLException("FATAL: connection to jdbc:postgresql://portfolio-db:5432/portfolio refused")
        );

        StatusProperties properties = new StatusProperties();
        properties.setDbTimeoutMs(250L);

        StatusService service = new StatusService(
                failingDataSource,
                new ApiLatencyTracker(new SimpleMeterRegistry(), properties),
                properties,
                emptyProvider(),
                emptyProvider()
        );

        try {
            MockMvc mockMvc = MockMvcBuilders.standaloneSetup(new StatusController(service)).build();
            mockMvc.perform(get("/api/v1/status"))
                    .andExpect(status().isOk())
                    .andExpect(header().string("Cache-Control", containsString("no-store")))
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.status").value("degraded"))
                    .andExpect(jsonPath("$.data.database.status").value("degraded"))
                    .andExpect(jsonPath("$.data.database.latencyMs").value(nullValue()))
                    .andExpect(content().string(not(containsString("jdbc:"))))
                    .andExpect(content().string(not(containsString("postgresql"))))
                    .andExpect(content().string(not(containsString("portfolio-db"))))
                    .andExpect(content().string(not(containsString(":5432"))))
                    .andExpect(content().string(not(containsString("SQLException"))));
        } finally {
            service.shutdown();
        }
    }
}
