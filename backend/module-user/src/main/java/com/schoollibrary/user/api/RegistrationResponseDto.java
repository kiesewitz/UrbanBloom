package com.schoollibrary.user.api;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * DTO for user registration response.
 */
@Data
@AllArgsConstructor
public class RegistrationResponseDto {
    private String userId;
    private String email;
    private String message;
    private boolean verificationRequired;
}
