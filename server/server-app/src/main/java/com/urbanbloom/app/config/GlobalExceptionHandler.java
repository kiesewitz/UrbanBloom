package com.urbanbloom.app.config;

import com.urbanbloom.user.api.ErrorResponseDto;
import com.urbanbloom.user.application.RegistrationException;
import com.urbanbloom.user.domain.AuthenticationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

/**
 * Global exception handler for the application.
 * Ensures consistent error responses in JSON format.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handle registration errors.
     */
    @ExceptionHandler(RegistrationException.class)
    public ResponseEntity<ErrorResponseDto> handleRegistrationException(RegistrationException ex) {
        log.warn("Registration error: {}", ex.getMessage());
        HttpStatus status = ex.getMessage().contains("bereits registriert") ? HttpStatus.CONFLICT : HttpStatus.BAD_REQUEST;
        String errorCode = status == HttpStatus.CONFLICT ? "CONFLICT" : "REGISTRATION_FAILED";
        return ResponseEntity
                .status(status)
                .body(new ErrorResponseDto(errorCode, ex.getMessage()));
    }

    /**
     * Handle authentication errors.
     */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponseDto> handleAuthenticationException(AuthenticationException ex) {
        log.warn("Authentication error: {}", ex.getMessage());
        HttpStatus status = "FORBIDDEN".equals(ex.getErrorCode()) ? HttpStatus.FORBIDDEN : HttpStatus.UNAUTHORIZED;
        return ResponseEntity
                .status(status)
                .body(new ErrorResponseDto(ex.getErrorCode(), ex.getMessage()));
    }

    /**
     * Handle validation errors (e.g. @Valid failures).
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDto> handleValidationError(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .collect(Collectors.joining(", "));

        log.warn("Validation error: {}", message);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponseDto("VALIDATION_ERROR", message));
    }

    /**
     * Handle generic exceptions.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleGenericException(Exception ex) {
        log.error("Unexpected error", ex);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponseDto("INTERNAL_ERROR", "An unexpected error occurred: " + ex.getMessage()));
    }
}
