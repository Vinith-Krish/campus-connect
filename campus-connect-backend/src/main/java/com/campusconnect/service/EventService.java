package com.campusconnect.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.campusconnect.dto.EventActionResponse;
import com.campusconnect.dto.EventRequest;
import com.campusconnect.dto.EventResponse;
import com.campusconnect.exception.BadRequestException;
import com.campusconnect.exception.ResourceNotFoundException;
import com.campusconnect.exception.UnauthorizedException;
import com.campusconnect.model.Category;
import com.campusconnect.model.Event;
import com.campusconnect.model.EventInterest;
import com.campusconnect.model.EventRegistration;
import com.campusconnect.model.Role;
import com.campusconnect.model.User;
import com.campusconnect.repository.EventInterestRepository;
import com.campusconnect.repository.EventRegistrationRepository;
import com.campusconnect.repository.EventRepository;
import com.campusconnect.repository.UserRepository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
@Service
@Slf4j
@Transactional
public class EventService {
	@Autowired
	private EventRepository eventRepository;
	@Autowired
	private EventRegistrationRepository registrationRepository;
	@Autowired
	private EventInterestRepository interestRepository;
	@Autowired
	private UserRepository userRepository;
	// paginated version to match controller
	public Page<EventResponse> getAllEvents(String search, Category category, int page, int size, String sortBy, String direction) {
	    try {
	        // Normalize search parameter
	        String normalizedSearch = (search == null || search.isBlank()) ? null : search.trim();
	        
	        // Validate sort field - only allow specific safe fields
	        String validSortBy = validateAndGetSortField(sortBy);
	        
	        // Create sort object
	        Sort sort = "desc".equalsIgnoreCase(direction)
	                ? Sort.by(validSortBy).descending()
	                : Sort.by(validSortBy).ascending();

	        Pageable pageable = PageRequest.of(page, size, sort);

	        Page<Event> events = eventRepository.searchEvents(normalizedSearch, category, pageable);

	        return events.map(event -> {
	            Long regCount = registrationRepository.countByEventId(event.getId());
	            Long intCount = interestRepository.countByEventId(event.getId());
	            return EventResponse.fromEntity(event, regCount, intCount);
	        });
	    } catch (Exception e) {
	        log.error("Error fetching all events", e);
	        throw e;
	    }
	}
	
	/**
	 * Validates sort field to prevent injection attacks and invalid column references
	 * @param sortBy the sort field name
	 * @return validated sort field or default "date"
	 */
	private String validateAndGetSortField(String sortBy) {
	    if (sortBy == null || sortBy.isBlank()) {
	        return "date";
	    }
	    
	    String field = sortBy.trim().toLowerCase();
	    // Whitelist of allowed sort fields
	    switch (field) {
	        case "id":
	        case "title":
	        case "date":
	        case "createdat":
	        case "createdAt":
	        case "category":
	            return field.equals("createdat") ? "createdAt" : field;
	        default:
	            log.warn("Invalid sort field requested: {}, defaulting to 'date'", sortBy);
	            return "date";
	    }
	}

	public EventActionResponse registerForEvent(Long eventId, String userEmail) {
	    Event event = eventRepository.findById(eventId)
	            .orElseThrow(() -> new ResourceNotFoundException("Event not found"));

	    User user = userRepository.findByEmail(userEmail)
	            .orElseThrow(() -> new ResourceNotFoundException("User not found"));

	    if (registrationRepository.existsByEventIdAndUserId(eventId, user.getId())) {
	        throw new BadRequestException("Already registered for this event");
	    }

	    EventRegistration registration = new EventRegistration();
	    registration.setEvent(event);
	    registration.setUser(user);
	    registration.setRegisteredAt(LocalDateTime.now());
	    registrationRepository.save(registration);

	    return new EventActionResponse("Successfully registered for event", eventId, user.getId());
	}

	public EventActionResponse markInterested(Long eventId, String userEmail) {
	    Event event = eventRepository.findById(eventId)
	            .orElseThrow(() -> new ResourceNotFoundException("Event not found"));

	    User user = userRepository.findByEmail(userEmail)
	            .orElseThrow(() -> new ResourceNotFoundException("User not found"));

	    if (interestRepository.existsByEventIdAndUserId(eventId, user.getId())) {
	        throw new BadRequestException("Already marked interested in this event");
	    }

	    EventInterest interest = new EventInterest();
	    interest.setEvent(event);
	    interest.setUser(user);
	    interest.setInterestedAt(LocalDateTime.now());
	    interestRepository.save(interest);

	    return new EventActionResponse("Successfully marked interest", eventId, user.getId());
	}

	public void deleteEvent(Long eventId, String userEmail) {
	    Event event = eventRepository.findById(eventId)
	            .orElseThrow(() -> new ResourceNotFoundException("Event not found"));

	    User user = userRepository.findByEmail(userEmail)
	            .orElseThrow(() -> new ResourceNotFoundException("User not found"));

	    if (!event.getCreatedBy().equals(user.getId())) {
	        throw new UnauthorizedException("Only the event creator can delete this event");
	    }

	    eventRepository.delete(event);
	}

	public List<EventResponse> getMyCreatedEvents(String userEmail) {
	    User user = userRepository.findByEmail(userEmail)
	            .orElseThrow(() -> new ResourceNotFoundException("User not found"));

	    if (user.getRole() != Role.CLUB_ADMIN) {
	        throw new UnauthorizedException("Only club admins can access created events");
	    }

	    List<Event> events = eventRepository.findByCreatedByOrderByDateDesc(user.getId());

	    return events.stream()
	            .map(event -> {
	                Long regCount = registrationRepository.countByEventId(event.getId());
	                Long intCount = interestRepository.countByEventId(event.getId());
	                return EventResponse.fromEntity(event, regCount, intCount);
	            })
	            .collect(Collectors.toList());
	}

	public EventResponse updateEvent(Long eventId, EventRequest request, String userEmail) {
	    Event event = eventRepository.findById(eventId)
	            .orElseThrow(() -> new ResourceNotFoundException("Event not found"));

	    User user = userRepository.findByEmail(userEmail)
	            .orElseThrow(() -> new ResourceNotFoundException("User not found"));

	    if (!event.getCreatedBy().equals(user.getId())) {
	        throw new UnauthorizedException("You can only edit your own events");
	    }

	    event.setTitle(request.getTitle());
	    event.setDescription(request.getDescription());
	    event.setDate(request.getDate());
	    event.setTime(request.getTime());
	    event.setVenue(request.getVenue());
	    event.setCategory(request.getCategory());
	    event.setImageUrl(request.getImageUrl());

	    Event updated = eventRepository.save(event);

	    Long regCount = registrationRepository.countByEventId(eventId);
	    Long intCount = interestRepository.countByEventId(eventId);

	    return EventResponse.fromEntity(updated, regCount, intCount);
	}
	public EventResponse getEventById(Long id) {
	    Event event = eventRepository.findById(id)
	            .orElseThrow(() -> new ResourceNotFoundException("Event not found with id: " + id));

	    Long regCount = registrationRepository.countByEventId(id);
	    Long intCount = interestRepository.countByEventId(id);

	    return EventResponse.fromEntity(event, regCount, intCount);
	}

	public EventResponse createEvent(EventRequest request, String userEmail) {
	    User user = userRepository.findByEmail(userEmail)
	            .orElseThrow(() -> new ResourceNotFoundException("User not found"));

	    if (user.getRole() != Role.CLUB_ADMIN) {
	        throw new UnauthorizedException("Only club admins can create events");
	    }

	    Event event = new Event();
	    event.setTitle(request.getTitle());
	    event.setDescription(request.getDescription());
	    event.setDate(request.getDate());
	    event.setTime(request.getTime());
	    event.setVenue(request.getVenue());
	    event.setCategory(request.getCategory());
	    event.setImageUrl(request.getImageUrl());
	    event.setCollegename(user.getCollegename());
	    event.setOrganizerName(user.getName());
	    event.setOrganizerEmail(user.getEmail());
	    event.setCreatedBy(user.getId());

	    Event savedEvent = eventRepository.save(event);
	    return EventResponse.fromEntity(savedEvent, 0L, 0L);
	}

	public void unregisterUserFromEvent(Long eventId, String userEmail) {
	    Event event = eventRepository.findById(eventId)
	            .orElseThrow(() -> new ResourceNotFoundException("Event not found"));

	    User user = userRepository.findByEmail(userEmail)
	            .orElseThrow(() -> new ResourceNotFoundException("User not found"));

	    EventRegistration registration = registrationRepository.findByEventAndUser(event, user)
	            .orElseThrow(() -> new BadRequestException("You are not registered for this event"));

	    registrationRepository.delete(registration);
	}

}
