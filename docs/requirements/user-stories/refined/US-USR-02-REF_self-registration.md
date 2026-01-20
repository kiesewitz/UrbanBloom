# User Story US-USR-02-REF: Self-Registration (Refined)

## Story
**As a** prospective student, teacher or library staff member  
**I want to** register an account using my school e‑mail address  
**So that** I can create an identity in the schoollibrary system and later login securely

## Priority
**Must-Have** | MVP Phase 1 (dependent on Keycloak availability)

## UI-Prototyp (Mobile)

- HTML: [registrierung_für_schul-app](../../../ui/prototypes/stitch_schulbibliotheks_app/registrierung_für_schul-app/code.html)
- Bild:

![registrierung_für_schul-app](../../../ui/prototypes/stitch_schulbibliotheks_app/registrierung_für_schul-app/screen.png)

## Summary / Motivation
Registration has its own flows and risks (domain validation, e‑mail verification, abuse protection, role assignment). Separating it from general authentication improves clarity for implementation, testing and acceptance.

## Refinement Discussion

- Registrierung nur mit erlaubten Schul‑Domänen.
- Lehrer‑Accounts werden zentral importiert / verifiziert durch Admin; normale E‑Mails => Schüler‑Rolle.
- Double‑opt: E‑Mail‑Verifikationslink aktiviert den Account.
- Rate‑Limiting / Captcha bei Missbrauchsverdacht.
- DSGVO: minimale Datenspeicherung, Einwilligungstext, Opt‑out/Widerruf.

## Tasks

### Backend
- Keycloak‑Client konfigurieren für Registrations‑Flow (if using Keycloak registration API)
- Backend‑Endpoint zum Triggern einer Registrierung (optional: benutzt Keycloak Admin API)
- Validierung der E‑Mail‑Domain gegen erlaubte Domains (configurable)
- Erzeugung und Versand der Verifikations‑E‑Mail (Template + Token)
- Webhook/Callback zum Aktivieren des Accounts nach Klick auf den Verifikationslink
- Role‑Assignment Rules (z. B. Lehrer vs. Schüler) implementieren
- Abuse‑Protection: Rate‑Limiting, Captcha‑Integration (optional)
- Logging / Audit‑Trail für Registrationsereignisse
- Implementiere und nutze das gleiche `IdentityProvider`-Interface (siehe Auth‑Story) für Registrations‑Provisioning, damit Keycloak-spezifische Logik gekapselt ist.
- Bei Nutzung von Keycloak: `KeycloakIdentityProvider` stellt Registration, Attribut‑Setzung (schoolClass, studentId) und Verifikations‑Hooks bereit.
- Speichere minimale Nutzer‑Metadaten in `UserProfile` (externalId → IdP user id) und synchronisiere bei Bedarf (idempotent).
- Stelle sicher, dass Verifikations‑E‑Mails / Tokenerzeugung vom Adapter oder einem klaren Registrierungsservice abstrahiert werden (nicht verstreut in Business‑Logik).

### Frontend Web (Web Admin App – nicht zutreffend)
- Self-Registration ist ein User-Flow und wird in der Mobile App umgesetzt; die Web Admin App bietet hierfür keine UI.

### Frontend Mobile
- Registrierungsflow analog Web (secure storage, deep link handling für Verifikationslink)
- UI für Verifikationsstatus (z. B. „Bitte E‑Mail bestätigen“)

### Testing
- Unit‑Tests für Domain‑Validation und Role‑Assignment
- Integration‑Tests für Verifikations‑Link Flow
- E2E: Registrierung → Verifikation → Login
- Security Tests: Rate‑Limiting, invalid token handling

## Akzeptanzkriterien

### Functional
- [ ] Benutzer können sich mit einer E‑Mail einer erlaubten Schuldomaine registrieren
- [ ] Registrierungsscreen zeigt Hinweis "Nur E-Mails mit @schule.de erlaubt" sowie die Felder "Schul-E-Mail", "Neues Passwort" und "Passwort bestätigen"
- [ ] Nach Absenden wird eine Verifikations‑E‑Mail mit einmaligem Token versendet
- [ ] Verifikationslink aktiviert Account; danach ist Login möglich
- [ ] Vor Verifikation ist der Account inaktiv / gesperrt für Login
- [ ] Automatische Rollenzuordnung erfolgt nach konfigurierbaren Regeln
- [ ] Fehlerfälle (bereits registriert, unzulässige Domain, abgelaufener Token) zeigen klare Meldungen

### Non‑Functional
- [ ] Registrierung dauert unter 3 Sekunden (server side response target)
- [ ] Registrationsendpunkte sind gegen Brute‑Force geschützt (Rate‑Limiting)
- [ ] Verifikationslinks laufen nach konfigurierbarer Zeit ab (z. B. 24h)
- [ ] E‑Mails werden asynchron versendet (Queue)

### Technical
- [ ] Registrierung erfolgt über Backend mit Keycloak Admin API und Keycloak Synchronisation
- [ ] Verifikations‑Token sind signiert/ungefälscht und kurzlebig
- [ ] Audit‑Logs für Registrationsereignisse vorhanden

## Acceptance Notes / UX
- Formulare sollten inline‑Validierung haben (z. B. Domain‑Check vor Absenden)
- Anzeige: „Verifikations‑E‑Mail gesendet — prüfe Postfach und Spam“

## Dependencies
- Keycloak Server + Admin API credentials
- E‑Mail service (SMTP or Send API)
- Configurable list of allowed school domains

## Risks & Open Questions
- Wer darf Lehrer‑Accounts anlegen — nur Admins oder Self‑Registration mit Verifikation?
- Wenn Keycloak Registration genutzt wird: wie werden Custom Attributes (schoolClass, studentId) gesetzt?
- E‑Mail‑Zustellbarkeit kann Blocker sein — Test‑SMTP in Staging nötig

## Definition of Done
- Implementierte Endpoints/UI + Tests (unit + integration + e2e)  
- Dokumentation: Konfiguration der erlaubten Domains + E‑Mail Templates  
- Security review for rate‑limiting and token handling

## Relation zur User Story 1
Diese Story trennt das Onboarding (Registrierung & Verifikation) vom reinen Login‑/Auth‑Mechanismus in `01-refined-user-authentication.md`. Nach Implementierung referenziert Auth‑Story die Registration‑Story für end‑to‑end Flows.
