package com.urbanbloom.gamification.application;

import com.urbanbloom.gamification.domain.UserPoints;
import com.urbanbloom.gamification.domain.UserPointsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Application service for gamification logic.
 */
@Service
@RequiredArgsConstructor
public class GamificationApplicationService {

    private final UserPointsRepository repository;

    @Transactional
    public void awardPoints(UUID userId, int points) {
        UserPoints userPoints = repository.findByUserId(userId)
                .orElseGet(() -> new UserPoints(userId));
        
        userPoints.addPoints(points);
        repository.save(userPoints);
    }

    public UserPoints getUserPoints(UUID userId) {
        return repository.findByUserId(userId)
                .orElseGet(() -> new UserPoints(userId));
    }
}
