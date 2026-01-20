package com.schoollibrary.user.domain;

import com.schoollibrary.shared.ddd.AggregateRoot;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * UserProfile aggregate root.
 * Represents a user in the school library system and manages their profile lifecycle.
 * 
 * <p>Business Rules:</p>
 * <ul>
 *   <li>A user profile must have a valid external ID from the Identity Provider</li>
 *   <li>Email must be unique across all profiles</li>
 *   <li>Only active users can perform library operations</li>
 *   <li>Deactivated users cannot be deactivated again</li>
 *   <li>Only deactivated users can be reactivated</li>
 *   <li>Role changes require valid role transitions</li>
 * </ul>
 *
 * @author Backend Team
 * @version 2.0
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // For ORM frameworks
public class UserProfile extends AggregateRoot {

    private ExternalUserId externalUserId;
    private Email email;
    private UserName userName;
    private UserRole role;
    private boolean active;

    /**
     * Creates a new user profile (Factory Method).
     * This represents the user registration process.
     *
     * @param externalUserId the external user ID from Identity Provider
     * @param email          the user's email
     * @param userName       the user's name
     * @param role           the user's role
     * @return new UserProfile instance
     * @throws IllegalArgumentException if any parameter is null
     */
    public static UserProfile create(ExternalUserId externalUserId, Email email, UserName userName, UserRole role) {
        if (externalUserId == null) {
            throw new IllegalArgumentException("External user ID cannot be null");
        }
        if (email == null) {
            throw new IllegalArgumentException("Email cannot be null");
        }
        if (userName == null) {
            throw new IllegalArgumentException("User name cannot be null");
        }
        if (role == null) {
            throw new IllegalArgumentException("Role cannot be null");
        }

        UserProfile profile = new UserProfile();
        profile.externalUserId = externalUserId;
        profile.email = email;
        profile.userName = userName;
        profile.role = role;
        profile.active = true;

        // Publish domain event
        profile.registerEvent(new UserProfileCreatedEvent(
                profile.getId(),
                externalUserId,
                email,
                role
        ));

        return profile;
    }

    /**
     * Deactivates this user profile.
     * Deactivated users cannot perform any library operations.
     *
     * @throws IllegalStateException if user is already deactivated
     */
    public void deactivate() {
        if (!active) {
            throw new IllegalStateException("User profile is already deactivated");
        }
        
        this.active = false;
        
        registerEvent(new UserProfileDeactivatedEvent(
                getId(),
                email
        ));
    }

    /**
     * Reactivates this user profile.
     * Allows previously deactivated users to perform library operations again.
     *
     * @throws IllegalStateException if user is already active
     */
    public void reactivate() {
        if (active) {
            throw new IllegalStateException("User profile is already active");
        }
        
        this.active = true;
        
        registerEvent(new UserProfileReactivatedEvent(
                getId(),
                email
        ));
    }

    /**
     * Changes the user's role.
     * Role changes may affect what operations the user can perform.
     *
     * @param newRole the new role
     * @throws IllegalArgumentException if newRole is null or same as current role
     * @throws IllegalStateException if user is deactivated
     */
    public void changeRole(UserRole newRole) {
        if (newRole == null) {
            throw new IllegalArgumentException("New role cannot be null");
        }
        if (this.role.equals(newRole)) {
            throw new IllegalArgumentException("New role is the same as current role");
        }
        if (!active) {
            throw new IllegalStateException("Cannot change role of deactivated user");
        }

        UserRole oldRole = this.role;
        this.role = newRole;

        registerEvent(new UserRoleChangedEvent(
                getId(),
                email,
                oldRole,
                newRole
        ));
    }

    /**
     * Updates the user's name.
     *
     * @param newName the new name
     * @throws IllegalArgumentException if newName is null
     * @throws IllegalStateException if user is deactivated
     */
    public void updateName(UserName newName) {
        if (newName == null) {
            throw new IllegalArgumentException("New name cannot be null");
        }
        if (!active) {
            throw new IllegalStateException("Cannot update name of deactivated user");
        }
        this.userName = newName;
    }

    /**
     * Checks if this user can borrow books.
     *
     * @return true if user is active and has borrowing privileges
     */
    public boolean canBorrowBooks() {
        return active && role.canBorrowBooks();
    }

    /**
     * Checks if this user has administrative privileges.
     *
     * @return true if user is active and has admin/librarian role
     */
    public boolean hasAdministrativePrivileges() {
        return active && role.hasAdministrativePrivileges();
    }

    /**
     * Checks if this user profile can be deactivated.
     *
     * @return true if user is currently active
     */
    public boolean canBeDeactivated() {
        return active;
    }

    /**
     * Checks if this user profile can be reactivated.
     *
     * @return true if user is currently deactivated
     */
    public boolean canBeReactivated() {
        return !active;
    }

    /**
     * Gets the full display name of the user.
     *
     * @return full name
     */
    public String getFullName() {
        return userName.getFullName();
    }
}
