package com.urbanbloom.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * UrbanBloom Main Application Entry Point.
 */
@SpringBootApplication
@ComponentScan(basePackages = {
    "com.urbanbloom.app",
    "com.urbanbloom.shared",
    "com.urbanbloom.user",
    "com.urbanbloom.action",
    "com.urbanbloom.plant",
    "com.urbanbloom.location",
    "com.urbanbloom.gamification",
    "com.urbanbloom.challenge",
    "com.urbanbloom.notification",
    "com.urbanbloom.analytics",
    "com.urbanbloom.sync"
})
@EnableJpaRepositories(basePackages = {
    "com.urbanbloom.user.adapter.persistence",
    "com.urbanbloom.action.adapter.persistence",
    "com.urbanbloom.plant.adapter.persistence",
    "com.urbanbloom.location.adapter.persistence",
    "com.urbanbloom.gamification.adapter.persistence",
    "com.urbanbloom.challenge.adapter.persistence",
    "com.urbanbloom.notification.adapter.persistence",
    "com.urbanbloom.analytics.adapter.persistence",
    "com.urbanbloom.sync.adapter.persistence"
})
@EntityScan(basePackages = {
    "com.urbanbloom.user.adapter.persistence",
    "com.urbanbloom.action.adapter.persistence",
    "com.urbanbloom.plant.adapter.persistence",
    "com.urbanbloom.location.adapter.persistence",
    "com.urbanbloom.gamification.adapter.persistence",
    "com.urbanbloom.challenge.adapter.persistence",
    "com.urbanbloom.notification.adapter.persistence",
    "com.urbanbloom.analytics.adapter.persistence",
    "com.urbanbloom.sync.adapter.persistence"
})
public class UrbanBloomApplication {

    public static void main(String[] args) {
        SpringApplication.run(UrbanBloomApplication.class, args);
    }
}
