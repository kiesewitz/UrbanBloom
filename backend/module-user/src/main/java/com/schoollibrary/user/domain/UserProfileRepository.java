package com.schoollibrary.user.domain;

import java.util.Optional;

/**
 * Repository port (interface) for UserProfile aggregate.
 * This is the domain's abstraction for persistence operations.
 * Implementation will be in the adapter layer (out/persistence).
 *
 * <p>Following Hexagonal Architecture principles:</p>
 * <ul>
 *   <li>Domain defines the port (interface)</li>
 *   <li>Adapter layer implements the port (JPA adapter)</li>
 *   <li>Application services depend on the port, not the implementation</li>
 * </ul>
 *
 * @author Backend Team
 * @version 1.0
 */
public interface UserProfileRepository {

    /**
     * Saves a user profile (create or update).
     *
     * @param userProfile the user profile to save
     * @return the saved user profile
     */
    UserProfile save(UserProfile userProfile);

    /**
     * Finds a user profile by its unique ID.
     *
     * @param id the aggregate ID
     * @return Optional containing the user profile, or empty if not found
     */
    Optional<UserProfile> findById(String id);

    /**
     * Finds a user profile by external user ID (from Identity Provider).
     *
     * @param externalUserId the external user ID
     * @return Optional containing the user profile, or empty if not found
     */
    Optional<UserProfile> findByExternalUserId(ExternalUserId externalUserId);

    /**
     * Finds a user profile by email address.
     *
     * @param email the email address
     * @return Optional containing the user profile, or empty if not found
     */
    Optional<UserProfile> findByEmail(Email email);

    /**
     * Checks if a user profile exists with the given email.
     *
     * @param email the email address
     * @return true if a profile exists with this email
     */
    boolean existsByEmail(Email email);

    /**
     * Checks if a user profile exists with the given external user ID.
     *
     * @param externalUserId the external user ID
     * @return true if a profile exists with this external user ID
     */
    boolean existsByExternalUserId(ExternalUserId externalUserId);

    /**
     * Deletes a user profile.
     *
     * @param userProfile the user profile to delete
     */
    void delete(UserProfile userProfile);

    /**
     * Deletes a user profile by ID.
     *
     * @param id the aggregate ID
     */
    void deleteById(String id);
}
