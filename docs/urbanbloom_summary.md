# UrbanBloom – Projekt-Zusammenfassung

## Überblick
UrbanBloom ist eine Citizen-Science-Plattform, die Bürger:innen motiviert, städtische Grünflächen zu bepflanzen und zu pflegen. Das System nutzt Gamification-Elemente (Punkte, Badges, Ranglisten) und ermöglicht der Stadtverwaltung, Fortschritte auf Bezirksebene zu analysieren und Challenges zu organisieren.

## Kernfunktionalität
**Bürger:innen** erfassen Begrünungsaktionen per GPS, QR-Code oder manueller Eingabe. Jede validierte Aktion wird mit Punkten belohnt. Das System funktioniert auch offline – Aktionen werden lokal gespeichert und später synchronisiert.

**Verwaltung** erhält Dashboards mit Bezirksvergleichen, kann Reports exportieren (CSV/PDF) und zeitlich begrenzte Challenges erstellen, um Engagement zu steigern.

## Domain-Driven Design Architektur
Das System ist in **9 Bounded Contexts** strukturiert:

1. **User/Identity** – Registrierung, Login, Datenschutz (DSGVO-Löschung)
2. **Action/Observation** – Erfassung und Validierung von Begrünungsaktionen
3. **Plant Catalog** – Zentraler Pflanzenkatalog mit Pflegehinweisen
4. **Location/District** – Standortverwaltung und Bezirkszuordnung
5. **Gamification** – Punkte, Badges, Leaderboards
6. **Challenge** – Zeitlich begrenzte Wettbewerbe (Stadt/Bezirk)
7. **Notification/Reminder** – Push-Benachrichtigungen und Pflegeerinnerungen
8. **Admin/Analytics** – Reports und Bezirksvergleiche für Verwaltung
9. **Sync/Offline** – Offline-Funktionalität mit Konfliktauflösung

## Technische Highlights
- **Event-Driven**: Domains kommunizieren über Events (z.B. `action.validated` → `points.awarded`)
- **Aggregates**: Action, User, District, Challenge als Konsistenzgrenzen
- **Domain Services**: GeoVerification, PlantPlausibility, PointsEngine, BadgeAssignment
- **Privacy-by-Design**: Anonymisierung, Consent-Management, verschlüsselte Datenübertragung

## Key User Stories (MUST)
- Punkte & Badges für Aktionen erhalten
- Ranglisten (User/Bezirk) einsehen
- Aktionen per GPS/QR/Manuell erfassen
- Offline-Modus mit automatischer Sync
- Bezirksfortschritt & -vergleich (Admin)
- Challenges erstellen (Admin)
- Sichere API mit Token-Auth
- DSGVO-konformer Datenschutz

## Business Rules
- Aktionen müssen validiert werden (Geo + Plausibilität)
- Punkte nur für validierte Aktionen
- Badges bei definierten Schwellenwerten
- Offline-Sync ist idempotent (keine Duplikate)
- Reports enthalten keine personenbezogenen Rohdaten
