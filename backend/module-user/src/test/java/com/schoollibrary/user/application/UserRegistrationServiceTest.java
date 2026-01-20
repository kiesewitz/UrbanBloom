package com.schoollibrary.user.application;

import com.schoollibrary.user.domain.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserRegistrationServiceTest {

    @Mock
    private IdentityProvider identityProvider;

    @Mock
    private UserProfileRepository userProfileRepository;

    @Mock
    private RegistrationService registrationService;

    @Captor
    private ArgumentCaptor<UserProfile> userProfileCaptor;

    private UserRegistrationService service;

    @BeforeEach
    void setUp() {
        service = new UserRegistrationService(identityProvider, userProfileRepository, registrationService);
        // Mock allowed domains
        setAllowedDomains(List.of("schule.de"));
    }

    private void setAllowedDomains(List<String> domains) {
        // Use reflection to set the field for testing
        try {
            var field = UserRegistrationService.class.getDeclaredField("allowedDomainStrings");
            field.setAccessible(true);
            field.set(service, domains);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void shouldSuccessfullyRegisterNewUser() {
        // Given
        RegisterUserCommand command = createValidCommand();
        String externalUserId = "keycloak-user-id-123";

        when(registrationService.isRegistrationAllowed(any(Email.class), anyList())).thenReturn(true);
        when(registrationService.determineInitialRole(any(Email.class))).thenReturn(UserRole.STUDENT);
        when(identityProvider.isEmailRegistered(anyString())).thenReturn(false);
        when(userProfileRepository.existsByEmail(any(Email.class))).thenReturn(false);
        when(identityProvider.createUser(anyString(), anyString(), anyString(), anyString(), anyMap()))
                .thenReturn(externalUserId);
        when(userProfileRepository.save(any(UserProfile.class))).thenAnswer(i -> i.getArgument(0));

        // When
        RegistrationResult result = service.registerUser(command);

        // Then
        assertThat(result.getUserId()).isEqualTo(externalUserId);
        assertThat(result.getEmail()).isEqualTo(command.getEmail());
        assertThat(result.isVerificationRequired()).isTrue();

        verify(identityProvider).createUser(
                eq(command.getEmail()),
                eq(command.getPassword()),
                eq(command.getFirstName()),
                eq(command.getLastName()),
                anyMap());
        verify(identityProvider).assignRole(externalUserId, "STUDENT");
        verify(identityProvider).sendVerificationEmail(externalUserId);
        verify(userProfileRepository).save(any(UserProfile.class));
    }

    @Test
    void shouldThrowExceptionWhenEmailDomainNotAllowed() {
        // Given
        RegisterUserCommand command = createValidCommand();
        when(registrationService.isRegistrationAllowed(any(Email.class), anyList())).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> service.registerUser(command))
                .isInstanceOf(RegistrationException.class)
                .hasMessageContaining("E-Mail-Domäne ist nicht zulässig");

        verify(identityProvider, never()).createUser(anyString(), anyString(), anyString(), anyString(), anyMap());
    }

    @Test
    void shouldThrowExceptionWhenEmailAlreadyRegisteredInIdP() {
        // Given
        RegisterUserCommand command = createValidCommand();
        when(registrationService.isRegistrationAllowed(any(Email.class), anyList())).thenReturn(true);
        when(identityProvider.isEmailRegistered(command.getEmail())).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> service.registerUser(command))
                .isInstanceOf(RegistrationException.class)
                .hasMessageContaining("E-Mail-Adresse ist bereits registriert");

        verify(identityProvider, never()).createUser(anyString(), anyString(), anyString(), anyString(), anyMap());
    }

    @Test
    void shouldThrowExceptionWhenUserProfileAlreadyExists() {
        // Given
        RegisterUserCommand command = createValidCommand();
        when(registrationService.isRegistrationAllowed(any(Email.class), anyList())).thenReturn(true);
        when(identityProvider.isEmailRegistered(anyString())).thenReturn(false);
        when(userProfileRepository.existsByEmail(any(Email.class))).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> service.registerUser(command))
                .isInstanceOf(RegistrationException.class)
                .hasMessageContaining("Benutzerprofil existiert bereits für E-Mail");

        verify(identityProvider, never()).createUser(anyString(), anyString(), anyString(), anyString(), anyMap());
    }

    @Test
    void shouldIncludeCustomAttributesWhenProvided() {
        // Given
        RegisterUserCommand command = createValidCommand();
        command.setStudentId("S12345");
        command.setSchoolClass("10A");
        String externalUserId = "keycloak-user-id-123";

        when(registrationService.isRegistrationAllowed(any(Email.class), anyList())).thenReturn(true);
        when(registrationService.determineInitialRole(any(Email.class))).thenReturn(UserRole.STUDENT);
        when(identityProvider.isEmailRegistered(anyString())).thenReturn(false);
        when(userProfileRepository.existsByEmail(any(Email.class))).thenReturn(false);
        when(identityProvider.createUser(anyString(), anyString(), anyString(), anyString(), anyMap()))
                .thenReturn(externalUserId);
        when(userProfileRepository.save(any(UserProfile.class))).thenAnswer(i -> i.getArgument(0));

        ArgumentCaptor<Map<String, List<String>>> attributesCaptor = ArgumentCaptor.forClass(Map.class);

        // When
        service.registerUser(command);

        // Then
        verify(identityProvider).createUser(
                anyString(), anyString(), anyString(), anyString(),
                attributesCaptor.capture());

        Map<String, List<String>> attributes = attributesCaptor.getValue();
        assertThat(attributes).containsEntry("studentId", List.of("S12345"));
        assertThat(attributes).containsEntry("schoolClass", List.of("10A"));
    }

    @Test
    void shouldValidateRequiredFields() {
        // Given
        RegisterUserCommand command = new RegisterUserCommand();

        // When & Then
        assertThatThrownBy(() -> service.registerUser(command))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("E-Mail ist erforderlich.");
    }

    private RegisterUserCommand createValidCommand() {
        RegisterUserCommand command = new RegisterUserCommand();
        command.setEmail("student@schule.de");
        command.setPassword("SecurePassword123!");
        command.setFirstName("Max");
        command.setLastName("Mustermann");
        return command;
    }
}
