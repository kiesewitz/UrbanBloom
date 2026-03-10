package com.urbanbloom.gamification.adapter.in.event;

import com.urbanbloom.action.domain.ActionValidatedEvent;
import com.urbanbloom.gamification.application.GamificationApplicationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Listener for ActionValidatedEvent to award points.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ActionValidatedListener {

    private final GamificationApplicationService gamificationService;

    @EventListener
    @Transactional
    public void onActionValidated(ActionValidatedEvent event) {
        log.info("Reacting to ActionValidatedEvent for user: {}", event.getUserId());
        gamificationService.awardPoints(event.getUserId(), event.getPoints());
    }
}
