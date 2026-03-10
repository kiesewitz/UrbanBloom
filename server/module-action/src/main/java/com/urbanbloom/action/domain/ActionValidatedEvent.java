package com.urbanbloom.action.domain;

import com.urbanbloom.shared.ddd.DomainEvent;
import lombok.Getter;
import lombok.ToString;

import java.time.Instant;
import java.util.UUID;

/**
 * Event published when an action is successfully validated.
 */
@Getter
@ToString
public class ActionValidatedEvent extends DomainEvent {
    private final String actionId;
    private final UUID userId;
    private final int points;
    private final Instant occurredOn;

    public ActionValidatedEvent(String actionId, UUID userId, int points) {
        this.actionId = actionId;
        this.userId = userId;
        this.points = points;
        this.occurredOn = Instant.now();
    }
}
