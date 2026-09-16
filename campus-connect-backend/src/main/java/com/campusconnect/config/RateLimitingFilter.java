package com.campusconnect.config;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class RateLimitingFilter extends OncePerRequestFilter {

    private static final String AUTH_PREFIX = "/api/auth/";
    private static final String EVENTS_PATH = "/api/events";

    private final RateLimitProperties properties;
    private final RateLimitService rateLimitService;
    private final ObjectMapper objectMapper;

    public RateLimitingFilter(
            RateLimitProperties properties,
            RateLimitService rateLimitService,
            ObjectMapper objectMapper) {
        this.properties = properties;
        this.rateLimitService = rateLimitService;
        this.objectMapper = objectMapper;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        if (isExcluded(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        if (rateLimitService.getStateCount() > rateLimitService.getMaxTrackedKeys()) {
            rateLimitService.removeExpiredStates();
        }

        HttpServletRequest requestToUse = request;
        String accountKey = null;

        if (isAuthenticationRoute(request)
                && request.getMethod().equalsIgnoreCase("POST")
                && isJsonRequest(request)) {
            CachedBodyRequest cachedRequest = new CachedBodyRequest(request);
            requestToUse = cachedRequest;
            accountKey = extractAccountKey(cachedRequest);
        }

        RateLimitService.Decision decision = applyLimit(request, accountKey);
        if (!decision.isAllowed()) {
            writeRateLimitResponse(response, decision.getRetryAfterSeconds());
            return;
        }

        filterChain.doFilter(requestToUse, response);
    }

    private RateLimitService.Decision applyLimit(
            HttpServletRequest request,
            String accountKey) {

        String ipKey = "ip:" + request.getRemoteAddr();

        if (isAuthenticationRoute(request)) {
            RateLimitService.Decision ipDecision = rateLimitService.tryConsume(
                    "auth:" + ipKey,
                    properties.getAuthIpCapacity(),
                    properties.getAuthIpWindowSeconds(),
                    true
            );

            if (!ipDecision.isAllowed() || accountKey == null) {
                return ipDecision;
            }

            return rateLimitService.tryConsume(
                    "auth:" + accountKey,
                    properties.getAuthAccountCapacity(),
                    properties.getAuthAccountWindowSeconds(),
                    true
            );
        }

        if (isPublicEndpoint(request)) {
            return rateLimitService.tryConsume(
                    "public:" + ipKey,
                    properties.getPublicCapacity(),
                    properties.getPublicWindowSeconds(),
                    false
            );
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userKey = authentication == null || authentication.getName() == null
                ? ipKey
                : "user:" + hashIdentifier(authentication.getName());

        return rateLimitService.tryConsume(
                "authenticated:" + userKey,
                properties.getAuthenticatedCapacity(),
                properties.getAuthenticatedWindowSeconds(),
                false
        );
    }

    private boolean isExcluded(HttpServletRequest request) {
        return request.getMethod().equalsIgnoreCase("OPTIONS")
                || request.getRequestURI().equals("/error");
    }

    private boolean isAuthenticationRoute(HttpServletRequest request) {
        return request.getRequestURI().startsWith(AUTH_PREFIX);
    }

    private boolean isPublicEndpoint(HttpServletRequest request) {
        if (!request.getMethod().equalsIgnoreCase("GET")) {
            return false;
        }

        String path = request.getRequestURI();
        return path.equals(EVENTS_PATH) || path.matches(EVENTS_PATH + "/\\d+");
    }

    private boolean isJsonRequest(HttpServletRequest request) {
        String contentType = request.getContentType();
        return contentType != null
                && contentType.toLowerCase().startsWith(MediaType.APPLICATION_JSON_VALUE);
    }

    private String extractAccountKey(CachedBodyRequest request) {
        try {
            JsonNode body = objectMapper.readTree(request.getCachedBody());
            JsonNode email = body == null ? null : body.get("email");

            if (email == null || !email.isTextual() || email.asText().isBlank()) {
                return null;
            }

            return hashIdentifier(email.asText().trim().toLowerCase());
        } catch (Exception exception) {
            log.debug("Unable to extract authentication account identifier for rate limiting", exception);
            return null;
        }
    }

    private void writeRateLimitResponse(
            HttpServletResponse response,
            long retryAfterSeconds) throws IOException {

        response.setStatus(429);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setHeader("Retry-After", Long.toString(retryAfterSeconds));
        response.getWriter().write(
                "{\"success\":false,\"message\":\"Too many requests. Please try again later.\"}"
        );
    }

    private String hashIdentifier(String identifier) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256")
                    .digest(identifier.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(digest);
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 is not available", exception);
        }
    }

    private static final class CachedBodyRequest extends HttpServletRequestWrapper {
        private final byte[] cachedBody;

        private CachedBodyRequest(HttpServletRequest request) throws IOException {
            super(request);
            this.cachedBody = request.getInputStream().readAllBytes();
        }

        private byte[] getCachedBody() {
            return cachedBody.clone();
        }

        @Override
        public ServletInputStream getInputStream() {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(cachedBody);

            return new ServletInputStream() {
                @Override
                public int read() {
                    return inputStream.read();
                }

                @Override
                public boolean isFinished() {
                    return inputStream.available() == 0;
                }

                @Override
                public boolean isReady() {
                    return true;
                }

                @Override
                public void setReadListener(ReadListener readListener) {
                }
            };
        }

        @Override
        public BufferedReader getReader() {
            return new BufferedReader(
                    new InputStreamReader(getInputStream(), StandardCharsets.UTF_8)
            );
        }
    }
}
