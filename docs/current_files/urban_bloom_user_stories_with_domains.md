# UrbanBloom — Refined User Stories mit Domain-Zuordnung

## Priorisierung (MoSCoW)
- **MUST:** 1.1, 1.2, 2.1, 2.2, 2.4, 3.1, 3.2, 3.3, T1, T2
- **SHOULD:** 1.3, 2.3
- **COULD:** —

---

## User Story 1.1 – Punkte & Auszeichnungen  
**Domain:** Gamification  
**MoSCoW:** MUST

**Beschreibung:**  
Als Bürger möchte ich für meine Begrünungsaktionen Punkte und Auszeichnungen erhalten, damit mein Engagement sichtbar ist und ich mich mit anderen vergleichen kann.

**Konversationspunkte:**
- Auszeichnungen werden automatisch bei definierten Punktschwellen vergeben.
- Zusätzliche Badges für außergewöhnliche Leistungen.
- Punkte/Badges sind im Profil & Rangliste sichtbar.

**Akzeptanzkriterien:**
- Punkte werden serverseitig gutgeschrieben.
- Badge bei Punktschwelle automatisch.
- Punkte- und Badge-Historie sichtbar.
- Badge-Logik vollständig serverseitig.

**Story Points:** 5

---

## User Story 1.2 – Ranglisten  
**Domain:** Gamification / Analytics  
**MoSCoW:** MUST

**Beschreibung:**  
Als Bürger möchte ich eine Rangliste einsehen, um meinen Fortschritt mit anderen zu vergleichen.

**Konversationspunkte:**
- Wöchentliche Aktualisierung.
- Filter nach Bezirken & Zeiträumen.
- Anonymisierte Anzeige by default.

**Akzeptanzkriterien:**
- Ranking: Benutzer / Bezirke / Stadt.
- Aktualisierung mindestens wöchentlich.
- Filter vorhanden.
- Username nur sichtbar mit Zustimmung.

**Story Points:** 3

---

## User Story 1.3 – Persönlicher Fortschritt  
**Domain:** Gamification / Analytics  
**MoSCoW:** SHOULD

**Beschreibung:**  
Als Benutzer möchte ich meinen persönlichen Fortschritt grafisch einsehen, um langfristige Entwicklung zu verfolgen.

**Konversationspunkte:**
- Wochenbasierter Balkenverlauf.
- Badges im selben Bereich.

**Akzeptanzkriterien:**
- Balkendiagramm zeigt Aktionen pro Woche.
- Badges werden grafisch angezeigt.
- Daten korrekt & konsistent mit Backend.

**Story Points:** 3

---

## User Story 2.1 – GPS & QR Erfassung  
**Domain:** Action / Observation & Location  
**MoSCoW:** MUST

**Beschreibung:**  
Als Benutzer möchte ich Aktionen per GPS oder QR erfassen, um diese schnell und verifizierbar zu registrieren.

**Konversationspunkte:**
- Jeder Standort hat eindeutige Location-ID.
- Offline-QR möglich.

**Akzeptanzkriterien:**
- QR oder GPS löst Action-Erfassung aus.
- Offline-Eingaben werden später synchronisiert.
- Ungültige IDs werden abgelehnt.

**Story Points:** 5

---

## User Story 2.2 – Manuelle Eingabe  
**Domain:** Action / Observation  
**MoSCoW:** MUST

**Beschreibung:**  
Als Benutzer möchte ich Aktionen manuell eintragen, damit auch Aktionen ohne GPS/QR zählen.

**Konversationspunkte:**
- Pflichtfelder: Pflanzenart, Datum, Ort.
- Plausibilitätsprüfung.

**Akzeptanzkriterien:**
- Pflichtfelder müssen befüllt werden.
- Validierungsfehler werden angezeigt.
- Gültige Eingaben führen zu sofortiger Punktevergabe.

**Story Points:** 3

---

## User Story 2.3 – Push-Benachrichtigungen  
**Domain:** Notification  
**MoSCoW:** SHOULD

**Beschreibung:**  
Als Benutzer möchte ich Pflegeerinnerungen erhalten, damit Pflanzen kontinuierlich betreut werden.

**Konversationspunkte:**
- Wiederkehrende Notifications.
- Nutzer kann deaktivieren.

**Akzeptanzkriterien:**
- Erinnerungen einstellbar.
- Benachrichtigungen zur gewählten Zeit.
- Abstellbar in Einstellungen.

**Story Points:** 2

---

## User Story 2.4 – Offline Modus  
**Domain:** Sync / Offline & Action  
**MoSCoW:** MUST

**Beschreibung:**  
Als Benutzer möchte ich Aktionen offline speichern, damit ich unabhängig vom Internet teilnehmen kann.

**Konversationspunkte:**
- Lokale Speicherung.
- Automatische Synchronisation.

**Akzeptanzkriterien:**
- Aktionen werden lokal gespeichert.
- Automatischer Upload.
- Fehlerstatus sichtbar.

**Story Points:** 5

---

## User Story 3.1 – Bezirksfortschritt  
**Domain:** Admin / Analytics  
**MoSCoW:** MUST

**Beschreibung:**  
Als Stadtverwaltung möchte ich Bezirksfortschritte einsehen, um Maßnahmen planen zu können.

**Akzeptanzkriterien:**
- Dashboard zeigt Bezirksdaten.
- Zeitfilter vorhanden.
- Daten konsistent.

**Story Points:** 4

---

## User Story 3.2 – Bezirksvergleich  
**Domain:** Admin / Analytics  
**MoSCoW:** MUST

**Beschreibung:**  
Als Verwaltung möchte ich Bezirke vergleichen, um Wettbewerbe zu fördern.

**Akzeptanzkriterien:**
- Bezirksranking Ansicht verfügbar.
- CSV-Export möglich.

**Story Points:** 5

---

## User Story 3.3 – Challenges erstellen  
**Domain:** Challenge / Campaign  
**MoSCoW:** MUST

**Beschreibung:**  
Als Administrator möchte ich Challenges erstellen, um Engagement zu steigern.

**Akzeptanzkriterien:**
- Titel, Zeitraum, Zielwert anlegbar.
- Challenge erscheint in Nutzeransicht.

**Story Points:** 5

---

## Technische Story T1 – Sichere API  
**Domain:** Security / Privacy  
**MoSCoW:** MUST

**Beschreibung:**  
Als Entwickler möchte ich eine sichere API, um Datenzugriffe zu schützen.

**Akzeptanzkriterien:**
- Token-basierte Authentifizierung.
- Logging sicherheitsrelevanter Aktionen.

**Story Points:** 8

---

## Technische Story T2 – Datenschutz  
**Domain:** Security / Privacy & Admin / Analytics  
**MoSCoW:** MUST

**Beschreibung:**  
Als Benutzer möchte ich Datenschutz gewährleistet wissen, damit meine persönlichen Daten geschützt sind.

**Akzeptanzkriterien:**
- End-to-End Verschlüsselung.
- Anonymisierte Analysen.

**Story Points:** 3

