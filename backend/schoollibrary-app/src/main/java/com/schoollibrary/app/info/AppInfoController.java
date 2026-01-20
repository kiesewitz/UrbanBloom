package com.schoollibrary.app.info;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import lombok.extern.slf4j.Slf4j;

/**
 * Application Information Controller.
 * Provides endpoints to retrieve application metadata and information.
 *
 * @author Backend Team
 * @version 1.0
 */
@Slf4j
@RestController
@RequestMapping("/api/v1")
public class AppInfoController {

    /**
     * Get basic application information.
     *
     * @return ResponseEntity with application info
     */
    @GetMapping("/app/info")
    public ResponseEntity<AppInfoDto> getAppInfo() {
        log.info("Application info requested");
        return ResponseEntity.ok(AppInfoDto.create());
    }
}
