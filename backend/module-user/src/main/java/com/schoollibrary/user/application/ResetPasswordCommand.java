package com.schoollibrary.user.application;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Command for completing a password reset.
 * Contains the new password and user identification.
 */
@Data
public class ResetPasswordCommand {

    /**
     * The email address of the user resetting the password.
     */
    @NotBlank(message = "E-Mail ist erforderlich")
    @Email(message = "Ung ültige E-Mail-Adresse")
    private String email;

    /**
     * The new password to set.
     */
    @NotBlank(message = "Neues Passwort ist erforderlich")
    @Size(min = 8, message = "Passwort muss mindestens 8 Zeichen lang sein")
    private String newPassword;

    /**
     * Optional reset token (for app-managed flow, not used with Keycloak built-in
     * flow).
     */
    private String token;
}
