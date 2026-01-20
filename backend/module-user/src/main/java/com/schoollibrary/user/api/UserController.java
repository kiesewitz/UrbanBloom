package com.schoollibrary.user.api;

import com.schoollibrary.user.application.UserProfileApplicationService;
import com.schoollibrary.user.application.UserProfileNotFoundException;
import com.schoollibrary.user.domain.UserProfile;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST Controller for user profile endpoints.
 * Requires authentication for all operations.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserProfileApplicationService userProfileApplicationService;
    private final UserProfileMapper userProfileMapper;

    /**
     * Gets the current authenticated user's profile.
     *
     * @return the user profile
     */
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUserProfile() {
        log.debug("Get current user profile request received");

        try {
            UserProfile profile = userProfileApplicationService.getCurrentUserProfile();
            UserDto dto = userProfileMapper.toDto(profile);
            return ResponseEntity.ok(dto);
        } catch (UserProfileNotFoundException e) {
            log.warn("User profile not found: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Unexpected error getting user profile", e);
            return ResponseEntity.internalServerError()
                    .body(new ErrorResponseDto("Failed to get user profile"));
        }
    }
}