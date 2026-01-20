package com.schoollibrary.user.api;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * DTO for email availability check response.
 */
@Data
@AllArgsConstructor
public class EmailAvailabilityDto {
    private String email;
    private boolean available;
}
