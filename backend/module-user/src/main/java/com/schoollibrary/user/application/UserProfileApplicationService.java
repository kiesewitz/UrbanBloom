package com.schoollibrary.user.application;

import com.schoollibrary.user.domain.ExternalUserId;
import com.schoollibrary.user.domain.UserProfile;
import com.schoollibrary.user.domain.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

/**
 * Application service for user profile operations.
 */
@Service
@RequiredArgsConstructor
public class UserProfileApplicationService {

    private final UserProfileRepository userProfileRepository;

    /**
     * Gets the current authenticated user's profile.
     *
     * @return the user profile
     * @throws IllegalStateException if no authenticated user
     * @throws UserProfileNotFoundException if profile not found
     */
    public UserProfile getCurrentUserProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof Jwt jwt)) {
            throw new IllegalStateException("No authenticated user");
        }

        String subject = jwt.getSubject();
        ExternalUserId externalUserId = new ExternalUserId(subject);

        return userProfileRepository.findByExternalUserId(externalUserId)
                .orElseThrow(() -> new UserProfileNotFoundException("User profile not found for external user ID: " + subject));
    }
}