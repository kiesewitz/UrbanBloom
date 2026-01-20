package com.schoollibrary.shared.ddd;

import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import java.util.Objects;
import java.util.UUID;

/**
 * Base class for all Entities.
 * Entities have identity and can be mutable. Identity is maintained throughout their lifecycle.
 *
 * @author Backend Team
 * @version 1.0
 */
@MappedSuperclass
public abstract class Entity {
    @Id
    protected String id;

    /**
     * Constructor for creating an entity with a new unique ID.
     */
    protected Entity() {
        this.id = UUID.randomUUID().toString();
    }

    /**
     * Constructor for creating an entity with a specific ID.
     *
     * @param id the entity's unique identifier
     */
    protected Entity(String id) {
        this.id = Objects.requireNonNull(id, "Entity ID cannot be null");
    }

    /**
     * Get the entity's unique identifier.
     *
     * @return the entity ID
     */
    public String getId() {
        return id;
    }

    /**
     * Entities are equal if they have the same ID.
     *
     * @param o object to compare
     * @return true if both objects have the same ID
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Entity entity = (Entity) o;
        return Objects.equals(id, entity.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
