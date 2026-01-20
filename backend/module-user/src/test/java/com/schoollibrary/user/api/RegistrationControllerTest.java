package com.schoollibrary.user.api;

import com.schoollibrary.user.application.RegisterUserCommand;
import com.schoollibrary.user.application.RegistrationException;
import com.schoollibrary.user.application.RegistrationResult;
import com.schoollibrary.user.application.UserRegistrationService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegistrationControllerTest {

    @Mock
    private UserRegistrationService registrationService;

    @Mock
    private RegistrationMapper mapper;

    @InjectMocks
    private RegistrationController registrationController;

    @Test
    @DisplayName("should register user successfully when request is valid")
    void shouldRegisterUserSuccessfully() {
        // Arrange
        RegistrationRequestDto requestDto = new RegistrationRequestDto();
        requestDto.setEmail("test@example.com");

        RegisterUserCommand command = new RegisterUserCommand();
        RegistrationResult result = new RegistrationResult("user123", "test@example.com", "Success", true);
        RegistrationResponseDto responseDto = new RegistrationResponseDto("user123", "test@example.com", "Success",
                true);

        when(mapper.toCommand(any(RegistrationRequestDto.class))).thenReturn(command);
        when(registrationService.registerUser(command)).thenReturn(result);
        when(mapper.toDto(result)).thenReturn(responseDto);

        // Act
        ResponseEntity<RegistrationResponseDto> response = registrationController.register(requestDto);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getEmail()).isEqualTo("test@example.com");
        assertThat(response.getBody().getUserId()).isEqualTo("user123");
        verify(registrationService).registerUser(command);
    }

    @Test
    @DisplayName("should return bad request when registration service throws RegistrationException")
    void shouldReturnBadRequestWhenRegistrationExceptionOccurs() {
        // Arrange
        RegistrationRequestDto requestDto = new RegistrationRequestDto();
        requestDto.setEmail("test@example.com");

        RegisterUserCommand command = new RegisterUserCommand();
        when(mapper.toCommand(any(RegistrationRequestDto.class))).thenReturn(command);
        when(registrationService.registerUser(command))
                .thenThrow(new RegistrationException("E-Mail-Adresse ist bereits registriert"));

        // Act
        ResponseEntity<RegistrationResponseDto> response = registrationController.register(requestDto);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getMessage()).isEqualTo("E-Mail-Adresse ist bereits registriert");
        assertThat(response.getBody().isVerificationRequired()).isFalse();
    }

    @Test
    @DisplayName("should return internal server error when unexpected exception occurs")
    void shouldReturnInternalServerErrorWhenUnexpectedExceptionOccurs() {
        // Arrange
        RegistrationRequestDto requestDto = new RegistrationRequestDto();
        requestDto.setEmail("test@example.com");

        RegisterUserCommand command = new RegisterUserCommand();
        when(mapper.toCommand(any(RegistrationRequestDto.class))).thenReturn(command);
        when(registrationService.registerUser(command)).thenThrow(new RuntimeException("Database down"));

        // Act
        ResponseEntity<RegistrationResponseDto> response = registrationController.register(requestDto);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getMessage())
                .isEqualTo("Registrierung aufgrund eines Serverfehlers fehlgeschlagen.");
        assertThat(response.getBody().isVerificationRequired()).isFalse();
    }

    @Test
    @DisplayName("should check email availability and return true")
    void shouldCheckEmailAvailability() {
        // Act
        ResponseEntity<EmailAvailabilityDto> response = registrationController
                .checkEmailAvailability("test@example.com");

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getEmail()).isEqualTo("test@example.com");
        assertThat(response.getBody().isAvailable()).isTrue();
    }
}
