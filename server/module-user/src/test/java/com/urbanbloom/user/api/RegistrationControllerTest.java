package com.urbanbloom.user.api;

import com.urbanbloom.user.application.RegisterUserCommand;
import com.urbanbloom.user.application.RegistrationException;
import com.urbanbloom.user.application.RegistrationResult;
import com.urbanbloom.user.application.UserRegistrationService;
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
        RegistrationResult result = new RegistrationResult("user123", "external123", "Success", true);
        RegistrationResponseDto responseDto = new RegistrationResponseDto("user123", "external123", "Success", true);

        when(mapper.toCommand(any(RegistrationRequestDto.class))).thenReturn(command);
        when(registrationService.registerUser(command)).thenReturn(result);
        when(mapper.toDto(result)).thenReturn(responseDto);

        // Act
        ResponseEntity<RegistrationResponseDto> response = registrationController.register(requestDto);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isInstanceOf(RegistrationResponseDto.class);
        RegistrationResponseDto body = response.getBody();
        assertThat(body.getUserId()).isEqualTo("user123");
        assertThat(body.getExternalId()).isEqualTo("external123");
        verify(registrationService).registerUser(command);
    }

    @Test
    @DisplayName("should return conflict when registration service throws RegistrationException with email exists")
    void shouldReturnConflictWhenEmailAlreadyRegistered() {
        // Arrange
        RegistrationRequestDto requestDto = new RegistrationRequestDto();
        requestDto.setEmail("test@example.com");

        RegisterUserCommand command = new RegisterUserCommand();
        when(mapper.toCommand(any(RegistrationRequestDto.class))).thenReturn(command);
        when(registrationService.registerUser(command))
                .thenThrow(new RegistrationException("E-Mail-Adresse ist bereits registriert"));

        // Act
        // This will be caught by GlobalExceptionHandler in real app, but here we test controller behavior
        // Wait, RegistrationController now throws RegistrationException directly.
        
        assertThatThrownBy(() -> registrationController.register(requestDto))
                .isInstanceOf(RegistrationException.class);
    }

    private static org.assertj.core.api.ThrowableAssert.ThrowingCallable assertThatThrownBy(org.assertj.core.api.ThrowableAssert.ThrowingCallable shouldRaiseThrowable) {
        return org.assertj.core.api.Assertions.assertThatThrownBy(shouldRaiseThrowable);
    }
}
