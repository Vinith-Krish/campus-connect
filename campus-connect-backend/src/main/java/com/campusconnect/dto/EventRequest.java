package com.campusconnect.dto;

import java.time.LocalDate;

import com.campusconnect.model.Category;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventRequest {
	@NotBlank(message = "Title is required")
	@Size(max = 120, message = "Title must not exceed 120 characters")
	private String title;

	@NotBlank(message = "Description is required")
	@Size(max = 5000, message = "Description must not exceed 5000 characters")
	private String description;

	@NotNull(message = "Date is required")
	@Future(message = "Date must be in the future")
	private LocalDate date;

	@NotBlank(message = "Time is required")
	@Pattern(regexp = "^([01]\\d|2[0-3]):[0-5]\\d$", message = "Time must use HH:mm format")
	private String time;

	@NotBlank(message = "Venue is required")
	@Size(max = 200, message = "Venue must not exceed 200 characters")
	private String venue;

	@NotNull(message = "Category is required")
	private Category category;  

	@Size(max = 2048, message = "Image URL must not exceed 2048 characters")
	@Pattern(regexp = "^$|https://[^\\s]+$", message = "Image URL must be a valid HTTPS URL")
	private String imageUrl;

}
