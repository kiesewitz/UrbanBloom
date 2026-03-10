package com.urbanbloom.gamification.adapter.out.persistence;

import com.urbanbloom.gamification.domain.UserPoints;
import com.urbanbloom.gamification.domain.UserPointsRepository;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory repository for UserPoints aggregates.
 */
@Repository
public class InMemoryUserPointsRepository implements UserPointsRepository {

    private final Map<UUID, UserPoints> points = new ConcurrentHashMap<>();

    @Override
    public void save(UserPoints userPoints) {
        points.put(userPoints.getUserId(), userPoints);
    }

    @Override
    public Optional<UserPoints> findByUserId(UUID userId) {
        return Optional.ofNullable(points.get(userId));
    }
}
