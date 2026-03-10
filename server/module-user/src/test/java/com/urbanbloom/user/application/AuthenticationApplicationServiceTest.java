package com.urbanbloom.user.application;

import com.urbanbloom.user.config.RegistrationConfigProperties;
import com.urbanbloom.user.domain.AuthenticationException;
import com.urbanbloom.user.domain.AuthenticationResult;
import com.urbanbloom.user.domain.IdentityProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationApplicationServiceTest {

    @Mock
    private IdentityProvider identityProvider;

    @Mock
    private RegistrationConfigProperties registrationConfig;

    @InjectMocks
    private AuthenticationApplicationService authService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(authService, "mobileRealm", "urbanbloom-mobile");
        ReflectionTestUtils.setField(authService, "adminRealm", "urbanbloom-admin");
        ReflectionTestUtils.setField(authService, "mobileClientId", "urbanbloom-mobile-app");
        ReflectionTestUtils.setField(authService, "adminClientId", "urbanbloom-admin-web");
    }

    @Test
    @DisplayName("should call identity provider for mobile login")
    void shouldLoginMobileThroughIdentityProvider() {
        // Arrange
        LoginCommand command = new LoginCommand();
        command.setEmail("test@urbanbloom.local");
        command.setPassword("password123");

        AuthenticationResult result = new AuthenticationResult(
                "token", "refresh", 3600, 7200, "Bearer");

        when(identityProvider.authenticate(eq("test@urbanbloom.local"), eq("password123"), anyString(), anyString()))
                .thenReturn(result);

        // Act
        AuthenticationResult actual = authService.loginMobile(command);

        // Assert
        assertThat(actual).isEqualTo(result);
        verify(identityProvider).authenticate(eq("test@urbanbloom.local"), eq("password123"), eq("urbanbloom-mobile"), eq("urbanbloom-mobile-app"));
    }

    @Test
    @DisplayName("should call identity provider for admin login and verify role")
    void shouldLoginAdminThroughIdentityProviderAndVerifyRole() {
        // Arrange
        LoginCommand command = new LoginCommand();
        command.setEmail("admin@urbanbloom.city");
        command.setPassword("admin123");

        AuthenticationResult result = new AuthenticationResult(
                "admin-token", "refresh", 3600, 7200, "Bearer");

        when(identityProvider.authenticate(eq("admin@urbanbloom.city"), eq("admin123"), anyString(), anyString()))
                .thenReturn(result);
        when(identityProvider.hasRole("admin-token", "ADMIN")).thenReturn(true);

        // Act
        AuthenticationResult actual = authService.loginAdmin(command);

        // Assert
        assertThat(actual).isEqualTo(result);
        verify(identityProvider).hasRole("admin-token", "ADMIN");
    }

    @Test
    @DisplayName("should throw Forbidden when admin login lacks ADMIN role")
    void shouldThrowForbiddenWhenAdminRoleMissing() {
        // Arrange
        LoginCommand command = new LoginCommand();
        command.setEmail("user@urbanbloom.city");
        command.setPassword("password");

        AuthenticationResult result = new AuthenticationResult(
                "user-token", "refresh", 3600, 7200, "Bearer");

        when(identityProvider.authenticate(anyString(), anyString(), anyString(), anyString()))
                .thenReturn(result);
        when(identityProvider.hasRole("user-token", "ADMIN")).thenReturn(false);

        // Act & Assert
        assertThatThrownBy(() -> authService.loginAdmin(command))
                .isInstanceOf(AuthenticationException.class)
                .matches(e -> "FORBIDDEN".equals(((AuthenticationException)e).getErrorCode()));
    }

    @Test
    @DisplayName("should call identity provider for token refresh")
    void shouldRefreshTokenThroughIdentityProvider() {
        // Arrange
        RefreshTokenCommand command = new RefreshTokenCommand();
        command.setRefreshToken("old-token");
        command.setRealm("urbanbloom-mobile");

        AuthenticationResult result = new AuthenticationResult(
                "new-token", "new-refresh", 3600, 7200, "Bearer");

        when(identityProvider.refreshToken(eq("old-token"), anyString(), anyString())).thenReturn(result);

        // Act
        AuthenticationResult actual = authService.refreshToken(command);

        // Assert
        assertThat(actual).isEqualTo(result);
        verify(identityProvider).refreshToken(eq("old-token"), eq("urbanbloom-mobile"), eq("urbanbloom-mobile-app"));
    }

    @Test
    @DisplayName("should return allowed domains from config")
    void shouldReturnAllowedDomains() {
        // Arrange
        List<String> domains = List.of("urbanbloom.local", "gmail.com");
        when(registrationConfig.getAllowedDomains()).thenReturn(domains);

        // Act
        List<String> actual = authService.getAllowedDomains();

        // Assert
        assertThat(actual).containsExactlyInAnyOrder("urbanbloom.local", "gmail.com");
    }
}
