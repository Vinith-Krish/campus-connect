package com.campusconnect.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.campusconnect.model.Category;
import com.campusconnect.model.Event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EventResponse {
	private Long id;
	private String title;
	private String description;
	private LocalDate date;
	private String time;
	private String venue;
	private Category category;
	private String collegename;
	private Long organizerId;
	private String organizerName;
	private String organizerEmail;
	private String imageUrl;
	private Long registeredCount;
	private Long interestedCount;
	private LocalDateTime createdAt;
	public static EventResponse fromEntity(Event event, Long registeredCount, Long interestedCount) {
	    return EventResponse.builder()
	        .id(event.getId())
	        .title(event.getTitle())
	        .description(event.getDescription())
	        .date(event.getDate())
	        .time(event.getTime())
	        .venue(event.getVenue())
	        .category(event.getCategory())
	        .collegename(event.getCollegename())
	        .organizerId(event.getCreatedBy())
	        .organizerName(event.getOrganizerName())
	        .organizerEmail(event.getOrganizerEmail())
	        .imageUrl(event.getImageUrl())
	        .registeredCount(registeredCount)
	        .interestedCount(interestedCount)
	        .createdAt(event.getCreatedAt())
	        .build();
	}

}
