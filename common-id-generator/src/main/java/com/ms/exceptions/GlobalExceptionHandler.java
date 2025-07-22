package com.ms.exceptions;


import com.ms.util.ExceptionUtil;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    // Logger instance for the class
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<?> handleEntityNotFoundException(EntityNotFoundException ex) {
        logger.error("Entity not found: {}", ex.getMessage(), ex);
        return ExceptionUtil.createErrorResponseMessage(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<?> handleUserAlreadyExistsException(UserAlreadyExistsException ex, WebRequest request) {
        logger.error("User already exists: {}", ex.getMessage(), ex);
        return ExceptionUtil.createErrorResponseMessage(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<?> handleResourceNotFound(ResourceNotFoundException ex) {
        logger.error("Resource not found: {}", ex.getMessage(), ex);
        return ExceptionUtil.createErrorResponseMessage(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationException(MethodArgumentNotValidException ex) {
        logger.error("Validation error: {}", ex.getMessage(), ex);
        Map<String, String> validationErrors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                validationErrors.put(error.getField(), error.getDefaultMessage()));
        return ExceptionUtil.createErrorResponse(validationErrors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BadApiRequestException.class)
    public ResponseEntity<?> handleBadApiRequest(BadApiRequestException ex) {
        logger.warn("Bad API request: {}", ex.getMessage());
        return ExceptionUtil.createErrorResponseMessage(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handleIllegalArgumentException(IllegalArgumentException e) {
        logger.error("IllegalArgumentException: {}", e.getMessage());
        return ExceptionUtil.createErrorResponseMessage(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<?> handleIllegalStateException(IllegalStateException ex) {
        logger.error("IllegalStateException: {}", ex.getMessage());
        return ExceptionUtil.createErrorResponseMessage(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<?> handleNullPointerException(NullPointerException e) {
        logger.error("NullPointerException: {}", e.getMessage(), e);
        return ExceptionUtil.createErrorResponseMessage(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGlobalException(Exception ex) {
        logger.error("Unhandled Exception: {}", ex.getMessage(), ex);
        return ExceptionUtil.createErrorResponseMessage("An unexpected error occurred: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // Uncomment if needed for BadCredentialsException (e.g., Spring Security)
    /*
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<?> handleBadCredentialsException(BadCredentialsException ex) {
        log.error("Bad credentials: {}", ex.getMessage(), ex);
        return ExceptionUtil.createErrorResponseMessage("Incorrect username or password", HttpStatus.UNAUTHORIZED);
    }
    */
}