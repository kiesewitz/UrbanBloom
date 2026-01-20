package com.schoollibrary.user.domain;

import com.schoollibrary.shared.ddd.DomainEvent;

import java.time.Instant;
import java.util.Objects;

/**
 * Domain event published when a new user profile is created.
 */
public final class UserProfileCreatedEvent extends DomainEvent {
    private final String aggregateId;
    private final ExternalUserId externalUserId;
    private final Email email;
    private final UserRole role;
    private final Instant occurredOn;

    public UserProfileCreatedEvent(String aggregateId, ExternalUserId externalUserId, Email email, UserRole role) {
        super();
        this.aggregateId = aggregateId;
        this.externalUserId = externalUserId;
        this.email = email;
        this.role = role;
        this.occurredOn = Instant.now();
    }

    public String getAggregateId() {
        return aggregateId;
    }

    public ExternalUserId getExternalUserId() {
        return externalUserId;
    }

    public Email getEmail() {
        return email;
    }

    public UserRole getRole() {
        return role;
    }

    public Instant getOccurredOn() {
        return occurredOn;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserProfileCreatedEvent that = (UserProfileCreatedEvent) o;
        return Objects.equals(aggregateId, that.aggregateId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(aggregateId);
    }

    @Override
    public String toString() {
        return "UserProfileCreatedEvent{" +
                "aggregateId='" + aggregateId + '\'' +
                ", email=" + email +
                ", role=" + role +
                ", occurredOn=" + occurredOn +
                '}';
    }
}
