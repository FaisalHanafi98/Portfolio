package com.faisal.portfolio.analytics;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.Instant;

@JsonIgnoreProperties(ignoreUnknown = false)
public record AnalyticsEventRequest(
        @NotBlank @Pattern(regexp = "^[A-Za-z0-9_-]{12,64}$") String eventId,
        @NotNull Integer schemaVersion,
        @NotBlank @Size(max = 40) String eventName,
        @Size(max = 64) String projectSlug,
        @Size(max = 48) String sectionKey,
        @Size(max = 16) String depthBucket,
        @Size(max = 48) String topicLabel,
        Instant clientOccurredAt
) {
}
