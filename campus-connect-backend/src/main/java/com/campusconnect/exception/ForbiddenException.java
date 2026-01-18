package com.campusconnect.exception;

import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.http.HttpStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class ForbiddenException extends RuntimeException {
    
    // Constructor 1: Message only
    public ForbiddenException(String message) {
        super(message);
    }
    
    // Constructor 2: Message and cause
    public ForbiddenException(String message, Throwable cause) {
        super(message, cause);
    }
}