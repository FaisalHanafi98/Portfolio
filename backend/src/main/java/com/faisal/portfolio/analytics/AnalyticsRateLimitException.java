package com.faisal.portfolio.analytics;

public class AnalyticsRateLimitException extends RuntimeException {

    private final long retryAfterSeconds;

    public AnalyticsRateLimitException(long retryAfterSeconds) {
        super("Rate limit exceeded for analytics ingestion");
        this.retryAfterSeconds = Math.max(1L, retryAfterSeconds);
    }

    public long getRetryAfterSeconds() {
        return retryAfterSeconds;
    }
}
