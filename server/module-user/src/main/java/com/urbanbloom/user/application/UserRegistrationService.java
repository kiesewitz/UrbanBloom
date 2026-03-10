package com.urbanbloom.user.application;

import com.urbanbloom.shared.ddd.DomainEventPublisher;
import com.urbanbloom.user.config.RegistrationConfigProperties;
import com.urbanbloom.user.domain.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Application service for user registration.
 * Orchestrates the registration process including domain validation,
 * IdP creation, and local profile persistence.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserRegistrationService {

    private final IdentityProvider identityProvider;
    private final UserProfileRepository userProfileRepository;
    private final RegistrationService registrationService;
    private final DomainEventPublisher eventPublisher;
    private final RegistrationConfigProperties registrationConfig;

    @Value("${urbanbloom.registration.allowed-domains:*}")
    private List<String> allowedDomainStrings;

    /**
     * Registers a new user.
     *
     * @param command the registration command
     * @return registration result
     * @throws RegistrationException if registration fails
     */
    @Transactional
    public RegistrationResult registerUser(RegisterUserCommand command) {
        log.info("Starting registration for email: {}", command.getEmail());

        // 1. Validate input
        validateCommand(command);

        // 2. Create domain objects
        Email email = Email.of(command.getEmail());
        UserName userName = UserName.of(command.getFirstName(), command.getLastName());

        // 3. Check domain allowlist
        if (allowedDomainStrings != null && !allowedDomainStrings.contains("*")) {
            List<AllowedDomain> allowedDomains = allowedDomainStrings.stream()
                    .map(AllowedDomain::of)
                    .toList();

            if (!registrationService.isRegistrationAllowed(email, allowedDomains)) {
                throw new RegistrationException("E-Mail-Domäne ist nicht zulässig: " + email.getValue());
            }
        }

        // 4. Check if already registered
        if (identityProvider.isEmailRegistered(email.getValue())) {
            throw new RegistrationException("E-Mail-Adresse ist bereits registriert: " + email.getValue());
        }

        if (userProfileRepository.existsByEmail(email)) {
            throw new RegistrationException("Benutzerprofil existiert bereits für E-Mail: " + email.getValue());
        }

        // 5. Determine initial role
        UserRole initialRole = registrationService.determineInitialRole(email);

        // 6. Create user in IdP
        Map<String, List<String>> attributes = new HashMap<>();
        
        String externalUserId = identityProvider.createUser(
                email.getValue(),
                command.getPassword(),
                command.getFirstName(),
                command.getLastName(),
                attributes);

        // 7. Assign role in IdP
        identityProvider.assignRole(externalUserId, initialRole.name());

        // 8. Trigger email verification
        identityProvider.sendVerificationEmail(externalUserId);

        // 9. Create local user profile
        UserProfile userProfile = UserProfile.create(
                ExternalUserId.of(externalUserId),
                email,
                userName,
                initialRole);

        userProfileRepository.save(userProfile);

        // 10. Publish domain events
        eventPublisher.publishAll(userProfile.getDomainEvents());
        userProfile.clearDomainEvents();

        log.info("Successfully registered user with local ID: {} and external ID: {}", userProfile.getId(), externalUserId);

        return new RegistrationResult(
                userProfile.getId(),
                externalUserId,
                "Registrierung erfolgreich. Bitte prüfe dein E-Mail-Postfach, um dein Konto zu verifizieren.",
                registrationConfig.isEmailVerificationRequired());
    }

    private void validateCommand(RegisterUserCommand command) {
        if (command == null) {
            throw new IllegalArgumentException("Registrierungsbefehl darf nicht null sein.");
        }
        if (command.getEmail() == null || command.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("E-Mail ist erforderlich.");
        }
        if (command.getPassword() == null || command.getPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("Passwort ist erforderlich.");
        }
        if (command.getFirstName() == null || command.getFirstName().trim().isEmpty()) {
            throw new IllegalArgumentException("Vorname ist erforderlich.");
        }
        if (command.getLastName() == null || command.getLastName().trim().isEmpty()) {
            throw new IllegalArgumentException("Nachname ist erforderlich.");
        }
    }
}
