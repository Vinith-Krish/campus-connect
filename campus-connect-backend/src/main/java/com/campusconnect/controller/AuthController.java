package com.campusconnect.controller;

import com.campusconnect.dto.AuthResponse;
import com.campusconnect.dto.LoginRequest;
import com.campusconnect.dto.RegisterRequest;
import com.campusconnect.dto.UserDTO;
import com.campusconnect.model.User;
import com.campusconnect.model.Role;
import com.campusconnect.service.JwtService;
import com.campusconnect.service.UserService;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Slf4j
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // ================= LOGIN =================
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            log.info("Login attempt for email: {}", loginRequest.getEmail());

            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(),
                            loginRequest.getPassword()
                    )
            );

            User user = userService.findByEmail(loginRequest.getEmail());
            UserDTO userDTO = convertToDTO(user);

            String token = jwtService.generateToken(user.getEmail(), user.getId(), user.getRole());

            return ResponseEntity.ok(AuthResponse.builder()
                    .token(token)
                    .user(userDTO)
                    .success(true)
                    .message("Login successful")
                    .build());

        } catch (AuthenticationException e) {
            log.error("Invalid credentials for {}", loginRequest.getEmail());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(AuthResponse.builder()
                            .success(false)
                            .message("Invalid email or password")
                            .build());
        } catch (Exception e) {
            log.error("Login error", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(AuthResponse.builder()
                            .success(false)
                            .message("Login failed")
                            .build());
        }
    }

    // ================= REGISTER =================
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest registerRequest) {
        try {
            log.info("Registration attempt for email: {}", registerRequest.getEmail());

            // Check existing user
            if (userService.findByEmail(registerRequest.getEmail()) != null) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(AuthResponse.builder()
                                .success(false)
                                .message("User already exists")
                                .build());
            }

            // Create user
            User newUser = new User();
            newUser.setName(registerRequest.getName());
            newUser.setEmail(registerRequest.getEmail());
            newUser.setCollegename(registerRequest.getCollegename());
            newUser.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
            newUser.setRole(Role.valueOf(registerRequest.getRole().toUpperCase()));

            User savedUser = userService.save(newUser);

            String token = jwtService.generateToken(
                    savedUser.getEmail(),
                    savedUser.getId(),
                    savedUser.getRole()
            );

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(AuthResponse.builder()
                            .token(token)
                            .user(convertToDTO(savedUser))
                            .success(true)
                            .message("Registration successful")
                            .build());

        } catch (Exception e) {
            log.error("Registration error", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(AuthResponse.builder()
                            .success(false)
                            .message("Registration failed")
                            .build());
        }
    }

    // ================= DTO CONVERTER =================
    private UserDTO convertToDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setCollegename(user.getCollegename());
        dto.setRole(user.getRole());
        return dto;
    }
}
