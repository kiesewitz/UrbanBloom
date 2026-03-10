package com.urbanbloom.gamification.domain;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for UserPoints aggregate.
 */
public interface UserPointsRepository {
    void save(UserPoints userPoints);
    Optional<UserPoints> findByUserId(UUID userId);
}
