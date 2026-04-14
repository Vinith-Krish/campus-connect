package com.campusconnect.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
public class RecaptchaService {

    @Value("${recaptcha.secret}")
    private String recaptchaSecret;

    @Value("${recaptcha.verify-url}")
    private String recaptchaVerifyUrl;

    @Value("${recaptcha.score-threshold:0.5}")
    private float scoreThreshold;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    // ✅ Constructor Injection ONLY (Best Practice)
    public RecaptchaService(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    /**
     * Verify reCAPTCHA token
     * @param token reCAPTCHA token from frontend
     * @return true if verification passes, false otherwise
     */
    public boolean verifyToken(String token) {
        try {
            if (token == null || token.isEmpty()) {
                log.warn("reCAPTCHA token is empty");
                return false;
            }

            // Build request body
            String requestBody = "secret=" + recaptchaSecret + "&response=" + token;

            // Set headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            // Create request entity
            HttpEntity<String> request = new HttpEntity<>(requestBody, headers);

            // Call Google API
            String response = restTemplate.postForObject(
                    recaptchaVerifyUrl,
                    request,
                    String.class
            );

            if (response == null) {
                log.error("reCAPTCHA verification response is null");
                return false;
            }

            // Parse JSON response
            JsonNode jsonNode = objectMapper.readTree(response);
            boolean success = jsonNode.get("success").asBoolean();
            float score = jsonNode.has("score") ? jsonNode.get("score").floatValue() : 0;

            log.info("reCAPTCHA verification - Success: {}, Score: {}", success, score);

            // Validate score threshold (for v3)
            if (success && score >= scoreThreshold) {
                return true;
            }

            log.warn("reCAPTCHA verification failed - Success: {}, Score: {} (threshold: {})",
                    success, score, scoreThreshold);

            return false;

        } catch (Exception e) {
            log.error("Error verifying reCAPTCHA token", e);
            return false;
        }
    }
}