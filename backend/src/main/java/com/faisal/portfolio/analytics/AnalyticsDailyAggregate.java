package com.faisal.portfolio.analytics;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(
        name = "analytics_daily_aggregates",
        uniqueConstraints = @UniqueConstraint(
                name = "uq_analytics_daily_bucket",
                columnNames = {"bucket_date", "project_slug", "event_name", "depth_bucket", "topic_label"}
        )
)
@Getter
@NoArgsConstructor
public class AnalyticsDailyAggregate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "bucket_date", nullable = false)
    private LocalDate bucketDate;

    @Column(name = "project_slug", nullable = false, length = 64)
    private String projectSlug;

    @Column(name = "event_name", nullable = false, length = 40)
    private String eventName;

    @Column(name = "depth_bucket", nullable = false, length = 16)
    private String depthBucket;

    @Column(name = "topic_label", nullable = false, length = 48)
    private String topicLabel;

    @Column(name = "event_count", nullable = false)
    private long eventCount;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    public AnalyticsDailyAggregate(
            LocalDate bucketDate,
            String projectSlug,
            String eventName,
            String depthBucket,
            String topicLabel,
            long eventCount,
            Instant updatedAt
    ) {
        this.bucketDate = bucketDate;
        this.projectSlug = projectSlug;
        this.eventName = eventName;
        this.depthBucket = depthBucket;
        this.topicLabel = topicLabel;
        this.eventCount = eventCount;
        this.updatedAt = updatedAt;
    }
}
