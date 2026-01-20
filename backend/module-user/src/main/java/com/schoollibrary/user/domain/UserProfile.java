package com.urbanbloom.user.domain;

import com.urbanbloom.shared.ddd.AggregateRoot;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * UserProfile aggregate root.
 * Represents a user in the UrbanBloom system and manages their profile lifecycle.
 *
 * <p>Business Rules:</p>
 * <ul>
 *   <li>A user profile must have a valid external ID from the Identity Provider</li>
 *   <li>Email must be unique across all profiles</li>
 *   <li>Only active users can perform actions</li>
 *   <li>Deactivated users cannot be deactivated again</li>
 *   <li>Only deactivated users can be reactivated</li>
 * </ul>
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // For ORM frameworks
public class UserProfile extends AggregateRoot {

    private ExternalUserId externalUserId;
    private Email email;
    private UserName userName;
    private int points;
    private boolean active;

    /**
     * Creates a new user profile (Factory Method).
     * This represents the user registration process.
     *
     * @param externalUserId the external user ID from Identity Provider
     * @param email          the user's email
     * @param userName       the user's name
     * @return new UserProfile instance
     * @throws IllegalArgumentException if any parameter is null
     */
    public static UserProfile create(ExternalUserId externalUserId, Email email, UserName userName) {
        if (externalUserId == null || email == null || userName == null) {
            throw new IllegalArgumentException("All parameters must be non-null");
        }

        UserProfile profile = new UserProfile();
        profile.externalUserId = externalUserId;
        profile.email = email;
        profile.userName = userName;
        profile.points = 0;
        profile.active = true;

        // Publish domain event
        profile.registerEvent(new UserProfileCreatedEvent(
                profile.getId(),
                externalUserId,
                email
        ));

        return profile;
    }

    /**
     * Adds points to the user's profile.
     *
     * @param pointsToAdd the number of points to add
     * @throws IllegalArgumentException if pointsToAdd is negative
     */
    public void addPoints(int pointsToAdd) {
        if (pointsToAdd < 0) {
            throw new IllegalArgumentException("Points to add must be non-negative");
        }
        this.points += pointsToAdd;
    }

    /**
     * Deactivates this user profile.
     *
     * @throws IllegalStateException if user is already deactivated
     */
    public void deactivate() {
        if (!active) {
            throw new IllegalStateException("User profile is already deactivated");
        }
        this.active = false;
        registerEvent(new UserProfileDeactivatedEvent(getId(), email));
    }

    /**
     * Reactivates this user profile.
     *
     * @throws IllegalStateException if user is already active
     */
    public void reactivate() {
        if (active) {
            throw new IllegalStateException("User profile is already active");
        }
        this.active = true;
        registerEvent(new UserProfileReactivatedEvent(getId(), email));
    }
}
