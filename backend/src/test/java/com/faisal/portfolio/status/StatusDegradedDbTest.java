package com.faisal.portfolio.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.client.RestClient;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
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

    private static GitHubStatusService disabledGitHubService(StatusProperties properties) {
        StatusProperties copy = new StatusProperties();
        copy.setRevision(properties.getRevision());
        copy.getGithub().setEnabled(false);
        return new GitHubStatusService(
                copy,
                RestClient.create(),
                new ObjectMapper().registerModule(new JavaTimeModule()),
                Clock.fixed(Instant.parse("2026-11-27T00:00:00Z"), ZoneOffset.UTC)
        );
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
                disabledGitHubService(properties),
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
                disabledGitHubService(properties),
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

    @Test
    void databaseOutageLeavesHealthyGitHubFeedIntactAndGitHubOutageLeavesCoreHealthy() throws Exception {
        DataSource downDb = Mockito.mock(DataSource.class);
        when(downDb.getConnection()).thenThrow(new SQLException("DB offline"));

        DataSource upDb = Mockito.mock(DataSource.class);
        Connection okConnection = Mockito.mock(Connection.class);
        when(okConnection.isValid(1)).thenReturn(true);
        when(upDb.getConnection()).thenReturn(okConnection);

        StatusProperties props = new StatusProperties();
        props.getGithub().setEnabled(true);
        props.getGithub().setOwner("FaisalHanafi98");
        props.getGithub().setRepo("Portfolio");

        RestClient.Builder healthyBuilder = RestClient.builder();
        MockRestServiceServer healthyServer = MockRestServiceServer.bindTo(healthyBuilder).build();
        healthyServer.expect(requestTo("https://api.github.com/repos/FaisalHanafi98/Portfolio/actions/runs?per_page=5"))
                .andRespond(withSuccess("""
                        {"workflow_runs":[{"id":1,"name":"CI","head_branch":"main","event":"push","status":"completed","conclusion":"success","head_sha":"21497cd","updated_at":"2026-11-26T03:42:00Z","html_url":"https://github.com/FaisalHanafi98/Portfolio/actions/runs/1"}]}
                        """, MediaType.APPLICATION_JSON));
        healthyServer.expect(requestTo("https://api.github.com/repos/FaisalHanafi98/Portfolio/commits?per_page=5"))
                .andRespond(withSuccess("""
                        [{"sha":"21497cd","html_url":"https://github.com/FaisalHanafi98/Portfolio/commit/21497cd","commit":{"message":"Feat(Status): Init","author":{"date":"2026-11-26T03:40:00Z"}}}]
                        """, MediaType.APPLICATION_JSON));

        GitHubStatusService healthyGitHub = new GitHubStatusService(
                props,
                healthyBuilder.build(),
                new ObjectMapper().registerModule(new JavaTimeModule()),
                Clock.fixed(Instant.parse("2026-11-27T00:00:00Z"), ZoneOffset.UTC)
        );
        healthyGitHub.pollIfDue();
        healthyServer.verify();

        StatusService dbDownService = new StatusService(
                downDb,
                new ApiLatencyTracker(new SimpleMeterRegistry(), props),
                healthyGitHub,
                props,
                emptyProvider(),
                emptyProvider()
        );
        StatusService githubDownService = new StatusService(
                upDb,
                new ApiLatencyTracker(new SimpleMeterRegistry(), props),
                disabledGitHubService(props),
                props,
                emptyProvider(),
                emptyProvider()
        );

        try {
            StatusSnapshotDto dbDown = dbDownService.getSnapshot();
            assertThat(dbDown.status()).isEqualTo("degraded");
            assertThat(dbDown.database().status()).isEqualTo("degraded");
            assertThat(dbDown.github().status()).isEqualTo("healthy");
            assertThat(dbDown.github().workflowRuns()).hasSize(1);

            StatusSnapshotDto ghDown = githubDownService.getSnapshot();
            assertThat(ghDown.status()).isEqualTo("healthy");
            assertThat(ghDown.database().status()).isEqualTo("up");
            assertThat(ghDown.github().status()).isEqualTo("unavailable");
        } finally {
            dbDownService.shutdown();
            githubDownService.shutdown();
        }
    }
}
