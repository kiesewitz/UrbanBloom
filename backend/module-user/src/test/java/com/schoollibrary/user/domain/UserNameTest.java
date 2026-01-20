package com.schoollibrary.user.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UserNameTest {

    @Test
    @DisplayName("should create UserName when names are valid")
    void shouldCreateUserName() {
        UserName name = UserName.of("John", "Doe");
        assertThat(name.getFirstName()).isEqualTo("John");
        assertThat(name.getLastName()).isEqualTo("Doe");
        assertThat(name.getFullName()).isEqualTo("John Doe");
        assertThat(name.getFormalName()).isEqualTo("Doe, John");
    }

    @Test
    @DisplayName("should throw exception when names are too short")
    void shouldThrowExceptionForShortNames() {
        assertThatThrownBy(() -> UserName.of("J", "Doe"))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> UserName.of("John", "D"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("should throw exception when names are null or blank")
    void shouldThrowExceptionForEmptyNames() {
        assertThatThrownBy(() -> UserName.of(null, "Doe"))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> UserName.of("John", ""))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("should trim white spaces")
    void shouldTrimNames() {
        UserName name = UserName.of(" John ", " Doe ");
        assertThat(name.getFirstName()).isEqualTo("John");
        assertThat(name.getLastName()).isEqualTo("Doe");
    }
}
