package com.faisal.portfolio.analytics;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Base64;
import java.util.HexFormat;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class AnalyticsIngestionRateLimiter {

    private static final int MAX_TRACKED_KEYS = 4096;

    private final AnalyticsProperties properties;
    private final Clock clock;
    private final String ephemeralSalt;
    private final Map<String, WindowCounter> counters = new ConcurrentHashMap<>();

    public AnalyticsIngestionRateLimiter(AnalyticsProperties properties, Clock clock) {
        this.properties = properties;
        this.clock = clock;
        byte[] randomBytes = new byte[24];
        new SecureRandom().nextBytes(randomBytes);
        this.ephemeralSalt = Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
    }

    public void checkAndConsume(HttpServletRequest request) {
        long epochSecond = clock.instant().getEpochSecond();
        long currentMinute = epochSecond / 60L;
        String hashedKey = hashClientKey(extractClientIp(request), currentMinute);

        if (counters.size() >= MAX_TRACKED_KEYS) {
            counters.entrySet().removeIf(entry -> entry.getValue().minuteBucket() < currentMinute);
        }
        if (counters.size() >= MAX_TRACKED_KEYS) {
            long retryAfter = 60L - (epochSecond % 60L);
            throw new AnalyticsRateLimitException(retryAfter);
        }

        WindowCounter counter = counters.compute(hashedKey, (ignored, existing) -> {
            if (existing == null || existing.minuteBucket() != currentMinute) {
                return new WindowCounter(currentMinute, new AtomicInteger(0));
            }
            return existing;
        });

        int count = counter.count().incrementAndGet();
        if (count > properties.getRateLimitPerMinute()) {
            long retryAfter = 60L - (epochSecond % 60L);
            throw new AnalyticsRateLimitException(retryAfter);
        }
    }

    private String extractClientIp(HttpServletRequest request) {
        if (properties.isTrustForwardedFor()) {
            String forwarded = request.getHeader("X-Forwarded-For");
            if (forwarded != null && !forwarded.isBlank()) {
                int comma = forwarded.indexOf(',');
                String first = (comma >= 0 ? forwarded.substring(0, comma) : forwarded).trim();
                if (!first.isEmpty()) {
                    return first;
                }
            }
        }
        String remote = request.getRemoteAddr();
        return remote != null ? remote : "unknown";
    }

    private String hashClientKey(String ip, long minuteBucket) {
        String datePart = LocalDate.ofInstant(clock.instant(), ZoneOffset.UTC).toString();
        String material = ephemeralSalt + ":" + datePart + ":" + minuteBucket + ":" + ip;
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256")
                    .digest(material.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(digest, 0, 16);
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException("SHA-256 unavailable", ex);
        }
    }

    private record WindowCounter(long minuteBucket, AtomicInteger count) {
    }
}
