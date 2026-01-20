package com.schoollibrary.user.domain;

import com.schoollibrary.shared.ddd.ValueObject;

import java.util.Objects;

/**
 * ExternalUserId value object representing the user's ID in the external Identity Provider.
 * This is the bridge between our system and Keycloak.
 */
public final class ExternalUserId extends ValueObject {
    private final String value;

    /**
     * Creates a new ExternalUserId value object.
     *
     * @param value the external user ID from IdP
     * @throws IllegalArgumentException if value is invalid
     */
    public ExternalUserId(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("External user ID cannot be null or blank");
        }
        this.value = value.trim();
    }

    /**
     * Factory method to create ExternalUserId instance.
     *
     * @param value the external user ID
     * @return ExternalUserId instance
     */
    public static ExternalUserId of(String value) {
        return new ExternalUserId(value);
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExternalUserId that = (ExternalUserId) o;
        return Objects.equals(value, that.value);
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
