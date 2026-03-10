package com.urbanbloom.user.api;

import com.urbanbloom.user.domain.UserGroup;
import com.urbanbloom.user.domain.UserProfile;
import com.urbanbloom.user.domain.UserRole;
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
                profile.isActive(),
                LocalDate.now(), // TODO: Add registration date to UserProfile
                LocalDateTime.now() // TODO: Add last login timestamp to UserProfile
        );
    }

    private UserGroup mapRoleToGroup(UserRole role) {
        return switch (role.name()) {
            case "CITIZEN", "VERIFIED_CITIZEN" -> UserGroup.CITIZEN;
            case "DISTRICT_MANAGER" -> UserGroup.DISTRICT_MANAGER;
            case "ADMIN" -> UserGroup.ADMIN;
            default -> UserGroup.CITIZEN;
        };
    }
}