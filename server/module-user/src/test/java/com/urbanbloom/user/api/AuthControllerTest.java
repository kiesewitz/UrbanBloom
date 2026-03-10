package com.urbanbloom.user.api;

import com.urbanbloom.user.application.*;
import com.urbanbloom.user.domain.AuthenticationException;
import com.urbanbloom.user.domain.AuthenticationResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthenticationApplicationService authService;

    @Mock
    private PasswordResetApplicationService passwordResetService;

    @InjectMocks
    private AuthController authController;

    @Test
    @DisplayName("should login mobile user successfully")
    void shouldLoginMobileSuccessfully() {
        // Arrange
        LoginRequestDto request = new LoginRequestDto();
        request.setEmail("test@urbanbloom.local");
        request.setPassword("password123");

        AuthenticationResult result = new AuthenticationResult(
                "access-token", "refresh-token", 3600, 7200, "Bearer");

        when(authService.loginMobile(any(LoginCommand.class))).thenReturn(result);

        // Act
        ResponseEntity<?> responseEntity = authController.loginMobile(request);

        // Assert
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        LoginResponseDto body = (LoginResponseDto) responseEntity.getBody();
        assertThat(body).isNotNull();
        assertThat(body.getAccessToken()).isEqualTo("access-token");
    }

    @Test
    @DisplayName("should login admin successfully")
    void shouldLoginAdminSuccessfully() {
        // Arrange
        LoginRequestDto request = new LoginRequestDto();
        request.setEmail("admin@urbanbloom.city");
        request.setPassword("admin123");

        AuthenticationResult result = new AuthenticationResult(
                "access-token", "refresh-token", 3600, 7200, "Bearer");

        when(authService.loginAdmin(any(LoginCommand.class))).thenReturn(result);

        // Act
        ResponseEntity<?> responseEntity = authController.loginAdmin(request);

        // Assert
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        LoginResponseDto body = (LoginResponseDto) responseEntity.getBody();
        assertThat(body).isNotNull();
        assertThat(body.getAccessToken()).isEqualTo("access-token");
    }

    @Test
    @DisplayName("should return 401 with redirect URL when admin needs password update")
    void shouldReturnRedirectOnAdminPasswordUpdateRequired() {
        // Arrange
        LoginRequestDto request = new LoginRequestDto();
        request.setEmail("admin@urbanbloom.city");
        request.setPassword("temp-password");

        when(authService.loginAdmin(any(LoginCommand.class)))
                .thenThrow(AuthenticationException.updatePasswordRequired());

        // Act
        ResponseEntity<?> responseEntity = authController.loginAdmin(request);

        // Assert
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        UpdatePasswordRequiredResponseDto body = (UpdatePasswordRequiredResponseDto) responseEntity.getBody();
        assertThat(body).isNotNull();
        assertThat(body.getError()).isEqualTo("UPDATE_PASSWORD_REQUIRED");
        assertThat(body.getKeycloakLoginUrl()).contains("protocol/openid-connect/auth");
    }

    @Test
    @DisplayName("should logout successfully")
    void shouldLogoutSuccessfully() {
        // Arrange
        LogoutRequestDto request = new LogoutRequestDto();
        request.setRefreshToken("some-refresh-token");

        // Act
        ResponseEntity<?> responseEntity = authController.logout(request);

        // Assert
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(authService).logout(any(LogoutCommand.class));
    }

    @Test
    @DisplayName("should request password reset successfully")
    void shouldRequestPasswordResetSuccessfully() {
        // Arrange
        PasswordResetRequestDto request = new PasswordResetRequestDto("test@example.com");

        // Act
        ResponseEntity<?> responseEntity = authController.requestPasswordReset(request);

        // Assert
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(passwordResetService).requestPasswordReset(any(RequestPasswordResetCommand.class));
    }
}
