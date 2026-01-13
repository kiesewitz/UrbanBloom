# Backend Development Instructions - UrbanBloom

**Technology Stack**: Spring Boot 3.2+, Java 17, PostgreSQL, Maven  
**Architectural Approach**: Domain-Driven Design (DDD)  
**Target Audience**: Backend developers (2 people)

---

## Domain-Driven Design (DDD) Principles

### Bounded Contexts
UrbanBloom has **9 bounded contexts** (independent domains):
1. **User/Identity** - Authentication, user management, privacy
2. **Action/Observation** - Green action documentation
3. **Plant Catalog** - Central plant database
4. **Location/District** - Geographic data management
5. **Gamification** - Points, badges, leaderboards
6. **Challenge** - City-wide challenges
7. **Notification/Reminder** - Push notifications
8. **Admin/Analytics** - Reporting and analytics
9. **Sync/Offline** - Data synchronization

**Rule**: Each bounded context is an independent module with its own package, schema, and domain model.

---

## Package Structure (Mandatory)

```
com.urbanbloom.backend/
├── domain/                          # PURE business logic (no framework dependencies)
│   ├── user/                        # User bounded context
│   │   ├── User.java                # Aggregate Root
│   │   ├── UserId.java              # Value Object (Identity)
│   │   ├── Email.java               # Value Object
│   │   ├── UserRepository.java      # Repository Interface (domain)
│   │   ├── UserDomainService.java   # Domain Service (cross-aggregate logic)
│   │   └── events/
│   │       └── UserRegistered.java  # Domain Event
│   ├── action/                      # Action bounded context
│   ├── gamification/                # Gamification bounded context
│   └── ...                          # Other 6 bounded contexts
├── application/                     # Application services (use cases)
│   ├── user/
│   │   ├── RegisterUserUseCase.java # Application Service
│   │   └── dto/
│   │       ├── RegisterUserCommand.java  # Input DTO
│   │       └── UserDTO.java              # Output DTO
│   └── ...
├── infrastructure/                  # Technical implementations
│   ├── persistence/
│   │   ├── user/
│   │   │   ├── UserJpaRepository.java    # Spring Data JPA Repository
│   │   │   └── UserEntity.java           # JPA Entity (separate from domain!)
│   │   └── ...
│   ├── messaging/
│   │   └── DomainEventPublisher.java     # Event publisher implementation
│   └── config/
│       └── PostgresConfig.java           # DB configuration
└── interfaces/                      # Controllers (REST, GraphQL, etc.)
    └── rest/
        ├── user/
        │   └── UserController.java       # REST Controller
        └── ...
```

**Critical Rules**:
- **Domain layer** has NO dependencies on Spring, JPA, or any framework
- **Infrastructure layer** implements domain interfaces (e.g., UserRepository)
- **Application layer** orchestrates use cases, manages transactions
- **Interfaces layer** handles HTTP requests/responses, validation

---

## Aggregate Rules (MUST FOLLOW)

### 1. One Aggregate per Transaction
**❌ WRONG**:
```java
@Transactional
public void registerUserAndAwardPoints(User user, int points) {
    userRepository.save(user);  // Aggregate 1
    gamificationRepository.awardPoints(user.getId(), points);  // Aggregate 2 ❌
}
```

**✅ CORRECT** (Use Domain Events):
```java
@Transactional
public void registerUser(User user) {
    userRepository.save(user);
    domainEventPublisher.publish(new UserRegistered(user.getId(), user.getEmail()));
}

// In Gamification context (separate transaction)
@EventListener
@Transactional
public void onUserRegistered(UserRegistered event) {
    gamificationService.createInitialProfile(event.getUserId());
}
```

### 2. Reference by ID Only
Aggregates NEVER hold references to other aggregates. Use IDs only.

**❌ WRONG**:
```java
public class Action {
    private User creator;  // ❌ Direct reference to another aggregate
}
```

**✅ CORRECT**:
```java
public class Action {
    private UserId creatorId;  // ✅ Reference by ID
}
```

### 3. Small Aggregates
Keep aggregates small. If an aggregate grows too large, split it.

**Example**: `User` aggregate should only contain identity and profile data. User actions belong to the `Action` aggregate in the Action context.

### 4. Invariants Protection
Aggregates enforce ALL business rules. NO business logic in controllers or services.

**✅ CORRECT**:
```java
public class Action {
    private ActionStatus status;
    private UserId verifierId;

    public void verify(UserId verifier) {
        if (this.status == ActionStatus.VERIFIED) {
            throw new ActionAlreadyVerifiedException(this.id);
        }
        if (verifier.equals(this.creatorId)) {
            throw new CannotVerifyOwnActionException();
        }
        this.status = ActionStatus.VERIFIED;
        this.verifierId = verifier;
        // Publish domain event
        registerEvent(new ActionVerified(this.id, verifier));
    }
}
```

---

## Value Objects (Best Practices)

Value Objects are **immutable** and validated in the constructor.

**Example**: Email Value Object
```java
public record Email(String value) {
    public Email {
        if (value == null || !value.matches("^[\\w.-]+@[\\w.-]+\\.[a-z]{2,}$")) {
            throw new IllegalArgumentException("Invalid email format: " + value);
        }
        value = value.toLowerCase();  // Normalization
    }
}
```

**Benefits**:
- Validation in ONE place (constructor)
- Immutable (thread-safe)
- Equality by value (not reference)

**When to Use**:
- Identifiers (UserId, ActionId)
- Email, Phone, Address
- Money, Points, Coordinates
- Any concept that has validation rules

---

## Domain Events (Required for Cross-Context Communication)

### Event Structure
```java
public record UserRegistered(
    UserId userId,
    Email email,
    Instant occurredOn
) implements DomainEvent {
    public UserRegistered(UserId userId, Email email) {
        this(userId, email, Instant.now());
    }
}
```

### Publishing Events (In Aggregate)
```java
public class User extends AggregateRoot {
    private List<DomainEvent> domainEvents = new ArrayList<>();

    public static User register(Email email, Password password) {
        User user = new User(UserId.generate(), email, password);
        user.registerEvent(new UserRegistered(user.getId(), email));
        return user;
    }

    protected void registerEvent(DomainEvent event) {
        this.domainEvents.add(event);
    }

    public List<DomainEvent> getDomainEvents() {
        return List.copyOf(domainEvents);
    }

    public void clearEvents() {
        this.domainEvents.clear();
    }
}
```

### Publishing Events (In Application Service)
```java
@Service
public class RegisterUserUseCase {
    private final UserRepository userRepository;
    private final DomainEventPublisher eventPublisher;

    @Transactional
    public UserDTO execute(RegisterUserCommand command) {
        User user = User.register(command.email(), command.password());
        userRepository.save(user);

        // Publish domain events
        user.getDomainEvents().forEach(eventPublisher::publish);
        user.clearEvents();

        return UserDTO.from(user);
    }
}
```

### Consuming Events (Other Contexts)
```java
@Component
public class GamificationEventHandler {

    @Async
    @EventListener
    @Transactional
    public void onUserRegistered(UserRegistered event) {
        gamificationService.createInitialProfile(event.userId());
    }
}
```

---

## Repository Pattern

### Domain Interface (in domain package)
```java
public interface UserRepository {
    User save(User user);
    Optional<User> findById(UserId id);
    Optional<User> findByEmail(Email email);
    void delete(UserId id);
}
```

### Infrastructure Implementation (in infrastructure package)
```java
@Repository
public class UserRepositoryImpl implements UserRepository {
    private final UserJpaRepository jpaRepository;  // Spring Data

    @Override
    public User save(User user) {
        UserEntity entity = UserEntity.fromDomain(user);
        UserEntity saved = jpaRepository.save(entity);
        return saved.toDomain();
    }

    @Override
    public Optional<User> findById(UserId id) {
        return jpaRepository.findById(id.value())
            .map(UserEntity::toDomain);
    }
}

// Spring Data JPA Repository (Infrastructure)
interface UserJpaRepository extends JpaRepository<UserEntity, String> {
    Optional<UserEntity> findByEmail(String email);
}
```

**Key Point**: Domain models (User) are **separate** from JPA entities (UserEntity). This keeps domain pure.

---

## Spring Boot Configuration

### Application Properties (`application.yml`)
```yaml
spring:
  application:
    name: urbanbloom-backend

  datasource:
    url: jdbc:postgresql://localhost:5432/urbanbloom
    username: ${DB_USER:urbanbloom}
    password: ${DB_PASSWORD:secret}
    hikari:
      maximum-pool-size: 10

  jpa:
    hibernate:
      ddl-auto: validate  # Use Flyway for schema changes
    properties:
      hibernate:
        default_schema: public
        jdbc:
          batch_size: 20

  flyway:
    enabled: true
    baseline-on-migrate: true
    locations: classpath:db/migration

springdoc:
  api-docs:
    path: /api-docs
  swagger-ui:
    path: /swagger-ui.html
    operationsSorter: alpha
```

### Database Schema Isolation
Each bounded context has its own schema:

```sql
-- Flyway migration: V1__create_schemas.sql
CREATE SCHEMA IF NOT EXISTS user_context;
CREATE SCHEMA IF NOT EXISTS action_context;
CREATE SCHEMA IF NOT EXISTS gamification_context;
-- ... (7 more schemas)
```

**JPA Configuration** (per context):
```java
@Entity
@Table(name = "users", schema = "user_context")
public class UserEntity {
    // ...
}
```

---

## OpenAPI Integration (CRITICAL)

### Step 1: Update OpenAPI Spec FIRST
Before implementing any endpoint, update `openapi/urbanbloom-api-v1.yaml`.

### Step 2: Generate DTOs (Optional)
Use `openapi-generator` to generate DTOs from spec:
```bash
openapi-generator generate \
  -i openapi/urbanbloom-api-v1.yaml \
  -g spring \
  -o server/generated \
  --api-package com.urbanbloom.backend.interfaces.rest \
  --model-package com.urbanbloom.backend.application.dto
```

### Step 3: Implement Controller Matching Spec
```java
@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "User Management")
public class UserController {

    @PostMapping
    @Operation(summary = "Register new user")
    @ApiResponse(responseCode = "201", description = "User created")
    @ApiResponse(responseCode = "400", description = "Invalid request")
    public ResponseEntity<UserDTO> registerUser(
        @Valid @RequestBody RegisterUserCommand command
    ) {
        UserDTO user = registerUserUseCase.execute(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }
}
```

### Step 4: DTO Validation
```java
public record RegisterUserCommand(
    @NotNull @Email String email,
    @NotNull @Size(min = 8) String password,
    @NotNull String displayName
) {}
```

### Step 5: Global Exception Handler
```java
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleUserExists(UserAlreadyExistsException ex) {
        return ResponseEntity
            .status(HttpStatus.CONFLICT)
            .body(new ErrorResponse("USER_ALREADY_EXISTS", ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationError(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
            .map(err -> err.getField() + ": " + err.getDefaultMessage())
            .collect(Collectors.joining(", "));
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(new ErrorResponse("VALIDATION_ERROR", message));
    }
}
```

---

## Testing Standards

### Unit Tests (Domain Layer)
Test aggregates, value objects, domain services:

```java
class UserAggregateTest {

    @Test
    void shouldRegisterUserWithValidEmail() {
        Email email = new Email("test@example.com");
        Password password = new Password("SecurePass123");

        User user = User.register(email, password);

        assertThat(user.getEmail()).isEqualTo(email);
        assertThat(user.getDomainEvents()).hasSize(1);
        assertThat(user.getDomainEvents().get(0)).isInstanceOf(UserRegistered.class);
    }

    @Test
    void shouldThrowExceptionWhenEmailIsInvalid() {
        assertThatThrownBy(() -> new Email("invalid-email"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Invalid email format");
    }
}
```

### Integration Tests (Application Layer)
Use TestContainers for real PostgreSQL:

```java
@SpringBootTest
@Testcontainers
@Transactional
class RegisterUserUseCaseIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
        .withDatabaseName("urbanbloom_test");

    @Autowired
    private RegisterUserUseCase registerUserUseCase;

    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldRegisterUserAndPublishEvent() {
        // Given
        RegisterUserCommand command = new RegisterUserCommand(
            "test@example.com", "SecurePass123", "Test User"
        );

        // When
        UserDTO result = registerUserUseCase.execute(command);

        // Then
        assertThat(result.id()).isNotNull();
        assertThat(userRepository.findById(new UserId(result.id()))).isPresent();
        // Verify event was published (use event spy)
    }
}
```

### API Compliance Tests
Verify endpoints match OpenAPI spec:

```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class OpenAPIComplianceTest {

    @LocalServerPort
    private int port;

    @Test
    void shouldMatchOpenAPISpecification() {
        // Use openapi-diff or similar tool to verify
        // Implementation matches spec
    }
}
```

---

## Error Handling

### Domain Exceptions
Create specific exceptions per domain:

```java
public class UserNotFoundException extends DomainException {
    public UserNotFoundException(UserId userId) {
        super("USER_NOT_FOUND", "User not found: " + userId.value());
    }
}

public class ActionAlreadyVerifiedException extends DomainException {
    public ActionAlreadyVerifiedException(ActionId actionId) {
        super("ACTION_ALREADY_VERIFIED", "Action already verified: " + actionId.value());
    }
}
```

**Rule**: NEVER catch generic `Exception`. Catch specific exceptions.

---

## Logging Standards

Use SLF4J with structured logging:

```java
@Slf4j
@Service
public class RegisterUserUseCase {

    @Transactional
    public UserDTO execute(RegisterUserCommand command) {
        log.info("Registering user with email: {}", command.email());

        try {
            User user = User.register(new Email(command.email()), new Password(command.password()));
            userRepository.save(user);
            log.debug("User {} created successfully", user.getId());
            return UserDTO.from(user);
        } catch (DomainException e) {
            log.error("Failed to register user: {}", e.getMessage(), e);
            throw e;
        }
    }
}
```

**Log Levels**:
- **ERROR**: Unhandled exceptions, critical failures
- **WARN**: Handled exceptions, degraded functionality
- **INFO**: Use case execution, domain events published
- **DEBUG**: Detailed flow, SQL queries (dev only)
- **TRACE**: Very detailed (avoid in production)

---

## Performance Optimization

### 1. N+1 Query Prevention
Use `@EntityGraph` or JOIN FETCH:

```java
@Query("SELECT a FROM ActionEntity a JOIN FETCH a.plant WHERE a.creatorId = :userId")
List<ActionEntity> findByCreatorIdWithPlant(@Param("userId") String userId);
```

### 2. Caching (Redis)
Cache frequently accessed data:

```java
@Cacheable(value = "plants", key = "#plantId")
public Optional<Plant> findPlantById(PlantId plantId) {
    return plantRepository.findById(plantId);
}
```

### 3. Pagination
Always paginate large collections:

```java
@GetMapping("/actions")
public Page<ActionDTO> getActions(Pageable pageable) {
    return actionService.findAll(pageable);
}
```

### 4. Async Operations
Use `@Async` for non-critical operations:

```java
@Async
public void sendWelcomeEmail(UserId userId) {
    // Send email in background
}
```

---

## Security

### Authentication (JWT)
```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/v1/auth/**").permitAll()
                .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            .oauth2ResourceServer(oauth2 -> oauth2.jwt());
        return http.build();
    }
}
```

### Authorization
Check permissions in application services:

```java
public void verifyAction(ActionId actionId, UserId verifierId) {
    Action action = actionRepository.findById(actionId)
        .orElseThrow(() -> new ActionNotFoundException(actionId));

    if (!hasVerificationPermission(verifierId)) {
        throw new InsufficientPermissionsException();
    }

    action.verify(verifierId);
    actionRepository.save(action);
}
```

---

## Database Migrations (Flyway)

**File Naming**: `V{version}__{description}.sql`

Example: `V1__create_user_schema.sql`
```sql
CREATE SCHEMA IF NOT EXISTS user_context;

CREATE TABLE user_context.users (
    id VARCHAR(36) PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    display_name VARCHAR(100) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

CREATE INDEX idx_users_email ON user_context.users(email);
```

**Rule**: NEVER modify existing migrations. Create new ones for changes.

---

## VS Code Tasks Integration

Use VS Code tasks for common operations (see `.vscode/tasks.json`):

- **Build**: `mvn clean install`
- **Run**: `mvn spring-boot:run`
- **Test**: `mvn test`
- **Validate OpenAPI**: Custom task to check compliance

---

## Related Files
- **Global Standards**: `.github/copilot-instructions.md`
- **DDD Agent**: `.github/agents/backend-ddd.agent.md`
- **Business Logic Agent**: `.github/agents/backend-business-logic.agent.md`
- **OpenAPI Spec**: `openapi/urbanbloom-api-v1.yaml`
- **Domain Documentation**: `shared-resources/documentation/domain-model-description-urbanbloom.md`
