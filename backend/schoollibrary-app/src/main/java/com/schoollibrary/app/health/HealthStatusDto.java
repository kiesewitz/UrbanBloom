package com.schoollibrary.app.health;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * DTO for health status response.
 *
 * @author Backend Team
 * @version 1.0
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class HealthStatusDto {
    private String status;

    @JsonProperty("timestamp")
    private String timestamp;

    private Map<String, String> checks = new HashMap<>();

    /**
     * Factory method to create a healthy status response.
     *
     * @return HealthStatusDto with UP status
     */
    public static HealthStatusDto healthy() {
        HealthStatusDto dto = new HealthStatusDto();
        dto.status = "UP";
        dto.timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        dto.checks.put("database", "UP");
        dto.checks.put("keycloak", "UP");
        return dto;
    }

    /**
     * Factory method to create an unhealthy status response.
     *
     * @return HealthStatusDto with DOWN status
     */
    public static HealthStatusDto unhealthy(String reason) {
        HealthStatusDto dto = new HealthStatusDto();
        dto.status = "DOWN";
        dto.timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        dto.checks.put("error", reason);
        return dto;
    }
}
