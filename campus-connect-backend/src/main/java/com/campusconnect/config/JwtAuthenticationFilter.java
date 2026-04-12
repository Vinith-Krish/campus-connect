package com.campusconnect.config;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.campusconnect.service.JwtService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {
	private final JwtService jwtService;
	private final UserDetailsService userDetailsService;
	// do filter internal
	@Override
	protected void doFilterInternal(
	        @NonNull HttpServletRequest request,
	        @NonNull HttpServletResponse response,
	        @NonNull FilterChain filterChain) throws ServletException, IOException {
	    
	    try {
	        // 1. Extract Authorization header
	        final String authHeader = request.getHeader("Authorization");
	        
	        // 2. Check if Authorization header exists and starts with "Bearer "
	        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
	            filterChain.doFilter(request, response);
	            return;
	        }
	        
	        // 3. Extract JWT token (remove "Bearer " prefix)
	        final String jwt = authHeader.substring(7);
	        
	        // 4. Extract username (email) from token
	        final String userEmail = jwtService.extractUsername(jwt);
	        
	        // 5. Validate token and set authentication
	        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
	            // Load user details
	            UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);
	            
	            // Validate token
	            if (jwtService.validateToken(jwt, userDetails.getUsername())) {
	                // Create authentication token
	                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
	                    userDetails,
	                    null,
	                    userDetails.getAuthorities()
	                );
	                
	                // Set authentication details
	                authToken.setDetails(
	                    new WebAuthenticationDetailsSource().buildDetails(request)
	                );
	                
	                // Set authentication in security context
	                SecurityContextHolder.getContext().setAuthentication(authToken);
	                
	                log.debug("JWT authentication successful for user: {}", userEmail);
	            }
	        }
	        
	        // 6. Continue filter chain
	        filterChain.doFilter(request, response);
	        
	    } catch (Exception e) {
	        log.error("JWT authentication failed: {}", e.getMessage());
	        
	        // Clear security context
	        SecurityContextHolder.clearContext();
	        
	        // Set error response
	        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
	        response.setContentType("application/json");
	        response.getWriter().write("{\"error\":\"Unauthorized\",\"message\":\"Invalid or expired token\"}");
	    }
	}
	@Override
	protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
	    // Description: Skip filter for public endpoints
	    // Parameters: request
	    // Returns: true if filter should be skipped
	    
	    String path = request.getRequestURI();
	    
	    // Skip filter for public endpoints
	    return path.startsWith("/api/auth/") || 
	           path.equals("/error") ||
	           (path.equals("/api/events") && request.getMethod().equals("GET")) ||
	           (path.matches("/api/events/\\d+") && request.getMethod().equals("GET"));
	}

}
