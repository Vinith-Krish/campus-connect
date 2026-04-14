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

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        try {
            final String authHeader = request.getHeader("Authorization");

            // âœ… If no token â†’ allow request (IMPORTANT FIX)
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                filterChain.doFilter(request, response);
                return;
            }

            final String jwt = authHeader.substring(7);

            String userEmail = null;

            // âœ… Safe extraction (prevents crash)
            try {
                userEmail = jwtService.extractUsername(jwt);
            } catch (Exception e) {
                log.warn("Invalid JWT token");
            }

            // âœ… Validate and set authentication
            if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);

                if (jwtService.validateToken(jwt, userDetails.getUsername())) {

                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities()
                            );

                    authToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request)
                    );

                    SecurityContextHolder.getContext().setAuthentication(authToken);

                    log.debug("JWT authentication successful for user: {}", userEmail);
                }
            }

            // âœ… ALWAYS continue filter chain
            filterChain.doFilter(request, response);

        } catch (Exception e) {
            log.error("JWT authentication failed: {}", e.getMessage());

            // âœ… IMPORTANT FIX â†’ do NOT break connection
            SecurityContextHolder.clearContext();

            // Just continue request instead of killing it
            filterChain.doFilter(request, response);
        }
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {

        String path = request.getRequestURI();

        // âœ… Public endpoints (no JWT needed)
        return path.startsWith("/api/auth/") ||
               path.equals("/error") ||
               (path.equals("/api/events") && request.getMethod().equals("GET")) ||
               (path.matches("/api/events/\\d+") && request.getMethod().equals("GET"));
    }
}
