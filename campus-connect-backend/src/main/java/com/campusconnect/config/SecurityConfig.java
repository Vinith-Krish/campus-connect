package com.campusconnect.config;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.campusconnect.model.User;
import com.campusconnect.repository.UserRepository;
import com.campusconnect.service.JwtService;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {
	//private final JwtAuthenticationFilter jwtAuthFilter;
	//private final AuthenticationProvider authenticationProvider;
	private final UserRepository userRepository;

    // CORS configuration source bean
	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http, AuthenticationProvider authenticationProvider, JwtAuthenticationFilter jwtAuthFilter) throws Exception {
	    // Description: Configure HTTP security
	    // Returns: SecurityFilterChain bean
	    
	    http
	        .csrf(csrf -> csrf.disable())  // Disable CSRF for REST API
	        .cors(cors -> cors.configurationSource(corsConfigurationSource()))
	        .authorizeHttpRequests(auth -> auth
	            // Public endpoints
	            .requestMatchers("/api/auth/**").permitAll()
	            .requestMatchers("/api/events").permitAll()
	            .requestMatchers("/api/events/{id}").permitAll()
	            .requestMatchers("/error").permitAll()
	            
	            // CLUB_ADMIN only endpoints
	            .requestMatchers("/api/events").hasRole("CLUB_ADMIN")  // POST
	            .requestMatchers("/api/events/{id}").hasRole("CLUB_ADMIN")  // DELETE
	            
	            // Authenticated endpoints
	            .requestMatchers("/api/events/{id}/register").authenticated()
	            .requestMatchers("/api/events/{id}/interested").authenticated()
	            .requestMatchers("/api/users/**").authenticated()
	            
	            // All other requests must be authenticated
	            .anyRequest().authenticated()
	        )
	        .sessionManagement(session -> session
	            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
	        )
	        .authenticationProvider(authenticationProvider)
	        .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
	        .exceptionHandling(exception -> exception
	            .authenticationEntryPoint((request, response, authException) -> {
	                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
	                response.setContentType("application/json");
	                response.getWriter().write("{\"error\":\"Unauthorized\",\"message\":\"" 
	                    + authException.getMessage() + "\"}");
	            })
	            .accessDeniedHandler((request, response, accessDeniedException) -> {
	                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
	                response.setContentType("application/json");
	                response.getWriter().write("{\"error\":\"Forbidden\",\"message\":\"Access denied\"}");
	            })
	        );
	    
	    return http.build();
	}
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        // Description: Configure CORS settings
        // Returns: CorsConfigurationSource bean
        
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:5173"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", configuration);
        return source;
    }
    @Bean
    AuthenticationProvider authenticationProvider(UserDetailsService userDetailsService, 
                                                         PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder);
        return authProvider;
    }
    @Bean
    PasswordEncoder passwordEncoder() {
        // Description: BCrypt password encoder
        // Returns: PasswordEncoder bean
        
        return new BCryptPasswordEncoder();
    }
    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration config) 
            throws Exception {
        // Description: Configure authentication manager
        // Parameters: AuthenticationConfiguration
        // Returns: AuthenticationManager bean
        
        return config.getAuthenticationManager();
    }
    @Bean
    UserDetailsService userDetailsService() {
        // Description: Custom UserDetailsService implementation
        // Returns: UserDetailsService bean
        
        return username -> {
            User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
            
            return org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .roles(user.getRole().name())
                .build();
        };
    }
    @Bean
    JwtAuthenticationFilter jwtAuthenticationFilter(JwtService jwtService, 
                                                           UserDetailsService userDetailsService) {
        return new JwtAuthenticationFilter(jwtService, userDetailsService);
    }

}
