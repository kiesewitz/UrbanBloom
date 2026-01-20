package com.schoollibrary.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * School Library Main Application Entry Point.
 *
 * @author Backend Team
 * @version 1.0
 */
@SpringBootApplication
@ComponentScan(basePackages = {
    "com.schoollibrary.app",
    "com.schoollibrary.lending",
    "com.schoollibrary.catalog",
    "com.schoollibrary.notification",
    "com.schoollibrary.reminding",
    "com.schoollibrary.user"
})
@EnableJpaRepositories(basePackages = {
    "com.schoollibrary.user.adapter.persistence",
    "com.schoollibrary.lending.adapter.persistence",
    "com.schoollibrary.catalog.adapter.persistence",
    "com.schoollibrary.notification.adapter.persistence",
    "com.schoollibrary.reminding.adapter.persistence"
})
@EntityScan(basePackages = {
    "com.schoollibrary.user.adapter.persistence",
    "com.schoollibrary.lending.adapter.persistence",
    "com.schoollibrary.catalog.adapter.persistence",
    "com.schoollibrary.notification.adapter.persistence",
    "com.schoollibrary.reminding.adapter.persistence"
})
public class SchoolLibraryApplication {

    public static void main(String[] args) {
        SpringApplication.run(SchoolLibraryApplication.class, args);
    }
}
