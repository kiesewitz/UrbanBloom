package com.urbanbloom.user.api;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Request DTO for logout endpoint.
 */
@Data
public class LogoutRequestDto {
    @NotBlank(message = "Refresh token is required")
    private String refreshToken;
}
