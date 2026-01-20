package com.schoollibrary.user.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EmailTest {

    @Test
    @DisplayName("should create Email when format is valid")
    void shouldCreateEmail() {
        Email email = Email.of("test@school.com");
        assertThat(email.getValue()).isEqualTo("test@school.com");
    }

    @ParameterizedTest
    @ValueSource(strings = {"invalid-email", "test@", "@school.com", "test@school", "test.school.com"})
    @DisplayName("should throw exception when email format is invalid")
    void shouldThrowExceptionForInvalidFormat(String invalidEmail) {
        assertThatThrownBy(() -> Email.of(invalidEmail))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid email format");
    }

    @Test
    @DisplayName("should throw exception when email is null or blank")
    void shouldThrowExceptionForEmptyEmail() {
        assertThatThrownBy(() -> Email.of(null))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> Email.of(""))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("should normalize email to lowercase")
    void shouldNormalizeEmail() {
        Email email = Email.of("TEST@SCHOOL.COM ");
        assertThat(email.getValue()).isEqualTo("test@school.com");
    }

    @Test
    @DisplayName("should be equal when values are same")
    void shouldBeEqual() {
        Email email1 = Email.of("test@school.com");
        Email email2 = Email.of("test@school.com");
        assertThat(email1).isEqualTo(email2);
        assertThat(email1.hashCode()).isEqualTo(email2.hashCode());
    }
}
