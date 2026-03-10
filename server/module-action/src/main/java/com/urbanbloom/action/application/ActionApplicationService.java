package com.urbanbloom.action.application;

import com.urbanbloom.action.domain.*;
import com.urbanbloom.shared.ddd.DomainEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Application service for managing actions.
 */
@Service
@RequiredArgsConstructor
public class ActionApplicationService {

    private final ActionRepository actionRepository;
    private final DomainEventPublisher eventPublisher;

    @Transactional
    public String createAction(UUID userId, PlantVO plant, LocationVO location, String description) {
        Action action = Action.create(userId, plant, location, description);
        actionRepository.save(action);
        publishEvents(action);
        return action.getId();
    }

    @Transactional
    public void uploadPhoto(String actionId, String photoUrl) {
        Action action = actionRepository.findById(actionId)
                .orElseThrow(() -> new IllegalArgumentException("Action not found: " + actionId));
        
        action.uploadPhoto(photoUrl);
        actionRepository.save(action);
        publishEvents(action);
    }

    @Transactional
    public void verifyAction(String actionId) {
        Action action = actionRepository.findById(actionId)
                .orElseThrow(() -> new IllegalArgumentException("Action not found: " + actionId));
        
        action.submitForVerification();
        actionRepository.save(action);
        publishEvents(action);
    }

    private void publishEvents(Action action) {
        eventPublisher.publishAll(action.getDomainEvents());
        action.clearDomainEvents();
    }
}
