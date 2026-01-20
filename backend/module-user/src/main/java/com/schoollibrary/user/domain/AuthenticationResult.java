package com.schoollibrary.user.domain;

/**
 * Domain Value Object representing the result of authentication.
 * Contains JWT tokens and their expiration times.
 * 
 * <p>
 * This is a pure domain object with no framework dependencies.
 * </p>
 *
 * @param accessToken      JWT access token
 * @param refreshToken     JWT refresh token
 * @param expiresIn        Access token expiration time in seconds
 * @param refreshExpiresIn Refresh token expiration time in seconds
 * @param tokenType        Token type (e.g., "Bearer")
 */
public record AuthenticationResult(
        String accessToken,
        String refreshToken,
        long expiresIn,
        long refreshExpiresIn,
        String tokenType) {
    /**
     * Creates an AuthenticationResult with validation.
     */
    public AuthenticationResult {
        if (accessToken == null || accessToken.isBlank()) {
            throw new IllegalArgumentException("Access token cannot be null or blank");
        }
        if (refreshToken == null || refreshToken.isBlank()) {
            throw new IllegalArgumentException("Refresh token cannot be null or blank");
        }
        if (tokenType == null || tokenType.isBlank()) {
            tokenType = "Bearer";
        }
    }

    /**
     * Checks if the access token is about to expire within the given seconds.
     *
     * @param thresholdSeconds seconds before expiration to consider "about to
     *                         expire"
     * @return true if token expires within threshold
     */
    public boolean isAccessTokenExpiringSoon(long thresholdSeconds) {
        return expiresIn <= thresholdSeconds;
    }
}
