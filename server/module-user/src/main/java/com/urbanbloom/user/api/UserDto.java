package com.urbanbloom.user.api;

import com.urbanbloom.user.domain.UserGroup;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO for user profile information.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

    private String userId;
    private String externalUserId;
    private String firstName;
    private String lastName;
    private String email;
    private UserGroup userGroup;
    private Boolean isActive;
    private LocalDate registrationDate;
    private LocalDateTime lastLoginAt;
}