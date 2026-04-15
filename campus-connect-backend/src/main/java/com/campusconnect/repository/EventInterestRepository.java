package com.campusconnect.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.campusconnect.model.EventInterest;

public interface EventInterestRepository extends JpaRepository<EventInterest, Long> {
    boolean existsByEventIdAndUserId(Long eventId, Long userId);
    List<EventInterest> findByUserId(Long userId);
    Long countByEventId(Long eventId);
    
    @Query("SELECT e.id as eventId, COUNT(ei.id) as count FROM EventInterest ei " +
           "RIGHT JOIN ei.event e ON e.id = ei.event.id " +
           "WHERE e.id IN :eventIds GROUP BY e.id")
    List<Object[]> countInterestsByEventIds(@Param("eventIds") List<Long> eventIds);
}
