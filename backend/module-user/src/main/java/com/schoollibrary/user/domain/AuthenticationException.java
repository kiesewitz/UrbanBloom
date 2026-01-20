package com.schoollibrary.user.domain;

/**
 * Exception thrown when authentication fails.
 * This is a domain exception that wraps authentication-related errors.
 */
public class AuthenticationException extends RuntimeException {

    public AuthenticationException(String message) {
        super(message);
    }

    public AuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Creates an exception for invalid credentials.
     */
    public static AuthenticationException invalidCredentials() {
        return new AuthenticationException("Ungültige E-Mail-Adresse oder Passwort.");
    }

    /**
     * Creates an exception for invalid/expired refresh token.
     */
    public static AuthenticationException invalidRefreshToken() {
        return new AuthenticationException("Ungültige oder abgelaufene Sitzung.");
    }

    /**
     * Creates an exception for identity provider communication failure.
     */
    public static AuthenticationException providerError(String message, Throwable cause) {
        return new AuthenticationException("Fehler beim Identitätsanbieter: " + message, cause);
    }
}
