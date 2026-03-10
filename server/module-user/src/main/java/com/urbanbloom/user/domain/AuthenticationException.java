package com.urbanbloom.user.domain;

import lombok.Getter;

/**
 * Exception thrown when authentication fails.
 * This is a domain exception that wraps authentication-related errors.
 */
@Getter
public class AuthenticationException extends RuntimeException {

    private final String errorCode;

    public AuthenticationException(String message) {
        this(message, "AUTHENTICATION_FAILED");
    }

    public AuthenticationException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public AuthenticationException(String message, String errorCode, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    /**
     * Creates an exception for invalid credentials.
     */
    public static AuthenticationException invalidCredentials() {
        return new AuthenticationException("Ungültige E-Mail-Adresse oder Passwort.", "INVALID_CREDENTIALS");
    }

    /**
     * Creates an exception for account not verified.
     */
    public static AuthenticationException accountNotVerified() {
        return new AuthenticationException("E-Mail-Adresse nicht verifiziert. Bitte prüfe dein Postfach.", "ACCOUNT_NOT_VERIFIED");
    }

    /**
     * Creates an exception for disabled account.
     */
    public static AuthenticationException accountDisabled() {
        return new AuthenticationException("Konto ist deaktiviert. Bitte kontaktiere den Support.", "ACCOUNT_DISABLED");
    }

    /**
     * Creates an exception for password update required.
     */
    public static AuthenticationException updatePasswordRequired() {
        return new AuthenticationException("Passwortänderung erforderlich.", "UPDATE_PASSWORD_REQUIRED");
    }

    /**
     * Creates an exception for insufficient permissions/roles.
     */
    public static AuthenticationException forbidden() {
        return new AuthenticationException("Unzureichende Berechtigungen.", "FORBIDDEN");
    }

    /**
     * Creates an exception for invalid/expired refresh token.
     */
    public static AuthenticationException invalidRefreshToken() {
        return new AuthenticationException("Ungültige oder abgelaufene Sitzung.", "INVALID_REFRESH_TOKEN");
    }

    /**
     * Creates an exception for identity provider communication failure.
     */
    public static AuthenticationException providerError(String message, Throwable cause) {
        return new AuthenticationException("Fehler beim Identitätsanbieter: " + message, "PROVIDER_ERROR", cause);
    }
}
