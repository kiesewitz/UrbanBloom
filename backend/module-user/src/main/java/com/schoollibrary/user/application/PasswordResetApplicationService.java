package com.schoollibrary.user.application;

import com.schoollibrary.user.domain.Email;
import com.schoollibrary.user.domain.IdentityProvider;
import com.schoollibrary.user.domain.PasswordResetCompleted;
import com.schoollibrary.user.domain.PasswordResetException;
import com.schoollibrary.user.domain.PasswordResetRequested;
import com.schoollibrary.user.domain.UserProfile;
import com.schoollibrary.user.domain.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

/**
 * Application service for password reset use cases.
 * Orchestrates password reset request and completion flows.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PasswordResetApplicationService {

    private final IdentityProvider identityProvider;
    private final UserProfileRepository userProfileRepository;
    private final ApplicationEventPublisher eventPublisher;

    /**
     * Requests a password reset for the given email address.
     * Sends a password reset email using Keycloak's built-in flow.
     *
     * @param command the request password reset command
     * @throws PasswordResetException if sending the email fails
     */
    @Transactional
    public void requestPasswordReset(RequestPasswordResetCommand command) {
        log.info("Processing password reset request for email: {}", command.getEmail());

        try {
            // Trigger password reset email via Keycloak
            // Keycloak handles: user lookup, token generation, and email sending
            identityProvider.sendPasswordResetEmail(command.getEmail());

            // Publish domain event for audit logging
            Instant now = Instant.now();
            Instant expiresAt = now.plus(1, ChronoUnit.HOURS); // Keycloak default is ~1 hour
            PasswordResetRequested event = new PasswordResetRequested(
                    command.getEmail(),
                    now,
                    expiresAt);
            eventPublisher.publishEvent(event);

            log.info("Password reset request processed successfully for email: {}", command.getEmail());
        } catch (PasswordResetException e) {
            log.warn("Password reset request failed for email {}: {}", command.getEmail(), e.getMessage());
            throw e;
        }
    }

    /**
     * Resets the password for a user.
     * Validates the user exists in our repository and sets the new password via
     * Keycloak.
     *
     * @param command the reset password command
     * @throws PasswordResetException if user not found or password reset fails
     */
    @Transactional
    public void resetPassword(ResetPasswordCommand command) {
        log.info("Processing password reset completion for email: {}", command.getEmail());

        try {
            // 1. Find user profile by email to get the Keycloak user ID
            Email emailVO = new Email(command.getEmail());
            UserProfile userProfile = userProfileRepository.findByEmail(emailVO)
                    .orElseThrow(() -> {
                        log.warn("User profile not found for email: {}", command.getEmail());
                        return PasswordResetException.userNotFound();
                    });

            String keycloakUserId = userProfile.getExternalUserId().getValue();

            // 2. Reset password via Keycloak Admin API
            // Keycloak validates the token internally (if using Keycloak flow)
            identityProvider.resetUserPassword(keycloakUserId, command.getNewPassword());

            // 3. Publish domain event for audit logging
            PasswordResetCompleted event = new PasswordResetCompleted(
                    keycloakUserId,
                    command.getEmail(),
                    Instant.now());
            eventPublisher.publishEvent(event);

            log.info("Password reset completed successfully for email: {}", command.getEmail());
        } catch (PasswordResetException e) {
            log.warn("Password reset failed for email {}: {}", command.getEmail(), e.getMessage());
            throw e;
        }
    }
}
