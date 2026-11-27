package com.faisal.portfolio.analytics;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

public interface AnalyticsDailyAggregateRepository extends JpaRepository<AnalyticsDailyAggregate, Long> {

    List<AnalyticsDailyAggregate> findByBucketDateGreaterThanEqualOrderByBucketDateDesc(LocalDate sinceDate);

    long deleteByBucketDateBefore(LocalDate cutoffDate);

    @Modifying
    @Query("""
            UPDATE AnalyticsDailyAggregate a
            SET a.eventCount = a.eventCount + 1, a.updatedAt = :updatedAt
            WHERE a.bucketDate = :bucketDate
              AND a.projectSlug = :projectSlug
              AND a.eventName = :eventName
              AND a.depthBucket = :depthBucket
              AND a.topicLabel = :topicLabel
            """)
    int incrementExistingBucket(
            @Param("bucketDate") LocalDate bucketDate,
            @Param("projectSlug") String projectSlug,
            @Param("eventName") String eventName,
            @Param("depthBucket") String depthBucket,
            @Param("topicLabel") String topicLabel,
            @Param("updatedAt") Instant updatedAt
    );
}
