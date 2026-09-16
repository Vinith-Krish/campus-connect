package com.campusconnect.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {
    
    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 50)
    private String name;
    
    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    @Size(max = 254, message = "Email must not exceed 254 characters")
    private String email;
    
    @NotBlank(message = "College name is required")
    @Size(min = 2, max = 150, message = "College name must be 2 to 150 characters")
    private String collegename;
    
    @NotBlank(message = "Password is required")
    @Size(min = 6, max = 128, message = "Password must be 6 to 128 characters")
    private String password;
    
    @NotBlank(message = "Role is required")
    @Pattern(regexp = "^(STUDENT|CLUB_ADMIN)$", message = "Role must be STUDENT or CLUB_ADMIN")
    private String role;
}