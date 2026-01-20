package com.schoollibrary.user.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Error response DTO for authentication endpoints.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponseDto {

    private String message;
    private String[] details;

    public ErrorResponseDto(String message) {
        this.message = message;
        this.details = new String[0];
    }
}
