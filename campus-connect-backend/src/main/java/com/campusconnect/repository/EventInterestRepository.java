package com.campusconnect.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.campusconnect.model.EventInterest;

public interface EventInterestRepository extends JpaRepository<EventInterest, Long> {
    boolean existsByEventIdAndUserId(Long eventId, Long userId);
    List<EventInterest> findByUserId(Long userId);
    Long countByEventId(Long eventId);
}
