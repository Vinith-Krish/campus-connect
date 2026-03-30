package com.campusconnect.dto;

import com.campusconnect.model.Role;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {
	@NotBlank(message = "Email is required")
	@Email(message = "Email should be valid")
	private String email;
	@NotBlank(message = "Password is required")
	@Size(min = 6, message = "Password must be at least 6 characters")
	private String password;
	@NotBlank(message = "Name is required")
	private String name;
	@NotBlank(message = "College name is required")
	private String collegename;
	@NotNull(message = "Role is required")
	private Role role;
	@NotBlank(message = "reCAPTCHA token is required")
	private String recaptchaToken;

}
