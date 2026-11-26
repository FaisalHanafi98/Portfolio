package com.faisal.portfolio.status;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "portfolio.status")
public class StatusProperties {

    private long dbTimeoutMs = 1000L;
    private int windowSeconds = 300;
    private int minSamples = 20;
    private String revision = "";

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
}
