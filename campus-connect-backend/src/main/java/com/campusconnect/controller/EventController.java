package com.campusconnect.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import com.campusconnect.dto.EventActionResponse;
import com.campusconnect.dto.EventRequest;
import com.campusconnect.dto.EventResponse;
import com.campusconnect.model.Category;
import com.campusconnect.service.EventService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    @GetMapping
    public ResponseEntity<Page<EventResponse>> getAllEvents(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Category category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(defaultValue = "date") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {

        Page<EventResponse> events = eventService.getAllEvents(search, category, page, size, sortBy, direction);
        return ResponseEntity.ok(events);
    }

    @GetMapping("/my-events")
    @PreAuthorize("hasRole('CLUB_ADMIN')")
    public ResponseEntity<List<EventResponse>> getMyCreatedEvents(
            @AuthenticationPrincipal UserDetails userDetails) {

        List<EventResponse> events = eventService.getMyCreatedEvents(userDetails.getUsername());
        return ResponseEntity.ok(events);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventResponse> getEventById(@PathVariable Long id) {
        EventResponse event = eventService.getEventById(id);
        return ResponseEntity.ok(event);
    }

    @PostMapping
    @PreAuthorize("hasRole('CLUB_ADMIN')")
    public ResponseEntity<EventResponse> createEvent(
            @Valid @RequestBody EventRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        EventResponse event = eventService.createEvent(request, userDetails.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(event);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('CLUB_ADMIN')")
    public ResponseEntity<EventResponse> updateEvent(
            @PathVariable Long id,
            @Valid @RequestBody EventRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        EventResponse event = eventService.updateEvent(id, request, userDetails.getUsername());
        return ResponseEntity.ok(event);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('CLUB_ADMIN')")
    public ResponseEntity<Void> deleteEvent(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {

        eventService.deleteEvent(id, userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/register")
    public ResponseEntity<EventActionResponse> registerForEvent(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {

        EventActionResponse response = eventService.registerForEvent(id, userDetails.getUsername());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/interested")
    public ResponseEntity<EventActionResponse> markInterested(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {

        EventActionResponse response = eventService.markInterested(id, userDetails.getUsername());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{eventId}/unregister")
    public ResponseEntity<Void> unregisterFromEvent(
            @PathVariable Long eventId,
            @AuthenticationPrincipal UserDetails userDetails) {

        eventService.unregisterUserFromEvent(eventId, userDetails.getUsername());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{eventId}/registrations/export")
    @PreAuthorize("hasRole('CLUB_ADMIN')")
    public ResponseEntity<byte[]> exportRegisteredStudents(
            @PathVariable Long eventId,
            @AuthenticationPrincipal UserDetails userDetails) {

        byte[] excelFile = eventService.exportRegisteredStudentsExcel(eventId, userDetails.getUsername());
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "registered_students_" + eventId + ".xlsx");
        
        return new ResponseEntity<>(excelFile, headers, HttpStatus.OK);
    }
}