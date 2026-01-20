package com.schoollibrary.user.domain;

import com.schoollibrary.shared.ddd.DomainEvent;

import java.time.Instant;
import java.util.Objects;

/**
 * Domain event published when a user profile is reactivated.
 */
public final class UserProfileReactivatedEvent extends DomainEvent {
    private final String aggregateId;
    private final Email email;
    private final Instant occurredOn;

    public UserProfileReactivatedEvent(String aggregateId, Email email) {
        super();
        this.aggregateId = aggregateId;
        this.email = email;
        this.occurredOn = Instant.now();
    }

    public String getAggregateId() {
        return aggregateId;
    }

    public Email getEmail() {
        return email;
    }

    public Instant getOccurredOn() {
        return occurredOn;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserProfileReactivatedEvent that = (UserProfileReactivatedEvent) o;
        return Objects.equals(aggregateId, that.aggregateId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(aggregateId);
    }

    @Override
    public String toString() {
        return "UserProfileReactivatedEvent{" +
                "aggregateId='" + aggregateId + '\'' +
                ", email=" + email +
                ", occurredOn=" + occurredOn +
                '}';
    }
}
