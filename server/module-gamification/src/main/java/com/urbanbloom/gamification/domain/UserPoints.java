package com.urbanbloom.gamification.domain;

import com.urbanbloom.shared.ddd.AggregateRoot;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * UserPoints aggregate root representing a user's gamification state.
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserPoints extends AggregateRoot {

    private UUID userId;
    private int totalPoints;
    private int level;

    public UserPoints(UUID userId) {
        this.userId = userId;
        this.totalPoints = 0;
        this.level = 1;
    }

    public void addPoints(int points) {
        this.totalPoints += points;
        updateLevel();
    }

    private void updateLevel() {
        // Simple level logic: 1 level per 100 points
        this.level = (totalPoints / 100) + 1;
    }
}
