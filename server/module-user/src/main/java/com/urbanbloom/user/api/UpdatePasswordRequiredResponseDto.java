package com.urbanbloom.user.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO for admin login when password update is required.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePasswordRequiredResponseDto {
    private String error = "UPDATE_PASSWORD_REQUIRED";
    private String message;
    private String keycloakLoginUrl;
}
