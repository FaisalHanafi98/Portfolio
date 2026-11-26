package com.faisal.portfolio.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class GitHubStatusServiceTest {

    private static final String RUNS_URL =
            "https://api.github.com/repos/FaisalHanafi98/Portfolio/actions/runs?per_page=5";
    private static final String COMMITS_URL =
            "https://api.github.com/repos/FaisalHanafi98/Portfolio/commits?per_page=5";
    private static final String TEST_SECRET_TOKEN = "ghp_syntheticSecretToken1234567890";

    private static final class MutableClock extends Clock {
        private final AtomicReference<Instant> current;

        MutableClock(Instant initial) {
            this.current = new AtomicReference<>(initial);
        }

        void advanceSeconds(long seconds) {
            current.updateAndGet(now -> now.plusSeconds(seconds));
        }

        @Override
        public ZoneId getZone() {
            return ZoneOffset.UTC;
        }

        @Override
        public Clock withZone(ZoneId zone) {
            return this;
        }

        @Override
        public Instant instant() {
            return current.get();
        }
    }

    private StatusProperties defaultProperties() {
        StatusProperties properties = new StatusProperties();
        properties.getGithub().setEnabled(true);
        properties.getGithub().setOwner("FaisalHanafi98");
        properties.getGithub().setRepo("Portfolio");
        properties.getGithub().setApiBaseUrl("https://api.github.com");
        properties.getGithub().setToken(TEST_SECRET_TOKEN);
        properties.getGithub().setCacheTtlSeconds(900);
        properties.getGithub().setInitialBackoffSeconds(60);
        properties.getGithub().setMaxBackoffSeconds(600);
        properties.getGithub().setMaxItems(5);
        return properties;
    }

    private String runsPayload(String runName, String branch, String htmlUrl) {
        return """
                {
                  "total_count": 1,
                  "workflow_runs": [
                    {
                      "id": 44970309509,
                      "name": "%s",
                      "head_branch": "%s",
                      "event": "push",
                      "status": "completed",
                      "conclusion": "success",
                      "head_sha": "21497cd1234567890abcdef",
                      "updated_at": "2026-11-26T03:42:00Z",
                      "html_url": "%s"
                    }
                  ]
                }
                """.formatted(runName, branch, htmlUrl);
    }

    private String commitsPayload(String sha, String message, String email, String htmlUrl) {
        return """
                [
                  {
                    "sha": "%s",
                    "html_url": "%s",
                    "commit": {
                      "message": "%s",
                      "author": {
                        "name": "Faisal",
                        "email": "%s",
                        "date": "2026-11-26T03:40:00Z"
                      }
                    }
                  }
                ]
                """.formatted(sha, htmlUrl, message, email);
    }

    @Test
    void initialStateBeforeFirstSync_isUnavailableWithoutBlockedWait() {
        MutableClock clock = new MutableClock(Instant.parse("2026-11-27T00:00:00Z"));
        RestClient.Builder builder = RestClient.builder();
        MockRestServiceServer.bindTo(builder).build();

        GitHubStatusService service = new GitHubStatusService(
                defaultProperties(),
                builder.build(),
                new ObjectMapper().registerModule(new JavaTimeModule()),
                clock
        );

        StatusSnapshotDto.GitHubStatusDto snapshot = service.getSnapshot(clock.instant());
        assertThat(snapshot.status()).isEqualTo("unavailable");
        assertThat(snapshot.repository()).isEqualTo("FaisalHanafi98/Portfolio");
        assertThat(snapshot.lastSyncedAt()).isNull();
        assertThat(snapshot.snapshotAgeSeconds()).isNull();
        assertThat(snapshot.cacheTtlSeconds()).isEqualTo(900);
        assertThat(snapshot.stateReason()).isEqualTo("awaiting_first_sync");
        assertThat(snapshot.workflowRuns()).isEmpty();
        assertThat(snapshot.recentCommits()).isEmpty();
    }

    @Test
    void invalidTargetConfigOrSchemeIsRejectedBeforeMakingOutboundRequests() {
        MutableClock clock = new MutableClock(Instant.parse("2026-11-27T00:00:00Z"));
        RestClient.Builder builder = RestClient.builder();
        MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();

        StatusProperties invalidProps = defaultProperties();
        invalidProps.getGithub().setOwner("../bad-owner");
        invalidProps.getGithub().setApiBaseUrl("http://evil.example.com");

        GitHubStatusService service = new GitHubStatusService(
                invalidProps,
                builder.build(),
                new ObjectMapper().registerModule(new JavaTimeModule()),
                clock
        );

        service.pollIfDue();
        server.verify();

        StatusSnapshotDto.GitHubStatusDto snapshot = service.getSnapshot(clock.instant());
        assertThat(snapshot.status()).isEqualTo("unavailable");
        assertThat(snapshot.stateReason()).isEqualTo("invalid_repository_config");
        assertThat(snapshot.repository()).isEqualTo("unconfigured");
    }

    @Test
    void successfulPollSanitizesEmailsAndUntrustedUrlsAndHonors15MinuteCache() {
        MutableClock clock = new MutableClock(Instant.parse("2026-11-27T00:00:00Z"));
        RestClient.Builder builder = RestClient.builder();
        MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();

        HttpHeaders runsHeaders = new HttpHeaders();
        runsHeaders.setETag("\"runs-etag-1\"");
        HttpHeaders commitsHeaders = new HttpHeaders();
        commitsHeaders.setETag("\"commits-etag-1\"");

        server.expect(requestTo(RUNS_URL))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header("Authorization", "Bearer " + TEST_SECRET_TOKEN))
                .andRespond(withSuccess(
                        runsPayload("CI", "main", "https://github.com/FaisalHanafi98/Portfolio/actions/runs/44970309509"),
                        MediaType.APPLICATION_JSON
                ).headers(runsHeaders));

        server.expect(requestTo(COMMITS_URL))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header("Authorization", "Bearer " + TEST_SECRET_TOKEN))
                .andRespond(withSuccess(
                        commitsPayload(
                                "21497cd000000000000000000000000000000000",
                                "Feat(Status): Add public /status view.\\n\\nBody details omitted",
                                "private-author@example.com",
                                "javascript:alert(1)"
                        ),
                        MediaType.APPLICATION_JSON
                ).headers(commitsHeaders));

        GitHubStatusService service = new GitHubStatusService(
                defaultProperties(),
                builder.build(),
                new ObjectMapper().registerModule(new JavaTimeModule()),
                clock
        );

        service.pollIfDue();
        server.verify();

        StatusSnapshotDto.GitHubStatusDto fresh = service.getSnapshot(clock.instant());
        assertThat(fresh.status()).isEqualTo("healthy");
        assertThat(fresh.snapshotAgeSeconds()).isZero();
        assertThat(fresh.nextPollAt()).isEqualTo(Instant.parse("2026-11-27T00:15:00Z"));
        assertThat(fresh.stateReason()).isNull();
        assertThat(fresh.workflowRuns()).hasSize(1);
        assertThat(fresh.workflowRuns().getFirst().name()).isEqualTo("CI");
        assertThat(fresh.workflowRuns().getFirst().shortSha()).isEqualTo("21497cd");
        assertThat(fresh.workflowRuns().getFirst().htmlUrl())
                .isEqualTo("https://github.com/FaisalHanafi98/Portfolio/actions/runs/44970309509");

        assertThat(fresh.recentCommits()).hasSize(1);
        assertThat(fresh.recentCommits().getFirst().shortSha()).isEqualTo("21497cd");
        assertThat(fresh.recentCommits().getFirst().message()).isEqualTo("Feat(Status): Add public /status view.");
        assertThat(fresh.recentCommits().getFirst().htmlUrl()).isNull();
        assertThat(fresh.toString()).doesNotContain("private-author@example.com");
        assertThat(fresh.toString()).doesNotContain(TEST_SECRET_TOKEN);

        clock.advanceSeconds(600);
        service.pollIfDue();
        server.verify();

        StatusSnapshotDto.GitHubStatusDto aged = service.getSnapshot(clock.instant());
        assertThat(aged.status()).isEqualTo("healthy");
        assertThat(aged.snapshotAgeSeconds()).isEqualTo(600L);
    }

    @Test
    void conditional304AfterCacheExpiryPreservesItemsAndExtendsCacheWindow() {
        MutableClock clock = new MutableClock(Instant.parse("2026-11-27T00:00:00Z"));
        RestClient.Builder builder = RestClient.builder();
        MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();

        HttpHeaders runsHeaders = new HttpHeaders();
        runsHeaders.setETag("\"runs-etag-1\"");
        HttpHeaders commitsHeaders = new HttpHeaders();
        commitsHeaders.setETag("\"commits-etag-1\"");

        server.expect(requestTo(RUNS_URL))
                .andRespond(withSuccess(
                        runsPayload("CI", "main", "https://github.com/FaisalHanafi98/Portfolio/actions/runs/1"),
                        MediaType.APPLICATION_JSON
                ).headers(runsHeaders));
        server.expect(requestTo(COMMITS_URL))
                .andRespond(withSuccess(
                        commitsPayload("21497cd1234", "Feat(Status): Init", "a@b.c", "https://github.com/FaisalHanafi98/Portfolio/commit/21497cd"),
                        MediaType.APPLICATION_JSON
                ).headers(commitsHeaders));

        server.expect(requestTo(RUNS_URL))
                .andExpect(header("If-None-Match", "\"runs-etag-1\""))
                .andRespond(withStatus(HttpStatus.NOT_MODIFIED));
        server.expect(requestTo(COMMITS_URL))
                .andExpect(header("If-None-Match", "\"commits-etag-1\""))
                .andRespond(withStatus(HttpStatus.NOT_MODIFIED));

        GitHubStatusService service = new GitHubStatusService(
                defaultProperties(),
                builder.build(),
                new ObjectMapper().registerModule(new JavaTimeModule()),
                clock
        );

        service.pollIfDue();
        clock.advanceSeconds(900);
        service.pollIfDue();
        server.verify();

        StatusSnapshotDto.GitHubStatusDto snapshot = service.getSnapshot(clock.instant());
        assertThat(snapshot.status()).isEqualTo("healthy");
        assertThat(snapshot.snapshotAgeSeconds()).isZero();
        assertThat(snapshot.lastSyncedAt()).isEqualTo(Instant.parse("2026-11-27T00:15:00Z"));
        assertThat(snapshot.nextPollAt()).isEqualTo(Instant.parse("2026-11-27T00:30:00Z"));
        assertThat(snapshot.workflowRuns()).hasSize(1);
        assertThat(snapshot.recentCommits()).hasSize(1);
    }

    @Test
    void rateLimitAndUpstreamFailuresReturnStaleSnapshotWithAgeBackoffAndSecretRedaction() {
        MutableClock clock = new MutableClock(Instant.parse("2026-11-27T00:00:00Z"));
        RestClient.Builder builder = RestClient.builder();
        MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();

        server.expect(requestTo(RUNS_URL))
                .andRespond(withSuccess(
                        runsPayload("CI", "main", "https://github.com/FaisalHanafi98/Portfolio/actions/runs/1"),
                        MediaType.APPLICATION_JSON
                ));
        server.expect(requestTo(COMMITS_URL))
                .andRespond(withSuccess(
                        commitsPayload("21497cd1234", "Feat(Status): Init", "a@b.c", "https://github.com/FaisalHanafi98/Portfolio/commit/21497cd"),
                        MediaType.APPLICATION_JSON
                ));

        HttpHeaders rateLimitHeaders = new HttpHeaders();
        rateLimitHeaders.set("Retry-After", "180");
        server.expect(requestTo(RUNS_URL))
                .andRespond(withStatus(HttpStatus.TOO_MANY_REQUESTS)
                        .headers(rateLimitHeaders)
                        .body("Bad credentials " + TEST_SECRET_TOKEN));
        server.expect(requestTo(COMMITS_URL))
                .andRespond(withStatus(HttpStatus.TOO_MANY_REQUESTS)
                        .headers(rateLimitHeaders));

        server.expect(requestTo(RUNS_URL))
                .andRespond(withStatus(HttpStatus.SERVICE_UNAVAILABLE));
        server.expect(requestTo(COMMITS_URL))
                .andRespond(withStatus(HttpStatus.SERVICE_UNAVAILABLE));

        GitHubStatusService service = new GitHubStatusService(
                defaultProperties(),
                builder.build(),
                new ObjectMapper().registerModule(new JavaTimeModule()),
                clock
        );

        service.pollIfDue();
        clock.advanceSeconds(900);
        service.pollIfDue();

        StatusSnapshotDto.GitHubStatusDto staleAfterRateLimit = service.getSnapshot(clock.instant());
        assertThat(staleAfterRateLimit.status()).isEqualTo("stale");
        assertThat(staleAfterRateLimit.snapshotAgeSeconds()).isEqualTo(900L);
        assertThat(staleAfterRateLimit.stateReason()).isEqualTo("upstream_rate_limited");
        assertThat(staleAfterRateLimit.nextPollAt()).isEqualTo(Instant.parse("2026-11-27T00:18:00Z"));
        assertThat(staleAfterRateLimit.workflowRuns()).hasSize(1);
        assertThat(staleAfterRateLimit.recentCommits()).hasSize(1);
        assertThat(staleAfterRateLimit.toString()).doesNotContain(TEST_SECRET_TOKEN);

        clock.advanceSeconds(60);
        service.pollIfDue();
        assertThat(service.getSnapshot(clock.instant()).snapshotAgeSeconds()).isEqualTo(960L);

        clock.advanceSeconds(120);
        service.pollIfDue();
        server.verify();

        StatusSnapshotDto.GitHubStatusDto secondFailure = service.getSnapshot(clock.instant());
        assertThat(secondFailure.status()).isEqualTo("stale");
        assertThat(secondFailure.stateReason()).isEqualTo("upstream_unavailable");
        assertThat(secondFailure.nextPollAt()).isEqualTo(Instant.parse("2026-11-27T00:20:00Z"));
    }

    @Test
    void partialFeedFailureMarksDegradedAndEmptyRepositoryMarksEmpty() {
        MutableClock clock = new MutableClock(Instant.parse("2026-11-27T00:00:00Z"));
        RestClient.Builder builder = RestClient.builder();
        MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();

        server.expect(requestTo(RUNS_URL))
                .andRespond(withSuccess(
                        runsPayload("CI", "main", "https://github.com/FaisalHanafi98/Portfolio/actions/runs/1"),
                        MediaType.APPLICATION_JSON
                ));
        server.expect(requestTo(COMMITS_URL))
                .andRespond(withStatus(HttpStatus.BAD_GATEWAY));

        server.expect(requestTo(RUNS_URL))
                .andRespond(withSuccess("{\"total_count\":0,\"workflow_runs\":[]}", MediaType.APPLICATION_JSON));
        server.expect(requestTo(COMMITS_URL))
                .andRespond(withSuccess("[]", MediaType.APPLICATION_JSON));

        GitHubStatusService service = new GitHubStatusService(
                defaultProperties(),
                builder.build(),
                new ObjectMapper().registerModule(new JavaTimeModule()),
                clock
        );

        service.pollIfDue();
        StatusSnapshotDto.GitHubStatusDto degraded = service.getSnapshot(clock.instant());
        assertThat(degraded.status()).isEqualTo("degraded");
        assertThat(degraded.stateReason()).isEqualTo("partial_feed_error");
        assertThat(degraded.workflowRuns()).hasSize(1);
        assertThat(degraded.recentCommits()).isEmpty();

        clock.advanceSeconds(60);
        service.pollIfDue();
        server.verify();

        StatusSnapshotDto.GitHubStatusDto empty = service.getSnapshot(clock.instant());
        assertThat(empty.status()).isEqualTo("empty");
        assertThat(empty.stateReason()).isEqualTo("empty_activity");
        assertThat(empty.workflowRuns()).isEmpty();
        assertThat(empty.recentCommits()).isEmpty();
        assertThat(empty.snapshotAgeSeconds()).isZero();
    }
}
