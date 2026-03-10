# Module: User Context (Generic Subdomain)

**Domänen-Kategorie:** Generic Subdomain  
**Architekturmuster:** Anti-Corruption Layer (ACL) + CRUD  
**Kritikalität:** ⭐ (Low - Standard IAM)

## Übersicht

Der **User Context** verwaltet Benutzeridentitäten und -profile. Die Authentifizierung erfolgt über externes Schul-SSO (Keycloak).

### Verantwortlichkeiten

- ✅ User-Profil-Verwaltung (Name, E-Mail, Nutzergruppe)
- ✅ Anti-Corruption Layer (ACL) zu externem SSO
- ✅ Mapping: SSO-Rollen → interne `UserGroup` Enum
- ✅ Borrowing Limit-Verwaltung (abhängig von UserGroup)

## Architektur-Struktur

```
module-user/
├── domain/                      # User Entity, UserGroup (Value Object)
├── application/                 # User CRUD Services
├── adapter/
│   ├── in/rest/                # User API
│   ├── out/persistence/        # JPA Repository
│   └── out/sso/                # Keycloak ACL Adapter
└── config/                      # Spring Configuration
```

---

## Domain Model

**User Entity:**
```java
@Data
@Builder
public class User {
    private UserId id;
    private String email;
    private String firstName;
    private String lastName;
    private UserGroup userGroup;
    private int borrowingLimit;
    
    public int getBorrowingLimit() {
        return switch (userGroup) {
            case STUDENT -> 5;
            case TEACHER -> 10;
            case LIBRARIAN -> 20;
        };
    }
}

public enum UserGroup {
    STUDENT,
    TEACHER,
    LIBRARIAN
}
```

---

## Anti-Corruption Layer (`adapter/out/sso/`)

**Zweck:** Konvertiert SSO-Attribute in interne Enums

**Beispiel:**
```java
@Component
public class KeycloakAdapter {
    
    public UserGroup mapToUserGroup(Authentication authentication) {
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        
        if (hasRole(authorities, "ROLE_LIBRARIAN")) {
            return UserGroup.LIBRARIAN;
        } else if (hasRole(authorities, "ROLE_TEACHER")) {
            return UserGroup.TEACHER;
        } else if (hasRole(authorities, "ROLE_STUDENT")) {
            return UserGroup.STUDENT;
        }
        
        throw new InvalidUserGroupException("Unknown user group");
    }
    
    private boolean hasRole(Collection<? extends GrantedAuthority> authorities, String role) {
        return authorities.stream()
            .anyMatch(auth -> auth.getAuthority().equals(role));
    }
}
```

---

## Geschäftsregeln

- ✅ Authentifizierung **nur über SSO** (keine lokalen Passwörter)
- ✅ UserGroup wird vom SSO übernommen
- ✅ Borrowing Limit abhängig von UserGroup:
  - Student: 5 Medien
  - Teacher: 10 Medien
  - Librarian: 20 Medien

---

## REST API (`adapter/in/rest/`)

**Endpoints:**
- `GET /api/v1/users/me` - Eigenes Profil
- `GET /api/v1/users/{id}` - User nach ID (nur Librarian)
- `PUT /api/v1/users/me` - Eigenes Profil aktualisieren

---

## Datenbankschema

Schema: **`user_schema`**

Tabellen:
- `users` - Nutzerprofile

---

## Referenzen

- 📖 [Strategic Architecture Summary](../../docs/architecture/strategic-architecture-summary.md)
- 🔐 [Security Configuration](../server-app/src/main/java/com/urbanbloom/app/config/SecurityConfig.java)


---

## Self-Registration Implementation (US-USR-02-REF)

### Architecture

#### Domain Layer (`domain/`)
Contains the core business logic and rules:

- **Aggregates**: `UserProfile` - manages user profile lifecycle
- **Value Objects**: 
  - `Email` - validated email address
  - `UserName` - user's name
  - `ExternalUserId` - reference to IdP user
  - `AllowedDomain` - permitted registration domains
  - `UserRole` - user roles (STUDENT, TEACHER, LIBRARIAN)
- **Domain Services**: 
  - `RegistrationService` - business rules for registration
  - `DefaultRegistrationService` - default implementation
- **Repository Port**: `UserProfileRepository` - persistence abstraction
- **IdP Port**: `IdentityProvider` - external identity provider abstraction

#### Application Layer (`application/`)
Orchestrates use cases:

- **Services**: `UserRegistrationService` - registration workflow
- **Commands**: `RegisterUserCommand` - registration input
- **Results**: `RegistrationResult` - registration output
- **Exceptions**: `RegistrationException` - registration errors

#### API Layer (`api/`)
- **Controllers**: `RegistrationController` - registration endpoints
- **DTOs**: `RegistrationRequestDto`, `RegistrationResponseDto`, `EmailAvailabilityDto`
- **Mappers**: `RegistrationMapper` - DTO ↔ Command/Result mapping

### Registration Endpoint

**Endpoint**: `POST /api/v1/registration` (Public - No Authentication Required)

**Request**:
```json
{
  "email": "student@schule.de",
  "password": "SecurePassword123!",
  "firstName": "Max",
  "lastName": "Mustermann",
  "studentId": "S12345",
  "schoolClass": "10A"
}
```

**Response (201 Created)**:
```json
{
  "userId": "keycloak-user-id",
  "email": "student@schule.de",
  "message": "Registration successful. Please check your email to verify your account.",
  "verificationRequired": true
}
```

**Response (400 Bad Request)**:
```json
{
  "userId": null,
  "email": "student@other.de",
  "message": "Email domain not allowed for registration: student@other.de",
  "verificationRequired": false
}
```

### Configuration

Add to `application.properties`:

```properties
# Registration Configuration
urbanbloom.registration.allowed-domains=schule.de,student.schule.de
urbanbloom.registration.email-verification-required=true
urbanbloom.registration.verification-token-expiration-hours=24
```

Environment variable: `REGISTRATION_ALLOWED_DOMAINS=schule.de,student.schule.de`

### Database Schema

**Schema**: `module_user_schema`

Migration `V3__migrate_user_profiles_to_module_schema.sql` migrates existing table.

### Business Rules

1. Only emails from allowed domains can register
2. Each email can only be registered once
3. All self-registered users start with STUDENT role
4. Teacher/Librarian roles must be assigned by admins post-registration
5. Email verification is required before login
6. Custom attributes (studentId, schoolClass) are optional

### Testing

Unit tests created:
- `AllowedDomainTest` - Domain validation
- `DefaultRegistrationServiceTest` - Business rules
- `UserRegistrationServiceTest` - Application service

Run tests:
```bash
cd backend
mvn test -pl module-user
```

