package com.urbanbloom.app.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for OpenAPI documentation (Swagger).
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI urbanBloomOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("UrbanBloom API")
                        .description("Citizen-science platform for urban green spaces")
                        .version("v1.0.0")
                        .contact(new Contact()
                                .name("UrbanBloom Support")
                                .email("support@urbanbloom.local")));
    }
}
