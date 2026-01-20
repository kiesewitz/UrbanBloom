package com.schoollibrary.user.application;

/**
 * Exception thrown when user registration fails.
 */
public class RegistrationException extends RuntimeException {
    public RegistrationException(String message) {
        super(message);
    }

    public RegistrationException(String message, Throwable cause) {
        super(message, cause);
    }
}
