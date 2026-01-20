package com.schoollibrary.shared.ddd;

import java.util.List;

/**
 * Port interface for publishing domain events.
 * Implementations should handle event distribution to interested subscribers.
 *
 * @author Backend Team
 * @version 1.0
 */
public interface DomainEventPublisher {
    /**
     * Publish a single domain event.
     *
     * @param event the domain event to publish
     */
    void publish(DomainEvent event);

    /**
     * Publish multiple domain events.
     *
     * @param events list of domain events to publish
     */
    void publishAll(List<DomainEvent> events);
}
