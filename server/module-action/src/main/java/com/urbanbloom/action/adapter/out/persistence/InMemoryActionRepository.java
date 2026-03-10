package com.urbanbloom.action.adapter.out.persistence;

import com.urbanbloom.action.domain.Action;
import com.urbanbloom.action.domain.ActionRepository;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * In-memory repository for Action aggregates.
 */
@Repository
public class InMemoryActionRepository implements ActionRepository {

    private final Map<String, Action> actions = new ConcurrentHashMap<>();

    @Override
    public void save(Action action) {
        actions.put(action.getId(), action);
    }

    @Override
    public Optional<Action> findById(String id) {
        return Optional.ofNullable(actions.get(id));
    }

    @Override
    public List<Action> findByUserId(UUID userId) {
        return actions.values().stream()
                .filter(action -> action.getUserId().equals(userId))
                .collect(Collectors.toList());
    }
}
