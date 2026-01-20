package com.schoollibrary.user.api;

import com.schoollibrary.user.application.RegisterUserCommand;
import com.schoollibrary.user.application.RegistrationResult;
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
        dto.setEmail("test@school.com");
        dto.setPassword("password123");
        dto.setFirstName("John");
        dto.setLastName("Doe");
        dto.setStudentId("S12345");
        dto.setSchoolClass("10A");

        // Act
        RegisterUserCommand command = mapper.toCommand(dto);

        // Assert
        assertThat(command.getEmail()).isEqualTo(dto.getEmail());
        assertThat(command.getPassword()).isEqualTo(dto.getPassword());
        assertThat(command.getFirstName()).isEqualTo(dto.getFirstName());
        assertThat(command.getLastName()).isEqualTo(dto.getLastName());
        assertThat(command.getStudentId()).isEqualTo(dto.getStudentId());
        assertThat(command.getSchoolClass()).isEqualTo(dto.getSchoolClass());
    }

    @Test
    @DisplayName("should map RegistrationResult to RegistrationResponseDto")
    void shouldMapResultToDto() {
        // Arrange
        RegistrationResult result = new RegistrationResult("user-id-567", "test@school.com", "User registered", true);

        // Act
        RegistrationResponseDto dto = mapper.toDto(result);

        // Assert
        assertThat(dto.getUserId()).isEqualTo(result.getUserId());
        assertThat(dto.getEmail()).isEqualTo(result.getEmail());
        assertThat(dto.getMessage()).isEqualTo(result.getMessage());
        assertThat(dto.isVerificationRequired()).isEqualTo(result.isVerificationRequired());
    }
}
