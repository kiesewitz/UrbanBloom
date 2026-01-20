package com.schoollibrary.user.domain;

import lombok.Value;

import java.time.Instant;

/**
 * Domain event published when a password reset is completed successfully.
 * This event can be used for audit logging or triggering additional business
 * logic.
 */
@Value
public class PasswordResetCompleted {

    /**
     * The Keycloak user ID.
     */
    String userId;

    /**
     * The email address of the user whose password was reset.
     */
    String email;

    /**
     * Timestamp when the password reset was completed.
     */
    Instant completedAt;

    /**
     * Creates a new PasswordResetCompleted event.
     *
     * @param userId      the Keycloak user ID
     * @param email       the user's email
     * @param completedAt when the reset was completed
     */
    public PasswordResetCompleted(String userId, String email, Instant completedAt) {
        this.userId = userId;
        this.email = email;
        this.completedAt = completedAt;
    }
}
