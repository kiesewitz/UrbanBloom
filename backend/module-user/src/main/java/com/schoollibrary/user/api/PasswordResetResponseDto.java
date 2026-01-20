package com.schoollibrary.user.api;

/**
 * DTO for password reset response.
 * Contains a success or informational message.
 */
public record PasswordResetResponseDto(
        String message) {
}
