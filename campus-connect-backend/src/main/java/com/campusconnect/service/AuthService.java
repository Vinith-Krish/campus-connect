package com.campusconnect.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import com.campusconnect.dto.AuthResponse;
import com.campusconnect.dto.LoginRequest;
import com.campusconnect.dto.RegisterRequest;
import com.campusconnect.dto.UserDTO;
import com.campusconnect.repository.UserRepository;

import lombok.extern.slf4j.Slf4j;
import com.campusconnect.exception.BadRequestException;
import com.campusconnect.exception.UnauthorizedException;
import com.campusconnect.model.User;

@Service
@Slf4j
public class AuthService {
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private PasswordEncoder passwordEncoder;
	@Autowired
	private JwtService jwtService;

	public AuthResponse register(RegisterRequest request) {
		// check if email already exists
		if (userRepository.existsByEmail(request.getEmail())) {
			throw new BadRequestException("Email already exists");
		}
		// create user entity
		User user = new User();
		user.setEmail(request.getEmail());
		user.setPassword(passwordEncoder.encode(request.getPassword()));
		user.setName(request.getName());
		user.setCollegename(request.getCollegename());
		user.setRole(request.getRole());
		// Save user to database
		User savedUser = userRepository.save(user);
		// Generate JWT token
		String token = jwtService.generateToken(savedUser.getEmail(), savedUser.getId(), savedUser.getRole());
		// Create and return AuthResponse
		UserDTO userDTO = UserDTO.fromEntity(savedUser);
		return AuthResponse.builder().token(token).user(userDTO).build();

	}

	public AuthResponse login(LoginRequest request) {
		User user = userRepository.findByEmail(request.getEmail())
				.orElseThrow(() -> new UnauthorizedException("Invalid email or password"));
		// Check password
		if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
			throw new UnauthorizedException("Invalid email or password");
		}
		// Generate JWT token
		String token = jwtService.generateToken(user.getEmail(), user.getId(), user.getRole());
		// Create and return AuthResponse
		UserDTO userDTO = UserDTO.fromEntity(user);
		return AuthResponse.builder().token(token).user(userDTO).build();
	}
}
