package com.schoollibrary.shared.ddd;

import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Transient;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Base class for all Aggregate Roots.
 * Aggregates are clusters of entities and value objects bound together by a root entity.
 * The aggregate root is the only external access point to the aggregate.
 *
 * @author Backend Team
 * @version 1.0
 */
@MappedSuperclass
public abstract class AggregateRoot extends Entity {
    @Transient
    private final List<DomainEvent> domainEvents = new ArrayList<>();

    /**
     * Constructor for creating an aggregate with a new unique ID.
     */
    protected AggregateRoot() {
        super();
    }

    /**
     * Constructor for creating an aggregate with a specific ID.
     *
     * @param id the aggregate's unique identifier
     */
    protected AggregateRoot(String id) {
        super(id);
    }

    /**
     * Register a domain event that occurred in this aggregate.
     *
     * @param event the domain event to register
     */
    protected void registerEvent(DomainEvent event) {
        domainEvents.add(event);
    }

    /**
     * Get all domain events that occurred in this aggregate.
     * This is typically called by the application service to publish events.
     *
     * @return immutable list of domain events
     */
    public List<DomainEvent> getDomainEvents() {
        return Collections.unmodifiableList(domainEvents);
    }

    /**
     * Clear all domain events after they have been published.
     * Should be called by the infrastructure layer after event publication.
     */
    public void clearDomainEvents() {
        domainEvents.clear();
    }
}
