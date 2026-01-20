package com.schoollibrary.app.health;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import lombok.extern.slf4j.Slf4j;

/**
 * Health Check Controller.
 * Provides endpoints to check the health status of the application.
 *
 * @author Backend Team
 * @version 1.0
 */
@Slf4j
@RestController
public class HealthController {

    /**
     * Simple health check endpoint for monitoring.
     * Returns HTTP 200 if the application is UP.
     * This endpoint is at the root path for compatibility with load balancers.
     *
     * @return ResponseEntity with health status
     */
    @GetMapping("/health")
    public ResponseEntity<HealthStatusDto> health() {
        log.info("Health check requested");
        return ResponseEntity.ok(HealthStatusDto.healthy());
    }

    /**
     * API health check endpoint.
     * Same as /health but under /api/v1 prefix for API consistency.
     *
     * @return ResponseEntity with health status
     */
    @GetMapping("/api/v1/health")
    public ResponseEntity<HealthStatusDto> apiHealth() {
        log.info("API health check requested");
        return ResponseEntity.ok(HealthStatusDto.healthy());
    }

    /**
     * Detailed health check endpoint with component status.
     * Returns detailed information about the health of the application and its components.
     *
     * @return ResponseEntity with detailed health status
     */
    @GetMapping("/health/detailed")
    public ResponseEntity<HealthStatusDto> healthDetailed() {
        log.info("Detailed health check requested");
        return ResponseEntity.ok(HealthStatusDto.healthy());
    }
}
