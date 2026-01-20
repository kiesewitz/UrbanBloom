package com.schoollibrary.user.domain;

/**
 * Domain exception for password reset operations.
 * Thrown when password reset-related errors occur.
 */
public class PasswordResetException extends RuntimeException {

    private PasswordResetException(String message) {
        super(message);
    }

    private PasswordResetException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Creates an exception for invalid or expired reset tokens.
     *
     * @return PasswordResetException
     */
    public static PasswordResetException invalidToken() {
        return new PasswordResetException("Ungültiger oder abgelaufener Reset-Token.");
    }

    /**
     * Creates an exception for tokens that have already been used.
     *
     * @return PasswordResetException
     */
    public static PasswordResetException tokenAlreadyUsed() {
        return new PasswordResetException("Dieser Reset-Token wurde bereits verwendet.");
    }

    /**
     * Creates an exception for when a user is not found.
     *
     * @return PasswordResetException
     */
    public static PasswordResetException userNotFound() {
        return new PasswordResetException("Benutzer nicht gefunden.");
    }

    /**
     * Creates an exception for identity provider errors.
     *
     * @param message the error message
     * @return PasswordResetException
     */
    public static PasswordResetException providerError(String message) {
        return new PasswordResetException(message);
    }

    /**
     * Creates an exception for identity provider errors with a cause.
     *
     * @param message the error message
     * @param cause   the underlying cause
     * @return PasswordResetException
     */
    public static PasswordResetException providerError(String message, Throwable cause) {
        return new PasswordResetException(message, cause);
    }
}
