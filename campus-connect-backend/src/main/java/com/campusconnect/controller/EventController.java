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
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
@Validated
public class EventController {

    private final EventService eventService;

    @GetMapping
    public ResponseEntity<Page<EventResponse>> getAllEvents(
            @RequestParam(required = false) @Size(max = 100) String search,
            @RequestParam(required = false) Category category,
            @RequestParam(defaultValue = "0") @Min(0) @Max(10000) int page,
            @RequestParam(defaultValue = "12") @Min(1) @Max(100) int size,
            @RequestParam(defaultValue = "date") @Pattern(regexp = "^(date|createdAt|title)$") String sortBy,
            @RequestParam(defaultValue = "asc") @Pattern(regexp = "^(asc|desc)$") String direction) {

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
    public ResponseEntity<EventResponse> getEventById(@PathVariable @Positive Long id) {
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
            @PathVariable @Positive Long id,
            @Valid @RequestBody EventRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        EventResponse event = eventService.updateEvent(id, request, userDetails.getUsername());
        return ResponseEntity.ok(event);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('CLUB_ADMIN')")
    public ResponseEntity<Void> deleteEvent(
            @PathVariable @Positive Long id,
            @AuthenticationPrincipal UserDetails userDetails) {

        eventService.deleteEvent(id, userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/register")
    public ResponseEntity<EventActionResponse> registerForEvent(
            @PathVariable @Positive Long id,
            @AuthenticationPrincipal UserDetails userDetails) {

        EventActionResponse response = eventService.registerForEvent(id, userDetails.getUsername());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/interested")
    public ResponseEntity<EventActionResponse> markInterested(
            @PathVariable @Positive Long id,
            @AuthenticationPrincipal UserDetails userDetails) {

        EventActionResponse response = eventService.markInterested(id, userDetails.getUsername());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{eventId}/unregister")
    public ResponseEntity<Void> unregisterFromEvent(
            @PathVariable @Positive Long eventId,
            @AuthenticationPrincipal UserDetails userDetails) {

        eventService.unregisterUserFromEvent(eventId, userDetails.getUsername());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{eventId}/registrations/export")
    @PreAuthorize("hasRole('CLUB_ADMIN')")
    public ResponseEntity<byte[]> exportRegisteredStudents(
            @PathVariable @Positive Long eventId,
            @AuthenticationPrincipal UserDetails userDetails) {

        byte[] excelFile = eventService.exportRegisteredStudentsExcel(eventId, userDetails.getUsername());
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "registered_students_" + eventId + ".xlsx");
        
        return new ResponseEntity<>(excelFile, headers, HttpStatus.OK);
    }
}