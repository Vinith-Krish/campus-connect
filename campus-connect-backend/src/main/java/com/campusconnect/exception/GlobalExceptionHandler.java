package com.campusconnect.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import jakarta.validation.ConstraintViolationException;

import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
	// handling resource not found exception
	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<ErrorResponse> handleResourceNotFoundException(
	        ResourceNotFoundException ex, 
	        WebRequest request) {
	    
	    log.error("Resource not found", ex);
	    
	    ErrorResponse errorResponse = ErrorResponse.of(
	        HttpStatus.NOT_FOUND,
	        "The requested resource was not found."
	    );
	    
	    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
	}
	// handling unauthorized exception
	@ExceptionHandler(UnauthorizedException.class)
	public ResponseEntity<ErrorResponse> handleUnauthorizedException(
	        UnauthorizedException ex, 
	        WebRequest request) {
	    // Description: Handle UnauthorizedException (401)
	    // Returns: ErrorResponse with 401 status
	    
	    log.error("Unauthorized request", ex);
	    
	    ErrorResponse errorResponse = ErrorResponse.of(
	        HttpStatus.UNAUTHORIZED,
	        "Authentication failed. Please check your credentials and try again."
	    );
	    
	    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
	}
	// handling bad request exception
	@ExceptionHandler(BadRequestException.class)
	public ResponseEntity<ErrorResponse> handleBadRequestException(
	        BadRequestException ex, 
	        WebRequest request) {
	    // Description: Handle BadRequestException (400)
	    // Returns: ErrorResponse with 400 status
	    
	    log.error("Bad request", ex);
	    
	    ErrorResponse errorResponse = ErrorResponse.of(
	        HttpStatus.BAD_REQUEST,
	        "The request could not be processed. Please check your input and try again."
	    );
	    
	    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
	}
	// handling forbidden exception
	@ExceptionHandler(ForbiddenException.class)
	public ResponseEntity<ErrorResponse> handleForbiddenException(
	        ForbiddenException ex, 
	        WebRequest request) {
	    // Description: Handle ForbiddenException (403)
	    // Returns: ErrorResponse with 403 status
	    
	    log.error("Forbidden request", ex);
	    
	    ErrorResponse errorResponse = ErrorResponse.of(
	        HttpStatus.FORBIDDEN,
	        "You do not have permission to perform this action."
	    );
	    
	    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
	}
	// handling validation errors
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(
	        MethodArgumentNotValidException ex, 
	        WebRequest request) {
	    // Description: Handle validation errors from @Valid annotation (400)
	    // Returns: ErrorResponse with 400 status and validation details
	    
	    log.error("Validation failed", ex);
	    
	    ErrorResponse errorResponse = ErrorResponse.of(
	        HttpStatus.BAD_REQUEST,
	        "The request contains invalid data. Please check your input and try again."
	    );
	    
	    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
	}

	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<ErrorResponse> handleConstraintViolation(
	        ConstraintViolationException ex,
	        WebRequest request) {

	    log.error("Request constraint validation failed", ex);

	    ErrorResponse errorResponse = ErrorResponse.of(
	        HttpStatus.BAD_REQUEST,
	        "The request contains invalid parameters. Please check your input and try again."
	    );

	    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
	}

	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	public ResponseEntity<ErrorResponse> handleTypeMismatch(
	        MethodArgumentTypeMismatchException ex,
	        WebRequest request) {

	    log.error("Request parameter type validation failed", ex);

	    ErrorResponse errorResponse = ErrorResponse.of(
	        HttpStatus.BAD_REQUEST,
	        "The request contains an invalid parameter. Please check your input and try again."
	    );

	    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
	}
	// handling data integrity violation exception
	@ExceptionHandler(DataIntegrityViolationException.class)
	public ResponseEntity<ErrorResponse> handleDataIntegrityViolation(
	        DataIntegrityViolationException ex, 
	        WebRequest request) {
	    // Description: Handle database constraint violations (409)
	    // Returns: ErrorResponse with 409 status
	    
	    log.error("Data integrity violation", ex);
	    
	    ErrorResponse errorResponse = ErrorResponse.of(
	        HttpStatus.CONFLICT,
	        "The request could not be completed because it conflicts with existing data."
	    );
	    
	    return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
	}
	// handling generic exceptions
	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponse> handleGenericException(
	        Exception ex, 
	        WebRequest request) {
	    // Description: Handle all other unhandled exceptions (500)
	    // Returns: ErrorResponse with 500 status
	    
	    log.error("Internal server error: ", ex);
	    
	    ErrorResponse errorResponse = ErrorResponse.of(
	        HttpStatus.INTERNAL_SERVER_ERROR,
	        "An unexpected error occurred. Please try again later.",
	        null
	    );
	    
	    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
	}
	// handling authentication exceptions
	@ExceptionHandler(AuthenticationException.class)
	public ResponseEntity<ErrorResponse> handleAuthenticationException(
	        AuthenticationException ex, 
	        WebRequest request) {
	    // Description: Handle Spring Security authentication exceptions (401)
	    // Returns: ErrorResponse with 401 status
	    
	    log.error("Authentication failed", ex);
	    
	    ErrorResponse errorResponse = ErrorResponse.of(
	        HttpStatus.UNAUTHORIZED,
	        "Authentication failed. Please sign in again."
	    );
	    
	    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
	}
	// handlng access denied exceptions
	@ExceptionHandler(AccessDeniedException.class)
	public ResponseEntity<ErrorResponse> handleAccessDeniedException(
	        AccessDeniedException ex, 
	        WebRequest request) {
	    // Description: Handle Spring Security access denied exceptions (403)
	    // Returns: ErrorResponse with 403 status
	    
	    log.error("Access denied", ex);
	    
	    ErrorResponse errorResponse = ErrorResponse.of(
	        HttpStatus.FORBIDDEN,
	        "You do not have permission to access this resource."
	    );
	    
	    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
	}

}
