package com.urbanbloom.user.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for user registration response.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegistrationResponseDto {
    private String userId;
    private String externalId;
    private String message;
    private boolean verificationRequired = true;
}
