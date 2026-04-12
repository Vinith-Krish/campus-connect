package com.campusconnect.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.campusconnect.dto.UpdateProfileRequest;
import com.campusconnect.dto.UserDTO;
import com.campusconnect.dto.UserEventsResponse;
import com.campusconnect.service.JwtService;
import com.campusconnect.service.UserService;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;

@RestController
@RequestMapping("/api/users")
public class UserController {
	@Autowired
	private UserService userService;
	@Autowired
	private JwtService jwtService;
	// get my events
	@GetMapping("/me/events")
	public ResponseEntity<UserEventsResponse> getMyEvents(
	        @RequestHeader("Authorization") String authHeader) {
	    
	    // Extract user ID from JWT token
	    String token = authHeader.substring(7);
	    Long userId = jwtService.extractUserId(token);
	    
	    UserEventsResponse response = userService.getMyEvents(userId);
	    return ResponseEntity.ok(response);
	}
	@GetMapping("/me")
	public ResponseEntity<UserDTO> getMyProfile(
	        @RequestHeader("Authorization") String authHeader) {
	    
	    // Extract user ID from JWT token
	    String token = authHeader.substring(7);
	    Long userId = jwtService.extractUserId(token);
	    
	    UserDTO user = userService.getUserById(userId);
	    return ResponseEntity.ok(user);
	}
	@PutMapping("/me")
	public ResponseEntity<UserDTO> updateProfile(
	        @Valid @RequestBody UpdateProfileRequest request,
	        @AuthenticationPrincipal UserDetails userDetails) {

	    UserDTO updatedUser = userService.updateUserProfile(userDetails.getUsername(), request.getName());
	    return ResponseEntity.ok(updatedUser);
	}
}
