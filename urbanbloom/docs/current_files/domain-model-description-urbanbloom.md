# UrbanBloom – Domain Model (Beschreibung)

## Überblick: Bounded Contexts (Domaingrenzen)
UrbanBloom besteht aus folgenden Bounded Contexts (Domains), die jeweils **eigene Konsistenzgrenzen** (Aggregate) besitzen. Kommunikation **über Domaingrenzen** erfolgt **nicht über direkte Objekt-Referenzen**, sondern über **IDs/Value Objects** und vor allem über **Domain Events / Integration Events**.

- User / Identity
- Action / Observation
- Plant Catalog
- Location / District
- Gamification
- Challenge
- Notification / Reminder
- Admin / Analytics
- Sync / Offline

---

## 1) User / Identity Context

### Aggregate Root
**User**  
**Begründung:** Hat eine eindeutige Identität (UUID) und ist die Konsistenzgrenze für Identität, Login/Privatsphäre und Einwilligung.

### Entitäten (innerhalb des Aggregates)
Keine separaten Entitäten im Aggregate (bewusst schlank gehalten).

### Value Objects
- **ConsentStatus**  
  **Begründung:** Reiner Wertzustand (Einwilligung + Version + Datum), unveränderlich gedacht, keine eigene Identität.

### Domain Services
- **AuthService** – Registrierung/Authentifizierung
- **PrivacyService** – Löschung/Anonymisierung

### Aggregate-Details: User
**ID:** `userId: UUID`

**Attribute:**
- `name: String`
- `email: String` (fachlich: eindeutig)
- `createdAt: DateTime`
- `totalPoints: int` (fachlich: nicht negativ)

**Referenzierte Aggregate:** keine direkten (andere Kontexte referenzieren User via `userId`)

### Geschäftsregeln (Invarianten)
- E-Mail ist **eindeutig** (Unique).
- `totalPoints >= 0`.
- `ConsentStatus` muss für datenschutzrelevante Features vorhanden/valid sein (z. B. Analytics-Opt-in).

### Methoden (Aggregate-Aktionen)
- `register(name, email)` – legt User an, validiert E-Mail-Format/Uniqueness.
- `addPoints(amount)` – erhöht `totalPoints`, nur `amount > 0`.
- `anonymize()` – entfernt/anonymisiert personenbezogene Daten (z. B. Name/Email), behält technische Referenzen minimal.
- `ConsentStatus.isValidForVersion(version)` – prüft Einwilligungs-Version.

### Domain Events
**user.registered**  
Payload:
- `userId`, `email`, `name`, `registeredAt`  
Publisher: `User` (bei `register()`)  
Subscriber:
- Notification (Welcome/Verification)
- Analytics (Tracking neuer Nutzer)

**user.anonymized**  
Payload:
- `userId`, `anonymizedAt`  
Publisher: `User` (bei `anonymize()`)  
Subscriber:
- Analytics (PII entfernen / re-aggregieren)
- Notification (offene Benachrichtigungen stoppen)

---

## 2) Action / Observation Context

### Aggregate Root
**Action**  
**Begründung:** Action ist die Konsistenzgrenze für eine konkrete Begrünungsaktion inkl. Validierung und Punktezuweisung.

### Value Objects
- **PlantVO** – kompakte Pflanzeninfo in der Action (stellt keine Plant-Entität dar)
- **LocationVO** – kompakte Standortinfo in der Action

### Domain Services
- **GeoVerificationService** – Standort plausibilisieren/verifizieren
- **PlantPlausibilityService** – Pflanze-zu-Standort-Fachprüfung
- **PointsEngine** – berechnet Punkte

### Aggregate-Details: Action
**ID:** `actionId: UUID`

**Attribute:**
- `userId: UUID` (Referenz auf User über ID)
- `photoUrl: String` (optional/extern)
- `status: String` (z. B. `DRAFT | VALIDATED | REJECTED | SYNCED`)
- `createdAt: DateTime`

**Enthaltene Value Objects:**
- `plant: PlantVO`
- `location: LocationVO`

**Referenzierte Aggregate:**
- User: via `userId`
- Plant: indirekt via `PlantVO.plantId`
- Location/District: indirekt via `LocationVO`

### Geschäftsregeln (Invarianten)
- Eine Action muss **genau eine** PlantVO und LocationVO besitzen.
- `validate()` darf nur ausgeführt werden, wenn `status` nicht final ist.
- `assignPoints(points)` nur, wenn Action valide ist und `points > 0`.
- `markSynced()` nur, wenn Action bereits valide/übertragbar ist.

### Methoden (Aggregate-Aktionen)
- `registerAction()` – erstellt/initialisiert Action (setzt Status, Zeitstempel).
- `validate()` – ruft GeoVerification + Plausibility auf, setzt Status.
- `assignPoints(points)` – speichert Punkte-Ergebnis (oder löst Event aus).
- `markSynced()` – setzt Status `SYNCED`.

### Domain Events
**action.registered**  
Payload:
- `actionId`, `userId`, `createdAt`  
Publisher: `Action`  
Subscriber:
- Sync (Queue/Offline-Anteile)
- Analytics (Zählung Aktionen)

**action.validated**  
Payload:
- `actionId`, `userId`, `validatedAt`  
Publisher: `Action` (bei `validate()`)  
Subscriber:
- Gamification (Punkte berechnen/verbuchen)
- Challenge (Fortschritt updaten)

**points.calculated** *(optional, falls PointsEngine event-basiert arbeitet)*  
Payload:
- `actionId`, `userId`, `points`  
Publisher: `PointsEngine`  
Subscriber:
- Gamification (Points draufrechnen)

---

## 3) Plant Catalog Context

### Aggregate Root
**Plant**  
**Begründung:** Plant ist die Konsistenzgrenze für Katalog-Infos (Pflege, Kategorie, Anforderungen).

### Domain Services
- **PlantInfoService** – Suche/Lookup und Kriterienfilter

### Aggregate-Details: Plant
**ID:** `plantId: UUID`

**Attribute:**
- `latinName: String`
- `commonName: String`
- `category: String`
- `sunExposure: String`
- `waterNeed: String`

### Geschäftsregeln (Invarianten)
- `latinName` (optional fachlich) eindeutig.
- `sunExposure/waterNeed` nur aus erlaubten Kategorien.

### Methoden
- `PlantInfoService.lookup(plantId)`
- `PlantInfoService.searchByCriteria(sun, water, category)`

### Domain Events
**plant.catalogUpdated** *(falls der Katalog gepflegt/aktualisiert wird)*  
Payload:
- `plantId`, `changedFields`, `updatedAt`  
Publisher: Plant Catalog  
Subscriber:
- Action (Plausibility-Regeln/Cache aktualisieren)

---

## 4) Location / District Context

### Aggregate Root
**District**  
**Begründung:** District bildet die Konsistenzgrenze für Bezirksdaten und aggregierte Statistik.

### Entitäten
- **Location** (Entity; wird einem District zugeordnet)

### Value Objects
- **DistrictStats** – aggregierte Kennzahlen

### Aggregate-Details: District
**ID:** `districtId: UUID`

**Attribute:**
- `name: String`
- `population: int`

**Enthaltene VOs:**
- `DistrictStats { totalActions, totalPoints, activeUsers }`

### Entity-Details: Location
**ID:** `locationId: UUID`

**Attribute:**
- `address: String`
- `latitude: double`
- `longitude: double`

### Geschäftsregeln (Invarianten)
- Location muss einem District zuordenbar sein (Geo/Mapping-Regel).
- DistrictStats Werte sind niemals negativ.

### Methoden
- `Location.register(address, lat, lng)`
- `District.getStats()`

### Domain Events
**location.assignedToDistrict**  
Payload:
- `locationId`, `districtId`  
Publisher: Location/District  
Subscriber:
- Analytics (Bezirks-Auswertung)
- Challenge (Fortschritt pro Bezirk)

---

## 5) Gamification Context

### Konsistenzgrenze (fachlich)
Gamification arbeitet mit `Points` pro User und Badge-Regeln. Konsistenzgrenze ist typischerweise **Points je User**.

### Entitäten
- **Points** (pro User)
- **Badge**
- **Leaderboard** (ReadModel)

### Domain Services
- **BadgeAssignmentService**

### Entity-Details: Points
**ID (fachlich):** `userId: UUID`

**Attribute:**
- `total: int`
- `lastUpdated: DateTime`

**Invarianten:**
- `total >= 0`

**Methoden:**
- `add(amount)`
- `getTotal()`

### Entity-Details: Badge
**ID:** `badgeId: UUID`

**Attribute:**
- `name: String`
- `description: String`
- `threshold: int`

**Invarianten:**
- `threshold > 0`

**Methoden:**
- `isAchieved(totalPoints)`

### ReadModel: Leaderboard
**Attribute:**
- `scope: String`
- `entries: List`

**Methoden:**
- `updateFor(userId, points)`
- `getTop(limit)`

### Domain Events
**points.awarded**  
Payload:
- `userId`, `actionId`, `points`, `awardedAt`  
Publisher: Gamification (nach ActionValidated)  
Subscriber:
- User/Identity (optional: `User.addPoints`)
- Analytics (KPIs)
- BadgeAssignmentService (Badges prüfen)

**badge.assigned**  
Payload:
- `userId`, `badgeId`, `assignedAt`  
Publisher: BadgeAssignmentService  
Subscriber:
- Notification (Glückwunsch Push)
- Analytics (Badge KPIs)

---

## 6) Challenge Context

### Aggregate Root
**Challenge**  
**Begründung:** Challenge ist Konsistenzgrenze für Zeitraum, Scope und Abschluss/Ergebnis.

### Domain Services
- **ChallengeEvaluationService**

### Aggregate-Details: Challenge
**ID:** `challengeId: UUID`

**Attribute:**
- `name: String`
- `scope: String` (z. B. City/District)
- `startDate: Date`
- `endDate: Date`

**Invarianten:**
- `startDate < endDate`
- `scope` muss gültig sein.

### Methoden
- `schedule(start, end, scope)`
- `ChallengeEvaluationService.evaluateProgress(challengeId)`
- `ChallengeEvaluationService.finishChallenge(challengeId)`

### Domain Events
**challenge.progressUpdated**  
Payload:
- `challengeId`, `scope`, `progressSnapshot`, `updatedAt`  
Publisher: ChallengeEvaluationService  
Subscriber:
- Analytics (Dashboard)

**challenge.completed**  
Payload:
- `challengeId`, `winner(s)`, `completedAt`  
Publisher: ChallengeEvaluationService  
Subscriber:
- Notification (Ergebnis)
- Gamification (Bonus-Badges/Points optional)

---

## 7) Notification / Reminder Context

### Value Objects
- **NotificationSetting** (pro User)

### Domain Services
- **NotificationService**
- **SchedulerService**

### VO-Details: NotificationSetting
**Attribute:**
- `userId: UUID`
- `channels: String`
- `frequency: String`
- `quietHours: String`

**Methoden:**
- `updatePreferences(channels, frequency, quietHours)`

**Invarianten:**
- gültige Kanäle (z. B. PUSH/EMAIL)
- `quietHours` Format valide

### Domain Events
**notification.scheduled**  
Payload:
- `userId`, `trigger`, `scheduledAt`  
Publisher: SchedulerService  
Subscriber:
- NotificationService (tatsächliches Senden)

**notification.sent**  
Payload:
- `userId`, `type`, `sentAt`  
Publisher: NotificationService  
Subscriber:
- Analytics (Delivery Rate)

---

## 8) Admin / Analytics Context

### Entitäten
- **Report**

### Value Objects / DTOs
- **DistrictComparison** (Vergleichsdaten mehrerer Bezirke)

### Domain Services
- **AnalyticsService**

### Entity-Details: Report
**ID:** `reportId: UUID`

**Beispiel-Attribute:**
- `districtId: UUID`
- `periodStart: Date`
- `periodEnd: Date`
- `generatedAt: DateTime`
- `kpis: Json/Map`

### Invarianten
- Zeitraum valide (`periodStart <= periodEnd`)
- Reports enthalten **keine** personenbezogenen Rohdaten (Privacy by Design)

### Methoden
- `AnalyticsService.generateDistrictReport(districtId, period)`
- `AnalyticsService.exportCSV(filter)`
- `AnalyticsService.compareDistricts(criteria)`
- `Report.renderAsPdf()`

### Domain Events
**report.generated**  
Payload:
- `reportId`, `districtId`, `period`, `generatedAt`  
Publisher: AnalyticsService  
Subscriber:
- Notification (Admin-Info optional)

---

## 9) Sync / Offline Context

### Value Objects
- **OfflinePayload**

### Domain Services
- **SyncService**

### VO-Details: OfflinePayload
**ID:** `payloadId: UUID`

**Attribute:**
- `userId: UUID`
- `actionJson: String`
- `createdAt: DateTime`

**Methoden:**
- `toAction(): Action`

**Invarianten:**
- `actionJson` ist vollständig genug, um eine Action rekonstruieren zu können.
- Payload wird idempotent verarbeitet (darf nicht doppelt „committed“ werden).

### Domain Events
**offlinePayload.enqueued**  
Payload:
- `payloadId`, `userId`, `enqueuedAt`  
Publisher: SyncService  
Subscriber:
- (intern) Queue/Worker

**sync.completed**  
Payload:
- `payloadId`, `actionId`, `syncedAt`  
Publisher: SyncService  
Subscriber:
- Action (Status `SYNCED`)
- Analytics (Offline Anteil)

**sync.conflictDetected**  
Payload:
- `payloadId`, `serverActionId`, `clientActionId`, `conflictType`  
Publisher: SyncService  
Subscriber:
- Notification (Konflikt melden)
- Action (Review Needed)

---

# Kommunikation über Domaingrenzen (Kurz)
- **Action → Gamification:** `action.validated` → `points.awarded`
- **Gamification → Notification:** `badge.assigned`
- **Action/Location → Challenge:** Fortschritt pro Bezirk (event-basiert)
- **(alle) → Analytics:** KPIs/Reports über Events, keine direkten DB-Reads über Bounded Contexts
- **Sync → Action:** Synchronisation/Statusänderung über klaren Prozess (idempotent)
- **User/Privacy → Analytics/Notification:** Anonymisierung/Opt-out propagieren
