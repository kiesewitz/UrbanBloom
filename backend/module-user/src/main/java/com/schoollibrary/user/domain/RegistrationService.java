package com.schoollibrary.user.domain;

import java.util.List;

/**
 * Domain service for managing self-registration.
 * Encapsulates business rules for user registration.
 */
public interface RegistrationService {

    /**
     * Validates if registration is allowed for the given email.
     *
     * @param email the email to validate
     * @param allowedDomains list of allowed domains
     * @return true if registration is allowed
     */
    boolean isRegistrationAllowed(Email email, List<AllowedDomain> allowedDomains);

    /**
     * Determines the role for a new user based on their email and business rules.
     * 
     * <p>Default rules:</p>
     * <ul>
     *   <li>Users are assigned STUDENT role by default</li>
     *   <li>TEACHER and LIBRARIAN roles must be assigned by admins post-registration</li>
     * </ul>
     *
     * @param email the user's email
     * @return the role to assign
     */
    UserRole determineInitialRole(Email email);
}
