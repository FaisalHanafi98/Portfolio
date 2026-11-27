package com.faisal.portfolio.analytics;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "portfolio.analytics")
public class AnalyticsProperties {

    private boolean enabled = true;
    private int maxBodyBytes = 2048;
    private int rateLimitPerMinute = 60;
    private int rawRetentionDays = 30;
    private int aggregateRetentionMonths = 12;
    private int minTopicCellCount = 5;
    private boolean trustForwardedFor = false;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public int getMaxBodyBytes() {
        return Math.clamp(maxBodyBytes, 256, 8192);
    }

    public void setMaxBodyBytes(int maxBodyBytes) {
        this.maxBodyBytes = maxBodyBytes;
    }

    public int getRateLimitPerMinute() {
        return Math.clamp(rateLimitPerMinute, 1, 600);
    }

    public void setRateLimitPerMinute(int rateLimitPerMinute) {
        this.rateLimitPerMinute = rateLimitPerMinute;
    }

    public int getRawRetentionDays() {
        return Math.clamp(rawRetentionDays, 1, 365);
    }

    public void setRawRetentionDays(int rawRetentionDays) {
        this.rawRetentionDays = rawRetentionDays;
    }

    public int getAggregateRetentionMonths() {
        return Math.clamp(aggregateRetentionMonths, 1, 60);
    }

    public void setAggregateRetentionMonths(int aggregateRetentionMonths) {
        this.aggregateRetentionMonths = aggregateRetentionMonths;
    }

    public int getMinTopicCellCount() {
        return Math.clamp(minTopicCellCount, 1, 100);
    }

    public void setMinTopicCellCount(int minTopicCellCount) {
        this.minTopicCellCount = minTopicCellCount;
    }

    public boolean isTrustForwardedFor() {
        return trustForwardedFor;
    }

    public void setTrustForwardedFor(boolean trustForwardedFor) {
        this.trustForwardedFor = trustForwardedFor;
    }
}
