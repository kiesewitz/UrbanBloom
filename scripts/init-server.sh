#!/bin/bash
# Server Project Initialization Script - UrbanBloom
# This script initializes the Spring Boot backend project with DDD structure

set -e  # Exit on error

echo "=========================================="
echo "UrbanBloom Server - Project Initialization"
echo "=========================================="

# Navigate to server directory
cd "$(dirname "$0")/../server" || exit 1

echo ""
echo "Step 1: Generating Spring Boot project from start.spring.io..."
if [ -f "pom.xml" ]; then
    echo "⚠️  Maven project already exists. Skipping generation."
else
    curl https://start.spring.io/starter.zip \
        -d type=maven-project \
        -d language=java \
        -d bootVersion=3.2.0 \
        -d baseDir=. \
        -d groupId=com.urbanbloom \
        -d artifactId=urbanbloom-backend \
        -d name=UrbanBloomBackend \
        -d description="UrbanBloom backend with DDD architecture" \
        -d packageName=com.urbanbloom.backend \
        -d packaging=jar \
        -d javaVersion=17 \
        -d dependencies=web,data-jpa,postgresql,validation,actuator,flyway,security,oauth2-resource-server \
        -o project.zip

    unzip -o project.zip
    rm project.zip
fi

echo ""
echo "Step 2: Adding additional dependencies to pom.xml..."

# Check if springdoc is already in pom.xml
if grep -q "springdoc-openapi" pom.xml; then
    echo "⚠️  Additional dependencies already added. Skipping."
else
    # Insert additional dependencies before </dependencies>
    sed -i.bak '/<\/dependencies>/i\
        <!-- OpenAPI Documentation -->\
        <dependency>\
            <groupId>org.springdoc</groupId>\
            <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>\
            <version>2.3.0</version>\
        </dependency>\
        \
        <!-- TestContainers for Integration Tests -->\
        <dependency>\
            <groupId>org.testcontainers</groupId>\
            <artifactId>postgresql</artifactId>\
            <version>1.19.3</version>\
            <scope>test</scope>\
        </dependency>\
        <dependency>\
            <groupId>org.testcontainers</groupId>\
            <artifactId>junit-jupiter</artifactId>\
            <version>1.19.3</version>\
            <scope>test</scope>\
        </dependency>\
        \
        <!-- ArchUnit for Architecture Tests -->\
        <dependency>\
            <groupId>com.tngtech.archunit</groupId>\
            <artifactId>archunit-junit5</artifactId>\
            <version>1.2.1</version>\
            <scope>test</scope>\
        </dependency>\
        \
        <!-- Lombok for boilerplate reduction -->\
        <dependency>\
            <groupId>org.projectlombok</groupId>\
            <artifactId>lombok</artifactId>\
            <optional>true</optional>\
        </dependency>\
    ' pom.xml

    rm pom.xml.bak
fi

echo ""
echo "Step 3: Creating DDD package structure..."
BASE_PATH="src/main/java/com/urbanbloom/backend"
mkdir -p "$BASE_PATH/domain"/{user,action,plant,location,gamification,challenge,notification,admin,sync}
mkdir -p "$BASE_PATH/domain/user/events"
mkdir -p "$BASE_PATH/domain/action/events"
mkdir -p "$BASE_PATH/domain/gamification/events"
mkdir -p "$BASE_PATH/application"/{user,action,plant,location,gamification,challenge,notification,admin,sync}
mkdir -p "$BASE_PATH/application/user/dto"
mkdir -p "$BASE_PATH/infrastructure/persistence"/{user,action,plant,location,gamification,challenge,notification,admin,sync}
mkdir -p "$BASE_PATH/infrastructure/messaging"
mkdir -p "$BASE_PATH/infrastructure/config"
mkdir -p "$BASE_PATH/interfaces/rest"/{user,action,plant,location,gamification,challenge,notification,admin,sync}

echo ""
echo "Step 4: Creating database migration directory..."
mkdir -p src/main/resources/db/migration

echo ""
echo "Step 5: Creating initial Flyway migration for schemas..."
cat > src/main/resources/db/migration/V1__create_schemas.sql << 'EOF'
-- Create schemas for each bounded context (DDD)
CREATE SCHEMA IF NOT EXISTS user_context;
CREATE SCHEMA IF NOT EXISTS action_context;
CREATE SCHEMA IF NOT EXISTS plant_context;
CREATE SCHEMA IF NOT EXISTS location_context;
CREATE SCHEMA IF NOT EXISTS gamification_context;
CREATE SCHEMA IF NOT EXISTS challenge_context;
CREATE SCHEMA IF NOT EXISTS notification_context;
CREATE SCHEMA IF NOT EXISTS admin_context;
CREATE SCHEMA IF NOT EXISTS sync_context;

-- Set search path for default schema
ALTER DATABASE urbanbloom SET search_path TO public, user_context, action_context, plant_context, location_context, gamification_context, challenge_context, notification_context, admin_context, sync_context;
EOF

echo ""
echo "Step 6: Creating application.yml configuration..."
cat > src/main/resources/application.yml << 'EOF'
spring:
  application:
    name: urbanbloom-backend

  datasource:
    url: jdbc:postgresql://localhost:5432/urbanbloom
    username: ${DB_USER:urbanbloom}
    password: ${DB_PASSWORD:secret}
    driver-class-name: org.postgresql.Driver
    hikari:
      maximum-pool-size: 10
      minimum-idle: 5

  jpa:
    hibernate:
      ddl-auto: validate  # Use Flyway for schema management
    properties:
      hibernate:
        default_schema: public
        jdbc:
          batch_size: 20
        format_sql: true
    show-sql: false

  flyway:
    enabled: true
    baseline-on-migrate: true
    locations: classpath:db/migration
    schemas: public,user_context,action_context,plant_context,location_context,gamification_context,challenge_context,notification_context,admin_context,sync_context

  security:
    oauth2:
      resourceserver:
        jwt:
          issuer-uri: ${JWT_ISSUER_URI:http://localhost:8080}

server:
  port: 8080
  error:
    include-message: always
    include-binding-errors: always

# OpenAPI/Swagger UI
springdoc:
  api-docs:
    path: /api-docs
  swagger-ui:
    path: /swagger-ui.html
    operationsSorter: alpha
    tagsSorter: alpha

# Logging
logging:
  level:
    root: INFO
    com.urbanbloom: DEBUG
    org.hibernate.SQL: DEBUG
    org.hibernate.type.descriptor.sql.BasicBinder: TRACE

# Actuator
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics
  endpoint:
    health:
      show-details: when-authorized
EOF

echo ""
echo "Step 7: Creating base domain classes..."

# Create AggregateRoot base class
cat > "$BASE_PATH/domain/AggregateRoot.java" << 'EOF'
package com.urbanbloom.backend.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Base class for all Aggregate Roots in DDD.
 * Aggregates are responsible for maintaining invariants and publishing domain events.
 */
public abstract class AggregateRoot {
    private final List<DomainEvent> domainEvents = new ArrayList<>();

    protected void registerEvent(DomainEvent event) {
        this.domainEvents.add(event);
    }

    public List<DomainEvent> getDomainEvents() {
        return Collections.unmodifiableList(domainEvents);
    }

    public void clearEvents() {
        this.domainEvents.clear();
    }
}
EOF

# Create DomainEvent interface
cat > "$BASE_PATH/domain/DomainEvent.java" << 'EOF'
package com.urbanbloom.backend.domain;

import java.time.Instant;

/**
 * Marker interface for all domain events.
 * Domain events represent important occurrences in the domain.
 */
public interface DomainEvent {
    Instant occurredOn();
}
EOF

# Create DomainException base class
cat > "$BASE_PATH/domain/DomainException.java" << 'EOF'
package com.urbanbloom.backend.domain;

/**
 * Base class for all domain-specific exceptions.
 */
public abstract class DomainException extends RuntimeException {
    private final String errorCode;

    protected DomainException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    protected DomainException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
EOF

# Create GlobalExceptionHandler
cat > "$BASE_PATH/interfaces/rest/GlobalExceptionHandler.java" << 'EOF'
package com.urbanbloom.backend.interfaces.rest;

import com.urbanbloom.backend.domain.DomainException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DomainException.class)
    public ResponseEntity<ErrorResponse> handleDomainException(DomainException ex) {
        ErrorResponse error = new ErrorResponse(
                ex.getErrorCode(),
                ex.getMessage(),
                Instant.now()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationError(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .collect(Collectors.joining(", "));

        ErrorResponse error = new ErrorResponse(
                "VALIDATION_ERROR",
                message,
                Instant.now()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        ErrorResponse error = new ErrorResponse(
                "INTERNAL_ERROR",
                "An unexpected error occurred",
                Instant.now()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    public record ErrorResponse(String code, String message, Instant timestamp) {}
}
EOF

echo ""
echo "Step 8: Creating test directory structure..."
TEST_PATH="src/test/java/com/urbanbloom/backend"
mkdir -p "$TEST_PATH/domain"
mkdir -p "$TEST_PATH/application"
mkdir -p "$TEST_PATH/architecture"

# Create sample ArchUnit test
cat > "$TEST_PATH/architecture/ArchitectureTest.java" << 'EOF'
package com.urbanbloom.backend.architecture;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

class ArchitectureTest {

    private final JavaClasses classes = new ClassFileImporter()
            .importPackages("com.urbanbloom.backend");

    @Test
    void domainLayerShouldNotDependOnInfrastructure() {
        ArchRule rule = noClasses()
                .that().resideInAPackage("..domain..")
                .should().dependOnClassesThat().resideInAPackage("..infrastructure..");

        rule.check(classes);
    }

    @Test
    void domainLayerShouldNotDependOnApplication() {
        ArchRule rule = noClasses()
                .that().resideInAPackage("..domain..")
                .should().dependOnClassesThat().resideInAPackage("..application..");

        rule.check(classes);
    }
}
EOF

echo ""
echo "Step 9: Creating README for server..."
cat > README.md << 'EOF'
# UrbanBloom Backend Server

Spring Boot backend with Domain-Driven Design (DDD) architecture.

## Architecture

- **Domain Layer**: Pure business logic (no framework dependencies)
- **Application Layer**: Use cases and application services
- **Infrastructure Layer**: Technical implementations (JPA, messaging, etc.)
- **Interfaces Layer**: REST controllers and API endpoints

## Running the Application

```bash
# Start PostgreSQL (if using Docker)
docker run -d -p 5432:5432 -e POSTGRES_DB=urbanbloom -e POSTGRES_USER=urbanbloom -e POSTGRES_PASSWORD=secret postgres:15

# Run application
mvn spring-boot:run

# Run tests
mvn test

# Build
mvn clean install
```

## API Documentation

- Swagger UI: http://localhost:8080/swagger-ui.html
- OpenAPI JSON: http://localhost:8080/api-docs

## Project Structure

```
src/main/java/com/urbanbloom/backend/
├── domain/              # Domain models, aggregates, value objects
├── application/         # Use cases and DTOs
├── infrastructure/      # JPA repositories, messaging
└── interfaces/          # REST controllers
```

## Database Migrations

Flyway migrations are in `src/main/resources/db/migration/`.

## For More Information

- Backend Instructions: `.github/instructions/backend-instructions.md`
- Backend DDD Agent: `.github/agents/backend-ddd.agent.md`
EOF

echo ""
echo "✅ Server project initialized successfully!"
echo ""
echo "Next steps:"
echo "1. Start PostgreSQL database:"
echo "   docker run -d -p 5432:5432 -e POSTGRES_DB=urbanbloom -e POSTGRES_USER=urbanbloom -e POSTGRES_PASSWORD=secret postgres:15"
echo "2. Review the DDD package structure in src/main/java/com/urbanbloom/backend/"
echo "3. Implement domain models for each bounded context"
echo "4. Create Flyway migrations for database schema"
echo "5. Implement REST controllers matching OpenAPI spec"
echo "6. Run the application: mvn spring-boot:run"
echo ""
echo "For more guidance, see:"
echo "- .github/instructions/backend-instructions.md"
echo "- .github/agents/backend-ddd.agent.md"
echo "- .github/agents/backend-business-logic.agent.md"
echo ""
