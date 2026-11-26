package com.faisal.portfolio.status;

import java.time.Instant;
import java.util.List;

public record StatusSnapshotDto(
        String status,
        Instant checkedAt,
        long uptimeSeconds,
        Instant startedAt,
        String revision,
        DatabaseStatusDto database,
        TrafficMetricsDto traffic,
        GitHubStatusDto github
) {
    public record DatabaseStatusDto(String status, Long latencyMs) {}

    public record TrafficMetricsDto(
            int windowSeconds,
            long requestCount,
            int minSamples,
            String p95Status,
            Double p95Ms
    ) {}

    public record GitHubStatusDto(
            String status,
            String repository,
            Instant lastSyncedAt,
            Long snapshotAgeSeconds,
            int cacheTtlSeconds,
            Instant nextPollAt,
            String stateReason,
            List<GitHubWorkflowRunDto> workflowRuns,
            List<GitHubCommitDto> recentCommits
    ) {}

    public record GitHubWorkflowRunDto(
            long id,
            String name,
            String branch,
            String event,
            String status,
            String conclusion,
            String shortSha,
            Instant updatedAt,
            String htmlUrl
    ) {}

    public record GitHubCommitDto(
            String shortSha,
            String message,
            Instant committedAt,
            String htmlUrl
    ) {}
}
