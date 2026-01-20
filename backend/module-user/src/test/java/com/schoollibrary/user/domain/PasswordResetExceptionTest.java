package com.schoollibrary.user.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for PasswordResetException factory methods.
 * Tests ensure that exceptions are created with correct messages.
 */
@DisplayName("PasswordResetException Tests")
class PasswordResetExceptionTest {

    @Test
    @DisplayName("should_CreateInvalidTokenException_when_CallingInvalidToken")
    void testInvalidToken() {
        // Act
        PasswordResetException exception = PasswordResetException.invalidToken();

        // Assert
        assertThat(exception).isNotNull();
        assertThat(exception.getMessage()).isEqualTo("Ungültiger oder abgelaufener Reset-Token.");
        assertThat(exception.getCause()).isNull();
    }

    @Test
    @DisplayName("should_CreateTokenAlreadyUsedException_when_CallingTokenAlreadyUsed")
    void testTokenAlreadyUsed() {
        // Act
        PasswordResetException exception = PasswordResetException.tokenAlreadyUsed();

        // Assert
        assertThat(exception).isNotNull();
        assertThat(exception.getMessage()).isEqualTo("Dieser Reset-Token wurde bereits verwendet.");
        assertThat(exception.getCause()).isNull();
    }

    @Test
    @DisplayName("should_CreateUserNotFoundException_when_CallingUserNotFound")
    void testUserNotFound() {
        // Act
        PasswordResetException exception = PasswordResetException.userNotFound();

        // Assert
        assertThat(exception).isNotNull();
        assertThat(exception.getMessage()).isEqualTo("Benutzer nicht gefunden.");
        assertThat(exception.getCause()).isNull();
    }

    @Test
    @DisplayName("should_CreateProviderErrorException_when_CallingProviderErrorWithMessage")
    void testProviderError_WithMessage() {
        // Arrange
        String errorMessage = "Keycloak service is unavailable";

        // Act
        PasswordResetException exception = PasswordResetException.providerError(errorMessage);

        // Assert
        assertThat(exception).isNotNull();
        assertThat(exception.getMessage()).isEqualTo(errorMessage);
        assertThat(exception.getCause()).isNull();
    }

    @Test
    @DisplayName("should_CreateProviderErrorException_when_CallingProviderErrorWithMessageAndCause")
    void testProviderError_WithMessageAndCause() {
        // Arrange
        String errorMessage = "Failed to connect to Keycloak";
        Throwable cause = new RuntimeException("Connection timeout");

        // Act
        PasswordResetException exception = PasswordResetException.providerError(errorMessage, cause);

        // Assert
        assertThat(exception).isNotNull();
        assertThat(exception.getMessage()).isEqualTo(errorMessage);
        assertThat(exception.getCause()).isEqualTo(cause);
        assertThat(exception.getCause().getMessage()).isEqualTo("Connection timeout");
    }

    @Test
    @DisplayName("should_BeInstanceOfRuntimeException_when_ExceptionCreated")
    void testExceptionInheritance() {
        // Act
        PasswordResetException exception = PasswordResetException.userNotFound();

        // Assert
        assertThat(exception).isInstanceOf(RuntimeException.class);
    }

    @Test
    @DisplayName("should_HandleNullMessage_when_CreatingProviderError")
    void testProviderError_WithNullMessage() {
        // Act
        PasswordResetException exception = PasswordResetException.providerError(null);

        // Assert
        assertThat(exception).isNotNull();
        assertThat(exception.getMessage()).isNull();
    }

    @Test
    @DisplayName("should_HandleNullCause_when_CreatingProviderError")
    void testProviderError_WithNullCause() {
        // Arrange
        String errorMessage = "Some error occurred";

        // Act
        PasswordResetException exception = PasswordResetException.providerError(errorMessage, null);

        // Assert
        assertThat(exception).isNotNull();
        assertThat(exception.getMessage()).isEqualTo(errorMessage);
        assertThat(exception.getCause()).isNull();
    }
}
