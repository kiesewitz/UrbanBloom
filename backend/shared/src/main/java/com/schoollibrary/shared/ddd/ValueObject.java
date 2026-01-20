package com.schoollibrary.shared.ddd;

import java.util.Objects;
import java.util.UUID;

/**
 * Base class for all Value Objects.
 * Value Objects are immutable objects with no identity, compared by their values.
 *
 * @author Backend Team
 * @version 1.0
 */
public abstract class ValueObject {
    /**
     * Generate a new UUID as a unique identifier.
     *
     * @return UUID string
     */
    protected static String generateId() {
        return UUID.randomUUID().toString();
    }

    /**
     * Check equality based on value, not identity.
     *
     * @param o object to compare
     * @return true if both objects have equal values
     */
    @Override
    public abstract boolean equals(Object o);

    @Override
    public abstract int hashCode();

    @Override
    public abstract String toString();
}
