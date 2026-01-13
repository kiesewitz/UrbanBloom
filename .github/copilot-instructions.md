# GitHub Copilot Instructions - UrbanBloom Project

## Project Overview

**UrbanBloom** is a mobile and web application for documenting and gamifying environmental actions ("green actions") in cities. Citizens document their contributions (planting trees, creating green spaces, etc.), earn points and badges, and participate in city-wide challenges.

### Architecture
- **Backend**: Spring Boot (Java 17) with Domain-Driven Design (DDD)
- **Frontend Mobile**: Flutter (Dart) for iOS and Android
- **Frontend Web Admin**: Flutter Web (Dart) for city administration
- **API**: RESTful with OpenAPI 3.0 specification as Single Source of Truth
- **Database**: PostgreSQL with schema-per-bounded-context

### 9 Bounded Contexts (DDD Domains)
1. **User/Identity** - Registration, authentication, privacy
2. **Action/Observation** - Green action documentation and verification
3. **Plant Catalog** - Central plant database
4. **Location/District** - Location and district management
5. **Gamification** - Points, badges, leaderboards
6. **Challenge** - City-wide challenges and campaigns
7. **Notification/Reminder** - Push notifications and reminders
8. **Admin/Analytics** - Reports and analytics for city administration
9. **Sync/Offline** - Offline-first data synchronization

---

## Coding Standards

### General Principles
- **OpenAPI First**: All API changes MUST update `openapi/urbanbloom-api-v1.yaml` BEFORE implementation
- **Line Length**: Maximum 120 characters per line
- **Language**: Code in English, documentation can be in German (German user base)
- **Comments**: Only where business logic is complex; prefer self-documenting code
- **Git Commits**: Conventional Commits format (`feat:`, `fix:`, `refactor:`, `docs:`, `test:`)

### Backend (Java/Spring Boot)

#### Naming Conventions
- **Classes**: PascalCase (`UserAggregate`, `ActionRepository`)
- **Methods**: camelCase (`createUser()`, `verifyAction()`)
- **Constants**: UPPER_SNAKE_CASE (`MAX_POINTS_PER_ACTION`)
- **Packages**: lowercase (`com.urbanbloom.backend.domain.user`)

#### Code Style
- **Constructor Injection**: Always use constructor injection for dependencies
- **Immutability**: Prefer immutable objects where possible (final fields, Value Objects)
- **Null Safety**: Use `Optional<T>` for nullable returns, never return null collections
- **Exceptions**: Throw domain-specific exceptions, never catch generic `Exception`

#### DDD Patterns (MUST FOLLOW)

##### Package Structure
```
com.urbanbloom.backend/
├── domain/              # Domain layer (pure business logic)
│   ├── user/
│   │   ├── User.java              # Aggregate Root
│   │   ├── UserId.java            # Value Object (Identity)
│   │   ├── Email.java             # Value Object
│   │   ├── UserRepository.java    # Repository Interface
│   │   ├── UserDomainService.java # Domain Service
│   │   └── events/
│   │       └── UserRegistered.java # Domain Event
│   ├── action/
│   ├── gamification/
│   └── ...
├── application/         # Application services (use cases)
│   ├── user/
│   │   ├── RegisterUserUseCase.java
│   │   └── dto/
│   │       ├── RegisterUserCommand.java
│   │       └── UserDTO.java
├── infrastructure/      # Technical implementations
│   ├── persistence/
│   │   ├── user/
│   │   │   ├── UserJpaRepository.java
│   │   │   └── UserEntity.java   # JPA Entity (separate from domain)
│   │   └── ...
│   └── messaging/
│       └── DomainEventPublisher.java
└── interfaces/          # Controllers (REST, GraphQL, etc.)
    └── rest/
        ├── user/
        │   └── UserController.java
        └── ...
```

##### Aggregate Rules
1. **One Aggregate per Transaction**: Never modify multiple aggregates in one transaction
2. **Reference by ID**: Aggregates reference other aggregates only by ID, never by object reference
3. **Small Aggregates**: Keep aggregates small; split into multiple if growing too large
4. **Invariant Protection**: Aggregates enforce all business rules/invariants

##### Domain Events Over Direct Calls
```java
// ❌ WRONG - Direct call across bounded contexts
public void registerUser(User user) {
    userRepository.save(user);
    gamificationService.createInitialProfile(user.getId()); // Cross-context coupling!
}

// ✅ CORRECT - Domain event
public void registerUser(User user) {
    userRepository.save(user);
    domainEventPublisher.publish(new UserRegistered(user.getId(), user.getEmail()));
}

// In Gamification context
@EventListener
public void onUserRegistered(UserRegistered event) {
    gamificationService.createInitialProfile(event.getUserId());
}
```

##### Value Objects
- Immutable, validated in constructor
- No identity (equality by value, not reference)
- Example: `Email`, `UserId`, `Points`, `GeoCoordinates`

```java
public record Email(String value) {
    public Email {
        if (value == null || !value.matches("^[\\w.-]+@[\\w.-]+\\.[a-z]{2,}$")) {
            throw new IllegalArgumentException("Invalid email format");
        }
    }
}
```

#### OpenAPI Integration
- **springdoc-openapi**: Use for runtime validation and Swagger UI
- **DTO Validation**: Use `@Valid` and Bean Validation annotations (`@NotNull`, `@Size`, etc.)
- **Exception Handling**: Use `@ControllerAdvice` to map domain exceptions to HTTP responses

```java
@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    @PostMapping
    public ResponseEntity<UserDTO> createUser(@Valid @RequestBody RegisterUserCommand command) {
        // Implementation
    }
}
```

### Frontend (Flutter/Dart)

#### Naming Conventions
- **Classes**: PascalCase (`ActionCard`, `UserProfile`)
- **Variables/Functions**: lowerCamelCase (`userName`, `fetchActions()`)
- **Files**: snake_case (`action_card.dart`, `user_profile_page.dart`)
- **Constants**: lowerCamelCase (`const double defaultPadding = 16.0;`)
- **Private Members**: Prefix with underscore (`_internalState`)

#### Code Style
- **Const Constructors**: Use `const` wherever possible for performance
- **Immutability**: Prefer immutable data classes (use `@immutable` annotation)
- **Null Safety**: Use null-safe Dart syntax (`String?`, `??`, `?.`)
- **Named Parameters**: Use named parameters for constructors with >2 parameters

#### Component-Driven Design (CDD) - Atomic Design

##### Directory Structure
```
lib/
├── ui/
│   ├── atoms/           # Basic UI elements (buttons, text, icons)
│   │   ├── action_button.dart
│   │   ├── heading_text.dart
│   │   └── icon_badge.dart
│   ├── molecules/       # Simple combinations (card headers, list items)
│   │   ├── action_card_header.dart
│   │   └── user_avatar_with_name.dart
│   ├── organisms/       # Complex components (forms, lists, cards)
│   │   ├── action_card.dart
│   │   ├── action_form.dart
│   │   └── leaderboard_list.dart
│   ├── templates/       # Page layouts (navigation, scaffolds)
│   │   └── main_layout.dart
│   └── pages/           # Full screens (home, profile, action details)
│       ├── home_page.dart
│       ├── profile_page.dart
│       └── action_details_page.dart
├── domain/
│   ├── models/          # Domain models (Action, User, Plant)
│   └── repositories/    # Repository interfaces
├── data/
│   ├── api/             # API client (generated from OpenAPI)
│   ├── local/           # Local database (Drift)
│   └── sync/            # Offline sync logic
└── providers/           # Riverpod providers (state management)
```

##### Atomic Design Principles
1. **Atoms**: No dependencies on other UI components, highly reusable
2. **Molecules**: Combine 2-3 atoms, minimal state
3. **Organisms**: Can have local state, combine molecules and atoms
4. **Templates**: Layout only, no business logic
5. **Pages**: Connect to providers (state management), orchestrate organisms

##### State Management with Riverpod
```dart
// Provider definition
@riverpod
class UserActions extends _$UserActions {
  @override
  Future<List<Action>> build(String userId) async {
    return ref.watch(actionRepositoryProvider).getUserActions(userId);
  }
}

// Usage in widget
class ActionListPage extends ConsumerWidget {
  const ActionListPage({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final asyncActions = ref.watch(userActionsProvider('user123'));

    return asyncActions.when(
      data: (actions) => ListView.builder(...),
      loading: () => CircularProgressIndicator(),
      error: (error, stack) => ErrorWidget(error),
    );
  }
}
```

##### API Client Integration
- **Generate from OpenAPI**: Use `openapi-generator` for Dart client
- **Repository Pattern**: Wrap generated client in repository for testability
- **Offline First**: Always read from local DB first, sync in background

```dart
class ActionRepository {
  final ActionApiClient _apiClient;
  final ActionLocalDatabase _localDb;

  Future<List<Action>> getUserActions(String userId) async {
    // 1. Return cached data immediately
    final cached = await _localDb.getActions(userId);

    // 2. Fetch from API in background
    try {
      final remote = await _apiClient.getUserActions(userId);
      await _localDb.saveActions(remote);
      return remote;
    } catch (e) {
      // Return cached data if API fails (offline support)
      return cached;
    }
  }
}
```

---

## Error Handling

### Backend
- **Domain Exceptions**: Create specific exceptions per domain (`UserNotFoundException`, `ActionAlreadyVerifiedException`)
- **Application Exceptions**: For use case failures (`InvalidCommandException`)
- **Never Catch Generic**: Never catch `Exception` or `Throwable` unless rethrowing
- **Logging**: Log exceptions with context (aggregate ID, domain, operation)

```java
@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFound(UserNotFoundException ex) {
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(new ErrorResponse("USER_NOT_FOUND", ex.getMessage()));
    }
}
```

### Frontend
- **Result Types**: Use Result<T, E> pattern for operations that can fail
- **User-Friendly Messages**: Never show technical error messages to users
- **Retry Logic**: Implement exponential backoff for network errors
- **Offline Graceful**: Show cached data with indicator when offline

```dart
sealed class Result<T, E> {
  const Result();
}

class Success<T, E> extends Result<T, E> {
  final T value;
  const Success(this.value);
}

class Failure<T, E> extends Result<T, E> {
  final E error;
  const Failure(this.error);
}
```

---

## Logging Standards

### Backend (SLF4J + Logback)
- **Structured Logging**: Use MDC for context (userId, domain, requestId)
- **Log Levels**:
  - `ERROR`: Unhandled exceptions, critical failures
  - `WARN`: Handled exceptions, degraded functionality
  - `INFO`: Use case execution, domain events published
  - `DEBUG`: Detailed flow, SQL queries
  - `TRACE`: Very detailed (avoid in production)

```java
@Slf4j
public class RegisterUserUseCase {
    public UserDTO execute(RegisterUserCommand command) {
        log.info("Registering user with email: {}", command.email());

        try {
            // Business logic
            log.debug("User {} created successfully", user.getId());
        } catch (DomainException e) {
            log.error("Failed to register user: {}", e.getMessage(), e);
            throw e;
        }
    }
}
```

### Frontend (Logging Package)
- **Log Levels**: INFO, WARNING, ERROR
- **Include Context**: User action, screen, timestamp
- **Privacy**: Never log PII (personal identifiable information) in production

---

## Testing Requirements

### Backend Tests

#### Unit Tests (Domain Layer)
- **Coverage**: 100% for domain logic (Aggregates, Value Objects, Domain Services)
- **Framework**: JUnit 5 + AssertJ
- **Naming**: `shouldXxxWhenYyy` pattern

```java
class UserAggregateTest {
    @Test
    void shouldThrowExceptionWhenEmailIsInvalid() {
        assertThatThrownBy(() -> new Email("invalid-email"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Invalid email format");
    }
}
```

#### Integration Tests (Application + Infrastructure)
- **Framework**: `@SpringBootTest` with TestContainers
- **Database**: Use PostgreSQL TestContainer (same as production)
- **Isolation**: Each test rolls back transaction

```java
@SpringBootTest
@Transactional
class RegisterUserUseCaseIntegrationTest {
    @Autowired
    private RegisterUserUseCase registerUserUseCase;

    @Test
    void shouldCreateUserAndPublishEvent() {
        // Given, When, Then
    }
}
```

#### Architecture Tests (ArchUnit)
- **DDD Constraints**: Verify domain layer has no dependencies on infrastructure
- **Naming Conventions**: Verify package naming matches DDD structure

```java
@AnalyzeClasses(packages = "com.urbanbloom.backend")
class ArchitectureTest {
    @Test
    void domainLayerShouldNotDependOnInfrastructure() {
        noClasses()
            .that().resideInAPackage("..domain..")
            .should().dependOnClassesThat().resideInAPackage("..infrastructure..")
            .check(classes);
    }
}
```

### Frontend Tests

#### Widget Tests
- **Coverage**: All organisms and molecules
- **Framework**: Flutter test framework
- **Golden Tests**: For pixel-perfect UI verification

```dart
void main() {
  testWidgets('ActionCard displays action details', (tester) async {
    await tester.pumpWidget(
      MaterialApp(
        home: ActionCard(action: testAction),
      ),
    );

    expect(find.text('Plant a Tree'), findsOneWidget);
    expect(find.byIcon(Icons.eco), findsOneWidget);
  });
}
```

#### Integration Tests
- **Framework**: `patrol` for full user flows
- **Coverage**: Critical user journeys (register, create action, verify)

---

## Git Workflow

### Branch Strategy (GitHub Flow)
- **Main Branch**: Always deployable, protected
- **Feature Branches**: `feature/action-verification`, `fix/login-bug`, `refactor/user-domain`
- **PR Required**: All changes via Pull Request with 1 approval minimum
- **CI/CD**: All tests must pass before merge

### Commit Messages (Conventional Commits)
```
<type>(<scope>): <subject>

<body>

<footer>
```

**Types**: `feat`, `fix`, `refactor`, `docs`, `test`, `chore`, `perf`
**Scope**: Domain name or component (`user`, `action`, `mobile`, `backend`)

**Examples**:
- `feat(action): add photo upload to action creation`
- `fix(gamification): correct points calculation for verified actions`
- `refactor(user): extract email validation to value object`
- `docs(architecture): add Mermaid diagram for domain interactions`

---

## Performance Guidelines

### Backend
- **Pagination**: Use cursor-based pagination for large collections
- **Caching**: Use Redis for frequently accessed data (leaderboards, plant catalog)
- **N+1 Queries**: Use `@EntityGraph` or JOIN FETCH to avoid
- **Async Operations**: Use `@Async` for non-critical operations (email sending, analytics)

### Frontend
- **Lazy Loading**: Load images and data on demand
- **Code Splitting**: Use deferred imports for large features
- **State Optimization**: Use `select` in Riverpod to listen to specific fields
- **List Performance**: Use `ListView.builder` for long lists (virtual scrolling)

---

## Security Guidelines

### Backend
- **Authentication**: JWT tokens with 15-minute expiry, refresh tokens with 7-day expiry
- **Authorization**: Role-based access control (CITIZEN, ADMIN, CITY_ADMIN)
- **Input Validation**: Validate all inputs with Bean Validation
- **SQL Injection**: Use parameterized queries (JPA does this automatically)
- **CORS**: Configure allowed origins explicitly (no wildcard in production)

### Frontend
- **Token Storage**: Use `flutter_secure_storage` for sensitive data
- **HTTPS Only**: Never send credentials over HTTP
- **Input Sanitization**: Sanitize user input before display (prevent XSS)
- **Biometric Auth**: Support fingerprint/Face ID for sensitive operations

---

## Accessibility (Frontend)

### WCAG AA Compliance
- **Color Contrast**: Minimum 4.5:1 for normal text, 3:1 for large text
- **Semantic Labels**: Use `Semantics` widget for screen readers
- **Keyboard Navigation**: Support tab navigation for web
- **Focus Indicators**: Visible focus states for interactive elements
- **Text Scaling**: Support system font size settings

```dart
Semantics(
  label: 'Create new action button',
  button: true,
  child: ActionButton(
    onPressed: () => createAction(),
    child: Text('Create Action'),
  ),
)
```

---

## Design Tokens

### Import from Shared Resources
All UI components MUST use design tokens from `shared-resources/design-tokens/tokens.json` for consistency across mobile and web.

**Usage**:
```dart
// Generated from design tokens
class AppColors {
  static const primary = Color(0xFF4CAF50);      // Green
  static const secondary = Color(0xFF8BC34A);    // Light Green
  static const accent = Color(0xFFFF9800);       // Orange
}

class AppSpacing {
  static const double xs = 4.0;
  static const double sm = 8.0;
  static const double md = 16.0;
  static const double lg = 24.0;
  static const double xl = 32.0;
}
```

---

## When in Doubt

1. **OpenAPI First**: Check if API contract needs updating
2. **Domain Events**: Use events for cross-context communication
3. **Small Commits**: Prefer small, focused commits over large changes
4. **Test Coverage**: Write tests before marking PR as ready
5. **Documentation**: Update docs when changing public APIs
6. **Ask for Review**: When unsure about design decisions, create draft PR for feedback

---

## Related Resources

- **DDD Documentation**: `shared-resources/documentation/domain-model-description-urbanbloom.md`
- **OpenAPI Spec**: `openapi/urbanbloom-api-v1.yaml`
- **Architecture Overview**: `docs/architecture.md`
- **Onboarding Guide**: `docs/onboarding.md`
- **AI Environment Guide**: `docs/ai-environment-guide.md`
