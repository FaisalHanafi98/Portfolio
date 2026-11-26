package com.faisal.portfolio.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Pattern;

@Service
public class GitHubStatusService {

    private static final Logger log = LoggerFactory.getLogger(GitHubStatusService.class);
    private static final int MAX_RESPONSE_BYTES = 512 * 1024;
    private static final Pattern SLUG_PATTERN = Pattern.compile("^[A-Za-z0-9._-]+$");
    private static final Pattern HEX_SHA_PATTERN = Pattern.compile("^[0-9a-fA-F]{7,64}$");
    private static final Pattern GITHUB_HTML_URL_PATTERN =
            Pattern.compile("^https://github\\.com/[A-Za-z0-9._-]+/[A-Za-z0-9._-]+(/.*)?$");

    private final StatusProperties properties;
    private final RestClient restClient;
    private final ObjectMapper objectMapper;
    private final Clock clock;
    private final Object pollLock = new Object();
    private final AtomicReference<CachedState> stateRef;
    private final URI validatedApiBaseUri;
    private final String repositoryDisplay;
    private final boolean validTargetConfig;

    public GitHubStatusService(
            StatusProperties properties,
            RestClient restClient,
            ObjectMapper objectMapper,
            Clock clock
    ) {
        this.properties = properties;
        this.restClient = restClient;
        this.objectMapper = objectMapper;
        this.clock = clock;

        StatusProperties.GitHubProperties cfg = properties.getGithub();
        String owner = cfg.getOwner() != null ? cfg.getOwner().trim() : "";
        String repo = cfg.getRepo() != null ? cfg.getRepo().trim() : "";
        URI baseUri = parseSafeBaseUri(cfg.getApiBaseUrl());

        this.validTargetConfig = SLUG_PATTERN.matcher(owner).matches()
                && SLUG_PATTERN.matcher(repo).matches()
                && baseUri != null;
        this.validatedApiBaseUri = baseUri;
        this.repositoryDisplay = (SLUG_PATTERN.matcher(owner).matches() && SLUG_PATTERN.matcher(repo).matches())
                ? owner + "/" + repo
                : "unconfigured";

        String initialReason = !cfg.isEnabled()
                ? "polling_disabled"
                : (!this.validTargetConfig ? "invalid_repository_config" : "awaiting_first_sync");

        this.stateRef = new AtomicReference<>(CachedState.initial(initialReason));
    }

    @Scheduled(
            fixedDelayString = "${portfolio.status.github.poll-tick-ms:30000}",
            initialDelayString = "${portfolio.status.github.poll-initial-delay-ms:10000}"
    )
    public void scheduledPoll() {
        pollIfDue();
    }

    public void pollIfDue() {
        StatusProperties.GitHubProperties cfg = properties.getGithub();
        if (!cfg.isEnabled() || !validTargetConfig) {
            return;
        }

        synchronized (pollLock) {
            Instant now = clock.instant();
            CachedState current = stateRef.get();

            if (current.nextPollAt() != null && now.isBefore(current.nextPollAt())) {
                return;
            }
            if (current.consecutiveFailures() == 0
                    && current.lastSyncedAt() != null
                    && now.isBefore(current.lastSyncedAt().plusSeconds(cfg.getCacheTtlSeconds()))) {
                return;
            }

            String token = cfg.getToken() != null && !cfg.getToken().isBlank() ? cfg.getToken().trim() : null;
            URI runsUri = buildEndpointUri(cfg.getOwner().trim(), cfg.getRepo().trim(), cfg.getMaxItems(), "actions", "runs");
            URI commitsUri = buildEndpointUri(cfg.getOwner().trim(), cfg.getRepo().trim(), cfg.getMaxItems(), "commits");

            FeedFetchResult<List<StatusSnapshotDto.GitHubWorkflowRunDto>> runsResult =
                    fetchRunsFeed(runsUri, current.runsEtag(), current.runsLastModified(), current.workflowRuns(), token, cfg.getMaxItems());
            FeedFetchResult<List<StatusSnapshotDto.GitHubCommitDto>> commitsResult =
                    fetchCommitsFeed(commitsUri, current.commitsEtag(), current.commitsLastModified(), current.recentCommits(), token, cfg.getMaxItems());

            stateRef.set(reconcileState(current, runsResult, commitsResult, now, cfg));
        }
    }

    public StatusSnapshotDto.GitHubStatusDto getSnapshot(Instant now) {
        StatusProperties.GitHubProperties cfg = properties.getGithub();
        int cacheTtl = cfg.getCacheTtlSeconds();

        if (!cfg.isEnabled()) {
            return new StatusSnapshotDto.GitHubStatusDto(
                    "unavailable",
                    repositoryDisplay,
                    null,
                    null,
                    cacheTtl,
                    null,
                    "polling_disabled",
                    List.of(),
                    List.of()
            );
        }

        CachedState state = stateRef.get();
        Long ageSeconds = state.lastSyncedAt() != null
                ? Math.max(0L, Duration.between(state.lastSyncedAt(), now).getSeconds())
                : null;

        String effectiveStatus = state.status();
        String effectiveReason = state.stateReason();
        if (("healthy".equals(effectiveStatus) || "empty".equals(effectiveStatus))
                && ageSeconds != null
                && ageSeconds > cacheTtl + 60L) {
            effectiveStatus = "stale";
            effectiveReason = "cache_ttl_expired";
        }

        return new StatusSnapshotDto.GitHubStatusDto(
                effectiveStatus,
                repositoryDisplay,
                state.lastSyncedAt(),
                ageSeconds,
                cacheTtl,
                state.nextPollAt(),
                effectiveReason,
                state.workflowRuns(),
                state.recentCommits()
        );
    }

    private CachedState reconcileState(
            CachedState prev,
            FeedFetchResult<List<StatusSnapshotDto.GitHubWorkflowRunDto>> runs,
            FeedFetchResult<List<StatusSnapshotDto.GitHubCommitDto>> commits,
            Instant now,
            StatusProperties.GitHubProperties cfg
    ) {
        int cacheTtl = cfg.getCacheTtlSeconds();
        boolean bothOk = runs.success() && commits.success();
        boolean eitherOk = runs.success() || commits.success();

        Instant runsSyncedAt = runs.success() ? now : prev.runsSyncedAt();
        Instant commitsSyncedAt = commits.success() ? now : prev.commitsSyncedAt();
        List<StatusSnapshotDto.GitHubWorkflowRunDto> nextRuns = runs.data();
        List<StatusSnapshotDto.GitHubCommitDto> nextCommits = commits.data();

        if (bothOk) {
            boolean isEmpty = nextRuns.isEmpty() && nextCommits.isEmpty();
            return new CachedState(
                    isEmpty ? "empty" : "healthy",
                    isEmpty ? "empty_activity" : null,
                    true,
                    now,
                    now.plusSeconds(cacheTtl),
                    now,
                    now,
                    0,
                    runs.etag(),
                    runs.lastModified(),
                    nextRuns,
                    commits.etag(),
                    commits.lastModified(),
                    nextCommits
            );
        }

        int nextFailures = prev.consecutiveFailures() + 1;
        long expBackoff = computeExponentialBackoff(nextFailures, cfg.getInitialBackoffSeconds(), cfg.getMaxBackoffSeconds());
        long retryHint = Math.max(
                runs.retryAfterSeconds() != null ? runs.retryAfterSeconds() : 0L,
                commits.retryAfterSeconds() != null ? commits.retryAfterSeconds() : 0L
        );
        long clampedHint = Math.min(retryHint, cfg.getMaxBackoffSeconds());
        long delaySeconds = Math.max(expBackoff, clampedHint);
        Instant nextPollAt = now.plusSeconds(delaySeconds);

        boolean rateLimited = runs.rateLimited() || commits.rateLimited();
        String failureReason = rateLimited ? "upstream_rate_limited" : "upstream_unavailable";

        if (eitherOk) {
            Instant effectiveLastSyncedAt = prev.hasCompleteBaseline()
                    ? minNonNull(runsSyncedAt, commitsSyncedAt)
                    : now;
            return new CachedState(
                    "degraded",
                    "partial_feed_error",
                    prev.hasCompleteBaseline(),
                    effectiveLastSyncedAt,
                    nextPollAt,
                    runsSyncedAt,
                    commitsSyncedAt,
                    nextFailures,
                    runs.etag(),
                    runs.lastModified(),
                    nextRuns,
                    commits.etag(),
                    commits.lastModified(),
                    nextCommits
            );
        }

        if (prev.lastSyncedAt() != null) {
            return new CachedState(
                    "stale",
                    failureReason,
                    prev.hasCompleteBaseline(),
                    prev.lastSyncedAt(),
                    nextPollAt,
                    prev.runsSyncedAt(),
                    prev.commitsSyncedAt(),
                    nextFailures,
                    prev.runsEtag(),
                    prev.runsLastModified(),
                    prev.workflowRuns(),
                    prev.commitsEtag(),
                    prev.commitsLastModified(),
                    prev.recentCommits()
            );
        }

        return new CachedState(
                "unavailable",
                failureReason,
                false,
                null,
                nextPollAt,
                prev.runsSyncedAt(),
                prev.commitsSyncedAt(),
                nextFailures,
                prev.runsEtag(),
                prev.runsLastModified(),
                List.of(),
                prev.commitsEtag(),
                prev.commitsLastModified(),
                List.of()
        );
    }

    private FeedFetchResult<List<StatusSnapshotDto.GitHubWorkflowRunDto>> fetchRunsFeed(
            URI uri,
            String etag,
            String lastModified,
            List<StatusSnapshotDto.GitHubWorkflowRunDto> cachedRuns,
            String token,
            int maxItems
    ) {
        return executeConditionalGet(uri, etag, lastModified, cachedRuns, token, body -> parseWorkflowRuns(body, maxItems));
    }

    private FeedFetchResult<List<StatusSnapshotDto.GitHubCommitDto>> fetchCommitsFeed(
            URI uri,
            String etag,
            String lastModified,
            List<StatusSnapshotDto.GitHubCommitDto> cachedCommits,
            String token,
            int maxItems
    ) {
        return executeConditionalGet(uri, etag, lastModified, cachedCommits, token, body -> parseCommits(body, maxItems));
    }

    private <T> FeedFetchResult<List<T>> executeConditionalGet(
            URI uri,
            String etag,
            String lastModified,
            List<T> cachedItems,
            String token,
            BodyParser<List<T>> parser
    ) {
        try {
            RestClient.RequestHeadersSpec<?> request = restClient.get()
                    .uri(uri)
                    .header(HttpHeaders.ACCEPT, "application/vnd.github+json")
                    .header("X-GitHub-Api-Version", "2022-11-28")
                    .header(HttpHeaders.USER_AGENT, "PortfolioStatusPoller/1.0");

            if (token != null) {
                request = request.header(HttpHeaders.AUTHORIZATION, "Bearer " + token);
            }
            if (etag != null && !etag.isBlank()) {
                request = request.header(HttpHeaders.IF_NONE_MATCH, etag);
            }
            if (lastModified != null && !lastModified.isBlank()) {
                request = request.header(HttpHeaders.IF_MODIFIED_SINCE, lastModified);
            }

            return request.exchange((req, res) -> {
                HttpStatusCode status = res.getStatusCode();
                HttpHeaders headers = res.getHeaders();
                String nextEtag = sanitizeHeaderValue(headers.getFirst(HttpHeaders.ETAG));
                String nextLastMod = sanitizeHeaderValue(headers.getFirst(HttpHeaders.LAST_MODIFIED));

                if (status.value() == 304) {
                    return FeedFetchResult.ok(
                            cachedItems != null ? cachedItems : List.of(),
                            nextEtag != null ? nextEtag : etag,
                            nextLastMod != null ? nextLastMod : lastModified
                    );
                }

                if (status.is2xxSuccessful()) {
                    try {
                        String body = readBoundedBody(res.getBody());
                        if (body == null) {
                            log.warn("GitHub poll response exceeded {} byte limit", MAX_RESPONSE_BYTES);
                            return FeedFetchResult.error(cachedItems, etag, lastModified, false, null);
                        }
                        List<T> parsed = parser.parse(body);
                        return FeedFetchResult.ok(
                                parsed,
                                nextEtag != null ? nextEtag : etag,
                                nextLastMod != null ? nextLastMod : lastModified
                        );
                    } catch (IOException parseOrReadEx) {
                        log.warn("GitHub upstream payload could not be parsed cleanly");
                        return FeedFetchResult.error(cachedItems, etag, lastModified, false, null);
                    }
                }

                boolean rateLimited = status.value() == 429
                        || (status.value() == 403 && "0".equals(sanitizeHeaderValue(headers.getFirst("X-RateLimit-Remaining"))));
                Long retryAfterSeconds = parseRetryWaitSeconds(headers, clock.instant());
                log.warn("GitHub upstream returned HTTP {} (rateLimited={})", status.value(), rateLimited);
                return FeedFetchResult.error(cachedItems, etag, lastModified, rateLimited, retryAfterSeconds);
            });
        } catch (RestClientException ex) {
            log.warn("GitHub upstream request failed without exposing payload details");
            return FeedFetchResult.error(cachedItems, etag, lastModified, false, null);
        }
    }

    private List<StatusSnapshotDto.GitHubWorkflowRunDto> parseWorkflowRuns(String body, int maxItems) throws IOException {
        JsonNode root = objectMapper.readTree(body);
        JsonNode runsNode = root != null ? root.get("workflow_runs") : null;
        if (runsNode == null || !runsNode.isArray()) {
            return List.of();
        }
        List<StatusSnapshotDto.GitHubWorkflowRunDto> runs = new ArrayList<>();
        for (JsonNode node : runsNode) {
            if (runs.size() >= maxItems) {
                break;
            }
            long id = node.path("id").asLong(0L);
            String name = sanitizeSingleLine(node.path("name").asText("Workflow"), 80);
            String branch = sanitizeSingleLine(node.path("head_branch").asText("unknown"), 64);
            String event = sanitizeSingleLine(node.path("event").asText("unknown"), 40);
            String status = sanitizeSingleLine(node.path("status").asText("unknown"), 32).toLowerCase(Locale.ROOT);
            String conclusionRaw = node.hasNonNull("conclusion") ? node.path("conclusion").asText() : null;
            String conclusion = conclusionRaw != null ? sanitizeSingleLine(conclusionRaw, 32).toLowerCase(Locale.ROOT) : null;
            String shortSha = sanitizeShortSha(node.path("head_sha").asText(""));
            Instant updatedAt = parseInstantOrNull(node.path("updated_at").asText(null));
            String htmlUrl = sanitizeGitHubUrl(node.path("html_url").asText(null));

            runs.add(new StatusSnapshotDto.GitHubWorkflowRunDto(
                    id,
                    name.isEmpty() ? "Workflow" : name,
                    branch.isEmpty() ? "unknown" : branch,
                    event.isEmpty() ? "unknown" : event,
                    status.isEmpty() ? "unknown" : status,
                    conclusion,
                    shortSha,
                    updatedAt,
                    htmlUrl
            ));
        }
        return List.copyOf(runs);
    }

    private List<StatusSnapshotDto.GitHubCommitDto> parseCommits(String body, int maxItems) throws IOException {
        JsonNode root = objectMapper.readTree(body);
        if (root == null || !root.isArray()) {
            return List.of();
        }
        List<StatusSnapshotDto.GitHubCommitDto> commits = new ArrayList<>();
        for (JsonNode node : root) {
            if (commits.size() >= maxItems) {
                break;
            }
            String shortSha = sanitizeShortSha(node.path("sha").asText(""));
            JsonNode commitObj = node.path("commit");
            String firstLine = extractHeadline(commitObj.path("message").asText("Commit recorded"));
            Instant committedAt = parseInstantOrNull(commitObj.path("author").path("date").asText(null));
            if (committedAt == null) {
                committedAt = parseInstantOrNull(commitObj.path("committer").path("date").asText(null));
            }
            String htmlUrl = sanitizeGitHubUrl(node.path("html_url").asText(null));

            commits.add(new StatusSnapshotDto.GitHubCommitDto(
                    shortSha,
                    firstLine.isEmpty() ? "Commit recorded" : firstLine,
                    committedAt,
                    htmlUrl
            ));
        }
        return List.copyOf(commits);
    }

    private URI buildEndpointUri(String owner, String repo, int maxItems, String... extraSegments) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromUri(validatedApiBaseUri)
                .pathSegment("repos", owner, repo)
                .pathSegment(extraSegments)
                .queryParam("per_page", maxItems);
        return builder.build(true).toUri();
    }

    private static URI parseSafeBaseUri(String rawUrl) {
        if (rawUrl == null || rawUrl.isBlank()) {
            return null;
        }
        try {
            URI uri = URI.create(rawUrl.trim());
            String scheme = uri.getScheme() != null ? uri.getScheme().toLowerCase(Locale.ROOT) : "";
            String host = uri.getHost() != null ? uri.getHost().toLowerCase(Locale.ROOT) : "";
            boolean isHttps = "https".equals(scheme);
            boolean isLocalHttp = "http".equals(scheme) && ("localhost".equals(host) || "127.0.0.1".equals(host));
            if ((!isHttps && !isLocalHttp) || host.isBlank() || uri.getUserInfo() != null) {
                return null;
            }
            return uri;
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }

    private static String readBoundedBody(InputStream stream) throws IOException {
        if (stream == null) {
            return "";
        }
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        byte[] chunk = new byte[4096];
        int total = 0;
        int read;
        while ((read = stream.read(chunk)) != -1) {
            total += read;
            if (total > MAX_RESPONSE_BYTES) {
                return null;
            }
            buffer.write(chunk, 0, read);
        }
        return buffer.toString(StandardCharsets.UTF_8);
    }

    private static Long parseRetryWaitSeconds(HttpHeaders headers, Instant now) {
        String retryAfter = sanitizeHeaderValue(headers.getFirst(HttpHeaders.RETRY_AFTER));
        if (retryAfter != null) {
            try {
                long seconds = Long.parseLong(retryAfter);
                if (seconds > 0L) {
                    return seconds;
                }
            } catch (NumberFormatException ignored) {
                try {
                    Instant retryInstant = ZonedDateTime.parse(retryAfter, DateTimeFormatter.RFC_1123_DATE_TIME).toInstant();
                    long diff = Duration.between(now, retryInstant).getSeconds();
                    if (diff > 0L) {
                        return diff;
                    }
                } catch (DateTimeParseException ignoredDate) {
                    // fall through to rate-limit reset check
                }
            }
        }

        String resetHeader = sanitizeHeaderValue(headers.getFirst("X-RateLimit-Reset"));
        String remaining = sanitizeHeaderValue(headers.getFirst("X-RateLimit-Remaining"));
        if (resetHeader != null && "0".equals(remaining)) {
            try {
                Instant resetAt = Instant.ofEpochSecond(Long.parseLong(resetHeader));
                long diff = Duration.between(now, resetAt).getSeconds();
                if (diff > 0L) {
                    return diff;
                }
            } catch (NumberFormatException ignored) {
                return null;
            }
        }
        return null;
    }

    private static long computeExponentialBackoff(int consecutiveFailures, int initialSeconds, int maxSeconds) {
        int shift = Math.min(Math.max(0, consecutiveFailures - 1), 10);
        long candidate = (long) initialSeconds << shift;
        return Math.min(maxSeconds, Math.max(initialSeconds, candidate));
    }

    private static Instant minNonNull(Instant a, Instant b) {
        if (a == null) return b;
        if (b == null) return a;
        return a.isBefore(b) ? a : b;
    }

    private static String extractHeadline(String message) {
        if (message == null || message.isBlank()) {
            return "";
        }
        String normalized = message.replace("\r\n", "\n").replace('\r', '\n');
        int newline = normalized.indexOf('\n');
        String firstLine = newline >= 0 ? normalized.substring(0, newline) : normalized;
        return sanitizeSingleLine(firstLine, 120);
    }

    private static String sanitizeSingleLine(String raw, int maxLen) {
        if (raw == null || raw.isBlank()) {
            return "";
        }
        StringBuilder cleaned = new StringBuilder(Math.min(raw.length(), maxLen));
        boolean prevSpace = false;
        for (int i = 0; i < raw.length(); i++) {
            char c = raw.charAt(i);
            boolean isControl = c < 0x20 || (c >= 0x7F && c <= 0x9F);
            char nextChar = (isControl || Character.isWhitespace(c)) ? ' ' : c;
            if (nextChar == ' ') {
                if (cleaned.isEmpty() || prevSpace) {
                    continue;
                }
                prevSpace = true;
            } else {
                prevSpace = false;
            }
            if (cleaned.length() >= maxLen) {
                break;
            }
            cleaned.append(nextChar);
        }
        return cleaned.toString().trim();
    }

    private static String sanitizeHeaderValue(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        String cleaned = sanitizeSingleLine(value, 160);
        return cleaned.isEmpty() ? null : cleaned;
    }

    private static String sanitizeShortSha(String rawSha) {
        if (rawSha == null) {
            return "unknown";
        }
        String trimmed = rawSha.trim();
        if (!HEX_SHA_PATTERN.matcher(trimmed).matches()) {
            return "unknown";
        }
        return trimmed.substring(0, 7).toLowerCase(Locale.ROOT);
    }

    private static String sanitizeGitHubUrl(String rawUrl) {
        if (rawUrl == null || rawUrl.isBlank()) {
            return null;
        }
        String trimmed = rawUrl.trim();
        if (!GITHUB_HTML_URL_PATTERN.matcher(trimmed).matches()) {
            return null;
        }
        return trimmed;
    }

    private static Instant parseInstantOrNull(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return Instant.parse(value.trim());
        } catch (DateTimeParseException ex) {
            return null;
        }
    }

    @FunctionalInterface
    private interface BodyParser<T> {
        T parse(String body) throws IOException;
    }

    private record FeedFetchResult<T>(
            boolean success,
            T data,
            String etag,
            String lastModified,
            boolean rateLimited,
            Long retryAfterSeconds
    ) {
        static <T> FeedFetchResult<T> ok(T data, String etag, String lastModified) {
            return new FeedFetchResult<>(true, data, etag, lastModified, false, null);
        }

        static <T> FeedFetchResult<T> error(T fallbackData, String etag, String lastModified, boolean rateLimited, Long retryAfterSeconds) {
            return new FeedFetchResult<>(false, fallbackData, etag, lastModified, rateLimited, retryAfterSeconds);
        }
    }

    private record CachedState(
            String status,
            String stateReason,
            boolean hasCompleteBaseline,
            Instant lastSyncedAt,
            Instant nextPollAt,
            Instant runsSyncedAt,
            Instant commitsSyncedAt,
            int consecutiveFailures,
            String runsEtag,
            String runsLastModified,
            List<StatusSnapshotDto.GitHubWorkflowRunDto> workflowRuns,
            String commitsEtag,
            String commitsLastModified,
            List<StatusSnapshotDto.GitHubCommitDto> recentCommits
    ) {
        static CachedState initial(String initialReason) {
            return new CachedState(
                    "unavailable",
                    initialReason,
                    false,
                    null,
                    null,
                    null,
                    null,
                    0,
                    null,
                    null,
                    List.of(),
                    null,
                    null,
                    List.of()
            );
        }
    }
}
