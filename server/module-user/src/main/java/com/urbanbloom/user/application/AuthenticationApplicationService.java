package com.urbanbloom.user.application;

import com.urbanbloom.user.config.RegistrationConfigProperties;
import com.urbanbloom.user.domain.AuthenticationException;
import com.urbanbloom.user.domain.AuthenticationResult;
import com.urbanbloom.user.domain.IdentityProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Application service for authentication use cases.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationApplicationService {

    private final IdentityProvider identityProvider;
    private final RegistrationConfigProperties registrationConfig;
    private final com.urbanbloom.user.domain.UserProfileRepository userProfileRepository;

    @Value("${keycloak.realm.mobile:urbanbloom-mobile}")
    private String mobileRealm;

    @Value("${keycloak.realm.admin:urbanbloom-admin}")
    private String adminRealm;

    @Value("${keycloak.token.client-id.mobile:urbanbloom-mobile-app}")
    private String mobileClientId;

    @Value("${keycloak.token.client-id.admin:urbanbloom-admin-web}")
    private String adminClientId;

    /**
     * Authenticates a citizen for the mobile app.
     */
    public AuthenticationResult loginMobile(LoginCommand command) {
        log.info("Processing mobile login request for email: {}", command.getEmail());
        AuthenticationResult result = identityProvider.authenticate(command.getEmail(), command.getPassword(), mobileRealm, mobileClientId);
        
        // Auto-activate local profile if login was successful (implies email verified in Keycloak)
        try {
            userProfileRepository.findByEmail(new com.urbanbloom.user.domain.Email(command.getEmail()))
                .ifPresent(profile -> {
                    if (!profile.isActive()) {
                        log.info("Auto-activating local profile for user: {}", command.getEmail());
                        profile.markAsActive();
                        userProfileRepository.save(profile);
                    }
                });
        } catch (Exception e) {
            log.warn("Failed to auto-activate local profile for {}: {}", command.getEmail(), e.getMessage());
        }
        
        return result;
    }

    /**
     * Authenticates an admin for the web portal.
     * Validates that the user has the ADMIN role.
     */
    public AuthenticationResult loginAdmin(LoginCommand command) {
        log.info("Processing admin login request for email: {}", command.getEmail());
        
        AuthenticationResult result = identityProvider.authenticate(command.getEmail(), command.getPassword(), adminRealm, adminClientId);
        
        // Verify ADMIN role
        if (!identityProvider.hasRole(result.accessToken(), "ADMIN")) {
            log.warn("User {} authenticated but missing ADMIN role", command.getEmail());
            throw AuthenticationException.forbidden();
        }
        
        log.info("Admin login successful for email: {}", command.getEmail());
        return result;
    }

    /**
     * Refreshes tokens.
     */
    public AuthenticationResult refreshToken(RefreshTokenCommand command) {
        String realm = command.getRealm() != null ? command.getRealm() : mobileRealm;
        String clientId = adminRealm.equals(realm) ? adminClientId : mobileClientId;
        return identityProvider.refreshToken(command.getRefreshToken(), realm, clientId);
    }

    /**
     * Logs out the user by revoking the refresh token.
     */
    public void logout(LogoutCommand command) {
        String realm = command.getRealm() != null ? command.getRealm() : mobileRealm;
        String clientId = adminRealm.equals(realm) ? adminClientId : mobileClientId;
        identityProvider.logout(command.getRefreshToken(), realm, clientId);
    }

    /**
     * Returns the list of allowed email domains for registration.
     */
    public List<String> getAllowedDomains() {
        return registrationConfig.getAllowedDomains();
    }
}
