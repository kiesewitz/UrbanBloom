package com.urbanbloom.user.application;

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
    private String externalId;
    private String message;
    private boolean verificationRequired;
    
    public RegistrationResult(String userId, String externalId, String message) {
        this(userId, externalId, message, true);
    }
}
