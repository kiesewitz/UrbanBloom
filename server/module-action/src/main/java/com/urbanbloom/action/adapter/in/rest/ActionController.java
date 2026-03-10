package com.urbanbloom.action.adapter.in.rest;

import com.urbanbloom.action.application.ActionApplicationService;
import com.urbanbloom.action.domain.LocationVO;
import com.urbanbloom.action.domain.PlantVO;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * REST Controller for green actions.
 */
@RestController
@RequestMapping("/api/v1/actions")
@RequiredArgsConstructor
public class ActionController {

    private final ActionApplicationService actionService;

    @PostMapping
    public ResponseEntity<ActionResponseDto> createAction(
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody CreateActionRequestDto request) {
        
        UUID userId = UUID.fromString(jwt.getSubject());
        
        String actionId = actionService.createAction(
                userId,
                new PlantVO(request.getPlantId(), request.getPlantName(), request.getScientificName()),
                new LocationVO(request.getLatitude(), request.getLongitude(), request.getAddress(), request.getDistrictId()),
                request.getDescription()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(new ActionResponseDto(actionId));
    }

    @PostMapping("/{actionId}/verify")
    public ResponseEntity<Void> verifyAction(@PathVariable String actionId) {
        actionService.verifyAction(actionId);
        return ResponseEntity.ok().build();
    }

    @Data
    public static class CreateActionRequestDto {
        private UUID plantId;
        private String plantName;
        private String scientificName;
        private double latitude;
        private double longitude;
        private String address;
        private UUID districtId;
        private String description;
    }

    @Data
    @RequiredArgsConstructor
    public static class ActionResponseDto {
        private final String actionId;
    }
}
