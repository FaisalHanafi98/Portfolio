package com.faisal.portfolio.analytics;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(name = "analytics_events")
@Getter
@NoArgsConstructor
public class AnalyticsEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "event_id", nullable = false, unique = true, length = 64)
    private String eventId;

    @Column(name = "schema_version", nullable = false)
    private int schemaVersion;

    @Column(name = "event_name", nullable = false, length = 40)
    private String eventName;

    @Column(name = "project_slug", nullable = false, length = 64)
    private String projectSlug;

    @Column(name = "section_key", nullable = false, length = 48)
    private String sectionKey;

    @Column(name = "depth_bucket", nullable = false, length = 16)
    private String depthBucket;

    @Column(name = "topic_label", nullable = false, length = 48)
    private String topicLabel;

    @Column(name = "occurred_at", nullable = false, updatable = false)
    private Instant occurredAt;

    public AnalyticsEvent(
            String eventId,
            int schemaVersion,
            String eventName,
            String projectSlug,
            String sectionKey,
            String depthBucket,
            String topicLabel,
            Instant occurredAt
    ) {
        this.eventId = eventId;
        this.schemaVersion = schemaVersion;
        this.eventName = eventName;
        this.projectSlug = projectSlug;
        this.sectionKey = sectionKey;
        this.depthBucket = depthBucket;
        this.topicLabel = topicLabel;
        this.occurredAt = occurredAt;
    }
}
