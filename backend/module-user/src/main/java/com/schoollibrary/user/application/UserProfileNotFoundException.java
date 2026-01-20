package com.schoollibrary.user.application;

/**
 * Exception thrown when a user profile is not found.
 */
public class UserProfileNotFoundException extends RuntimeException {

    public UserProfileNotFoundException(String message) {
        super(message);
    }

    public UserProfileNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}