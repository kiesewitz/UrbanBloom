package com.urbanbloom.user.api;

import com.urbanbloom.user.domain.UserProfile;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

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
                profile.getEmail().getValue(),
                profile.getUserName().getFirstName(),
                profile.getUserName().getLastName(),
                profile.isActive() ? "ENABLED" : "DISABLED",
                List.of(profile.getRole().name()),
                LocalDateTime.now() // TODO: Registration date in UserProfile
        );
    }
}
