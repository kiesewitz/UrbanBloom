# Backend Development Prompts Catalog - UrbanBloom

**Purpose**: Reusable prompts for backend development with DDD patterns  
**Target Users**: Backend developers  
**Usage**: Copy prompt, fill placeholders, use with Backend DDD Agent or Business Logic Agent

---

## Domain Modeling Prompts

### 1. Create Aggregate Root
```
Create an Aggregate Root for the [DOMAIN] bounded context:

Name: [AGGREGATE_NAME]
Properties: [LIST PROPERTIES WITH TYPES]
Business Rules/Invariants: [LIST RULES]
Domain Events: [EVENTS THAT THIS AGGREGATE EMITS]

Generate:
- Aggregate class with all properties
- Constructor with validation
- Business methods that enforce invariants
- Domain event registration
- Value objects for complex properties
- Repository interface

Follow DDD patterns and package structure from backend-instructions.md.
```

### 2. Model Value Object
```
Create a Value Object for [CONCEPT]:

Properties: [LIST]
Validation Rules: [CONSTRAINTS]
Normalization: [e.g., lowercase email, trim whitespace]

Generate:
- Record class (immutable)
- Validation in constructor
- Normalization logic
- equals/hashCode based on value
- toString for debugging

Example: Email, UserId, GeoCoordinates, Points
```

### 3. Design Domain Events
```
Design domain events for the [AGGREGATE] aggregate:

Triggers: [WHAT ACTIONS CAUSE EVENTS]
Consumers: [WHICH OTHER CONTEXTS LISTEN TO THESE EVENTS]

Generate:
- Event record classes (immutable, with occurredOn timestamp)
- Event publisher integration in aggregate
- Event handler skeleton for consuming contexts

Use event storming notation for complex flows.
```

### 4. Create Domain Service
```
Create a Domain Service for [CROSS-AGGREGATE OPERATION]:

Operation: [WHAT IT DOES]
Aggregates Involved: [LIST]
Business Rules: [RULES THAT SPAN AGGREGATES]

Generate:
- Domain service class (in domain package)
- Method signatures
- Coordination logic between aggregates
- Return value (often void or domain event)

Note: Domain services should NOT access repositories directly.
```

---

## API Design Prompts

### 5. Design RESTful Endpoint
```
Design a RESTful endpoint for [OPERATION]:

Resource: [ENTITY NAME, e.g., Action, User]
Operation: [CREATE/READ/UPDATE/DELETE/CUSTOM]
Requirements: [BUSINESS REQUIREMENTS]

Generate OpenAPI 3.0 specification with:
- HTTP method and path
- Request body schema (if POST/PUT/PATCH)
- Response schemas (success and error cases)
- Status codes (200, 201, 400, 404, 500, etc.)
- Query parameters (filtering, pagination, sorting)
- Authentication requirements
- Example requests/responses

Follow Richardson Maturity Level 3 (HATEOAS if applicable).
```

### 6. Design Paginated Response
```
Create paginated API response for [COLLECTION]:

Resource: [e.g., /actions, /users]
Pagination Strategy: [cursor-based or offset-based]
Default Page Size: [e.g., 20]
Max Page Size: [e.g., 100]

Generate:
- Response schema with items array and metadata
- Query parameters (page, limit, cursor)
- Link headers for next/prev pages (RFC 5988)
- Total count (if offset-based)

Update OpenAPI spec with pagination schema.
```

### 7. Design Error Response Schema
```
Design standardized error response for the API:

Error Types: [Validation, NotFound, Unauthorized, ServerError, etc.]

Generate OpenAPI schema with:
- Error code (string enum)
- Message (user-friendly description)
- Details (array of field-specific errors for validation)
- Timestamp
- Request ID (for tracing)

Example HTTP status code mapping:
- 400: Validation errors
- 401: Unauthorized
- 403: Forbidden
- 404: Not found
- 409: Conflict
- 500: Internal server error
```

---

## Use Case Implementation Prompts

### 8. Implement Use Case
```
Implement the use case: [USE CASE NAME]

User Story: [PASTE USER STORY]
Affected Domains: [LIST DOMAINS]
Domain Events to Publish: [LIST]

Generate:
- Application service class (in application package)
- Command DTO (input)
- Result DTO (output)
- Transaction boundaries (@Transactional)
- Domain event publishing
- Exception handling
- Logging

Follow structure:
1. Validate input (command)
2. Load aggregates from repositories
3. Execute business logic on aggregates
4. Save changes
5. Publish domain events
6. Return result DTO
```

### 9. Create REST Controller
```
Create REST controller for [ENTITY] matching OpenAPI spec:

OpenAPI Path: [PATH FROM SPEC]
Operations: [LIST HTTP METHODS]

Generate:
- @RestController class
- Endpoint methods matching OpenAPI spec exactly
- @Valid annotations for request validation
- ResponseEntity with correct status codes
- OpenAPI annotations (@Operation, @ApiResponse)
- Exception handling delegation to @ControllerAdvice

Ensure 100% compliance with openapi/urbanbloom-api-v1.yaml.
```

---

## Repository Prompts

### 10. Create Repository Interface
```
Create repository interface for [AGGREGATE]:

Required Queries:
- [e.g., findById, findByEmail, findByStatus]

Generate:
- Repository interface (in domain package)
- Method signatures with domain types (not JPA entities!)
- Return types: Optional<T>, List<T>, Page<T>
- No implementation (that's in infrastructure layer)

Example: UserRepository, ActionRepository
```

### 11. Implement JPA Repository
```
Implement JPA repository for [AGGREGATE]:

Domain Repository: [INTERFACE NAME]
Database Schema: [SCHEMA NAME, e.g., user_context]
Table: [TABLE NAME]

Generate:
- JPA entity class (in infrastructure/persistence package)
- Spring Data JPA repository interface
- Repository implementation that implements domain interface
- Mapping methods: fromDomain(), toDomain()
- Handle Optional correctly

Ensure domain models stay pure (no JPA annotations in domain layer).
```

---

## Testing Prompts

### 12. Generate Unit Test for Aggregate
```
Generate unit test for [AGGREGATE] aggregate:

Test Scenario: [WHAT TO TEST]
Business Rule: [RULE BEING TESTED]

Generate JUnit 5 test with:
- Test class name: [Aggregate]Test
- Test method: shouldXxxWhenYyy pattern
- Given-When-Then structure
- AssertJ assertions
- Test domain events are emitted
- Test exceptions are thrown when invariants violated

No Spring context needed (pure unit test).
```

### 13. Generate Integration Test for Use Case
```
Generate integration test for [USE CASE]:

Use Case: [NAME]
Repositories Involved: [LIST]
Domain Events Published: [LIST]

Generate Spring Boot test with:
- @SpringBootTest annotation
- @Testcontainers for PostgreSQL
- @Transactional (rollback after test)
- Given-When-Then structure
- Verify database state after operation
- Verify domain events published (use event spy)
- Test error cases (e.g., entity not found)

Use TestContainers for real database.
```

### 14. Generate API Compliance Test
```
Generate OpenAPI compliance test for [ENDPOINT]:

Endpoint: [PATH AND METHOD]
OpenAPI Spec: openapi/urbanbloom-api-v1.yaml

Generate test that:
- Sends HTTP request matching spec
- Validates response schema matches spec
- Validates status codes match spec
- Validates error responses match spec

Use openapi-generator or similar tool for validation.
```

---

## Database Prompts

### 15. Create Flyway Migration
```
Create Flyway migration for [CHANGE]:

Change Type: [CREATE TABLE/ALTER TABLE/ADD INDEX/etc.]
Schema: [BOUNDED CONTEXT SCHEMA, e.g., user_context]
Details: [DESCRIBE CHANGE]

Generate SQL file:
- File name: V[VERSION]__[description].sql
- SQL statements for change
- Indexes for foreign keys and frequently queried columns
- Constraints (NOT NULL, UNIQUE, CHECK, FK)
- Comments for complex structures

Never modify existing migrations - create new ones for changes.
```

### 16. Optimize Query (N+1 Prevention)
```
Fix N+1 query problem in [REPOSITORY METHOD]:

Current Query: [DESCRIBE CURRENT APPROACH]
Entities: [MAIN ENTITY AND RELATED ENTITIES]

Generate optimized query using:
- @EntityGraph annotation
- JOIN FETCH in JPQL
- Batch fetch hints
- Proper indexing recommendations

Verify with SQL logging (show.sql=true).
```

---

## Event-Driven Architecture Prompts

### 17. Create Event Publisher
```
Implement domain event publisher for [BOUNDED CONTEXT]:

Events to Publish: [LIST EVENT TYPES]
Transport: [Spring Events / Message Queue / etc.]

Generate:
- Event publisher interface (domain layer)
- Event publisher implementation (infrastructure layer)
- @Async annotation if background processing needed
- Transaction synchronization (publish after commit)

Ensure events are published AFTER transaction commits successfully.
```

### 18. Create Event Handler
```
Create event handler for [EVENT] in [CONSUMING CONTEXT]:

Event: [EVENT NAME, e.g., UserRegistered]
Source Context: [PUBLISHING CONTEXT]
Consumer Context: [THIS CONTEXT]
Action: [WHAT TO DO WHEN EVENT RECEIVED]

Generate:
- Event listener method with @EventListener
- @Transactional annotation (new transaction)
- @Async for background processing (if applicable)
- Error handling (retry logic, dead letter queue)
- Idempotency check (don't process same event twice)

Example: Gamification context listens to UserRegistered from User context.
```

---

## Security Prompts

### 19. Add Authentication to Endpoint
```
Secure endpoint [PATH] with JWT authentication:

Endpoint: [PATH AND METHOD]
Required Role: [ROLE_USER/ROLE_ADMIN/ROLE_CITY_ADMIN]

Generate:
- @PreAuthorize annotation with role check
- JWT token extraction in controller
- User context loading from token
- 401 Unauthorized if token missing
- 403 Forbidden if insufficient permissions

Use Spring Security configuration.
```

### 20. Implement Authorization Check
```
Add authorization check in use case [USE CASE]:

Rule: [WHO CAN PERFORM THIS ACTION]
Example: "Users can only edit their own actions"

Generate:
- Authorization logic in application service
- Extract current user from SecurityContext
- Check ownership or permission
- Throw InsufficientPermissionsException if denied

Don't rely on controller-level checks alone - enforce in business logic.
```

---

## Performance Prompts

### 21. Add Caching
```
Add caching for [RESOURCE]:

Resource: [e.g., Plant catalog, Leaderboard]
Cache Strategy: [Cache-aside, Write-through, etc.]
TTL: [TIME TO LIVE]
Invalidation: [WHEN TO CLEAR CACHE]

Generate:
- @Cacheable annotation on repository method
- @CacheEvict for invalidation
- Redis configuration (if using Redis)
- Cache key generation logic

Verify cache hits with metrics.
```

### 22. Implement Async Operation
```
Make [OPERATION] asynchronous:

Operation: [e.g., Send email, Process analytics]
Reason: [WHY IT SHOULD BE ASYNC]

Generate:
- @Async annotation on method
- CompletableFuture return type
- Async executor configuration
- Error handling (log errors, don't silently fail)

Use for non-critical operations that don't require immediate response.
```

---

## Architecture Validation Prompts

### 23. Generate ArchUnit Test
```
Create ArchUnit test to enforce [RULE]:

Rule: [e.g., "Domain layer must not depend on infrastructure"]

Generate test with:
- @AnalyzeClasses annotation
- Architecture rule definition
- Package structure validation
- Naming convention checks
- Layer dependency checks

Example rules:
- Domain can't import Spring
- Controllers must end with "Controller"
- Repositories must end with "Repository"
```

---

## Documentation Prompts

### 24. Document API Endpoint
```
Generate API documentation for [ENDPOINT]:

Endpoint: [PATH AND METHOD FROM OPENAPI]

Generate markdown documentation with:
- Purpose: What this endpoint does
- Authentication: Required authentication
- Request: Schema, example JSON
- Response: Schema, example JSON for success and errors
- Status Codes: All possible codes and meanings
- Curl Example: Working curl command
- Rate Limiting: If applicable

Save to docs/api/[context]-api-guide.md.
```

---

## Related Files
- **Backend Instructions**: `.github/instructions/backend-instructions.md`
- **Backend DDD Agent**: `.github/agents/backend-ddd.agent.md`
- **Backend Business Logic Agent**: `.github/agents/backend-business-logic.agent.md`
- **OpenAPI Spec**: `openapi/urbanbloom-api-v1.yaml`
- **Domain Documentation**: `shared-resources/documentation/domain-model-description-urbanbloom.md`
