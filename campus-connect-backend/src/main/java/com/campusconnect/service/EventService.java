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

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class EventService {
	private final EventRepository eventRepository;
	private final EventRegistrationRepository registrationRepository;
	private final EventInterestRepository interestRepository;
	private final UserRepository userRepository;
	// get all events
	public List<EventResponse> getAllEvents(String search, Category category) {
	    // Description: Get all events with optional search and category filter
	    // Parameters: search (optional), category (optional)
	    // Returns: List of EventResponse
	    
	    List<Event> events;
	    
	    // Apply filters
	    if (search != null && category != null) {
	        events = eventRepository.findByTitleContainingIgnoreCaseAndCategory(search, category);
	    } else if (search != null) {
	        events = eventRepository.findByTitleContainingIgnoreCase(search);
	    } else if (category != null) {
	        events = eventRepository.findByCategory(category);
	    } else {
	        events = eventRepository.findAll();
	    }
	    
	    // Convert to EventResponse with counts
	    return events.stream()
	        .map(event -> {
	            Long regCount = registrationRepository.countByEventId(event.getId());
	            Long intCount = interestRepository.countByEventId(event.getId());
	            return EventResponse.fromEntity(event, regCount, intCount);
	        })
	        .collect(Collectors.toList());
	}
	// get event by id
	public EventResponse getEventById(Long id) {
	    
	    Event event = eventRepository.findById(id)
	        .orElseThrow(() -> new ResourceNotFoundException("Event not found with id: " + id));
	    
	    Long regCount = registrationRepository.countByEventId(id);
	    Long intCount = interestRepository.countByEventId(id);
	    
	    return EventResponse.fromEntity(event, regCount, intCount);
	}
	// create event
	public EventResponse createEvent(EventRequest request, Long userId) {
	    User user = userRepository.findById(userId)
	        .orElseThrow(() -> new ResourceNotFoundException("User not found"));
	    
	    if (user.getRole() != Role.CLUB_ADMIN) {
	        throw new UnauthorizedException("Only club admins can create events");
	    }
	    
	    // Create event entity
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
	    event.setCreatedBy(userId);
	    
	    // Save event
	    Event savedEvent = eventRepository.save(event);
	    // while creating an event, registration and interest counts are zero
	    return EventResponse.fromEntity(savedEvent, 0L, 0L);
	}
	public EventActionResponse registerForEvent(Long eventId, Long userId) {
	    
	    
	    // Verify event exists
	    Event event = eventRepository.findById(eventId)
	        .orElseThrow(() -> new ResourceNotFoundException("Event not found"));
	    
	    // Verify user exists
	    User user = userRepository.findById(userId)
	        .orElseThrow(() -> new ResourceNotFoundException("User not found"));
	    
	    // Check if already registered
	    if (registrationRepository.existsByEventIdAndUserId(eventId, userId)) {
	        throw new BadRequestException("Already registered for this event");
	    }
	    
	    // Create registration
	    EventRegistration registration = new EventRegistration();
	    registration.setEvent(event);
	    registration.setUser(user);
	    registration.setRegisteredAt(LocalDateTime.now());
	    
	    registrationRepository.save(registration);
	    
	    return new EventActionResponse("Successfully registered for event", eventId, userId);
	}
	// mark interest in event
	public EventActionResponse markInterested(Long eventId, Long userId) {
	    
	    // Verify event exists
	    Event event = eventRepository.findById(eventId)
	        .orElseThrow(() -> new ResourceNotFoundException("Event not found"));
	    
	    // Verify user exists
	    User user = userRepository.findById(userId)
	        .orElseThrow(() -> new ResourceNotFoundException("User not found"));
	    
	    // Check if already interested
	    if (interestRepository.existsByEventIdAndUserId(eventId, userId)) {
	        throw new BadRequestException("Already marked interested in this event");
	    }
	    
	    // Create interest
	    EventInterest interest = new EventInterest();
	    interest.setEvent(event);
	    interest.setUser(user);
	    interest.setInterestedAt(LocalDateTime.now());
	    
	    interestRepository.save(interest);
	    
	    return new EventActionResponse("Successfully marked interest", eventId, userId);
	}
	// delete event
	public void deleteEvent(Long eventId, Long userId) {
	    
	    Event event = eventRepository.findById(eventId)
	        .orElseThrow(() -> new ResourceNotFoundException("Event not found"));
	    
	    if (!event.getCreatedBy().equals(userId)) {
	        throw new UnauthorizedException("Only the event creator can delete this event");
	    }
	    
	    eventRepository.delete(event);
	}

}
