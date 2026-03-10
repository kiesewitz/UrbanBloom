package com.urbanbloom.user.api;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * DTO for password reset request.
 * Used to request a password reset email.
 */
public record PasswordResetRequestDto(
        @NotBlank(message = "E-Mail ist erforderlich") @Email(message = "Ungültige E-Mail-Adresse") String email) {
}
