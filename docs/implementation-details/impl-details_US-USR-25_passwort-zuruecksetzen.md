# Implementierungs-Dokumentation: [STORY] Passwort zurücksetzen

## 📋 Übersicht
- **User Story / Feature ID:** US-25 (Sub-Issue von #22)
- **Datum:** 2026-01-14
- **Status:** Abgeschlossen
- **Verantwortlich:** Antigravity (AI Assistant)

## 1. 🎯 Kontext & Ziele
Benutzer der Digitalen Schulbibliothek müssen in der Lage sein, ihr Passwort sicher zurückzusetzen. Die Implementierung umfasst eine End-to-End Lösung vom Mobile-Frontend über das Spring Boot Backend bis hin zur Keycloak-Integration.

- **Ziel:** Bereitstellung einer benutzerfreundlichen Oberfläche zur Password-Reset-Anforderung und die technische Umsetzung mittels Keycloak Admin API.
- **Referenz:** [Issue #25](https://github.com/ukondert/pr_digital-school-library/issues/25)

## 2. 🏗️ Architektur-Überblick
Die Lösung folgt der **Hexagonalen Architektur** im Backend und dem **Feature-First Pattern** im Flutter-Frontend.

### Komponenten & Layer
- **Frontend (Flutter):**
  - Feature: `user`
  - Pattern: MVVM mit Riverpod für State-Management.
  - Routing: GoRouter für Navigation zwischen Login, Request-Form und Success-Page.
- **Backend (Spring Boot):**
  - Modul: `module-user`
  - Pattern: Domain-Driven Design (DDD) mit klarer Trennung in API, Application, Domain und Infrastructure Layer.
- **Identity Provider (Keycloak):**
  - Verantwortlich für Token-Management, E-Mail-Versand und Passwort-Speicherung.

## 3. 🧩 Detailliertes Design & Klassen-Struktur

### Backend Struktur
Das Backend nutzt den `IdentityProvider`-Port, um Keycloak-spezifische Implementierungen zu kapseln (Anti-Corruption Layer).

```plantuml
@startuml
skinparam classAttributeIconSize 0

package "API Layer" {
    class AuthController {
        +requestPasswordReset(PasswordResetRequestDto)
        +resetPassword(PasswordResetDto)
    }
}

package "Application Layer" {
    class PasswordResetApplicationService {
        +requestPasswordReset(RequestPasswordResetCommand)
        +resetPassword(ResetPasswordCommand)
    }
}

package "Domain Layer" {
    class PasswordResetRequested <<domain-event>> {
        +email: String
        +requestedAt: Instant
    }
    class PasswordResetCompleted <<domain-event>> {
        +userId: String
        +email: String
    }
}

package "Infrastructure Layer" {
    interface IdentityProvider <<Anti-Corruption Layer>> {
        +sendPasswordResetEmail(email: String)
        +resetUserPassword(userId: String, newPassword: String)
    }
    class KeycloakIdentityProvider {
        +sendPasswordResetEmail(email: String)
        +resetUserPassword(userId: String, newPassword: String)
    }
}

AuthController --> PasswordResetApplicationService
PasswordResetApplicationService --> IdentityProvider
KeycloakIdentityProvider ..|> IdentityProvider
PasswordResetApplicationService ..> PasswordResetRequested : publishes
PasswordResetApplicationService ..> PasswordResetCompleted : publishes
@enduml
```

### Frontend Struktur
Das Flutter-Frontend verwendet Riverpod-Provider für die API-Kommunikation und GoRouter für den Screen-Flow.

- **Routes:**
  - `/forgot-password`: [ForgotPasswordScreen](../../frontend-mobile/lib/features/user/presentation/pages/forgot_password_screen.dart)
  - `/password-sent`: [PasswordSentScreen](../../frontend-mobile/lib/features/user/presentation/pages/password_sent_screen.dart)
- **State & Data:**
  - API Service: `userApiProvider` (Riverpod)
  - DTOs: `PasswordResetRequestDTO`

## 🎬 4. Kommunikationsabläufe

### Sequenzdiagramm: Passwort-Reset anfordern

```plantuml
@startuml
actor User
participant "ForgotPasswordScreen" as UI
participant "UserApi (Riverpod)" as API_SVC
participant "AuthController (REST)" as API
participant "PasswordResetApplicationService" as SVC
participant "KeycloakIdentityProvider" as ADP
participant "Keycloak Server" as KC

User -> UI: Gibt E-Mail ein & klickt "Link anfordern"
UI -> API_SVC: requestPasswordReset(email)
API_SVC -> API: POST /api/v1/auth/password/reset-request
API -> SVC: requestPasswordReset(command)
SVC -> ADP: sendPasswordResetEmail(email)
ADP -> KC: executeActionsEmail(["UPDATE_PASSWORD"])
KC -> User: Sendet Email mit Link
SVC -> SVC: Publish PasswordResetRequested
API -->> API_SVC: 200 OK
API_SVC -->> UI: Success
UI -> UI: Navigation zu /password-sent
@enduml
```

### Sequenzdiagramm: Passwort zurücksetzen (Abschluss)

```plantuml
@startuml
actor User
participant "Email Client" as Mail
participant "Mobile App" as UI
participant "Auth Controller" as API
participant "Password Reset Service" as SVC
participant "Keycloak Adapter" as ADP
participant "Keycloak Server" as KC

User -> Mail: Klickt auf Reset-Link
Mail -> UI: Öffnet App via Deep Link (inkl. Token)
User -> UI: Gibt neues Passwort ein
UI -> API: POST /api/v1/auth/password/reset {email, newPassword, token}
API -> SVC: resetPassword(command)
SVC -> SVC: UserProfile laden (extUserId ermitteln)
SVC -> ADP: resetUserPassword(extUserId, newPassword)
ADP -> KC: resetPassword(credential)
KC -->> ADP: Success
SVC -> SVC: Publish PasswordResetCompleted
API -->> UI: 200 OK
UI -->> User: "Passwort erfolgreich geändert"
@enduml
```

**Erläuterung:**
- **Deep Linking:** Der Klick in der E-Mail wird vom Betriebssystem erkannt und an die App weitergeleitet. Das Token aus der URL wird extrahiert.
- **Sicherheit:** Das Token wird von Keycloak validiert (im Rahmen des Passwort-Resets). Das Backend ermittelt über das `UserProfile` die interne Keycloak-ID.
## 🗄️ 5. Datenmodell
In der lokalen PostgreSQL-Datenbank werden keine Reset-Informationen persistent gespeichert. Das System verlässt sich vollständig auf die Keycloak-Infrastruktur.

- **Domain Events:**
    - `PasswordResetRequested`: Audit-Log für die Anforderung.
    - `PasswordResetCompleted`: Audit-Log für den erfolgreichen Abschluss.

## 💻 6. Implementierungs-Details

### Frontend (Mobile)
- **Komponenten:**
  - `PasswordResetForm`: Validiert die E-Mail-Adresse (Format-Check) und steuert den Ladezustand des Buttons.
  - `PasswordResetSuccessCard`: Zeigt eine Bestätigung an und bietet einen Button zurück zum Login.
- **Deep Linking:** Die App ist so konfiguriert (via AndroidManifest/Entitlements), dass sie Keycloak-Reset-Links erkennt und den Benutzer direkt zur Passwort-Eingabe führt.

### Backend (Infrastructure)
- **Keycloak Admin Client:** Nutzt `keycloak-admin-client` Bibliothek für die Kommunikation.
- **Security:** CSRF ist für die öffentlichen Reset-Endpunkte deaktiviert.

### Wichtige Dateien:
- **Frontend:**
    - [forgot_password_screen.dart](../../frontend-mobile/lib/features/user/presentation/pages/forgot_password_screen.dart)
    - [password_reset_form.dart](../../frontend-mobile/lib/design_system/components/molecules/password_reset_form.dart)
- **Backend:**
    - [AuthController.java](../../backend/module-user/src/main/java/com/schoollibrary/user/api/AuthController.java)
    - [KeycloakIdentityProvider.java](../../backend/module-user/src/main/java/com/schoollibrary/user/adapter/infrastructure/keycloak/KeycloakIdentityProvider.java)

## ✅ 7. Verifizierung & Tests
- **Automatisierte Tests:**
    - Unit Tests (Backend): `PasswordResetApplicationServiceTest`
    - Widget Tests (Frontend): Geplant für den nächsten Sprint.
- **Manuelle Verifizierung:**
    - Vollständiger Flow vom Login-Screen über E-Mail-Eingabe bis zum Empfang der E-Mail (verifiziert via Mailhog).

## 🔗 8. Referenzen
- [API-Spec (OpenAPI)](../api/user.yaml)
- [Keycloak Configuration](../configurations/keycloak-setup.md)
