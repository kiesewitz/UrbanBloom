package com.schoollibrary.user.application;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Result of user registration.
 * Contains information about the registered user.
 */
@Data
@AllArgsConstructor
public class RegistrationResult {
    private String userId;
    private String email;
    private String message;
    private boolean verificationRequired;
}
