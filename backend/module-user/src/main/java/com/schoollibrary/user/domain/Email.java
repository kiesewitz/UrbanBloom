package com.schoollibrary.user.domain;

import com.schoollibrary.shared.ddd.ValueObject;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Email value object representing a valid email address.
 * Enforces email format validation as domain invariant.
 */
public final class Email extends ValueObject {
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );

    private final String value;

    /**
     * Creates a new Email value object.
     *
     * @param value the email address
     * @throws IllegalArgumentException if email format is invalid
     */
    public Email(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Email cannot be null or blank");
        }
        String trimmedValue = value.trim();
        if (!EMAIL_PATTERN.matcher(trimmedValue).matches()) {
            throw new IllegalArgumentException("Invalid email format: " + trimmedValue);
        }
        this.value = trimmedValue.toLowerCase();
    }

    /**
     * Factory method to create Email instance.
     *
     * @param value the email address
     * @return Email instance
     */
    public static Email of(String value) {
        return new Email(value);
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Email email = (Email) o;
        return Objects.equals(value, email.value);
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
