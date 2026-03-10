package com.urbanbloom.user.api;

import com.urbanbloom.user.application.*;
import com.urbanbloom.user.domain.AuthenticationException;
import com.urbanbloom.user.domain.AuthenticationResult;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * REST Controller for authentication endpoints.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationApplicationService authService;
    private final PasswordResetApplicationService passwordResetService;

    @Value("${keycloak.realm.mobile:urbanbloom-mobile}")
    private String mobileRealm;

    @Value("${keycloak.realm.admin:urbanbloom-admin}")
    private String adminRealm;

    @Value("${keycloak.admin.server-url}")
    private String keycloakUrl;

    /**
     * Login for mobile citizens.
     */
    @PostMapping("/mobile/login")
    public ResponseEntity<?> loginMobile(@Valid @RequestBody LoginRequestDto request) {
        log.info("Mobile login request for email: {}", request.getEmail());

        try {
            LoginCommand command = new LoginCommand();
            command.setEmail(request.getEmail());
            command.setPassword(request.getPassword());

            AuthenticationResult result = authService.loginMobile(command);
            return ResponseEntity.ok(mapToLoginResponse(result));
        } catch (AuthenticationException e) {
            log.warn("Mobile login failed: {}", e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponseDto(e.getErrorCode(), e.getMessage()));
        }
    }

    /**
     * Login for administrators.
     */
    @PostMapping("/admin/login")
    public ResponseEntity<?> loginAdmin(@Valid @RequestBody LoginRequestDto request) {
        log.info("Admin login request for email: {}", request.getEmail());

        try {
            LoginCommand command = new LoginCommand();
            command.setEmail(request.getEmail());
            command.setPassword(request.getPassword());

            AuthenticationResult result = authService.loginAdmin(command);
            return ResponseEntity.ok(mapToLoginResponse(result));
        } catch (AuthenticationException e) {
            log.warn("Admin login failed for {}: {}", request.getEmail(), e.getErrorCode());
            
            if ("UPDATE_PASSWORD_REQUIRED".equals(e.getErrorCode())) {
                String loginUrl = String.format("%s/realms/%s/protocol/openid-connect/auth", keycloakUrl, adminRealm);
                return ResponseEntity
                        .status(HttpStatus.UNAUTHORIZED)
                        .body(new UpdatePasswordRequiredResponseDto("UPDATE_PASSWORD_REQUIRED", e.getMessage(), loginUrl));
            }
            
            HttpStatus status = "FORBIDDEN".equals(e.getErrorCode()) ? HttpStatus.FORBIDDEN : HttpStatus.UNAUTHORIZED;
            return ResponseEntity
                    .status(status)
                    .body(new ErrorResponseDto(e.getErrorCode(), e.getMessage()));
        }
    }

    /**
     * Logout.
     */
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@Valid @RequestBody LogoutRequestDto request) {
        // Determine realm from context or token if needed, here we assume mobile as default or use a field
        authService.logout(new LogoutCommand(request.getRefreshToken(), mobileRealm));
        return ResponseEntity.ok(Map.of("message", "Logged out successfully"));
    }

    /**
     * Password reset request.
     */
    @PostMapping("/password/reset-request")
    public ResponseEntity<?> requestPasswordReset(@Valid @RequestBody PasswordResetRequestDto request) {
        try {
            RequestPasswordResetCommand command = new RequestPasswordResetCommand();
            command.setEmail(request.email());
            passwordResetService.requestPasswordReset(command);
        } catch (Exception e) {
            log.warn("Password reset request error (silenced): {}", e.getMessage());
        }
        // Always return 200 OK
        return ResponseEntity.ok(Map.of("message", "If this email is registered, a reset link has been sent."));
    }

    private LoginResponseDto mapToLoginResponse(AuthenticationResult result) {
        return new LoginResponseDto(
                result.accessToken(),
                result.refreshToken(),
                result.expiresIn(),
                result.tokenType());
    }
}
