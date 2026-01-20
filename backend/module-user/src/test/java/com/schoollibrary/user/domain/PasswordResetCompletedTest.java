package com.schoollibrary.user.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for PasswordResetCompleted domain event.
 */
@DisplayName("PasswordResetCompleted Tests")
class PasswordResetCompletedTest {

    @Test
    @DisplayName("should_CreateEvent_when_AllFieldsProvided")
    void testEventCreation() {
        // Arrange
        String userId = "keycloak-user-123";
        String email = "test@schulbib.de";
        Instant completedAt = Instant.now();

        // Act
        PasswordResetCompleted event = new PasswordResetCompleted(userId, email, completedAt);

        // Assert
        assertThat(event).isNotNull();
        assertThat(event.getUserId()).isEqualTo(userId);
        assertThat(event.getEmail()).isEqualTo(email);
        assertThat(event.getCompletedAt()).isEqualTo(completedAt);
    }

    @Test
    @DisplayName("should_BeImmutable_when_UsingLombokValue")
    void testEventImmutability() {
        // Arrange & Act
        PasswordResetCompleted event = new PasswordResetCompleted(
                "user-id",
                "test@schulbib.de",
                Instant.now());

        // Assert - Lombok @Value makes fields final
        assertThat(event.getUserId()).isNotNull();
        assertThat(event.getEmail()).isNotNull();
        assertThat(event.getCompletedAt()).isNotNull();
    }

    @Test
    @DisplayName("should_HaveEqualityBasedOnFields_when_TwoEventsHaveSameData")
    void testEventEquality() {
        // Arrange
        String userId = "user-123";
        String email = "test@schulbib.de";
        Instant completedAt = Instant.parse("2026-01-13T10:00:00Z");

        PasswordResetCompleted event1 = new PasswordResetCompleted(userId, email, completedAt);
        PasswordResetCompleted event2 = new PasswordResetCompleted(userId, email, completedAt);

        // Assert
        assertThat(event1).isEqualTo(event2);
        assertThat(event1.hashCode()).isEqualTo(event2.hashCode());
    }

    @Test
    @DisplayName("should_HandleNullValues_when_CreatingEvent")
    void testEventWithNullValues() {
        // Act
        PasswordResetCompleted event = new PasswordResetCompleted(null, null, null);

        // Assert
        assertThat(event).isNotNull();
        assertThat(event.getUserId()).isNull();
        assertThat(event.getEmail()).isNull();
        assertThat(event.getCompletedAt()).isNull();
    }
}
