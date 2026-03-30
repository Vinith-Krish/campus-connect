package com.campusconnect.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.campusconnect.dto.EventActionResponse;
import com.campusconnect.dto.EventRequest;
import com.campusconnect.dto.EventResponse;
import com.campusconnect.model.Category;
import com.campusconnect.service.EventService;
import com.campusconnect.service.JwtService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class EventController {
	private final EventService eventService;
	private final JwtService jwtService;
	// get all events
	@GetMapping
	public ResponseEntity<List<EventResponse>> getAllEvents(
	        @RequestParam(required = false) String search,
	        @RequestParam(required = false) Category category) {
	    
	    List<EventResponse> events = eventService.getAllEvents(search, category);
	    return ResponseEntity.ok(events);
	}
	// get events created by logged-in admin
	@GetMapping("/my-events")
	@PreAuthorize("hasRole('CLUB_ADMIN')")
	public ResponseEntity<List<EventResponse>> getMyCreatedEvents(
	        @RequestHeader("Authorization") String authHeader) {
	    
	    // Extract user ID from JWT token
	    String token = authHeader.substring(7);
	    Long userId = jwtService.extractUserId(token);
	    
	    List<EventResponse> events = eventService.getMyCreatedEvents(userId);
	    return ResponseEntity.ok(events);
	}
	// get event by id
	@GetMapping("/{id}")
	public ResponseEntity<EventResponse> getEventById(@PathVariable Long id) {
	    
	    EventResponse event = eventService.getEventById(id);
	    return ResponseEntity.ok(event);
	}
	// create event
	@PostMapping
	@PreAuthorize("hasRole('CLUB_ADMIN')")
	public ResponseEntity<EventResponse> createEvent(
	        @Valid @RequestBody EventRequest request,
	        @RequestHeader("Authorization") String authHeader) {
	    
	    // Extract user ID from JWT token
	    String token = authHeader.substring(7); // Remove "Bearer " prefix
	    Long userId = jwtService.extractUserId(token);
	    
	    EventResponse event = eventService.createEvent(request, userId);
	    return ResponseEntity.status(HttpStatus.CREATED).body(event);
	}
	// register for event
	@PostMapping("/{id}/register")
	public ResponseEntity<EventActionResponse> registerForEvent(
	        @PathVariable Long id,
	        @RequestHeader("Authorization") String authHeader) {
	    // Description: Register current user for an event
	    // HTTP Method: POST
	    // Path: /api/events/{id}/register
	    // Path Variable: id (event ID)
	    // Headers: Authorization Bearer token
	    // Access: Authenticated
	    // Returns: EventActionResponse with 200 OK status
	    
	    // Extract user ID from JWT token
	    String token = authHeader.substring(7);
	    Long userId = jwtService.extractUserId(token);
	    
	    EventActionResponse response = eventService.registerForEvent(id, userId);
	    return ResponseEntity.ok(response);
	}
	@PostMapping("/{id}/interested")
	public ResponseEntity<EventActionResponse> markInterested(
	        @PathVariable Long id,
	        @RequestHeader("Authorization") String authHeader) {
	    
	    // Extract user ID from JWT token
	    String token = authHeader.substring(7);
	    Long userId = jwtService.extractUserId(token);
	    
	    EventActionResponse response = eventService.markInterested(id, userId);
	    return ResponseEntity.ok(response);
	}
	@DeleteMapping("/{id}")
	@PreAuthorize("hasRole('CLUB_ADMIN')")
	public ResponseEntity<Void> deleteEvent(
	        @PathVariable Long id,
	        @RequestHeader("Authorization") String authHeader) {
	    
	    // Extract user ID from JWT token
	    String token = authHeader.substring(7);
	    Long userId = jwtService.extractUserId(token);
	    
	    eventService.deleteEvent(id, userId);
	    return ResponseEntity.noContent().build();
	}
	@PutMapping("/{id}")
	public ResponseEntity<EventResponse> updateEvent(
	    @PathVariable Long id,
	    @Valid @RequestBody EventRequest request,
	    @RequestHeader("Authorization") String token) {
	    
	    String jwt = token.substring(7);
	    Long userId = jwtService.extractUserId(jwt);
	    
	    EventResponse event = eventService.updateEvent(id, request, userId);
	    return ResponseEntity.ok(event);
	}
	@DeleteMapping("/{eventId}/unregister")
	public ResponseEntity<?> unregisterFromEvent(
	    @PathVariable Long eventId,
	    @AuthenticationPrincipal UserDetails userDetails
	) {
	    eventService.unregisterUserFromEvent(eventId, userDetails.getUsername());
	    return ResponseEntity.ok().build();
	}

}
