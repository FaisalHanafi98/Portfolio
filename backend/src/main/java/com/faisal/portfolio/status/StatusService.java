package com.faisal.portfolio.status;

import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.info.BuildProperties;
import org.springframework.boot.info.GitProperties;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.lang.management.ManagementFactory;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.Instant;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Service
public class StatusService {

    private static final Logger log = LoggerFactory.getLogger(StatusService.class);
    private static final int MAX_CONCURRENT_DB_PROBES = 2;

    private final DataSource dataSource;
    private final ApiLatencyTracker latencyTracker;
    private final StatusProperties properties;
    private final ObjectProvider<BuildProperties> buildProperties;
    private final ObjectProvider<GitProperties> gitProperties;
    private final ExecutorService probeExecutor;
    private final Semaphore probePermits = new Semaphore(MAX_CONCURRENT_DB_PROBES);

    public StatusService(
            DataSource dataSource,
            ApiLatencyTracker latencyTracker,
            StatusProperties properties,
            ObjectProvider<BuildProperties> buildProperties,
            ObjectProvider<GitProperties> gitProperties
    ) {
        this.dataSource = dataSource;
        this.latencyTracker = latencyTracker;
        this.properties = properties;
        this.buildProperties = buildProperties;
        this.gitProperties = gitProperties;
        this.probeExecutor = Executors.newFixedThreadPool(MAX_CONCURRENT_DB_PROBES, runnable -> {
            Thread thread = new Thread(runnable, "status-db-probe");
            thread.setDaemon(true);
            return thread;
        });
    }

    public StatusSnapshotDto getSnapshot() {
        Instant now = Instant.now();
        var runtime = ManagementFactory.getRuntimeMXBean();
        long uptimeSeconds = Math.max(0L, runtime.getUptime() / 1000L);
        Instant startedAt = Instant.ofEpochMilli(runtime.getStartTime());

        StatusSnapshotDto.DatabaseStatusDto database = probeDatabase();
        String overallStatus = "up".equals(database.status()) ? "healthy" : "degraded";
        StatusSnapshotDto.TrafficMetricsDto traffic = latencyTracker.snapshot();

        return new StatusSnapshotDto(
                overallStatus,
                now,
                uptimeSeconds,
                startedAt,
                resolveRevision(),
                database,
                traffic
        );
    }

    private StatusSnapshotDto.DatabaseStatusDto probeDatabase() {
        if (!probePermits.tryAcquire()) {
            return new StatusSnapshotDto.DatabaseStatusDto("degraded", null);
        }

        long timeoutMs = properties.getDbTimeoutMs();
        CompletableFuture<StatusSnapshotDto.DatabaseStatusDto> future = CompletableFuture.supplyAsync(() -> {
            long startNanos = System.nanoTime();
            try (Connection connection = dataSource.getConnection()) {
                int timeoutSeconds = Math.max(1, (int) Math.ceil(timeoutMs / 1000.0));
                if (!connection.isValid(timeoutSeconds)) {
                    return new StatusSnapshotDto.DatabaseStatusDto("degraded", null);
                }
                long latencyMs = Math.max(0L, TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNanos));
                return new StatusSnapshotDto.DatabaseStatusDto("up", latencyMs);
            } catch (SQLException | RuntimeException ex) {
                log.warn("Status database probe reported degraded connectivity");
                return new StatusSnapshotDto.DatabaseStatusDto("degraded", null);
            }
        }, probeExecutor);

        try {
            return future.get(timeoutMs, TimeUnit.MILLISECONDS);
        } catch (TimeoutException | ExecutionException ex) {
            future.cancel(true);
            log.warn("Status database probe exceeded {} ms bound", timeoutMs);
            return new StatusSnapshotDto.DatabaseStatusDto("degraded", null);
        } catch (InterruptedException ex) {
            future.cancel(true);
            Thread.currentThread().interrupt();
            return new StatusSnapshotDto.DatabaseStatusDto("degraded", null);
        } finally {
            probePermits.release();
        }
    }

    private String resolveRevision() {
        String configured = properties.getRevision();
        if (configured != null && !configured.isBlank()) {
            return configured.trim();
        }
        GitProperties git = gitProperties.getIfAvailable();
        if (git != null && git.getShortCommitId() != null && !git.getShortCommitId().isBlank()) {
            return git.getShortCommitId();
        }
        BuildProperties build = buildProperties.getIfAvailable();
        if (build != null && build.getVersion() != null && !build.getVersion().isBlank()) {
            return build.getVersion();
        }
        return "unconfigured";
    }

    @PreDestroy
    void shutdown() {
        probeExecutor.shutdownNow();
    }
}
