package com.schoollibrary.shared.ddd;

/**
 * Base class for all Domain Events.
 * Domain Events represent something important that has happened in the domain.
 *
 * @author Backend Team
 * @version 1.0
 */
public abstract class DomainEvent {
    private final long timestamp = System.currentTimeMillis();

    /**
     * Get the timestamp when this event was created.
     *
     * @return timestamp in milliseconds since epoch
     */
    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public abstract String toString();
}
