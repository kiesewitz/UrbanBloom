package com.schoollibrary.user.domain;

import lombok.Value;

import java.time.Instant;

/**
 * Domain event published when a password reset is requested.
 * This event can be used for audit logging or triggering additional business
 * logic.
 */
@Value
public class PasswordResetRequested {

    /**
     * The email address for which password reset was requested.
     */
    String email;

    /**
     * Timestamp when the password reset was requested.
     */
    Instant requestedAt;

    /**
     * Timestamp when the reset token will expire.
     */
    Instant expiresAt;

    /**
     * Creates a new PasswordResetRequested event.
     *
     * @param email       the user's email
     * @param requestedAt when the request occurred
     * @param expiresAt   when the token expires
     */
    public PasswordResetRequested(String email, Instant requestedAt, Instant expiresAt) {
        this.email = email;
        this.requestedAt = requestedAt;
        this.expiresAt = expiresAt;
    }
}
