package com.campusconnect.repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.campusconnect.model.Event;
import com.campusconnect.model.EventRegistration;
import com.campusconnect.model.User;

public interface EventRegistrationRepository extends JpaRepository<EventRegistration, Long> {
    boolean existsByEventIdAndUserId(Long eventId, Long userId);
    List<EventRegistration> findByUserId(Long userId);
    Long countByEventId(Long eventId);
    Optional<EventRegistration> findByEventAndUser(Event event, User user);
    List<EventRegistration> findByEventIdOrderByRegisteredAtAsc(Long eventId);
    
    @Query("SELECT e.id as eventId, COUNT(er.id) as count FROM EventRegistration er " +
           "RIGHT JOIN er.event e ON e.id = er.event.id " +
           "WHERE e.id IN :eventIds GROUP BY e.id")
    List<Object[]> countRegistrationsByEventIds(@Param("eventIds") List<Long> eventIds);
}
