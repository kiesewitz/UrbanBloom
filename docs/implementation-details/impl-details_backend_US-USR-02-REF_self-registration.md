# Implementierungs-Dokumentation: US-USR-02-REF: Self-Registration

## 📋 Übersicht
- **User Story / Feature ID:** US-USR-02-REF
- **Datum:** 2026-01-12
- **Status:** Abgeschlossen
- **Verantwortlich:** Backend Team / Senior Architect

## 1. 🎯 Kontext & Ziele
Prospective students and teachers need to be able to register accounts using their school email addresses to access the school library system.
- **Ziel:** Secure self-registration flow with domestic validation and email verification via Keycloak.
- **Referenz:** [US-USR-02-REF: Self-Registration (Refined)](file:///e:/SW-Dev\Git\ukondert\_school-projects\pr_digital-school-library\docs\requirements\user-stories\refined\US-USR-02-REF_self-registration.md)

## 2. 🏗️ Architektur-Überblick
Die Implementierung erfolgt im **User Context (Generic Subdomain)** innerhalb des `module-user`.
- **Architektur:** Hexagonale Architektur / Layered DDD (API -> Application -> Domain -> Infrastructure).
- **Betroffene Komponenten:**
    - **Backend:** `module-user` (API, Domain Logic, Keycloak Adapter).
    - **Infrastructure:** Keycloak (IAM) as the Identity Provider.

## 3. 🧩 Detailliertes Design & Klassen-Struktur
The design uses the **Adapter Pattern** for IdP abstraction and a **Domain Service** for business rule validation.

### Klassendiagramm
```plantuml
@startuml
!define AGGREGATE_BG #90EE90
!define ENTITY_BG #ADD8E6
!define VALUE_OBJECT_BG #FFFFE0
!define SERVICE_BG #F5DEB3
!define REPOSITORY_BG #D3D3D3

skinparam class {
    BackgroundColor<<AggregateRoot>> AGGREGATE_BG
    BackgroundColor<<Entity>> ENTITY_BG
    BackgroundColor<<ValueObject>> VALUE_OBJECT_BG
    BackgroundColor<<DomainService>> SERVICE_BG
    BackgroundColor<<Repository>> REPOSITORY_BG
    BorderColor #505050
    ArrowColor #333333
}

class RegistrationController <<DomainService>> {
    +register(RegistrationRequestDto)
    +checkEmailAvailability(String)
}

class UserRegistrationService <<ApplicationService>> {
    +registerUser(RegisterUserCommand)
}

interface RegistrationService <<DomainService>> {
    +isRegistrationAllowed(Email, List<AllowedDomain>)
    +determineInitialRole(Email)
}

class DefaultRegistrationService <<DomainService>> {
    +isRegistrationAllowed()
    +determineInitialRole()
}

interface IdentityProvider <<DomainService>> {
    +createUser(String, String, String, String, Map)
    +isEmailRegistered(String)
    +assignRole(String, String)
    +sendVerificationEmail(String)
}

class KeycloakIdentityProvider <<DomainService>> {
    +createUser()
    +isEmailRegistered()
    +assignRole()
    +sendVerificationEmail()
}

interface UserProfileRepository <<Repository>> {
    +save(UserProfile)
    +existsByEmail(Email)
}

class UserProfile <<AggregateRoot>> {
    +create(ExternalUserId, Email, UserName, UserRole)
    -externalUserId
    -email
    -userName
    -role
    -active
}

RegistrationController --> UserRegistrationService
UserRegistrationService --> RegistrationService
UserRegistrationService --> IdentityProvider
UserRegistrationService --> UserProfileRepository
DefaultRegistrationService ..|> RegistrationService
KeycloakIdentityProvider ..|> IdentityProvider
UserRegistrationService ..> UserProfile : creates
@enduml
```

**Erläuterung:**
- **UserRegistrationService:** Orchestriert den Registrierungsprozess (Validierung, IdP-Aktion, Persistierung).
- **RegistrationService:** Beinhaltet reine Domänenlogik (z.B. Domain-Check, Rollen-Logik).
- **KeycloakIdentityProvider:** Adapter, der die Keycloak Admin API anspricht und gegen das domäneninterne Interface kapselt.
- **UserProfile:** Aggregate Root für das lokale Benutzerprofil.

## 🎬 4. Kommunikationsabläufe
Der Fluss startet bei der öffentlichen REST-API und endet mit einer E-Mail-Verifikation in Keycloak.

### Sequenzdiagramm

```plantuml
@startuml
actor User
participant "RegistrationController" as Controller
participant "UserRegistrationService" as AppSvc
participant "DefaultRegistrationService" as DomSvc
participant "KeycloakIdentityProvider" as IdP
participant "UserProfileRepository" as Repo

User -> Controller : POST /registration (DTO)
activate Controller
Controller -> AppSvc : registerUser(Command)
activate AppSvc

AppSvc -> DomSvc : isRegistrationAllowed(email, allowedDomains)
activate DomSvc
DomSvc --[#blue]> AppSvc : true
deactivate DomSvc

AppSvc -> IdP : isEmailRegistered(email)
activate IdP
IdP --[#blue]> AppSvc : false
deactivate IdP

AppSvc -> Repo : existsByEmail(email)
activate Repo
Repo --[#blue]> AppSvc : false
deactivate Repo

AppSvc -> DomSvc : determineInitialRole(email)
activate DomSvc
DomSvc --[#blue]> AppSvc : role
deactivate DomSvc

AppSvc -> IdP : createUser(details, attributes)
activate IdP
IdP -> IdP : Creates user in Keycloak realm
IdP --[#blue]> AppSvc : externalUserId
deactivate IdP

AppSvc -> IdP : assignRole(userId, roleName)
AppSvc -> IdP : sendVerificationEmail(userId)

AppSvc -> AppSvc : UserProfile.create(...)
AppSvc -> Repo : save(new UserProfile)
activate Repo
Repo --[#blue]> AppSvc : savedProfile
deactivate Repo

AppSvc --[#blue]> Controller : RegistrationResult (Success)
deactivate AppSvc
Controller --[#blue]> User : 201 Created (Success Message)
deactivate Controller
@enduml
```

## 🗄️ 5. Datenmodell (Optional)
Ein lokales `UserProfile` wird erstellt, um die Brücke zwischen IdP-Identität und domänenspezifischen Daten (z.B. Ausleihen) zu schlagen.

### ER-Diagramm
```plantuml
@startuml
!define ENTITY_BG #ADD8E6

skinparam class {
    BackgroundColor ENTITY_BG
    BorderColor #505050
}

entity "user_profile" as user_profile {
    * id : string <<PK>>
    --
    * external_user_id : string <<UK>> "Keycloak ID"
    * email : string <<UK>>
    * first_name : string
    * last_name : string
    * role : string
    * active : boolean
    * created_at : timestamp
    * updated_at : timestamp
}
@enduml
```

## 💻 6. Implementierungs-Details
- **Design Patterns:** 
    - **Anti-Corruption Layer (ACL):** Der `KeycloakIdentityProvider` kapselt die Komplexität der Keycloak Admin API.
    - **Domain Service:** `RegistrationService` validiert Geschäftsregeln unabhängig von Infrastruktur.
    - **Factory Method:** `UserProfile.create(...)` stellt Invarianten bei der Erstellung sicher.
- **Besondere Herausforderungen:** Integration der Keycloak Admin Java Client Bibliothek und das Mapping von custom attributes (`studentId`, `schoolClass`).
- **Wichtige Code-Stellen:**
    - [UserRegistrationService.java](file:///e:/SW-Dev/Git/ukondert/_school-projects/pr_digital-school-library/backend/module-user/src/main/java/com/schoollibrary/user/application/UserRegistrationService.java#L39)
    - [KeycloakIdentityProvider.java](file:///e:/SW-Dev/Git/ukondert/_school-projects/pr_digital-school-library/backend/module-user/src/main/java/com/schoollibrary/user/adapter/infrastructure/keycloak/KeycloakIdentityProvider.java#L55)
    - [UserProfile.java](file:///e:/SW-Dev/Git/ukondert/_school-projects/pr_digital-school-library/backend/module-user/src/main/java/com/schoollibrary/user/domain/UserProfile.java#L46)

## ✅ 7. Verifizierung & Tests
- **Automatisierte Tests:**
    - Unit Tests: `UserRegistrationServiceTest`, `DefaultRegistrationServiceTest`
    - Integration Tests: `UserRegistrationIntegrationTest` (nutzt Testcontainers mit Keycloak)
- **Manuelle Tests:**
    - [ ] Registrierung mit `@gmail.com` (sollte fehlschlagen)
    - [ ] Registrierung mit `@schule.de` (Erfolg + E-Mail Trigger)
    - [ ] Login Versuch vor E-Mail Bestätigung (sollte fehlschlagen)

## 🔗 8. Referenzen
- [Keycloak Documentation](https://www.keycloak.org/documentation)
- [Architecture Index](file:///e:/SW-Dev/Git/ukondert/_school-projects/pr_digital-school-library/docs/architecture/README.md)
