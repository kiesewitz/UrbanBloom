package com.schoollibrary.user.domain;

import com.schoollibrary.shared.ddd.ValueObject;

import java.util.Objects;

/**
 * UserName value object representing a person's first and last name.
 * Enforces name validation as domain invariant.
 */
public final class UserName extends ValueObject {
    private final String firstName;
    private final String lastName;

    /**
     * Creates a new UserName value object.
     *
     * @param firstName the first name
     * @param lastName  the last name
     * @throws IllegalArgumentException if names are invalid
     */
    public UserName(String firstName, String lastName) {
        if (firstName == null || firstName.isBlank()) {
            throw new IllegalArgumentException("First name cannot be null or blank");
        }
        if (lastName == null || lastName.isBlank()) {
            throw new IllegalArgumentException("Last name cannot be null or blank");
        }
        if (firstName.length() < 2 || firstName.length() > 50) {
            throw new IllegalArgumentException("First name must be between 2 and 50 characters");
        }
        if (lastName.length() < 2 || lastName.length() > 50) {
            throw new IllegalArgumentException("Last name must be between 2 and 50 characters");
        }
        this.firstName = firstName.trim();
        this.lastName = lastName.trim();
    }

    /**
     * Factory method to create UserName instance.
     *
     * @param firstName the first name
     * @param lastName the last name
     * @return UserName instance
     */
    public static UserName of(String firstName, String lastName) {
        return new UserName(firstName, lastName);
    }

    /**
     * Returns the full name in "FirstName LastName" format.
     *
     * @return full name
     */
    public String getFullName() {
        return firstName + " " + lastName;
    }

    /**
     * Returns the full name in "LastName, FirstName" format.
     *
     * @return formal full name
     */
    public String getFormalName() {
        return lastName + ", " + firstName;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserName userName = (UserName) o;
        return Objects.equals(firstName, userName.firstName) && 
               Objects.equals(lastName, userName.lastName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(firstName, lastName);
    }

    @Override
    public String toString() {
        return getFullName();
    }
}
