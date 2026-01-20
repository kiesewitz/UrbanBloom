package com.schoollibrary.user.api;

import com.schoollibrary.user.domain.UserGroup;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO for user profile information.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

    private String userId;
    private String schoolIdentity;
    private String firstName;
    private String lastName;
    private String email;
    private UserGroup userGroup;
    private Integer borrowingLimit;
    private Boolean isActive;
    private LocalDate registrationDate;
    private LocalDateTime lastLoginAt;
}