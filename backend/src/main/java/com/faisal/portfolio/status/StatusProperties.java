package com.faisal.portfolio.status;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "portfolio.status")
public class StatusProperties {

    private long dbTimeoutMs = 1000L;
    private int windowSeconds = 300;
    private int minSamples = 20;
    private String revision = "";
    private final GitHubProperties github = new GitHubProperties();

    public long getDbTimeoutMs() {
        return Math.clamp(dbTimeoutMs, 50L, 5000L);
    }

    public void setDbTimeoutMs(long dbTimeoutMs) {
        this.dbTimeoutMs = dbTimeoutMs;
    }

    public int getWindowSeconds() {
        return Math.clamp(windowSeconds, 30, 3600);
    }

    public void setWindowSeconds(int windowSeconds) {
        this.windowSeconds = windowSeconds;
    }

    public int getMinSamples() {
        return Math.max(1, minSamples);
    }

    public void setMinSamples(int minSamples) {
        this.minSamples = minSamples;
    }

    public String getRevision() {
        return revision;
    }

    public void setRevision(String revision) {
        this.revision = revision;
    }

    public GitHubProperties getGithub() {
        return github;
    }

    public static class GitHubProperties {

        private boolean enabled = true;
        private boolean schedulingEnabled = true;
        private String owner = "FaisalHanafi98";
        private String repo = "Portfolio";
        private String apiBaseUrl = "https://api.github.com";
        private String token = "";
        private int cacheTtlSeconds = 900;
        private int initialBackoffSeconds = 60;
        private int maxBackoffSeconds = 3600;
        private long requestTimeoutMs = 4000L;
        private int maxItems = 5;
        private long pollTickMs = 30_000L;
        private long pollInitialDelayMs = 10_000L;

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public boolean isSchedulingEnabled() {
            return schedulingEnabled;
        }

        public void setSchedulingEnabled(boolean schedulingEnabled) {
            this.schedulingEnabled = schedulingEnabled;
        }

        public String getOwner() {
            return owner;
        }

        public void setOwner(String owner) {
            this.owner = owner;
        }

        public String getRepo() {
            return repo;
        }

        public void setRepo(String repo) {
            this.repo = repo;
        }

        public String getApiBaseUrl() {
            return apiBaseUrl;
        }

        public void setApiBaseUrl(String apiBaseUrl) {
            this.apiBaseUrl = apiBaseUrl;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public int getCacheTtlSeconds() {
            return Math.clamp(cacheTtlSeconds, 60, 3600);
        }

        public void setCacheTtlSeconds(int cacheTtlSeconds) {
            this.cacheTtlSeconds = cacheTtlSeconds;
        }

        public int getInitialBackoffSeconds() {
            return Math.clamp(initialBackoffSeconds, 10, 3600);
        }

        public void setInitialBackoffSeconds(int initialBackoffSeconds) {
            this.initialBackoffSeconds = initialBackoffSeconds;
        }

        public int getMaxBackoffSeconds() {
            int minBackoff = getInitialBackoffSeconds();
            return Math.clamp(maxBackoffSeconds, minBackoff, 86_400);
        }

        public void setMaxBackoffSeconds(int maxBackoffSeconds) {
            this.maxBackoffSeconds = maxBackoffSeconds;
        }

        public long getRequestTimeoutMs() {
            return Math.clamp(requestTimeoutMs, 500L, 10_000L);
        }

        public void setRequestTimeoutMs(long requestTimeoutMs) {
            this.requestTimeoutMs = requestTimeoutMs;
        }

        public int getMaxItems() {
            return Math.clamp(maxItems, 1, 10);
        }

        public void setMaxItems(int maxItems) {
            this.maxItems = maxItems;
        }

        public long getPollTickMs() {
            return Math.clamp(pollTickMs, 1000L, 900_000L);
        }

        public void setPollTickMs(long pollTickMs) {
            this.pollTickMs = pollTickMs;
        }

        public long getPollInitialDelayMs() {
            return Math.max(0L, pollInitialDelayMs);
        }

        public void setPollInitialDelayMs(long pollInitialDelayMs) {
            this.pollInitialDelayMs = pollInitialDelayMs;
        }
    }
}
