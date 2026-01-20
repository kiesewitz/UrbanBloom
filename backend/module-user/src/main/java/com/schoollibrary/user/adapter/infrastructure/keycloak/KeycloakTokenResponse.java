package com.schoollibrary.user.adapter.infrastructure.keycloak;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Internal DTO for Keycloak token endpoint response.
 * Uses Jackson annotations to map snake_case JSON properties.
 */
record KeycloakTokenResponse(
        @JsonProperty("access_token") String accessToken,
        @JsonProperty("refresh_token") String refreshToken,
        @JsonProperty("expires_in") long expiresIn,
        @JsonProperty("refresh_expires_in") long refreshExpiresIn,
        @JsonProperty("token_type") String tokenType,
        @JsonProperty("session_state") String sessionState,
        @JsonProperty("scope") String scope) {
}
