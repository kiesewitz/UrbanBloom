package com.schoollibrary.user.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for PasswordResetRequested domain event.
 */
@DisplayName("PasswordResetRequested Tests")
class PasswordResetRequestedTest {

    @Test
    @DisplayName("should_CreateEvent_when_AllFieldsProvided")
    void testEventCreation() {
        // Arrange
        String email = "test@schulbib.de";
        Instant requestedAt = Instant.now();
        Instant expiresAt = requestedAt.plusSeconds(3600);

        // Act
        PasswordResetRequested event = new PasswordResetRequested(email, requestedAt, expiresAt);

        // Assert
        assertThat(event).isNotNull();
        assertThat(event.getEmail()).isEqualTo(email);
        assertThat(event.getRequestedAt()).isEqualTo(requestedAt);
        assertThat(event.getExpiresAt()).isEqualTo(expiresAt);
    }

    @Test
    @DisplayName("should_BeImmutable_when_UsingLombokValue")
    void testEventImmutability() {
        // Arrange & Act
        PasswordResetRequested event = new PasswordResetRequested(
                "test@schulbib.de",
                Instant.now(),
                Instant.now().plusSeconds(3600));

        // Assert - Lombok @Value makes fields final, so they can't be changed
        assertThat(event.getEmail()).isNotNull();
        assertThat(event.getRequestedAt()).isNotNull();
        assertThat(event.getExpiresAt()).isNotNull();
    }

    @Test
    @DisplayName("should_HaveEqualityBasedOnFields_when_TwoEventsHaveSameData")
    void testEventEquality() {
        // Arrange
        String email = "test@schulbib.de";
        Instant requestedAt = Instant.parse("2026-01-13T10:00:00Z");
        Instant expiresAt = Instant.parse("2026-01-13T11:00:00Z");

        PasswordResetRequested event1 = new PasswordResetRequested(email, requestedAt, expiresAt);
        PasswordResetRequested event2 = new PasswordResetRequested(email, requestedAt, expiresAt);

        // Assert
        assertThat(event1).isEqualTo(event2);
        assertThat(event1.hashCode()).isEqualTo(event2.hashCode());
    }

    @Test
    @DisplayName("should_HandleNullValues_when_CreatingEvent")
    void testEventWithNullValues() {
        // Act
        PasswordResetRequested event = new PasswordResetRequested(null, null, null);

        // Assert
        assertThat(event).isNotNull();
        assertThat(event.getEmail()).isNull();
        assertThat(event.getRequestedAt()).isNull();
        assertThat(event.getExpiresAt()).isNull();
    }
}
