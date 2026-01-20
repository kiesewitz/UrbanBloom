package com.schoollibrary.user.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Request DTO for token refresh endpoint.
 * Property names follow OpenAPI spec (snake_case in JSON).
 */
@Data
public class RefreshRequestDto {

    @NotBlank(message = "Refresh token is required")
    @JsonProperty("refresh_token")
    private String refreshToken;
}
