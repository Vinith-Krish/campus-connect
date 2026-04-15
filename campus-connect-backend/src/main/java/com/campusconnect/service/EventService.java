package com.campusconnect.service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import com.campusconnect.exception.ForbiddenException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
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

	        // Get all event IDs from the page
	        List<Long> eventIds = events.getContent().stream()
	                .map(Event::getId)
	                .collect(Collectors.toList());

	        // Fetch all registrations counts in one query
	        Map<Long, Long> registrationCounts = buildCountMap(
	            registrationRepository.countRegistrationsByEventIds(eventIds));
	        
	        // Fetch all interests counts in one query
	        Map<Long, Long> interestCounts = buildCountMap(
	            interestRepository.countInterestsByEventIds(eventIds));

	        return events.map(event -> {
	            Long regCount = registrationCounts.getOrDefault(event.getId(), 0L);
	            Long intCount = interestCounts.getOrDefault(event.getId(), 0L);
	            return EventResponse.fromEntity(event, regCount, intCount);
	        });
	    } catch (Exception e) {
	        log.error("Error fetching all events", e);
	        throw e;
	    }
	}
	
	/**
	 * Converts the result of countBy*EventIds queries to a Map<EventId, Count>
	 */
	private Map<Long, Long> buildCountMap(List<Object[]> queryResults) {
	    Map<Long, Long> countMap = new HashMap<>();
	    for (Object[] result : queryResults) {
	        Long eventId = ((Number) result[0]).longValue();
	        Long count = ((Number) result[1]).longValue();
	        countMap.put(eventId, count);
	    }
	    return countMap;
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
	private void ensureUserCanParticipateInEvents(User user) {
	    if (user.getRole() == Role.CLUB_ADMIN) {
	        throw new ForbiddenException("Club admins are not allowed to register or mark interest in events");
	    }
	}

	public EventActionResponse registerForEvent(Long eventId, String userEmail) {
	    Event event = eventRepository.findById(eventId)
	            .orElseThrow(() -> new ResourceNotFoundException("Event not found"));

	    User user = userRepository.findByEmail(userEmail)
	            .orElseThrow(() -> new ResourceNotFoundException("User not found"));

	    ensureUserCanParticipateInEvents(user);

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

	    ensureUserCanParticipateInEvents(user);

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

	    // Get all event IDs
	    List<Long> eventIds = events.stream()
	            .map(Event::getId)
	            .collect(Collectors.toList());

	    // Fetch all counts in bulk if there are events
	    Map<Long, Long> registrationCounts = eventIds.isEmpty() ? new HashMap<>() : 
	        buildCountMap(registrationRepository.countRegistrationsByEventIds(eventIds));
	    Map<Long, Long> interestCounts = eventIds.isEmpty() ? new HashMap<>() : 
	        buildCountMap(interestRepository.countInterestsByEventIds(eventIds));

	    return events.stream()
	            .map(event -> {
	                Long regCount = registrationCounts.getOrDefault(event.getId(), 0L);
	                Long intCount = interestCounts.getOrDefault(event.getId(), 0L);
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

	    // For single event, using countByEventId is acceptable (only 2 queries total)
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

	public byte[] exportRegisteredStudentsExcel(Long eventId, String userEmail) {
	    Event event = eventRepository.findById(eventId)
	            .orElseThrow(() -> new ResourceNotFoundException("Event not found"));

	    User user = userRepository.findByEmail(userEmail)
	            .orElseThrow(() -> new ResourceNotFoundException("User not found"));

	    if (user.getRole() != Role.CLUB_ADMIN) {
	        throw new UnauthorizedException("Only club admins can export registrations");
	    }

	    if (!event.getCreatedBy().equals(user.getId())) {
	        throw new UnauthorizedException("You can only export registrations for your own events");
	    }

	    List<EventRegistration> registrations = registrationRepository.findByEventIdOrderByRegisteredAtAsc(eventId);

	    try (Workbook workbook = new XSSFWorkbook();
	         ByteArrayOutputStream out = new ByteArrayOutputStream()) {

	        Sheet sheet = workbook.createSheet("Registered Students");

	        Row header = sheet.createRow(0);
	        header.createCell(0).setCellValue("Name");
	        header.createCell(1).setCellValue("College");
	        header.createCell(2).setCellValue("Email");

	        int rowIdx = 1;
	        for (EventRegistration registration : registrations) {
	            User student = registration.getUser();
	            Row row = sheet.createRow(rowIdx++);
	            row.createCell(0).setCellValue(student.getName() != null ? student.getName() : "");
	            row.createCell(1).setCellValue(student.getCollegename() != null ? student.getCollegename() : "");
	            row.createCell(2).setCellValue(student.getEmail() != null ? student.getEmail() : "");
	        }

	        sheet.autoSizeColumn(0);
	        sheet.autoSizeColumn(1);
	        sheet.autoSizeColumn(2);

	        workbook.write(out);
	        return out.toByteArray();
	    } catch (IOException e) {
	        throw new RuntimeException("Failed to generate Excel file", e);
	    }
	}

}
