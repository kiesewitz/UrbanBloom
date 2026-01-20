package com.schoollibrary.user.application;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Command for token refresh.
 */
@Data
public class RefreshTokenCommand {

    @NotBlank(message = "Refresh token is required")
    private String refreshToken;
}
