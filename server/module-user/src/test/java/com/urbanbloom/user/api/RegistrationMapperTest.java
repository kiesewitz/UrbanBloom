package com.urbanbloom.user.api;

import com.urbanbloom.user.application.RegisterUserCommand;
import com.urbanbloom.user.application.RegistrationResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RegistrationMapperTest {

    private final RegistrationMapper mapper = new RegistrationMapper();

    @Test
    @DisplayName("should map RegistrationRequestDto to RegisterUserCommand")
    void shouldMapRequestToCommand() {
        // Arrange
        RegistrationRequestDto dto = new RegistrationRequestDto();
        dto.setEmail("test@urbanbloom.local");
        dto.setPassword("password123");
        dto.setFirstName("John");
        dto.setLastName("Doe");

        // Act
        RegisterUserCommand command = mapper.toCommand(dto);

        // Assert
        assertThat(command.getEmail()).isEqualTo(dto.getEmail());
        assertThat(command.getPassword()).isEqualTo(dto.getPassword());
        assertThat(command.getFirstName()).isEqualTo(dto.getFirstName());
        assertThat(command.getLastName()).isEqualTo(dto.getLastName());
    }

    @Test
    @DisplayName("should map RegistrationResult to RegistrationResponseDto")
    void shouldMapResultToDto() {
        // Arrange
        RegistrationResult result = new RegistrationResult("user-id-567", "external-id-890", "User registered");

        // Act
        RegistrationResponseDto dto = mapper.toDto(result);

        // Assert
        assertThat(dto.getUserId()).isEqualTo(result.getUserId());
        assertThat(dto.getExternalId()).isEqualTo(result.getExternalId());
        assertThat(dto.getMessage()).isEqualTo(result.getMessage());
    }
}
