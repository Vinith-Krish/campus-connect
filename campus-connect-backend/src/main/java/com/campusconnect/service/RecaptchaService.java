package com.campusconnect.service;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;


@Service
public class RecaptchaService {
    
    private static final Logger logger = LoggerFactory.getLogger(RecaptchaService.class);
    
    private static final String RECAPTCHA_VERIFY_URL = 
        "https://www.google.com/recaptcha/api/siteverify";
    
    @Value("${recaptcha.secret.key}")
    private String secretKey;
    
    private final RestTemplate restTemplate;
    
    public RecaptchaService() {
        this.restTemplate = new RestTemplate();
    }
    
    @SuppressWarnings("unchecked")
    public boolean verifyRecaptcha(String recaptchaToken) {
        if (recaptchaToken == null || recaptchaToken.isEmpty()) {
            logger.warn("reCAPTCHA token is null or empty");
            return false;
        }
        
        try {
            // Prepare request parameters as form data
            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("secret", secretKey);
            params.add("response", recaptchaToken);
            
            // Set headers for form data
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            
            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
            
            // Make POST request to Google reCAPTCHA API
            ResponseEntity<Map> responseEntity = restTemplate.postForEntity(
                RECAPTCHA_VERIFY_URL,
                request,
                Map.class
            );
            
            Map<String, Object> response = responseEntity.getBody();
            
            if (response == null) {
                logger.error("reCAPTCHA verification response is null");
                return false;
            }
            
            boolean success = Boolean.TRUE.equals(response.get("success"));
            
            if (!success) {
                logger.warn("reCAPTCHA verification failed. Response: {}", response);
            } else {
                logger.debug("reCAPTCHA verification successful");
            }
            
            return success;
        } catch (Exception e) {
            logger.error("Error during reCAPTCHA verification", e);
            return false;
        }
    }
}