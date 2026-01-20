package com.schoollibrary.user.api;

import com.schoollibrary.user.domain.UserGroup;
import com.schoollibrary.user.domain.UserProfile;
import com.schoollibrary.user.domain.UserRole;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Mapper for converting UserProfile domain objects to UserDto.
 */
@Component
public class UserProfileMapper {

    /**
     * Converts a UserProfile to UserDto.
     *
     * @param profile the user profile
     * @return the user DTO
     */
    public UserDto toDto(UserProfile profile) {
        return new UserDto(
                profile.getId(),
                profile.getExternalUserId().getValue(),
                profile.getUserName().getFirstName(),
                profile.getUserName().getLastName(),
                profile.getEmail().getValue(),
                mapRoleToGroup(profile.getRole()),
                getBorrowingLimit(profile.getRole()),
                profile.isActive(),
                LocalDate.now(), // TODO: Add registration date to UserProfile
                LocalDateTime.now() // TODO: Add last login timestamp to UserProfile
        );
    }

    private UserGroup mapRoleToGroup(UserRole role) {
        return switch (role.name()) {
            case "STUDENT" -> UserGroup.STUDENT;
            case "TEACHER" -> UserGroup.TEACHER;
            case "LIBRARIAN" -> UserGroup.LIBRARIAN;
            case "ADMIN" -> UserGroup.LIBRARIAN; // Map ADMIN to LIBRARIAN for API
            default -> UserGroup.STUDENT;
        };
    }

    private Integer getBorrowingLimit(UserRole role) {
        return switch (role.name()) {
            case "STUDENT" -> 5;
            case "TEACHER" -> 10;
            case "LIBRARIAN" -> 20;
            case "ADMIN" -> 20;
            default -> 5;
        };
    }
}