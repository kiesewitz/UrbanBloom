package com.urbanbloom.user.adapter.infrastructure.keycloak;

import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.urbanbloom.user.config.RegistrationConfigProperties;
import com.urbanbloom.user.domain.AuthenticationException;
import com.urbanbloom.user.domain.AuthenticationResult;
import com.urbanbloom.user.domain.IdentityProvider;
import com.urbanbloom.user.domain.PasswordResetException;
import jakarta.annotation.PostConstruct;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.text.ParseException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Keycloak implementation of the IdentityProvider interface.
 */
@Slf4j
@Component
public class KeycloakIdentityProvider implements IdentityProvider {

    private final Keycloak keycloak;
    private final RegistrationConfigProperties registrationConfig;
    private final RestTemplate restTemplate;

    private final String adminRealm;
    private final String serverUrl;
    private final String clientSecret;

    public KeycloakIdentityProvider(
            Keycloak keycloak, 
            RegistrationConfigProperties registrationConfig,
            @Value("${keycloak.admin.realm}") String adminRealm,
            @Value("${keycloak.admin.server-url}") String serverUrl,
            @Value("${keycloak.admin.client-secret:8gNbhElYUd3WvfxwIeBBEWAho0tGURPW}") String clientSecret) {
        this.keycloak = keycloak;
        this.registrationConfig = registrationConfig;
        this.adminRealm = adminRealm;
        this.serverUrl = serverUrl;
        this.clientSecret = clientSecret;
        this.restTemplate = new RestTemplate();
        
        log.info("Initialized KeycloakIdentityProvider with Server: {}, Realm: {}, Secret length: {}", 
                serverUrl, adminRealm, clientSecret != null ? clientSecret.length() : 0);
    }

    @PostConstruct
    public void init() {
        log.info("KeycloakIdentityProvider PostConstruct - Client Secret configured: {}", 
                (clientSecret != null && !clientSecret.isEmpty()));
    }

    @Override
    public String createUser(String email, String password, String firstName, String lastName,
            Map<String, List<String>> attributes) {
        log.info("Creating user in Keycloak realm {}: {}", adminRealm, email);
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

        try (Response response = keycloak.realm(adminRealm).users().create(user)) {
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
    }

    @Override
    public boolean isEmailRegistered(String email) {
        List<UserRepresentation> users = keycloak.realm(adminRealm).users().search(email, true);
        return !users.isEmpty();
    }

    @Override
    public void assignRole(String userId, String roleName) {
        log.info("Assigning role {} to user {} in realm {}", roleName, userId, adminRealm);
        var roleRepresentation = keycloak.realm(adminRealm).roles().get(roleName).toRepresentation();
        keycloak.realm(adminRealm).users().get(userId).roles().realmLevel()
                .add(Collections.singletonList(roleRepresentation));
        log.info("Successfully assigned role {} to user {}", roleName, userId);
    }

    @Override
    public void sendVerificationEmail(String userId) {
        try {
            keycloak.realm(adminRealm).users().get(userId).sendVerifyEmail();
            log.info("Triggered verification email for user {}", userId);
        } catch (Exception e) {
            log.warn("Failed to send verification email for user {}: {}", userId, e.getMessage());
        }
    }

    @Override
    public AuthenticationResult authenticate(String email, String password, String realm, String clientId) {
        log.debug("Attempting to authenticate user: {} in realm: {} with client: {}", email, realm, clientId);

        String tokenUrl = String.format("%s/realms/%s/protocol/openid-connect/token", serverUrl, realm);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add(OAuth2Constants.GRANT_TYPE, OAuth2Constants.PASSWORD);
        body.add(OAuth2Constants.CLIENT_ID, clientId);
        
        // Use client secret if available (e.g. for server-side clients)
        // Note: in MS-01, both urbanbloom-mobile-app and urbanbloom-admin-web are configured as non-public clients in the export
        if (clientSecret != null && !clientSecret.isEmpty()) {
            body.add(OAuth2Constants.CLIENT_SECRET, clientSecret);
        }
        
        body.add(OAuth2Constants.USERNAME, email);
        body.add(OAuth2Constants.PASSWORD, password);

        try {
            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);
            ResponseEntity<KeycloakTokenResponse> response = restTemplate.postForEntity(
                    tokenUrl, request, KeycloakTokenResponse.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                KeycloakTokenResponse tokenResponse = response.getBody();
                return new AuthenticationResult(
                        tokenResponse.accessToken(),
                        tokenResponse.refreshToken(),
                        tokenResponse.expiresIn(),
                        tokenResponse.refreshExpiresIn(),
                        tokenResponse.tokenType());
            } else {
                throw AuthenticationException.invalidCredentials();
            }
        } catch (HttpClientErrorException e) {
            String errorBody = e.getResponseBodyAsString();
            log.warn("Auth error from Keycloak for {} in realm {}: {} - {}", email, realm, e.getStatusCode(), errorBody);
            
            if (e.getStatusCode() == HttpStatus.UNAUTHORIZED || e.getStatusCode() == HttpStatus.BAD_REQUEST) {
                if (errorBody.contains("invalid_grant")) {
                    if (errorBody.contains("Account not fully set up") || errorBody.contains("update_password")) {
                        throw AuthenticationException.updatePasswordRequired();
                    }
                    if (errorBody.contains("Account is disabled")) {
                        throw AuthenticationException.accountDisabled();
                    }
                    if (errorBody.contains("Email not verified")) {
                        throw AuthenticationException.accountNotVerified();
                    }
                }
                throw AuthenticationException.invalidCredentials();
            }
            throw AuthenticationException.providerError("Keycloak API error: " + e.getStatusCode(), e);
        } catch (Exception e) {
            log.error("Unexpected error during authentication for {}", email, e);
            throw AuthenticationException.providerError("Interner Authentifizierungsfehler", e);
        }
    }
            throw AuthenticationException.providerError(e.getMessage(), e);
        }
    }

    @Override
    public AuthenticationResult refreshToken(String refreshToken, String realm, String clientId) {
        log.debug("Attempting to refresh token in realm: {} with client: {}", realm, clientId);

        String tokenUrl = String.format("%s/realms/%s/protocol/openid-connect/token", serverUrl, realm);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add(OAuth2Constants.GRANT_TYPE, OAuth2Constants.REFRESH_TOKEN);
        body.add(OAuth2Constants.CLIENT_ID, clientId);
        if (clientSecret != null && !clientSecret.isEmpty()) {
            body.add(OAuth2Constants.CLIENT_SECRET, clientSecret);
        }
        body.add(OAuth2Constants.REFRESH_TOKEN, refreshToken);

        try {
            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);
            ResponseEntity<KeycloakTokenResponse> response = restTemplate.postForEntity(
                    tokenUrl, request, KeycloakTokenResponse.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                KeycloakTokenResponse tokenResponse = response.getBody();
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
            throw AuthenticationException.invalidRefreshToken();
        } catch (Exception e) {
            log.error("Unexpected error during token refresh", e);
            throw AuthenticationException.providerError(e.getMessage(), e);
        }
    }

    @Override
    public boolean hasRole(String accessToken, String roleName) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(accessToken);
            JWTClaimsSet claimsSet = signedJWT.getJWTClaimsSet();
            
            log.debug("Checking role {} in JWT claims: {}", roleName, claimsSet.toJSONObject());

            // 1. Check realm roles
            Map<String, Object> realmAccess = (Map<String, Object>) claimsSet.getClaim("realm_access");
            if (realmAccess != null) {
                List<String> roles = (List<String>) realmAccess.get("roles");
                if (roles != null && roles.contains(roleName)) {
                    log.debug("Role {} found in realm_access", roleName);
                    return true;
                }
            }

            // 2. Check resource access (client roles)
            Map<String, Object> resourceAccess = (Map<String, Object>) claimsSet.getClaim("resource_access");
            if (resourceAccess != null) {
                for (Object clientAccessObj : resourceAccess.values()) {
                    if (clientAccessObj instanceof Map) {
                        Map<String, Object> clientAccess = (Map<String, Object>) clientAccessObj;
                        List<String> roles = (List<String>) clientAccess.get("roles");
                        if (roles != null && roles.contains(roleName)) {
                            log.debug("Role {} found in resource_access", roleName);
                            return true;
                        }
                    }
                }
            }

            log.warn("Role {} not found in JWT. Available realm roles: {}", roleName, 
                    realmAccess != null ? realmAccess.get("roles") : "none");
            return false;
        } catch (ParseException | ClassCastException e) {
            log.error("Failed to parse access token for role check", e);
            return false;
        }
    }

    @Override
    public void logout(String refreshToken, String realm, String clientId) {
        log.info("Attempting to logout in realm: {} with client: {}", realm, clientId);

        String logoutUrl = String.format("%s/realms/%s/protocol/openid-connect/logout", serverUrl, realm);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add(OAuth2Constants.CLIENT_ID, clientId);
        if (clientSecret != null && !clientSecret.isEmpty()) {
            body.add(OAuth2Constants.CLIENT_SECRET, clientSecret);
        }
        body.add(OAuth2Constants.REFRESH_TOKEN, refreshToken);

        try {
            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);
            restTemplate.postForEntity(logoutUrl, request, Void.class);
            log.info("Logout successful");
        } catch (Exception e) {
            log.warn("Logout failed in Keycloak: {}", e.getMessage());
        }
    }

    @Override
    public void sendPasswordResetEmail(String email) {
        log.debug("Sending password reset email to: {}", email);

        try {
            List<UserRepresentation> users = keycloak.realm(adminRealm)
                    .users()
                    .search(email, true);

            if (users.isEmpty()) {
                log.warn("Password reset requested for non-existent email: {}", email);
                return;
            }

            UserRepresentation user = users.get(0);
            String userId = user.getId();

            keycloak.realm(adminRealm)
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
            CredentialRepresentation credential = new CredentialRepresentation();
            credential.setType(CredentialRepresentation.PASSWORD);
            credential.setValue(newPassword);
            credential.setTemporary(false);

            keycloak.realm(adminRealm)
                    .users()
                    .get(userId)
                    .resetPassword(credential);

            log.info("Password reset successful for user ID: {}", userId);
        } catch (Exception e) {
            log.error("Failed to reset password", e);
            throw PasswordResetException.providerError("Fehler beim Zurücksetzen des Passworts: " + e.getMessage(), e);
        }
    }

    @Override
    public void deleteUser(String userId) {
        keycloak.realm(adminRealm).users().get(userId).remove();
        log.info("Deleted user {} from Keycloak", userId);
    }
}
