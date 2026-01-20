# Domain-Modell - Digitale Schulbibliothek

**Version:** 2.0  
**Erstellt:** 2025-01-27  
**Autor:** GitHub Copilot (Claude Sonnet 4.5)  
**Status:** ✅ Complete  
**Letzte Änderung:** 2025-01-27

---

## 1. Übersicht

### 1.1 Projektkontext

Das Projekt "Digitale Schulbibliothek" ist eine Webanwendung zur Verwaltung einer Schulbibliothek mit Katalogsuche, Ausleihe, Reservierung, Klassensätzen und Mahnwesen. Die Anwendung integriert sich mit einem bestehenden SSO-System der Schule.

**Kernziele:**
- Selbstbedienung für Schüler:innen (Katalog durchsuchen, reservieren)
- Effiziente Administration für Bibliothekar:innen (Ausleihe, Rückgabe, Mahnwesen)
- Digitale Klassensatzverwaltung für Lehrer:innen
- Benachrichtigungen und Erinnerungen für alle Nutzer

### 1.2 Domänen-Kategorisierung

Das Projekt umfasst folgende DDD-Subdomänen:

| Subdomäne | Typ | Bounded Context | Begründung |
|-----------|-----|-----------------|------------|
| Ausleihe & Reservierung | **Core Domain** | Lending Context | Differenzierung, Wettbewerbsvorteil, geschäftskritisch |
| Katalogverwaltung | Supporting Subdomain | Catalog Context | Notwendig, aber Standard-Funktionalität |
| Nutzerverwaltung | Generic Subdomain | User Context | SSO-Integration, keine fachliche Differenzierung |
| Benachrichtigungen | Supporting Subdomain | Notification Context | Standardisiert, austauschbar |
| Mahnwesen | Supporting Subdomain | Reminding Context | Batch-Job, konfigurierbar |

### 1.3 Bounded Contexts Map

```
┌─────────────────────────────────────────────────────────────────────┐
│                         LENDING CONTEXT (Core)                       │
│  Aggregates: Loan, Reservation, PreReservation, ClassSet            │
│  Komplexe Geschäftsregeln, CQRS-ready, Event-Driven                 │
└────────────┬────────────────────────────────────────────┬───────────┘
             │                                             │
             │ Domain Events                               │ Domain Events
             │ (MediaCheckedOut,                           │ (LoanOverdue,
             │  MediaReturned,                             │  ReminderTriggered)
             │  MediaReserved)                             │
             │                                             │
             ▼                                             ▼
┌────────────────────────┐                    ┌────────────────────────┐
│   CATALOG CONTEXT      │                    │  NOTIFICATION CONTEXT  │
│  Aggregates: Media,    │                    │  Aggregate: Notification│
│  MediaCopy             │                    │  Event Subscriber       │
│  Event Subscriber      │                    │  Multi-Channel Delivery │
└────────────────────────┘                    └────────────────────────┘
             ▲                                             ▲
             │                                             │
             │ Queries (Media Metadata)                    │ SSO User Data
             │                                             │
┌────────────┴───────────┐                    ┌────────────┴───────────┐
│    USER CONTEXT        │                    │   REMINDING CONTEXT    │
│  Aggregate: User       │                    │  Aggregate:            │
│  SSO Integration (ACL) │                    │  ReminderCampaign      │
│  BorrowingLimit Rules  │                    │  Scheduled Job (08:00) │
└────────────────────────┘                    └────────────────────────┘
```

**Context-Beziehungen:**
- **Lending → Catalog**: Queries für Media-Metadaten (ISBN, Titel, Autor)
- **Lending → Notification**: Domain Events für Benachrichtigungen
- **Lending → Reminding**: Overdue Loans werden täglich evaluiert
- **User → Lending**: User-Referenz über `userId` (lose Kopplung)
- **Notification/Reminding → User**: User-Daten für Benachrichtigungen

---

## 2. Bounded Context: LENDING CONTEXT (Core Domain)

### 2.1 Ubiquitous Language

Die wichtigsten Begriffe im Lending Context:

| Begriff | Definition | Rolle |
|---------|------------|-------|
| **Loan** | Eine aktive Ausleihe zwischen User und MediaCopy | Aggregate Root |
| **DueDate** | Fälligkeitsdatum der Ausleihe | Value Object |
| **Reservation** | Eine bestätigte Reservierung für verfügbare Medien | Aggregate Root |
| **PreReservation** | Vormerkung in Warteliste bei ausgeliehenen Medien | Aggregate Root |
| **ClassSet** | Gebündelte Ausleihe mehrerer Kopien an Lehrer | Aggregate Root |
| **Fine** | Mahngebühr für überfällige/beschädigte Medien | Aggregate Root |
| **LoanPolicy** | Ausleihregeln (Dauer, max. Anzahl) | Value Object |
| **ReservationPolicy** | Reservierungsregeln (Abholzeit, max. Anzahl) | Value Object |
| **RenewalPolicy** | Verlängerungsregeln (max. Anzahl, Bedingungen) | Value Object |
| **FinePolicy** | Regeln für Mahngebühren (Betrag pro Tag) | Value Object |
| **CheckOut** | Vorgang der Ausleihe (Verb) | LoanCheckoutService.checkout() |
| **Return** | Rückgabe einer Ausleihe (Verb) | LoanReturnService.returnLoan() |
| **Renew** | Verlängern einer Ausleihe (Verb) | LoanRenewalService.renew() |
| **Reserve** | Reservieren einer verfügbaren Kopie (Verb) | ReservationWaitlistService.reserveOrQueue() |
| **PreReserve** | Vormerkung bei ausgeliehener Kopie (Verb) | ReservationWaitlistService.reserveOrQueue() |

**Geschäftsregeln:**
1. **User eligibility**: User muss aktiv sein (UserStatus.Active) und keine überfälligen Loans haben
2. **BorrowingLimit enforcement**: Student max. 5, Teacher max. 10, Librarian 999
3. **DueDate calculation**: LoanPolicy.loanDurationDays ab Ausleihdatum
4. **Renewal constraints**: Max. RenewalPolicy.maxRenewals, keine aktive Reservation
5. **FIFO waitlist**: PreReservations nach CreatedAt sortiert, älteste zuerst
6. **ClassSet teacher-only**: Nur UserGroup.Teacher kann ClassSets ausleihen

### 2.2 Aggregate: Loan

**Verantwortung:** Verwaltet die Ausleihe eines Mediums an einen User mit Fälligkeitsdatum und Verlängerungslogik.

**Attribute:**
- `loanId: UUID` (Identity)
- `userId: UUID` (Referenz auf User Context)
- `mediaCopyId: UUID` (Referenz auf MediaCopy)
- `checkedOutAt: Instant`
- `dueDate: DueDate` (Value Object)
- `returnedAt: Instant?`
- `renewalCount: Integer`
- `loanStatus: LoanStatus` (Active | Returned | Overdue)

**Methoden:**
- `checkout(userId, mediaCopyId, policy)`: Factory Method
- `renew(policy)`: Verlängert DueDate, erhöht renewalCount
- `markReturned()`: Setzt returnedAt, ändert Status
- `isOverdue()`: Boolean Query
- `canRenew(policy)`: Boolean Query (max renewals, keine Reservation)
- `getDaysUntilDue()`: Integer Query

**Invarianten:**
- DueDate muss in der Zukunft liegen
- renewalCount ≤ RenewalPolicy.maxRenewals
- Returned Loan kann nicht verlängert werden

**Domain Events:**
- `MediaCheckedOut(loanId, userId, mediaCopyId, dueDate)`
- `LoanRenewed(loanId, newDueDate, renewalCount)`
- `MediaReturned(loanId, mediaCopyId, returnedAt)`

### 2.3 Aggregate: Reservation

**Verantwortung:** Verwaltet eine bestätigte Reservierung für eine verfügbare MediaCopy mit Abholzeit.

**Attribute:**
- `reservationId: UUID`
- `userId: UUID`
- `mediaCopyId: UUID`
- `reservedAt: Instant`
- `expiresAt: Instant` (ReservationPolicy.pickupHours)
- `status: ReservationStatus` (Pending | Collected | Expired | Cancelled)

**Methoden:**
- `create(userId, mediaCopyId, policy)`: Factory Method
- `collect()`: User holt Reservierung ab
- `expire()`: Nach Ablauf der Abholzeit
- `cancel()`: User storniert
- `isExpired()`: Boolean Query

**Invarianten:**
- expiresAt = reservedAt + ReservationPolicy.pickupHours
- Collected/Expired/Cancelled Reservations können nicht mehr geändert werden

**Domain Events:**
- `MediaReserved(reservationId, userId, mediaCopyId, expiresAt)`
- `ReservationCollected(reservationId, loanId)`
- `ReservationExpired(reservationId, mediaCopyId)` → MediaCopy wieder verfügbar

### 2.4 Aggregate: PreReservation

**Verantwortung:** Verwaltet eine Vormerkung in der Warteliste für ausgeliehene Medien.

**Attribute:**
- `preReservationId: UUID`
- `userId: UUID`
- `mediaId: UUID` (nicht mediaCopyId!)
- `createdAt: Instant`
- `position: Integer` (FIFO-Position)
- `status: PreReservationStatus` (Queued | Promoted | Expired | Cancelled)

**Methoden:**
- `create(userId, mediaId)`: Factory Method
- `promote()`: Wird zu Reservation bei Rückgabe
- `expire()`: Nach 24h keine Abholung
- `cancel()`: User storniert
- `updatePosition(newPosition)`: FIFO-Queue neu sortiert

**Invarianten:**
- position ≥ 1
- Promoted/Expired/Cancelled PreReservations können nicht mehr geändert werden

**Domain Events:**
- `PreReservationCreated(preReservationId, userId, mediaId, position)`
- `PreReservationPromoted(preReservationId, reservationId)`
- `PreReservationCancelled(preReservationId, mediaId)` → Position nachrutschen

### 2.5 Aggregate: ClassSet

**Verantwortung:** Verwaltet die gebündelte Ausleihe mehrerer MediaCopies an einen Lehrer.

**Attribute:**
- `classSetId: UUID`
- `teacherId: UUID` (nur UserGroup.Teacher)
- `subject: String` (z.B. "Deutsch 8a")
- `checkedOutAt: Instant`
- `dueDate: DueDate`
- `members: List<SetMember>` (Entity)
- `returnedAt: Instant?`
- `status: ClassSetStatus` (Active | PartiallyReturned | Returned)

**Entity: SetMember**
- `mediaCopyId: UUID`
- `returnedAt: Instant?`
- `condition: MediaCondition` (bei Rückgabe)

**Methoden:**
- `create(teacherId, mediaCopyIds, subject, policy)`: Factory Method
- `returnCopy(mediaCopyId, condition)`: Einzelne Kopie zurückgeben
- `returnAll(conditions)`: Alle Kopien zurückgeben
- `getAllCopies()`: List<UUID>
- `getUnreturnedCopies()`: List<UUID>

**Invarianten:**
- teacherId muss UserGroup.Teacher haben
- members.size() ≥ 1
- Alle members müssen returned sein für status=Returned

**Domain Events:**
- `ClassSetCheckedOut(classSetId, teacherId, mediaCopyIds, dueDate)`
- `ClassSetReturned(classSetId, returnedCopies)`

### 2.6 Aggregate: Fine

**Verantwortung:** Verwaltet Mahngebühren für überfällige oder beschädigte Medien.

**Attribute:**
- `fineId: UUID`
- `userId: UUID`
- `loanId: UUID`
- `amount: Money` (Value Object)
- `reason: FineReason` (Overdue | Damaged | Lost)
- `status: FineStatus` (Open | Paid | Waived)
- `createdAt: Instant`
- `paidAt: Instant?`

**Methoden:**
- `create(userId, loanId, amount, reason)`: Factory Method
- `pay()`: Markiert als bezahlt
- `waive(reason)`: Erlässt die Gebühr (z.B. Kulanz)
- `isPaid()`: Boolean Query

**Invarianten:**
- amount muss positiv sein
- Paid/Waived Fines können nicht mehr geändert werden

**Domain Events:**
- `FineCreated(fineId, userId, amount, reason)`
- `FinePaid(fineId, paidAt)`
- `FineWaived(fineId, reason)`

### 2.7 Domain Services

#### LoanCheckoutService
**Verantwortung:** Orchestriert die Ausleihe mit Validierung und Policy-Durchsetzung.

**Methoden:**
- `checkout(userId, mediaCopyId, policy): Loan`
  - Validiert User eligibility (aktiv, keine Overdues)
  - Prüft BorrowingLimit
  - Erstellt Loan
  - Publiziert MediaCheckedOut Event
  - Aktualisiert MediaCopy.availabilityStatus

#### LoanReturnService
**Verantwortung:** Orchestriert die Rückgabe mit Promotion von PreReservations.

**Methoden:**
- `returnLoan(loanId, condition): void`
  - Markiert Loan als returned
  - Publiziert MediaReturned Event
  - Aktualisiert MediaCopy.condition
  - Triggert ReservationWaitlistService.promoteOnReturn()

#### LoanRenewalService
**Verantwortung:** Orchestriert die Verlängerung mit Policy-Prüfung.

**Methoden:**
- `renew(loanId, policy): Loan`
  - Prüft RenewalPolicy (maxRenewals)
  - Prüft keine aktive Reservation vorhanden
  - Berechnet neue DueDate
  - Publiziert LoanRenewed Event

#### ReservationWaitlistService
**Verantwortung:** Orchestriert Reservierungen und Warteliste (FIFO-Logik).

**Methoden:**
- `reserveOrQueue(userId, mediaId, policy): Reservation | PreReservation`
  - Wenn MediaCopy verfügbar → Reservation.create()
  - Wenn alle Kopien ausgeliehen → PreReservation.create()
- `promoteOnReturn(mediaId): void`
  - Holt älteste PreReservation (FIFO)
  - Promoted zu Reservation
- `cancel(reservationId | preReservationId): void`
- `collect(reservationId): Loan`
  - Ändert Reservation zu Collected
  - Triggert LoanCheckoutService
- `expirePending(): void` (Batch-Job)

#### ClassSetOrchestrationService
**Verantwortung:** Orchestriert ClassSet-Ausleihe und Rückgabe.

**Methoden:**
- `checkoutClassSet(teacherId, mediaCopyIds, subject, policy): ClassSet`
  - Validiert teacherId hat UserGroup.Teacher
  - Prüft alle MediaCopies verfügbar
  - Erstellt ClassSet
  - Publiziert ClassSetCheckedOut Event
- `returnClassSet(classSetId, conditions): void`
- `returnPartial(classSetId, mediaCopyId, condition): void`

### 2.8 Application Services

#### UserDashboardService
**Verantwortung:** Orchestriert die Use Cases für das Nutzer-Dashboard (US-013).

**Methoden:**
- `getDashboardData(userId): UserDashboardDTO`
  - Aggregiert aktive Loans, Reservations, Fines und History
- `renewLoan(userId, loanId): void`
  - Lädt Loan, prüft User-Berechtigung
  - Ruft Loan.renew() auf
  - Speichert Änderungen
- `cancelReservation(userId, reservationId): void`
  - Lädt Reservation, prüft User-Berechtigung
  - Ruft Reservation.cancel() auf

### 2.9 PlantUML-Diagramm


[Lending Context Domain Model](domain-models/lending-context.domain-model.puml)


**Visualisierung:** Das Diagramm zeigt die 5 Aggregates (Loan, Reservation, PreReservation, ClassSet, Fine) mit ihren Value Objects, Domain Services und Repositories. Beziehungen sind als lose Kopplung (ID-Referenzen) modelliert.

---

## 3. Bounded Context: CATALOG CONTEXT (Supporting Subdomain)

### 3.1 Ubiquitous Language

| Begriff | Definition | Rolle |
|---------|------------|-------|
| **Media** | Ein Medienobjekt (Buch, DVD) mit Metadaten | Aggregate Root |
| **MediaCopy** | Eine physische Kopie eines Mediums mit Barcode | Aggregate Root |
| **Barcode** | Eindeutige Identifikation der physischen Kopie | Value Object |
| **ISBN** | International Standard Book Number | Value Object |
| **AvailabilityStatus** | Verfügbarkeitszustand (Available, OnLoan, Reserved, Lost) | Value Object |
| **MediaCondition** | Zustand der Kopie (Good, Damaged, Lost) | Value Object |
| **ShelfLocation** | Regalstandort (z.B. "A3-15") | Value Object |
| **Acquire** | Neue Medien beschaffen (Verb) | CatalogInventoryService.acquireMedia() |
| **MarkDamaged** | Kopie als beschädigt markieren (Verb) | MediaCopy.markDamaged() |

**Geschäftsregeln:**
1. **ISBN uniqueness**: Pro ISBN nur ein Media-Eintrag
2. **Barcode uniqueness**: Pro Barcode nur eine MediaCopy
3. **Availability calculation**: MediaCopy.availabilityStatus wird durch Lending Context Events aktualisiert
4. **Condition validation**: Damaged MediaCopies können nicht ausgeliehen werden

### 3.2 Aggregate: Media

**Verantwortung:** Verwaltet Metadaten eines Mediums (Buch, DVD, etc.).

**Attribute:**
- `mediaId: UUID`
- `isbn: ISBN` (Value Object, UNIQUE)
- `title: String`
- `author: String`
- `publisher: String`
- `category: MediaCategory` (Fiction, NonFiction, Reference)
- `metadata: MediaMetadata` (Value Object mit publicationYear, language, etc.)

**Methoden:**
- `create(isbn, title, author, publisher, category)`: Factory Method
- `updateMetadata(metadata)`: void
- `getTotalCopies()`: Integer (Query über Repository)
- `getAvailableCopies()`: Integer (Query)

**Invarianten:**
- isbn muss gültig sein (10 oder 13 Ziffern)
- title darf nicht leer sein

### 3.3 Aggregate: MediaCopy

**Verantwortung:** Verwaltet eine physische Kopie eines Mediums mit Verfügbarkeit und Zustand.

**Attribute:**
- `mediaCopyId: UUID`
- `mediaId: UUID` (Referenz auf Media)
- `barcode: Barcode` (Value Object, UNIQUE)
- `availabilityStatus: AvailabilityStatus`
- `condition: MediaCondition`
- `shelfLocation: ShelfLocation`
- `acquiredAt: LocalDate`
- `lastModifiedAt: Instant`

**Methoden:**
- `create(mediaId, barcode, shelfLocation)`: Factory Method
- `markAsOnLoan()`: void (Event Handler)
- `markAsReturned(condition)`: void (Event Handler)
- `markAsReserved()`: void
- `markAsAvailable()`: void
- `markDamaged(condition)`: void
- `markLost()`: void
- `updateShelfLocation(newLocation)`: void
- `isAvailableForLoan()`: Boolean

**Invarianten:**
- Barcode muss eindeutig sein
- Damaged/Lost MediaCopies haben availabilityStatus != Available

**Event Handlers:**
- Bei `MediaCheckedOut`: markAsOnLoan()
- Bei `MediaReturned`: markAsReturned(condition)
- Bei `MediaReserved`: markAsReserved()

### 3.4 Domain Services

#### CatalogInventoryService
**Verantwortung:** Orchestriert Medienakquise und Bestandsverwaltung.

**Methoden:**
- `acquireMedia(isbn, title, author, copyCount)`: Media + List<MediaCopy>`
  - Erstellt Media (falls ISBN neu)
  - Erstellt MediaCopies mit Barcodes
  - Publiziert MediaAcquired Event
- `addCopy(mediaId, barcode, shelfLocation)`: MediaCopy
- `withdrawMedia(mediaCopyId, reason)`: void (markLost)

#### MediaSearchService
**Verantwortung:** Queries für Katalogsuche (Read-Only).

**Methoden:**
- `search(query, filters)`: List<MediaSearchResult>`
- `findByISBN(isbn)`: Media?
- `findByBarcode(barcode)`: MediaCopy?
- `getAvailableCopies(mediaId)`: List<MediaCopy>`

#### CatalogEventHandler
**Verantwortung:** Reagiert auf Events aus Lending Context, um Availability zu aktualisieren.

**Subscribed Events:**
- `MediaCheckedOut` → MediaCopy.markAsOnLoan()
- `MediaReturned` → MediaCopy.markAsReturned()
- `MediaReserved` → MediaCopy.markAsReserved()

### 3.5 PlantUML-Diagramm

![Catalog Context Domain Model](domain-models/catalog-context.domain-model.puml)

---

## 4. Bounded Context: USER CONTEXT (Generic Subdomain)

### 4.1 Ubiquitous Language

| Begriff | Definition | Rolle |
|---------|------------|-------|
| **User** | Ein Benutzer der Bibliothek (Schüler, Lehrer, Bibliothekar) | Aggregate Root |
| **SchoolIdentity** | Schulweite Identität aus SSO (IMMUTABLE) | Value Object |
| **UserGroup** | Rollenbasierte Gruppe (Student, Teacher, Librarian) | Value Object |
| **BorrowingLimit** | Max. Anzahl gleichzeitiger Ausleihen | Value Object |
| **Authenticate** | SSO-Login (Verb) | SSOUserProvisioningService.authenticateUser() |
| **Provision** | User-Daten aus SSO synchronisieren (Verb) | SSOUserProvisioningService.provisionUser() |

**Geschäftsregeln:**
1. **SSO as source of truth**: SchoolIdentity und UserGroup werden NICHT lokal editiert
2. **BorrowingLimit by group**: Student=5, Teacher=10, Librarian=999
3. **Anti-Corruption Layer**: SSOAdapter schützt Domain vor externen SSO-Änderungen

### 4.2 Aggregate: User

**Verantwortung:** Repräsentiert einen Bibliotheksnutzer mit SSO-Identität und Ausleihregeln.

**Attribute:**
- `userId: UUID`
- `schoolIdentity: SchoolIdentity` (IMMUTABLE, von SSO)
- `userGroup: UserGroup` (SSO-controlled)
- `borrowingLimit: BorrowingLimit` (abgeleitet von UserGroup)
- `userStatus: UserStatus` (Active, Suspended, Graduated)
- `profile: UserProfile` (Name, Email, etc.)
- `contactInfo: ContactInfo` (Phone, Address)

**Methoden:**
- `{static} createFromSSO(ssoData)`: User (Factory Method)
- `syncWithSSO(ssoData)`: void (Update bei SSO-Änderungen)
- `suspend()`: void (Admin-Aktion)
- `reactivate()`: void
- `canBorrow()`: Boolean (status=Active, keine Overdues)
- `getRemainingBorrowingCapacity(activeLoans)`: Integer

**Invarianten:**
- schoolIdentity IMMUTABLE nach Erstellung
- borrowingLimit.max entspricht UserGroup-Regel

### 4.3 Domain Services

#### SSOUserProvisioningService
**Verantwortung:** Synchronisiert User-Daten aus externem SSO (mit Anti-Corruption Layer).

**Methoden:**
- `authenticateUser(ssoToken): User`
  - Validiert SSO-Token über SSOAdapter
  - Erstellt/aktualisiert User
  - Publiziert UserAuthenticated Event
- `provisionUser(ssoUserId): User`
- `syncUserGroup(userId, ssoData): void`

**Anti-Corruption Layer (SSOAdapter):**
- Konvertiert externe SSO-Datenmodelle in Domain-Modelle
- Schützt vor Breaking Changes im SSO

### 4.4 PlantUML-Diagramm

![User Context Domain Model](domain-models/user-context.domain-model.puml)

---

## 5. Bounded Context: NOTIFICATION CONTEXT (Supporting Subdomain)

### 5.1 Ubiquitous Language

| Begriff | Definition | Rolle |
|---------|------------|-------|
| **Notification** | Eine Benachrichtigung an einen User | Aggregate Root |
| **NotificationChannel** | Versandkanal (Email, Push) | Value Object |
| **NotificationType** | Art der Nachricht (LoanDue, Reserved, Overdue, etc.) | Value Object |
| **Retry** | Zustellversuch wiederholen (Verb) | Notification.retry() |
| **Compose** | Nachricht aus Event erstellen (Verb) | NotificationComposerService.composeFromEvent() |

**Geschäftsregeln:**
1. **Deduplication**: Pro (eventId, userId, type) nur 1 Notification
2. **Retry logic**: Max. 3 Retries mit exponential backoff
3. **Fallback**: Email immer verfügbar, Push optional

### 5.2 Aggregate: Notification

**Verantwortung:** Verwaltet eine Benachrichtigung mit Versandstatus und Retry-Logik.

**Attribute:**
- `notificationId: UUID`
- `userId: UUID`
- `eventId: UUID` (Referenz auf auslösendes Event)
- `type: NotificationType`
- `channel: NotificationChannel`
- `subject: String`
- `body: String`
- `status: NotificationStatus` (Pending, Sent, Failed)
- `retryCount: Integer`
- `maxRetries: Integer` (default: 3)
- `createdAt: Instant`
- `sentAt: Instant?`
- `failedAt: Instant?`

**Methoden:**
- `create(userId, eventId, type, channel, subject, body)`: Factory Method
- `markSent()`: void
- `retry()`: void (increment retryCount)
- `markFailed()`: void
- `canRetry()`: Boolean (retryCount < maxRetries)

**Invarianten:**
- retryCount ≤ maxRetries
- Sent Notifications können nicht mehr retried werden

### 5.3 Domain Services

#### NotificationComposerService
**Verantwortung:** Erstellt Notifications aus Domain Events mit Templating und Deduplication.

**Methoden:**
- `composeFromEvent(event, userId): Notification`
  - Dedupliziert anhand (eventId, userId, type)
  - Rendert Subject/Body aus Template
  - Wählt Channel (Email > Push)
- `renderTemplate(type, data): (subject, body)`

#### NotificationDeliveryService
**Verantwortung:** Versendet Notifications über externe Adapter.

**Methoden:**
- `send(notificationId): void`
  - Lädt Notification
  - Ruft EmailAdapter oder PushAdapter auf
  - Markiert als Sent/Failed

#### NotificationEventHandler
**Verantwortung:** Subscribes zu Domain Events und triggert Notification-Erstellung.

**Subscribed Events:**
- `MediaCheckedOut` → "Ausleihe bestätigt"
- `LoanRenewed` → "Verlängerung bestätigt"
- `MediaReturned` → "Rückgabe bestätigt"
- `MediaReserved` → "Reservierung bestätigt"
- `ReservationExpired` → "Reservierung abgelaufen"
- `ReminderTriggered` → "Erinnerung fällig"
- `ClassSetCheckedOut` → "Klassensatz ausgeliehen"
- `ClassSetReturned` → "Klassensatz zurückgegeben"
- `PreReservationPromoted` → "Vormerkung verfügbar"

### 5.4 PlantUML-Diagramm

![Notification Context Domain Model](domain-models/notification-context.domain-model.puml)

---

## 6. Bounded Context: REMINDING CONTEXT (Supporting Subdomain)

### 6.1 Ubiquitous Language

| Begriff | Definition | Rolle |
|---------|------------|-------|
| **ReminderCampaign** | Batch-Lauf für tägliche Mahnungen | Aggregate Root |
| **ReminderPolicy** | Regeln für Mahnzeitpunkte | Value Object |
| **ReminderType** | Art der Mahnung (Upcoming, Overdue, Escalation) | Value Object |
| **Evaluate** | Loan gegen Policy prüfen (Verb) | RemindingEvaluationService.evaluateLoan() |

**Geschäftsregeln:**
1. **Daily Schedule**: Täglich 08:00 Uhr Batch-Job
2. **Reminder Timing**: T-3 (Upcoming), T+1 (Overdue), T+7 (Escalation)
3. **Campaign Uniqueness**: Nur 1 Campaign pro Tag

### 6.2 Aggregate: ReminderCampaign

**Verantwortung:** Verwaltet einen täglichen Batch-Lauf für Mahnungen mit Metriken.

**Attribute:**
- `campaignId: UUID`
- `executionDate: LocalDate`
- `executionTime: LocalTime`
- `totalLoansChecked: Integer`
- `remindersTriggered: Integer`
- `status: CampaignStatus` (Running, Completed, Failed)
- `startedAt: Instant`
- `completedAt: Instant?`
- `errorMessage: String?`

**Methoden:**
- `{static} startCampaign(executionDate, executionTime, policy)`: ReminderCampaign
- `addLoanChecked()`: void
- `addReminderTriggered()`: void
- `complete()`: void
- `fail(errorMessage)`: void
- `isRunning()`: Boolean

**Invarianten:**
- Nur 1 Campaign pro executionDate
- Status=Completed/Failed ist final

**Domain Events:**
- `ReminderTriggered(reminderId, loanId, userId, type, daysDelta)`

### 6.3 Domain Services

#### RemindingEvaluationService
**Verantwortung:** Führt täglichen Campaign-Lauf aus und evaluiert alle aktiven Loans.

**Methoden:**
- `runDailyCampaign(policy, atTime): ReminderCampaign`
  - Startet Campaign
  - Query alle aktiven Loans
  - Für jeden Loan: evaluateLoan()
  - Complete Campaign
- `evaluateLoan(loan, policy): ReminderEvaluation?`
  - Prüft gegen ReminderPolicy
  - Bei Match: Publiziert ReminderTriggered Event
- `completeCampaign(campaignId)`: void
- `failCampaign(campaignId, error)`: void

#### ReminderPolicyService
**Verantwortung:** Verwaltet konfigurierbare ReminderPolicy (Admin Web-App).

**Methoden:**
- `getActivePolicy()`: ReminderPolicy
- `updatePolicy(policy)`: void
- `calculateReminderDates(loan, policy)`: List<LocalDate>`

### 6.4 PlantUML-Diagramm

![Reminding Context Domain Model](domain-models/reminding-context.domain-model.puml)

---

## 7. Context-Beziehungen und Integration

### 7.1 Wie kommunizieren die Contexts?

**Primäres Integrationsmuster: Domain Events (Event-Driven Architecture)**

```
LENDING CONTEXT
    ↓ publiziert Domain Events
    ↓ (MediaCheckedOut, MediaReturned, LoanRenewed, etc.)
    ↓
    ├─→ CATALOG CONTEXT (Event Subscriber)
    │     → Aktualisiert MediaCopy.availabilityStatus
    │
    ├─→ NOTIFICATION CONTEXT (Event Subscriber)
    │     → Erstellt Notifications
    │
    └─→ REMINDING CONTEXT (Event Subscriber)
          → Registriert Loans für tägliche Evaluation
```

**Implementierung:**
- **Transactional Outbox Pattern**: Events werden in DB gespeichert und asynchron verarbeitet
- **Event Store**: Alle Domain Events für Audit Log
- **Message Bus**: Spring Events (in-process) oder RabbitMQ/Kafka (distributed)

### 7.2 Welche gemeinsamen IDs werden verwendet?

**Lose Kopplung über ID-Referenzen:**

| Context | Referenziert | Via ID |
|---------|--------------|--------|
| Lending → User | User | `userId: UUID` |
| Lending → Catalog | Media, MediaCopy | `mediaId: UUID`, `mediaCopyId: UUID` |
| Notification → User | User | `userId: UUID` |
| Notification → Lending | Loan, Reservation | `eventId: UUID` (Event Reference) |
| Reminding → User | User | `userId: UUID` |
| Reminding → Lending | Loan | `loanId: UUID` (via Query) |

**Keine direkten Aggregate-Referenzen!**
- Statt `loan.user` → `loan.userId`
- Statt `notification.loan` → `notification.eventId`

### 7.3 Anti-Corruption Layer notwendig?

**Ja, beim User Context (SSO-Integration):**

```
Externes SSO-System (SAML/OAuth2)
    ↓
SSOAdapter (Anti-Corruption Layer)
    ↓ konvertiert
    ↓ SSO-Datenmodell → Domain-Modell
    ↓
User Aggregate (Domain)
```

**Vorteile:**
- Schützt Domain vor Breaking Changes im externen SSO
- Konvertiert externe Rollenbezeichnungen in UserGroup-Enum
- Trennt technische SSO-Details von fachlicher Logik

---

## 8. Architektur-Empfehlungen

### 8.1 Welche Architekturstile pro Context?

| Context | Architekturstil | Begründung |
|---------|----------------|------------|
| **Lending** (Core) | **Clean Architecture / Hexagonal** | Maximale Testbarkeit, Business Logic isoliert, Ports & Adapters |
| **Catalog** (Supporting) | **Layered Architecture** | Standardisiert, CRUD-lastig, wenig komplexe Logik |
| **User** (Generic) | **ACL + Layered** | SSO-Integration gekapselt, einfache User-Verwaltung |
| **Notification** (Supporting) | **Event-Driven + Layered** | Reagiert auf Events, asynchrone Verarbeitung |
| **Reminding** (Supporting) | **Batch Processing + Layered** | Scheduled Jobs, konfigurierbar |

### 8.2 Core Domain → Clean Architecture / Hexagonal Architecture

**Lending Context:**

```
┌─────────────────────────────────────────────────┐
│           Application Layer (Use Cases)         │
│  - CheckoutLoanUseCase                          │
│  - ReturnLoanUseCase                            │
│  - ReservationUseCase                           │
└───────────────────┬─────────────────────────────┘
                    │
┌───────────────────▼─────────────────────────────┐
│           Domain Layer (Core)                   │
│  - Aggregates: Loan, Reservation, ClassSet      │
│  - Domain Services: LoanCheckoutService, etc.   │
│  - Domain Events: MediaCheckedOut, etc.         │
│  - Policies: LoanPolicy, ReservationPolicy      │
└───────────────────┬─────────────────────────────┘
                    │
┌───────────────────▼─────────────────────────────┐
│     Infrastructure Layer (Adapters)             │
│  - Repositories: JPA Implementations            │
│  - Event Publishing: Spring Events / RabbitMQ   │
│  - External APIs: SSO, Email                    │
└─────────────────────────────────────────────────┘
```

**Ports & Adapters:**
- **Inbound Ports**: REST Controllers, GraphQL Resolvers
- **Outbound Ports**: Repository Interfaces, Event Publisher
- **Adapters**: JPA Repositories, Spring Event Publisher, Email Service

### 8.3 Supporting Subdomain → Einfachere Patterns

**Catalog, Notification, Reminding:**

```
┌─────────────────────────────────────────────────┐
│           Presentation Layer (REST API)         │
└───────────────────┬─────────────────────────────┘
                    │
┌───────────────────▼─────────────────────────────┐
│           Service Layer                         │
│  - CatalogService (CRUD + Search)               │
│  - NotificationService (Compose & Send)         │
│  - ReminderService (Campaign Execution)         │
└───────────────────┬─────────────────────────────┘
                    │
┌───────────────────▼─────────────────────────────┐
│           Domain Layer (simplified)             │
│  - Entities: Media, MediaCopy, Notification     │
│  - Value Objects: Barcode, ISBN, etc.           │
└───────────────────┬─────────────────────────────┘
                    │
┌───────────────────▼─────────────────────────────┐
│     Infrastructure Layer                        │
│  - JPA Repositories, Event Listeners            │
└─────────────────────────────────────────────────┘
```

### 8.4 Generic Subdomain → Fertige Lösungen

**User Context:**
- SSO-Integration via **Spring Security SAML/OAuth2**
- User-Daten in lokaler DB cachen
- Anti-Corruption Layer mit **SSOAdapter**

---

## 9. Implementierungshinweise

### 9.1 Transaktionsgrenzen

**Jedes Aggregat = 1 Transaktion:**
- Loan.checkout() → 1 DB-Transaktion
- Reservation.create() → 1 DB-Transaktion
- ClassSet.returnCopy() → 1 DB-Transaktion

**Cross-Aggregate Konsistenz via Events:**
- `MediaCheckedOut` Event → MediaCopy.markAsOnLoan() (eventual consistency)

### 9.2 Repository-Pattern

**Pro Aggregate Root 1 Repository:**
- `LoanRepository`
- `ReservationRepository`
- `PreReservationRepository`
- `ClassSetRepository`
- `MediaRepository`
- `MediaCopyRepository`
- `UserRepository`
- `NotificationRepository`
- `ReminderCampaignRepository`

**Queries:**
- Repositories enthalten nur Queries für Aggregate Roots
- Queries auf Basis von Business Keys (z.B. `findByBarcode()`)
- Read-Only Queries für Reporting via separate Query Services (CQRS)

### 9.3 Domain Events & Event Sourcing

**Domain Events:**
- Speicherung via **Transactional Outbox** (Event Store Table)
- Publishing via **Message Broker** (RabbitMQ/Kafka) oder Spring Events
- Event Handler in anderen Contexts subscriben

**Event Store:**
- `event_id: UUID`
- `aggregate_id: UUID`
- `event_type: String`
- `payload: JSON`
- `occurred_at: Instant`
- `published: Boolean`

### 9.4 Testing-Strategie

**Unit Tests:**
- Aggregate Logic (z.B. `Loan.renew()`, `Reservation.expire()`)
- Domain Services (z.B. `LoanCheckoutService.checkout()`)
- Value Objects (z.B. `DueDate.isOverdue()`)

**Integration Tests:**
- Repository-Tests mit H2/Testcontainers
- Event Publishing & Handling
- Use Case Tests

**Acceptance Tests:**
- User Story → Cucumber/Gherkin
- End-to-End mit REST API

---

## 10. Nächste Schritte

### 10.1 Technologie-Stack-Entscheidungen

- **Backend:** Java 21 + Spring Boot 3.x
- **Persistenz:** PostgreSQL + Spring Data JPA
- **Messaging:** Spring Events (MVP) → RabbitMQ/Kafka (später)
- **Frontend:** React + TypeScript
- **Auth:** Spring Security SAML/OAuth2

### 10.2 Implementierungsreihenfolge (MVP zuerst)

**Phase 1: Core Domain (MVP)**
1. User Context (SSO-Integration)
2. Catalog Context (Media, MediaCopy)
3. Lending Context (Loan, Checkout, Return)
4. Notification Context (Email-only)

**Phase 2: Advanced Features**
5. Lending Context (Reservation, PreReservation)
6. Reminding Context (Batch-Jobs)
7. Lending Context (ClassSet)
8. Notification Context (Push)

**Phase 3: Reporting & Admin**
9. Reporting (Query Services)
10. Admin Web-App (Policy Configuration)

### 10.3 Testing-Strategie

- **TDD für Core Domain:** Loan, Reservation, ClassSet
- **Acceptance Tests:** Cucumber für User Stories
- **Integration Tests:** Spring Boot Test + Testcontainers
- **E2E Tests:** Selenium/Playwright

---

## 11. Referenzen

- **User Stories:** `/docs/requirements/user-stories/*.md`
- **Bounded Contexts Map:** `/docs/architecture/bounded-contexts-map.md`
- **Aggregates & Entities:** `/docs/architecture/aggregates-entities-valueobjects.md`
- **Ubiquitous Language:** `/docs/architecture/ubiquitous-language-glossar-complete.md`
- **PlantUML Diagrams:** `/docs/architecture/domain-models/*.puml`

---

**Autor:** GitHub Copilot (Claude Sonnet 4.5)  
**Version:** 2.0  
**Datum:** 2025-01-27
