package com.schoollibrary.user.application;

import com.schoollibrary.user.config.RegistrationConfigProperties;
import com.schoollibrary.user.domain.AuthenticationException;
import com.schoollibrary.user.domain.AuthenticationResult;
import com.schoollibrary.user.domain.IdentityProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Application service for authentication use cases.
 * Orchestrates login, token refresh, and allowed domains retrieval.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationApplicationService {

    private final IdentityProvider identityProvider;
    private final RegistrationConfigProperties registrationConfig;

    /**
     * Authenticates a user with email and password.
     *
     * @param command the login command
     * @return AuthenticationResult containing tokens
     * @throws AuthenticationException if authentication fails
     */
    public AuthenticationResult login(LoginCommand command) {
        log.info("Processing login request for email: {}", command.getEmail());

        try {
            AuthenticationResult result = identityProvider.authenticateWithCredentials(
                    command.getEmail(),
                    command.getPassword());
            log.info("Login successful for email: {}", command.getEmail());
            return result;
        } catch (AuthenticationException e) {
            log.warn("Login failed for email {}: {}", command.getEmail(), e.getMessage());
            throw e;
        }
    }

    /**
     * Refreshes tokens using a refresh token.
     *
     * @param command the refresh token command
     * @return AuthenticationResult containing new tokens
     * @throws AuthenticationException if refresh fails
     */
    public AuthenticationResult refreshToken(RefreshTokenCommand command) {
        log.debug("Processing token refresh request");

        try {
            AuthenticationResult result = identityProvider.refreshToken(command.getRefreshToken());
            log.debug("Token refresh successful");
            return result;
        } catch (AuthenticationException e) {
            log.warn("Token refresh failed: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Returns the list of allowed email domains for registration.
     *
     * @return list of allowed domain strings
     */
    public List<String> getAllowedDomains() {
        return registrationConfig.getAllowedDomains();
    }
}
