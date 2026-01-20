package com.schoollibrary.user.domain;

import com.schoollibrary.shared.ddd.DomainEvent;

import java.time.Instant;
import java.util.Objects;

/**
 * Domain event published when a user's role is changed.
 */
public final class UserRoleChangedEvent extends DomainEvent {
    private final String aggregateId;
    private final Email email;
    private final UserRole oldRole;
    private final UserRole newRole;
    private final Instant occurredOn;

    public UserRoleChangedEvent(String aggregateId, Email email, UserRole oldRole, UserRole newRole) {
        super();
        this.aggregateId = aggregateId;
        this.email = email;
        this.oldRole = oldRole;
        this.newRole = newRole;
        this.occurredOn = Instant.now();
    }

    public String getAggregateId() {
        return aggregateId;
    }

    public Email getEmail() {
        return email;
    }

    public UserRole getOldRole() {
        return oldRole;
    }

    public UserRole getNewRole() {
        return newRole;
    }

    public Instant getOccurredOn() {
        return occurredOn;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserRoleChangedEvent that = (UserRoleChangedEvent) o;
        return Objects.equals(aggregateId, that.aggregateId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(aggregateId);
    }

    @Override
    public String toString() {
        return "UserRoleChangedEvent{" +
                "aggregateId='" + aggregateId + '\'' +
                ", email=" + email +
                ", oldRole=" + oldRole +
                ", newRole=" + newRole +
                ", occurredOn=" + occurredOn +
                '}';
    }
}
