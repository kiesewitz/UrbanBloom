package com.schoollibrary.user.application;

import com.schoollibrary.user.config.RegistrationConfigProperties;
import com.schoollibrary.user.domain.AuthenticationException;
import com.schoollibrary.user.domain.AuthenticationResult;
import com.schoollibrary.user.domain.IdentityProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationApplicationServiceTest {

    @Mock
    private IdentityProvider identityProvider;

    @Mock
    private RegistrationConfigProperties registrationConfig;

    @InjectMocks
    private AuthenticationApplicationService authService;

    @Test
    @DisplayName("should call identity provider for login")
    void shouldLoginThroughIdentityProvider() {
        // Arrange
        LoginCommand command = new LoginCommand();
        command.setEmail("test@school.com");
        command.setPassword("password123");

        AuthenticationResult result = new AuthenticationResult(
                "token", "refresh", 3600, 7200, "Bearer");

        when(identityProvider.authenticateWithCredentials("test@school.com", "password123")).thenReturn(result);

        // Act
        AuthenticationResult actual = authService.login(command);

        // Assert
        assertThat(actual).isEqualTo(result);
        verify(identityProvider).authenticateWithCredentials("test@school.com", "password123");
    }

    @Test
    @DisplayName("should throw AuthenticationException when identity provider fails login")
    void shouldThrowOnLoginFailure() {
        // Arrange
        LoginCommand command = new LoginCommand();
        command.setEmail("test@school.com");
        command.setPassword("wrong");

        when(identityProvider.authenticateWithCredentials(anyString(), anyString()))
                .thenThrow(new AuthenticationException("Fehlgeschlagen"));

        // Act & Assert
        assertThatThrownBy(() -> authService.login(command))
                .isInstanceOf(AuthenticationException.class)
                .hasMessage("Fehlgeschlagen");
    }

    @Test
    @DisplayName("should call identity provider for token refresh")
    void shouldRefreshTokenThroughIdentityProvider() {
        // Arrange
        RefreshTokenCommand command = new RefreshTokenCommand();
        command.setRefreshToken("old-token");

        AuthenticationResult result = new AuthenticationResult(
                "new-token", "new-refresh", 3600, 7200, "Bearer");

        when(identityProvider.refreshToken("old-token")).thenReturn(result);

        // Act
        AuthenticationResult actual = authService.refreshToken(command);

        // Assert
        assertThat(actual).isEqualTo(result);
        verify(identityProvider).refreshToken("old-token");
    }

    @Test
    @DisplayName("should return allowed domains from config")
    void shouldReturnAllowedDomains() {
        // Arrange
        List<String> domains = List.of("school.com", "edu.com");
        when(registrationConfig.getAllowedDomains()).thenReturn(domains);

        // Act
        List<String> actual = authService.getAllowedDomains();

        // Assert
        assertThat(actual).containsExactlyInAnyOrder("school.com", "edu.com");
    }
}
