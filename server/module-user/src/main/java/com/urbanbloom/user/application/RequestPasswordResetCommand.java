package com.urbanbloom.user.application;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Command for requesting a password reset.
 * Initiates the password reset flow by sending an email to the user.
 */
@Data
public class RequestPasswordResetCommand {

    /**
     * The email address for which to request password reset.
     */
    @NotBlank(message = "E-Mail ist erforderlich")
    @Email(message = "Ungültige E-Mail-Adresse")
    private String email;
}
