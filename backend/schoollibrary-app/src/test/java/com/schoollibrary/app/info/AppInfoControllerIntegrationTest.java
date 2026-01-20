package com.schoollibrary.app.info;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration Tests for App Info Controller.
 *
 * @author Backend Team
 * @version 1.0
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("App Info Controller Integration Tests")
class AppInfoControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("GET /api/v1/app/info should return application information")
    void testAppInfoEndpoint() throws Exception {
        mockMvc.perform(get("/api/v1/app/info"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("Digital School Library"))
            .andExpect(jsonPath("$.version").value("0.0.1-SNAPSHOT"))
            .andExpect(jsonPath("$.bounded_contexts").isArray())
            .andExpect(jsonPath("$.bounded_contexts", hasItems(
                "lending", "catalog", "notification", "reminding", "user"
            )));
    }

    @Test
    @DisplayName("GET /api/v1/app/info should contain all required contexts")
    void testAppInfoContainsAllContexts() throws Exception {
        mockMvc.perform(get("/api/v1/app/info"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.bounded_contexts").isArray())
            .andExpect(jsonPath("$.bounded_contexts.length()").value(5));
    }
}
