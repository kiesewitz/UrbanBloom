package com.schoollibrary.user.api;

import com.schoollibrary.user.application.RegistrationException;
import com.schoollibrary.user.application.RegistrationResult;
import com.schoollibrary.user.application.UserRegistrationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for user registration.
 * Public endpoint - no authentication required.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class RegistrationController {

    private final UserRegistrationService registrationService;
    private final RegistrationMapper mapper;

    /**
     * Register a new user.
     *
     * @param request the registration request
     * @return registration response with user details
     */
    @PostMapping("/registration")
    public ResponseEntity<RegistrationResponseDto> register(@Valid @RequestBody RegistrationRequestDto request) {
        log.info("Registration request received for email: {}", request.getEmail());

        try {
            RegistrationResult result = registrationService.registerUser(mapper.toCommand(request));
            RegistrationResponseDto response = mapper.toDto(result);

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(response);
        } catch (RegistrationException e) {
            log.warn("Registration failed: {}", e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new RegistrationResponseDto(null, request.getEmail(), e.getMessage(), false));
        } catch (Exception e) {
            log.error("Unexpected error during registration", e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new RegistrationResponseDto(null, request.getEmail(),
                            "Registrierung aufgrund eines Serverfehlers fehlgeschlagen.", false));
        }
    }

    /**
     * Check if an email is available for registration.
     *
     * @param email the email to check
     * @return availability status
     */
    @GetMapping("/registration/check-email")
    public ResponseEntity<EmailAvailabilityDto> checkEmailAvailability(@RequestParam String email) {
        // This would use the IdentityProvider to check
        // Implementation can be added later if needed
        return ResponseEntity.ok(new EmailAvailabilityDto(email, true));
    }
}
