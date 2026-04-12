package com.campusconnect.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.campusconnect.dto.AuthResponse;
import com.campusconnect.dto.LoginRequest;
import com.campusconnect.dto.RegisterRequest;
import com.campusconnect.service.AuthService;
import com.campusconnect.service.RecaptchaService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
	@Autowired
	private AuthService authService;
	@Autowired
	private RecaptchaService recaptchaService;
	// endpoint for user registration
	@PostMapping("/register")
	public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
		 if (!recaptchaService.verifyRecaptcha(request.getRecaptchaToken())) {
	            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
	                .body(Map.of("message", "reCAPTCHA verification failed"));
	        }
	    AuthResponse response = authService.register(request);
	    return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}
	// endpoint for user login
	@PostMapping("/login")
	public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
		// Verify reCAPTCHA token
		if (!recaptchaService.verifyRecaptcha(request.getRecaptchaToken())) {
	            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
	                .body(Map.of("message", "reCAPTCHA verification failed"));
	        }
	    AuthResponse response = authService.login(request);
	    return ResponseEntity.ok(response);
	}

}