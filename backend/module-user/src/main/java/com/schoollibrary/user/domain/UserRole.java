package com.schoollibrary.user.domain;

import com.schoollibrary.shared.ddd.ValueObject;

import java.util.Arrays;
import java.util.Objects;

/**
 * UserRole value object representing valid user roles in the system.
 * Enforces allowed roles as domain invariant.
 */
public final class UserRole extends ValueObject {
    public static final UserRole STUDENT = new UserRole("STUDENT");
    public static final UserRole TEACHER = new UserRole("TEACHER");
    public static final UserRole LIBRARIAN = new UserRole("LIBRARIAN");
    public static final UserRole ADMIN = new UserRole("ADMIN");

    private final String value;

    /**
     * Creates a new UserRole value object.
     *
     * @param value the role name
     * @throws IllegalArgumentException if role is not valid
     */
    public UserRole(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Role cannot be null or blank");
        }
        String normalized = value.toUpperCase().trim();
        if (!isValidRole(normalized)) {
            throw new IllegalArgumentException("Invalid role: " + value + 
                ". Allowed roles: STUDENT, TEACHER, LIBRARIAN, ADMIN");
        }
        this.value = normalized;
    }

    /**
     * Creates a UserRole from string value.
     *
     * @param value the role string
     * @return UserRole instance
     */
    public static UserRole of(String value) {
        return new UserRole(value);
    }

    /**
     * Returns the role name.
     *
     * @return role name
     */
    public String name() {
        return value;
    }

    private static boolean isValidRole(String role) {
        return Arrays.asList("STUDENT", "TEACHER", "LIBRARIAN", "ADMIN").contains(role);
    }

    /**
     * Checks if this role has administrative privileges.
     *
     * @return true if role is ADMIN or LIBRARIAN
     */
    public boolean hasAdministrativePrivileges() {
        return value.equals("ADMIN") || value.equals("LIBRARIAN");
    }

    /**
     * Checks if this role can borrow books.
     *
     * @return true if role is STUDENT or TEACHER
     */
    public boolean canBorrowBooks() {
        return value.equals("STUDENT") || value.equals("TEACHER");
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserRole userRole = (UserRole) o;
        return Objects.equals(value, userRole.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
