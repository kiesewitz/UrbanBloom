package com.schoollibrary.user.application;

import com.schoollibrary.user.domain.Email;
import com.schoollibrary.user.domain.ExternalUserId;
import com.schoollibrary.user.domain.IdentityProvider;
import com.schoollibrary.user.domain.PasswordResetCompleted;
import com.schoollibrary.user.domain.PasswordResetException;
import com.schoollibrary.user.domain.PasswordResetRequested;
import com.schoollibrary.user.domain.UserName;
import com.schoollibrary.user.domain.UserProfile;
import com.schoollibrary.user.domain.UserProfileRepository;
import com.schoollibrary.user.domain.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for PasswordResetApplicationService.
 * Tests follow AAA (Arrange, Act, Assert) pattern and FIRST principles.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("PasswordResetApplicationService Tests")
class PasswordResetApplicationServiceTest {

    @Mock
    private IdentityProvider identityProvider;

    @Mock
    private UserProfileRepository userProfileRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private PasswordResetApplicationService service;

    @Captor
    private ArgumentCaptor<PasswordResetRequested> resetRequestedCaptor;

    @Captor
    private ArgumentCaptor<PasswordResetCompleted> resetCompletedCaptor;

    private static final String TEST_EMAIL = "test@schulbib.de";
    private static final String TEST_USER_ID = "keycloak-user-123";
    private static final String NEW_PASSWORD = "NewSecurePassword123!";

    private RequestPasswordResetCommand requestCommand;
    private ResetPasswordCommand resetCommand;
    private UserProfile mockUserProfile;

    @BeforeEach
    void setUp() {
        // Arrange - Common test data
        requestCommand = new RequestPasswordResetCommand();
        requestCommand.setEmail(TEST_EMAIL);

        resetCommand = new ResetPasswordCommand();
        resetCommand.setEmail(TEST_EMAIL);
        resetCommand.setNewPassword(NEW_PASSWORD);

        mockUserProfile = createMockUserProfile();
    }

    // ========== requestPasswordReset Tests ==========

    @Test
    @DisplayName("should_SendPasswordResetEmail_when_RequestIsValid")
    void testRequestPasswordReset_HappyPath() {
        // Arrange
        doNothing().when(identityProvider).sendPasswordResetEmail(TEST_EMAIL);

        // Act
        service.requestPasswordReset(requestCommand);

        // Assert
        verify(identityProvider).sendPasswordResetEmail(TEST_EMAIL);
        verify(eventPublisher).publishEvent(resetRequestedCaptor.capture());

        PasswordResetRequested event = resetRequestedCaptor.getValue();
        assertThat(event).isNotNull();
        assertThat(event.getEmail()).isEqualTo(TEST_EMAIL);
        assertThat(event.getRequestedAt()).isNotNull();
        assertThat(event.getExpiresAt()).isNotNull();
        assertThat(event.getExpiresAt()).isAfter(event.getRequestedAt());
    }

    @Test
    @DisplayName("should_PublishDomainEvent_when_PasswordResetEmailSent")
    void testRequestPasswordReset_PublishesDomainEvent() {
        // Arrange
        doNothing().when(identityProvider).sendPasswordResetEmail(TEST_EMAIL);

        // Act
        service.requestPasswordReset(requestCommand);

        // Assert
        verify(eventPublisher).publishEvent(any(PasswordResetRequested.class));
    }

    @Test
    @DisplayName("should_ThrowPasswordResetException_when_EmailSendingFails")
    void testRequestPasswordReset_ThrowsException_WhenProviderFails() {
        // Arrange
        PasswordResetException providerError = PasswordResetException.providerError("Email service unavailable");
        doThrow(providerError).when(identityProvider).sendPasswordResetEmail(TEST_EMAIL);

        // Act & Assert
        assertThatThrownBy(() -> service.requestPasswordReset(requestCommand))
                .isInstanceOf(PasswordResetException.class)
                .hasMessageContaining("Email service unavailable");

        verify(identityProvider).sendPasswordResetEmail(TEST_EMAIL);
        verify(eventPublisher, never()).publishEvent(any(PasswordResetRequested.class));
    }

    // ========== resetPassword Tests ==========

    @Test
    @DisplayName("should_ResetPassword_when_UserExistsAndPasswordIsValid")
    void testResetPassword_HappyPath() {
        // Arrange
        Email emailVO = new Email(TEST_EMAIL);
        when(userProfileRepository.findByEmail(emailVO)).thenReturn(Optional.of(mockUserProfile));
        doNothing().when(identityProvider).resetUserPassword(TEST_USER_ID, NEW_PASSWORD);

        // Act
        service.resetPassword(resetCommand);

        // Assert
        verify(userProfileRepository).findByEmail(emailVO);
        verify(identityProvider).resetUserPassword(TEST_USER_ID, NEW_PASSWORD);
        verify(eventPublisher).publishEvent(resetCompletedCaptor.capture());

        PasswordResetCompleted event = resetCompletedCaptor.getValue();
        assertThat(event).isNotNull();
        assertThat(event.getUserId()).isEqualTo(TEST_USER_ID);
        assertThat(event.getEmail()).isEqualTo(TEST_EMAIL);
        assertThat(event.getCompletedAt()).isNotNull();
    }

    @Test
    @DisplayName("should_ThrowPasswordResetException_when_UserNotFound")
    void testResetPassword_ThrowsException_WhenUserNotFound() {
        // Arrange
        Email emailVO = new Email(TEST_EMAIL);
        when(userProfileRepository.findByEmail(emailVO)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> service.resetPassword(resetCommand))
                .isInstanceOf(PasswordResetException.class)
                .hasMessageContaining("Benutzer nicht gefunden");

        verify(userProfileRepository).findByEmail(emailVO);
        verify(identityProvider, never()).resetUserPassword(any(), any());
        verify(eventPublisher, never()).publishEvent(any(PasswordResetCompleted.class));
    }

    @Test
    @DisplayName("should_ThrowPasswordResetException_when_KeycloakResetFails")
    void testResetPassword_ThrowsException_WhenKeycloakFails() {
        // Arrange
        Email emailVO = new Email(TEST_EMAIL);
        when(userProfileRepository.findByEmail(emailVO)).thenReturn(Optional.of(mockUserProfile));

        PasswordResetException keycloakError = PasswordResetException.providerError("Keycloak unavailable");
        doThrow(keycloakError).when(identityProvider).resetUserPassword(TEST_USER_ID, NEW_PASSWORD);

        // Act & Assert
        assertThatThrownBy(() -> service.resetPassword(resetCommand))
                .isInstanceOf(PasswordResetException.class)
                .hasMessageContaining("Keycloak unavailable");

        verify(userProfileRepository).findByEmail(emailVO);
        verify(identityProvider).resetUserPassword(TEST_USER_ID, NEW_PASSWORD);
        verify(eventPublisher, never()).publishEvent(any(PasswordResetCompleted.class));
    }

    @Test
    @DisplayName("should_PublishDomainEvent_when_PasswordResetSuccessful")
    void testResetPassword_PublishesDomainEvent() {
        // Arrange
        Email emailVO = new Email(TEST_EMAIL);
        when(userProfileRepository.findByEmail(emailVO)).thenReturn(Optional.of(mockUserProfile));
        doNothing().when(identityProvider).resetUserPassword(TEST_USER_ID, NEW_PASSWORD);

        // Act
        service.resetPassword(resetCommand);

        // Assert
        verify(eventPublisher).publishEvent(any(PasswordResetCompleted.class));
    }

    @Test
    @DisplayName("should_NotPublishEvent_when_UserLookupFails")
    void testResetPassword_NoEventPublished_WhenUserNotFound() {
        // Arrange
        Email emailVO = new Email(TEST_EMAIL);
        when(userProfileRepository.findByEmail(emailVO)).thenReturn(Optional.empty());

        // Act & Assert
        try {
            service.resetPassword(resetCommand);
        } catch (PasswordResetException e) {
            // Expected
        }

        verify(eventPublisher, never()).publishEvent(any());
    }

    @Test
    @DisplayName("should_UseCorrectKeycloakUserId_when_ResettingPassword")
    void testResetPassword_UsesCorrectUserId() {
        // Arrange
        Email emailVO = new Email(TEST_EMAIL);
        when(userProfileRepository.findByEmail(emailVO)).thenReturn(Optional.of(mockUserProfile));
        doNothing().when(identityProvider).resetUserPassword(TEST_USER_ID, NEW_PASSWORD);

        // Act
        service.resetPassword(resetCommand);

        // Assert
        verify(identityProvider).resetUserPassword(eq(TEST_USER_ID), eq(NEW_PASSWORD));
    }

    // ========== Helper Methods ==========

    private UserProfile createMockUserProfile() {
        // Create a UserProfile using the factory method
        return UserProfile.create(
                new ExternalUserId(TEST_USER_ID),
                new Email(TEST_EMAIL),
                new UserName("Test", "User"),
                UserRole.STUDENT);
    }
}
