package com.urbanbloom.action.domain;

import com.urbanbloom.shared.ddd.AggregateRoot;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

/**
 * Action aggregate root representing a green action performed by a user.
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Action extends AggregateRoot {

    private UUID userId;
    private PlantVO plant;
    private LocationVO location;
    private String description;
    private String photoUrl;
    private ActionStatus status;
    private Integer pointsAwarded;
    private Instant createdAt;
    private Instant verifiedAt;

    /**
     * Factory method to create a new action.
     */
    public static Action create(UUID userId, PlantVO plant, LocationVO location, String description) {
        Action action = new Action();
        action.userId = userId;
        action.plant = plant;
        action.location = location;
        action.description = description;
        action.status = ActionStatus.DRAFT;
        action.createdAt = Instant.now();
        return action;
    }

    /**
     * Set the photo for the action.
     */
    public void uploadPhoto(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    /**
     * Submit the action for verification.
     */
    public void submitForVerification() {
        if (this.photoUrl == null || this.photoUrl.isBlank()) {
            throw new IllegalStateException("Cannot verify action without a photo");
        }
        // In a real system, this might trigger an asynchronous verification process
        // For now, we simulate immediate validation for some actions
        validate();
    }

    /**
     * Mark the action as validated.
     */
    public void validate() {
        this.status = ActionStatus.VALIDATED;
        this.verifiedAt = Instant.now();
        this.pointsAwarded = calculatePoints();
        
        // Publish event for Gamification module
        registerEvent(new ActionValidatedEvent(getId(), userId, pointsAwarded));
    }

    /**
     * Mark the action as rejected.
     */
    public void reject(String reason) {
        this.status = ActionStatus.REJECTED;
    }

    private int calculatePoints() {
        // Simple points logic: 10 points per action
        return 10;
    }
}
