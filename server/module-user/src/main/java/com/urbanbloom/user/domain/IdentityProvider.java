package com.urbanbloom.user.domain;

import java.util.List;
import java.util.Map;

/**
 * Interface to abstract interaction with an external Identity Provider (e.g., Keycloak).
 */
public interface IdentityProvider {

    /**
     * Creates a new user in the identity provider (default realm).
     */
    String createUser(String email, String password, String firstName, String lastName,
            Map<String, List<String>> attributes);

    /**
     * Checks if a user with the given email already exists (default realm).
     */
    boolean isEmailRegistered(String email);

    /**
     * Assigns a role to a user (default realm).
     */
    void assignRole(String userId, String roleName);

    /**
     * Triggers the verification email flow for a user (default realm).
     */
    void sendVerificationEmail(String userId);

    /**
     * Authenticates a user with email and password credentials in a specific realm and client.
     */
    AuthenticationResult authenticate(String email, String password, String realm, String clientId);

    /**
     * Refreshes authentication tokens using a valid refresh token in a specific realm and client.
     */
    AuthenticationResult refreshToken(String refreshToken, String realm, String clientId);

    /**
     * Checks if the user associated with the token has the given role.
     */
    boolean hasRole(String accessToken, String roleName);

    /**
     * Revokes a refresh token (logs out the user).
     */
    void logout(String refreshToken, String realm, String clientId);

    /**
     * Sends a password reset email (default realm).
     */
    void sendPasswordResetEmail(String email);

    /**
     * Resets the password for a user using Keycloak Admin API (default realm).
     */
    void resetUserPassword(String userId, String newPassword);

    /**
     * Deletes a user (default realm).
     */
    void deleteUser(String userId);
}
