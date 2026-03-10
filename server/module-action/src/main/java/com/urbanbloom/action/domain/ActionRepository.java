package com.urbanbloom.action.domain;

import java.util.Optional;
import java.util.UUID;
import java.util.List;

/**
 * Repository interface for Action aggregates.
 */
public interface ActionRepository {
    void save(Action action);
    Optional<Action> findById(String id);
    List<Action> findByUserId(UUID userId);
}
