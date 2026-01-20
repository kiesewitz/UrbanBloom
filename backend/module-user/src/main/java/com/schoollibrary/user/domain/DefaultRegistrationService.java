package com.schoollibrary.user.domain;

import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Default implementation of RegistrationService.
 * Implements the standard business rules for user registration.
 */
@Service
public class DefaultRegistrationService implements RegistrationService {

    @Override
    public boolean isRegistrationAllowed(Email email, List<AllowedDomain> allowedDomains) {
        if (email == null || allowedDomains == null || allowedDomains.isEmpty()) {
            return false;
        }

        return allowedDomains.stream()
                .anyMatch(domain -> domain.matches(email));
    }

    @Override
    public UserRole determineInitialRole(Email email) {
        // Default rule: all self-registered users start as STUDENT
        // Teachers/Librarians must be promoted by admins post-registration
        return UserRole.STUDENT;
    }
}
