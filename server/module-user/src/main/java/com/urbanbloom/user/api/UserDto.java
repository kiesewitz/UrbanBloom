package com.urbanbloom.user.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO for user profile information (UserDetail in OpenAPI).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private String id;
    private String externalId;
    private String email;
    private String firstName;
    private String lastName;
    private String status;
    private List<String> roles;
    private LocalDateTime registeredAt;
}
