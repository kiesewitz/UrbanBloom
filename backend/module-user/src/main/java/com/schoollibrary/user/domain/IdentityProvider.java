package com.schoollibrary.user.domain;

import java.util.List;
import java.util.Map;

/**
 * Interface to abstract interaction with an external Identity Provider (e.g.,
 * Keycloak).
 * This follows the Anti-Corruption Layer (ACL) pattern to avoid vendor lock-in.
 */
public interface IdentityProvider {

    /**
     * Creates a new user in the identity provider.
     *
     * @param email      The user's email (will be used as username)
     * @param password   The plain text password (will be hashed by IdP)
     * @param firstName  First name of the user
     * @param lastName   Last name of the user
     * @param attributes Custom attributes (e.g., studentId, schoolClass)
     * @return The unique ID of the created user in the IdP
     */
    String createUser(String email, String password, String firstName, String lastName,
            Map<String, List<String>> attributes);

    /**
     * Checks if a user with the given email already exists.
     *
     * @param email The email to check
     * @return true if the email is already registered
     */
    boolean isEmailRegistered(String email);

    /**
     * Assigns a role to a user.
     *
     * @param userId   The IdP user ID
     * @param roleName The name of the role (e.g., "STUDENT", "TEACHER")
     */
    void assignRole(String userId, String roleName);

    /**
     * Triggers the verification email flow for a user.
     *
     * @param userId The IdP user ID
     */
    void sendVerificationEmail(String userId);

    /**
     * Authenticates a user with email and password credentials.
     * Returns tokens if authentication is successful.
     *
     * @param email    The user's email address
     * @param password The user's password
     * @return AuthenticationResult containing access and refresh tokens
     * @throws AuthenticationException if authentication fails
     */
    AuthenticationResult authenticateWithCredentials(String email, String password);

    /**
     * Refreshes authentication tokens using a valid refresh token.
     *
     * @param refreshToken The refresh token to exchange
     * @return AuthenticationResult containing new access and refresh tokens
     * @throws AuthenticationException if refresh token is invalid or expired
     */
    AuthenticationResult refreshToken(String refreshToken);

    /**
     * Sends a password reset email to the user with the given email address.
     * Uses Keycloak's built-in password reset email flow.
     *
     * @param email The email address of the user
     * @throws PasswordResetException if user not found or email sending fails
     */
    void sendPasswordResetEmail(String email);

    /**
     * Resets the password for a user using Keycloak Admin API.
     * This is called after the user has received the reset token via email
     * and submitted the new password.
     *
     * @param userId      The Keycloak user ID
     * @param newPassword The new password to set
     * @throws PasswordResetException if password reset fails
     */
    void resetUserPassword(String userId, String newPassword);

    /**
     * Deletes a user (e.g., for cleanup in tests or rollback).
     *
     * @param userId The IdP user ID
     */
    void deleteUser(String userId);
}
