package com.schoollibrary.user.api;

import com.schoollibrary.user.application.AuthenticationApplicationService;
import com.schoollibrary.user.application.LoginCommand;
import com.schoollibrary.user.application.PasswordResetApplicationService;
import com.schoollibrary.user.application.RefreshTokenCommand;
import com.schoollibrary.user.application.RequestPasswordResetCommand;
import com.schoollibrary.user.application.ResetPasswordCommand;
import com.schoollibrary.user.domain.AuthenticationException;
import com.schoollibrary.user.domain.AuthenticationResult;
import com.schoollibrary.user.domain.PasswordResetException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for authentication endpoints.
 * Public endpoints - no authentication required.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationApplicationService authService;
    private final PasswordResetApplicationService passwordResetService;

    /**
     * Authenticates a user with email and password.
     * Returns JWT tokens on success.
     *
     * @param request the login request
     * @return login response with tokens
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequestDto request) {
        log.info("Login request received for email: {}", request.getEmail());

        try {
            LoginCommand command = new LoginCommand();
            command.setEmail(request.getEmail());
            command.setPassword(request.getPassword());

            AuthenticationResult result = authService.login(command);

            LoginResponseDto response = new LoginResponseDto(
                    result.accessToken(),
                    result.refreshToken(),
                    result.tokenType(),
                    result.expiresIn(),
                    result.refreshExpiresIn());

            return ResponseEntity.ok(response);
        } catch (AuthenticationException e) {
            log.warn("Login failed for email {}: {}", request.getEmail(), e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponseDto(e.getMessage()));
        } catch (Exception e) {
            log.error("Unexpected error during login", e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponseDto("Login aufgrund eines Serverfehlers fehlgeschlagen."));
        }
    }

    /**
     * Refreshes tokens using a valid refresh token.
     *
     * @param request the refresh request
     * @return new tokens on success
     */
    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@Valid @RequestBody RefreshRequestDto request) {
        log.debug("Token refresh request received");

        try {
            RefreshTokenCommand command = new RefreshTokenCommand();
            command.setRefreshToken(request.getRefreshToken());

            AuthenticationResult result = authService.refreshToken(command);

            LoginResponseDto response = new LoginResponseDto(
                    result.accessToken(),
                    result.refreshToken(),
                    result.tokenType(),
                    result.expiresIn(),
                    result.refreshExpiresIn());

            return ResponseEntity.ok(response);
        } catch (AuthenticationException e) {
            log.warn("Token refresh failed: {}", e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponseDto(e.getMessage()));
        } catch (Exception e) {
            log.error("Unexpected error during token refresh", e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponseDto("Token-Refresh aufgrund eines Serverfehlers fehlgeschlagen."));
        }
    }

    /**
     * Returns the list of allowed email domains for registration.
     *
     * @return allowed domains response
     */
    @GetMapping("/allowed-domains")
    public ResponseEntity<AllowedDomainsResponseDto> getAllowedDomains() {
        log.debug("Allowed domains request received");
        List<String> domains = authService.getAllowedDomains();
        return ResponseEntity.ok(new AllowedDomainsResponseDto(domains));
    }

    /**
     * Requests a password reset email for the given email address.
     *
     * @param request the password reset request
     * @return success message (200) or error (400/500)
     */
    @PostMapping("/password/reset-request")
    public ResponseEntity<?> requestPasswordReset(@Valid @RequestBody PasswordResetRequestDto request) {
        log.info("Password reset request received for email: {}", request.email());

        try {
            RequestPasswordResetCommand command = new RequestPasswordResetCommand();
            command.setEmail(request.email());

            passwordResetService.requestPasswordReset(command);

            return ResponseEntity.ok(new PasswordResetResponseDto(
                    "Falls ein Konto mit dieser E-Mail existiert, wurde eine E-Mail zum Zurücksetzen des Passworts gesendet."));
        } catch (PasswordResetException e) {
            log.warn("Password reset request failed: {}", e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponseDto(e.getMessage()));
        } catch (Exception e) {
            log.error("Unexpected error during password reset request", e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponseDto("Passwort-Reset-Anfrage aufgrund eines Serverfehlers fehlgeschlagen."));
        }
    }

    /**
     * Resets the password for a user.
     *
     * @param request the password reset data
     * @return success message (200) or error (400/401/500)
     */
    @PostMapping("/password/reset")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody PasswordResetDto request) {
        log.info("Password reset completion request for email: {}", request.email());

        try {
            ResetPasswordCommand command = new ResetPasswordCommand();
            command.setEmail(request.email());
            command.setNewPassword(request.newPassword());
            command.setToken(request.token());

            passwordResetService.resetPassword(command);

            return ResponseEntity.ok(new PasswordResetResponseDto(
                    "Passwort wurde erfolgreich zurückgesetzt. Sie können sich jetzt mit dem neuen Passwort anmelden."));
        } catch (PasswordResetException e) {
            log.warn("Password reset failed: {}", e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponseDto(e.getMessage()));
        } catch (Exception e) {
            log.error("Unexpected error during password reset", e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponseDto("Passwort-Reset aufgrund eines Serverfehlers fehlgeschlagen."));
        }
    }
}
