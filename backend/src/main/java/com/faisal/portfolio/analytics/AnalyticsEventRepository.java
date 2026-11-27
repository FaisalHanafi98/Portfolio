package com.faisal.portfolio.analytics;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;

public interface AnalyticsEventRepository extends JpaRepository<AnalyticsEvent, Long> {

    boolean existsByEventId(String eventId);

    long deleteByOccurredAtBefore(Instant cutoff);
}
