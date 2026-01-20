package com.schoollibrary.user.api;

import com.schoollibrary.user.application.AuthenticationApplicationService;
import com.schoollibrary.user.application.LoginCommand;
import com.schoollibrary.user.application.RefreshTokenCommand;
import com.schoollibrary.user.domain.AuthenticationException;
import com.schoollibrary.user.domain.AuthenticationResult;
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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthenticationApplicationService authService;

    @InjectMocks
    private AuthController authController;

    @Test
    @DisplayName("should login successfully and return tokens")
    void shouldLoginSuccessfully() {
        // Arrange
        LoginRequestDto request = new LoginRequestDto();
        request.setEmail("test@school.com");
        request.setPassword("password123");

        AuthenticationResult result = new AuthenticationResult(
                "access-token", "refresh-token", 3600, 7200, "Bearer");

        when(authService.login(any(LoginCommand.class))).thenReturn(result);

        // Act
        ResponseEntity<?> responseEntity = authController.login(request);

        // Assert
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        LoginResponseDto body = (LoginResponseDto) responseEntity.getBody();
        assertThat(body).isNotNull();
        assertThat(body.getAccessToken()).isEqualTo("access-token");
        assertThat(body.getRefreshToken()).isEqualTo("refresh-token");
        assertThat(body.getTokenType()).isEqualTo("Bearer");
    }

    @Test
    @DisplayName("should return 401 Unauthorized when AuthenticationException occurs during login")
    void shouldReturnUnauthorizedOnLoginFailure() {
        // Arrange
        LoginRequestDto request = new LoginRequestDto();
        request.setEmail("test@school.com");
        request.setPassword("wrong-password");

        when(authService.login(any(LoginCommand.class)))
                .thenThrow(new AuthenticationException("Ungültige E-Mail-Adresse oder Passwort."));

        // Act
        ResponseEntity<?> responseEntity = authController.login(request);

        // Assert
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        ErrorResponseDto error = (ErrorResponseDto) responseEntity.getBody();
        assertThat(error).isNotNull();
        assertThat(error.getMessage()).isEqualTo("Ungültige E-Mail-Adresse oder Passwort.");
    }

    @Test
    @DisplayName("should refresh token successfully")
    void shouldRefreshTokenSuccessfully() {
        // Arrange
        RefreshRequestDto request = new RefreshRequestDto();
        request.setRefreshToken("old-refresh-token");

        AuthenticationResult result = new AuthenticationResult(
                "new-access-token", "new-refresh-token", 3600, 7200, "Bearer");

        when(authService.refreshToken(any(RefreshTokenCommand.class))).thenReturn(result);

        // Act
        ResponseEntity<?> responseEntity = authController.refresh(request);

        // Assert
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        LoginResponseDto body = (LoginResponseDto) responseEntity.getBody();
        assertThat(body).isNotNull();
        assertThat(body.getAccessToken()).isEqualTo("new-access-token");
    }

    @Test
    @DisplayName("should return 401 Unauthorized when AuthenticationException occurs during refresh")
    void shouldReturnUnauthorizedOnRefreshFailure() {
        // Arrange
        RefreshRequestDto request = new RefreshRequestDto();
        request.setRefreshToken("invalid-token");

        when(authService.refreshToken(any(RefreshTokenCommand.class)))
                .thenThrow(new AuthenticationException("Ungültige oder abgelaufene Sitzung."));

        // Act
        ResponseEntity<?> responseEntity = authController.refresh(request);

        // Assert
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        ErrorResponseDto error = (ErrorResponseDto) responseEntity.getBody();
        assertThat(error).isNotNull();
        assertThat(error.getMessage()).isEqualTo("Ungültige oder abgelaufene Sitzung.");
    }
}
