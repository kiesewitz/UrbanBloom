package com.schoollibrary.user.api;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO for password reset completion.
 * Contains the new password and user identification.
 */
public record PasswordResetDto(
        @NotBlank(message = "E-Mail ist erforderlich") @Email(message = "Ungültige E-Mail-Adresse") String email,

        @NotBlank(message = "Neues Passwort ist erforderlich") @Size(min = 8, message = "Passwort muss mindestens 8 Zeichen lang sein") String newPassword,

        String token // Optional: for app-managed tokens (not used with Keycloak flow)
) {
}
