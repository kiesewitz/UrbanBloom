package com.urbanbloom.user.api;

import com.urbanbloom.user.application.RegistrationException;
import com.urbanbloom.user.application.RegistrationResult;
import com.urbanbloom.user.application.UserRegistrationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for user registration.
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
     */
    @PostMapping("/registration")
    public ResponseEntity<RegistrationResponseDto> register(@Valid @RequestBody RegistrationRequestDto request) {
        log.info("Registration request received for email: {}", request.getEmail());

        RegistrationResult result = registrationService.registerUser(mapper.toCommand(request));
        RegistrationResponseDto response = mapper.toDto(result);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }
}
