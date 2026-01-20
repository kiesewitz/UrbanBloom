package com.schoollibrary.user.adapter.infrastructure.keycloak;

import com.schoollibrary.user.config.RegistrationConfigProperties;
import com.schoollibrary.user.domain.AuthenticationException;
import com.schoollibrary.user.domain.AuthenticationResult;
import com.schoollibrary.user.domain.IdentityProvider;
import com.schoollibrary.user.domain.PasswordResetException;
import jakarta.ws.rs.NotAuthorizedException;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.representations.AccessTokenResponse;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Keycloak implementation of the IdentityProvider interface.
 * Implements the Anti-Corruption Layer (ACL) pattern to abstract Keycloak
 * specifics.
 */
@Slf4j
@Component
public class KeycloakIdentityProvider implements IdentityProvider {

    private final Keycloak keycloak;
    private final RegistrationConfigProperties registrationConfig;

    @Value("${keycloak.admin.realm}")
    private String realm;

    @Value("${keycloak.admin.server-url}")
    private String serverUrl;

    @Value("${keycloak.token.client-id:schoollibrary-app}")
    private String tokenClientId;

    @Value("${keycloak.token.client-secret:}")
    private String tokenClientSecret;

    public KeycloakIdentityProvider(Keycloak keycloak, RegistrationConfigProperties registrationConfig) {
        this.keycloak = keycloak;
        this.registrationConfig = registrationConfig;
    }

    @Override
    public String createUser(String email, String password, String firstName, String lastName,
            Map<String, List<String>> attributes) {
        UserRepresentation user = new UserRepresentation();
        user.setUsername(email);
        user.setEmail(email);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEnabled(true);
        user.setEmailVerified(!registrationConfig.isEmailVerificationRequired());
        user.setAttributes(attributes);

        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(password);
        credential.setTemporary(false);
        user.setCredentials(Collections.singletonList(credential));

        Response response = keycloak.realm(realm).users().create(user);

        if (response.getStatus() == 201) {
            String userId = response.getLocation().getPath().replaceAll(".*/([^/]+)$", "$1");
            log.info("Successfully created user {} in Keycloak with ID {}", email, userId);
            return userId;
        } else {
            String errorMsg = String.format("Fehler beim Erstellen des Benutzers in Keycloak. Status: %s, Error: %s",
                    response.getStatus(), response.readEntity(String.class));
            log.error(errorMsg);
            throw new RuntimeException(errorMsg);
        }
    }

    @Override
    public boolean isEmailRegistered(String email) {
        List<UserRepresentation> users = keycloak.realm(realm).users().search(email, true);
        return !users.isEmpty();
    }

    @Override
    public void assignRole(String userId, String roleName) {
        var roleRepresentation = keycloak.realm(realm).roles().get(roleName).toRepresentation();
        keycloak.realm(realm).users().get(userId).roles().realmLevel()
                .add(Collections.singletonList(roleRepresentation));
        log.info("Assigned role {} to user {}", roleName, userId);
    }

    @Override
    public void sendVerificationEmail(String userId) {
        try {
            keycloak.realm(realm).users().get(userId).sendVerifyEmail();
            log.info("Triggered verification email for user {}", userId);
        } catch (Exception e) {
            log.warn("Failed to send verification email for user {}: {}", userId, e.getMessage());
        }
    }

    @Override
    public AuthenticationResult authenticateWithCredentials(String email, String password) {
        log.debug("Attempting to authenticate user: {}", email);

        try (Keycloak userKeycloak = KeycloakBuilder.builder()
                .serverUrl(serverUrl)
                .realm(realm)
                .grantType(OAuth2Constants.PASSWORD)
                .clientId(tokenClientId)
                .clientSecret(tokenClientSecret)
                .username(email)
                .password(password)
                .build()) {

            AccessTokenResponse tokenResponse = userKeycloak.tokenManager().getAccessToken();

            if (tokenResponse != null) {
                log.info("Successfully authenticated user: {}", email);
                return new AuthenticationResult(
                        tokenResponse.getToken(),
                        tokenResponse.getRefreshToken(),
                        tokenResponse.getExpiresIn(),
                        tokenResponse.getRefreshExpiresIn(),
                        tokenResponse.getTokenType());
            } else {
                throw AuthenticationException.invalidCredentials();
            }
        } catch (NotAuthorizedException e) {
            log.warn("Invalid credentials for user: {}", email);
            throw AuthenticationException.invalidCredentials();
        } catch (Exception e) {
            log.error("Unexpected error during authentication", e);
            throw AuthenticationException.providerError(e.getMessage(), e);
        }
    }

    @Override
    public AuthenticationResult refreshToken(String refreshToken) {
        log.debug("Attempting to refresh token");

        String tokenUrl = String.format("%s/realms/%s/protocol/openid-connect/token", serverUrl, realm);
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "refresh_token");
        body.add("client_id", tokenClientId);
        if (tokenClientSecret != null && !tokenClientSecret.isBlank()) {
            body.add("client_secret", tokenClientSecret);
        }
        body.add("refresh_token", refreshToken);

        try {
            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);
            ResponseEntity<KeycloakTokenResponse> response = restTemplate.exchange(
                    tokenUrl,
                    HttpMethod.POST,
                    request,
                    KeycloakTokenResponse.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                KeycloakTokenResponse tokenResponse = response.getBody();
                log.info("Successfully refreshed token");
                return new AuthenticationResult(
                        tokenResponse.accessToken(),
                        tokenResponse.refreshToken(),
                        tokenResponse.expiresIn(),
                        tokenResponse.refreshExpiresIn(),
                        tokenResponse.tokenType());
            } else {
                throw AuthenticationException.invalidRefreshToken();
            }
        } catch (HttpClientErrorException.BadRequest | HttpClientErrorException.Unauthorized e) {
            log.warn("Invalid or expired refresh token");
            throw AuthenticationException.invalidRefreshToken();
        } catch (Exception e) {
            log.error("Unexpected error during token refresh", e);
            throw AuthenticationException.providerError(e.getMessage(), e);
        }
    }

    @Override
    public void sendPasswordResetEmail(String email) {
        log.debug("Sending password reset email to: {}", email);

        try {
            // 1. Search for user by email
            // UserRepresentation is a Keycloak Admin Client class
            List<UserRepresentation> users = keycloak.realm(realm)
                    .users()
                    .search(email, true); // exact match

            if (users.isEmpty()) {
                log.warn("Password reset requested for non-existent email: {}", email);
                // Security best practice: don't reveal if email exists
                // Just log and return silently
                return;
            }

            // 2. Get user ID
            UserRepresentation user = users.get(0);
            String userId = user.getId();

            // 3. Trigger Keycloak's password reset email
            // executeActionsEmail sends an UPDATE_PASSWORD action email
            keycloak.realm(realm)
                    .users()
                    .get(userId)
                    .executeActionsEmail(Collections.singletonList("UPDATE_PASSWORD"));

            log.info("Password reset email triggered for user: {}", email);
        } catch (Exception e) {
            log.error("Failed to send password reset email", e);
            throw PasswordResetException
                    .providerError("Fehler beim Senden der Passwort-Reset-E-Mail: " + e.getMessage(), e);
        }
    }

    @Override
    public void resetUserPassword(String userId, String newPassword) {
        log.debug("Resetting password for user ID: {}", userId);

        try {
            // Create new password credential
            // CredentialRepresentation is a Keycloak Admin Client class
            CredentialRepresentation credential = new CredentialRepresentation();
            credential.setType(CredentialRepresentation.PASSWORD);
            credential.setValue(newPassword);
            credential.setTemporary(false);

            // Set password via Keycloak Admin API
            keycloak.realm(realm)
                    .users()
                    .get(userId)
                    .resetPassword(credential);

            log.info("Password reset successful for user ID: {}", userId);
        } catch (jakarta.ws.rs.NotFoundException e) {
            log.error("User not found for password reset: {}", userId);
            throw PasswordResetException.userNotFound();
        } catch (Exception e) {
            log.error("Failed to reset password", e);
            throw PasswordResetException.providerError("Fehler beim Zurücksetzen des Passworts: " + e.getMessage(), e);
        }
    }

    @Override
    public void deleteUser(String userId) {
        keycloak.realm(realm).users().get(userId).remove();
        log.info("Deleted user {} from Keycloak", userId);
    }
}
