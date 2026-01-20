# API Review: US-001 Benutzerkonto & SSO

**Reviewer:** GitHub Copilot
**Date:** 2025-12-18
**Spec:** docs/api/nutzerprofil.yaml

## Checklist

- [x] **Naming Conventions:**
  - `User`, `UserGroup` (Role) entsprechen der Ubiquitous Language.
  - `SchoolIdentity` wird als `email` abgebildet.
- [x] **RESTful Best Practices:**
  - HTTP Methoden (POST, GET, PUT) korrekt eingesetzt.
  - Resource Naming (`/auth`, `/config`) ist verständlich.
- [x] **Vollständigkeit der Akzeptanzkriterien:**
  - Registrierung mit Passwort-Setzen: ✅ (`POST /auth/register`)
  - Login mit Passwort: ✅ (`POST /auth/login`)
  - Konfiguration der Domänen: ✅ (`GET/PUT /config/domains`)
- [x] **Error Handling:**
  - Standard HTTP Codes (201, 400, 401, 403, 409) definiert.
  - `ErrorResponse` Schema vorhanden.
- [x] **Security:**
  - Bearer Auth für administrative Endpunkte definiert.
  - Passwort-MinLength Validierung im Schema.

## Findings & Decisions

1. **Authentication Flow:**
   - Die API weicht von der ursprünglichen "Pure SSO" Architektur ab, um die Anforderung "Passwort festlegen" zu erfüllen. Dies wird als "Local Auth with Domain Validation" implementiert.

2. **Role Assignment:**
   - Die Rolle wird im `RegisterRequest` nicht übergeben (Security), sondern muss vom Backend bestimmt werden (Default: STUDENT, oder basierend auf E-Mail-Pattern).

3. **Config Endpoint:**
   - `/config/domains` erlaubt das Setzen einer Liste von Strings. Dies ist ausreichend für das MVP.

## Approval
API Spec ist bereit für die Implementierung.
