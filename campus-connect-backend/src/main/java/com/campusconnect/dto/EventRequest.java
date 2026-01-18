package com.campusconnect.dto;

import java.time.LocalDate;

import com.campusconnect.model.Category;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventRequest {
	@NotBlank(message = "Title is required")
	private String title;

	@NotBlank(message = "Description is required")
	private String description;

	@NotNull(message = "Date is required")
	@Future(message = "Date must be in the future")
	private LocalDate date;

	@NotBlank(message = "Time is required")
	private String time;

	@NotBlank(message = "Venue is required")
	private String venue;

	@NotNull(message = "Category is required")
	private Category category;  

	private String imageUrl;    

}
