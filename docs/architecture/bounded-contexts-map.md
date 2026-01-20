# Strategische Context Map: Digital School Library

## 🎯 Übersicht der Bounded Contexts (MVP)

Das Digital School Library System ist in **5 Bounded Contexts** organisiert, die jeweils ein spezialisiertes Domänenmodell und eine eigene Ubiquitous Language haben.

```
┌──────────────────────────────────────────────────────────────────────────────┐
│                    DIGITAL SCHOOL LIBRARY - CONTEXT MAP                      │
└──────────────────────────────────────────────────────────────────────────────┘

  ┌─────────────────┐
  │  School SSO     │  (EXTERN)
  │  (Identity)     │
  └────────┬────────┘
           │ authenticates
           │
           ▼
  ┌──────────────────────────────────────────────────────────────┐
  │                      USER CONTEXT                            │
  │              (Generic Subdomain - MVP)                       │
  │                                                               │
  │  • User (Entity)                                             │
  │  • UserProfile (Entity)                                      │
  │  • SchoolIdentity (Value Object)                             │
  │  • UserGroup (Value Object): Student, Teacher, Librarian    │
  │                                                               │
  │  Responsibility: Benutzeridentität & Nutzergruppen-Verwaltung│
  │  Integration: SSO-Authentication via Adapter                │
  └────────────────────────┬─────────────────────────────────────┘
                           │
         ┌─────────────────┼─────────────────┐
         │                 │                 │
         ▼                 ▼                 ▼
┌──────────────┐  ┌──────────────────┐  ┌─────────────────┐
│   CATALOG    │  │ LENDING CONTEXT  │  │ NOTIFICATION    │
│   CONTEXT    │  │                  │  │ CONTEXT         │
│              │  │  (CORE DOMAIN)   │  │                 │
│ (Supporting) │  │                  │  │ (Supporting)    │
│              │  │  • Loan          │  │                 │
│ • Media      │  │  • Reservation   │  │ • Notification  │
│ • Inventory  │  │  • Waitlist      │  │ • Channel       │
│ • Category   │  │  • ClassSet      │  │ • Event Listener│
│              │  │                  │  │                 │
└──────┬───────┘  │  • LoanPolicy    │  └────────┬────────┘
       │          │  • ReservPolicy  │           │
       │          │                  │           │
       │          │  Responsibility: │           │
       │          │  Lending,        │           │
       │          │  Reservation &   │           │
       │          │  Waitlist Mgmt   │           │
       │          └──────┬───────────┘           │
       │                 │                       │
       └────────┬────────┴───────────┬───────────┘
                │                    │
                │ publishes          │ publishes
                │ Domain Events      │ Domain Events
                │                    │
                ▼                    ▼
        ┌──────────────────────────────────────┐
        │   REMINDING CONTEXT                  │
        │                                      │
        │   (Supporting Subdomain)             │
        │                                      │
        │   • ReminderPolicy                   │
        │   • ReminderCampaign                 │
        │   • Reminder (Value Object)          │
        │                                      │
        │   Responsibility:                    │
        │   Auto-Reminder für Überfälligkeiten │
        │   und Rückgabefälligkeit             │
        └──────────────────────────────────────┘
```

---

## 📍 Bounded Contexts - Details

### 1. **USER CONTEXT** (Generic Subdomain)

**Kategorie:** Generic Subdomain
**MVP-Status:** Ja
**Kritikalität:** Mittel (Standard-Identitätsverwaltung)

#### Ubiquitous Language (Kernel-Begriffe):

| Begriff | Definition | Beispiel |
|---------|-----------|---------|
| **User** | Eine eindeutig identifizierte Person im System | Max Mustermann |
| **UserProfile** | Die Sammlung aller Metadaten eines Nutzers | Name, E-Mail, Nutzergruppe |
| **SchoolIdentity** | Die eindeutige Schulidentität vom SSO | `max.mustermann@schulbib.de` |
| **UserGroup** | Die Rolle/Kategorie des Nutzers | `Student`, `Teacher`, `Librarian` |
| **Borrowing Limit** | Max. Anzahl gleichzeitig ausgeliehener Medien | Schüler: 5, Lehrer: 10 |

#### Geschäftsregeln:

- ✅ Authentifizierung erfolgt **ausschließlich über Schul-SSO** (keine lokalen Passwörter)
- ✅ **UserGroup** wird vom SSO übernommen und nur gelesen
- ✅ Jeder User hat genau eine **SchoolIdentity**
- ✅ **Borrowing Limit** ist abhängig von der UserGroup

#### Abhängigkeiten:

- **INPUT:** SSO-Authentifizierung (extern)
- **OUTPUT:** User-Events: `UserCreated`, `UserProfileUpdated`
- **CONSUMED BY:** Lending Context, Notification Context

---

### 2. **CATALOG CONTEXT** (Supporting Subdomain)

**Kategorie:** Supporting Subdomain
**MVP-Status:** Ja
**Kritikalität:** Hoch (Kernprodukt, aber nicht einzigartig)

#### Ubiquitous Language (Kernel-Begriffe):

| Begriff | Definition | Beispiel |
|---------|-----------|---------|
| **Media** | Ein Medienwerk im Bestand (Aggregate Root) | "Der Herr der Ringe" |
| **MediaCopy** | Ein physisches Exemplar eines Medienwerks | "Der Herr der Ringe (Exemplar 1)" |
| **Inventory** | Verwaltung des gesamten Bestands | Alle MediaCopy + Status |
| **AvailabilityStatus** | Der aktuelle Status einer MediaCopy | `Available`, `CheckedOut`, `Reserved`, `OnHold` |
| **MediaCategory** | Klassifizierung des Mediums | `Fiction`, `NonFiction`, `Reference` |
| **MediaMetadata** | Beschreibende Informationen | Titel, Autor, ISBN, Klappentext |

#### Geschäftsregeln:

- ✅ Jede **Media** kann mehrere **MediaCopy**-Exemplare haben
- ✅ Die **AvailabilityStatus** wird von Events aus dem **Lending Context** aktualisiert
- ✅ **MediaCategory** ist eine feste Liste (nicht erweiterbar im MVP)
- ✅ Suche ist **Read-Only** aus diesem Context (keine Reservation/Loan-Logik hier)
- ✅ Bestandsverwaltung erfolgt über ein Admin-Interface

#### Abhängigkeiten:

- **INPUT:** Events aus Lending Context (`MediaCheckedOut`, `MediaReturned`, `MediaReserved`)
- **OUTPUT:** Verfügbarkeitsdaten für Suche
- **CONSUMED BY:** Lending Context, Notification Context

---

### 3. **LENDING CONTEXT** ⭐ (Core Domain)

**Kategorie:** Core Domain (Kerndomäne)
**MVP-Status:** Ja
**Kritikalität:** ⭐⭐⭐ (Größter Wettbewerbsvorteil, höchste Komplexität)

#### Ubiquitous Language (Kernel-Begriffe):

| Begriff | Definition | Geschäftsregel |
|---------|-----------|-----------------|
| **Loan** (Aggregate Root) | Ein Ausleihvorgang für einen User | Stellt Beziehung zu User + MediaCopy dar |
| **LoanItem** | Eine Zeile in einem Loan | Ein MediaCopy pro Loan |
| **DueDate** | Das Rückgabedatum | Abhängig von LoanPolicy + UserGroup |
| **Renewal** | Verlängerung der Ausleihfrist | Max. 2x, nur wenn nicht vorgemerkt |
| **Reservation** (Aggregate Root) | Reservierung eines verfügbaren Mediums | Gültig für 48h, dann verfällt |
| **PreReservation** (Waitlist) | Vormerkung auf ein verliehenes Medium | FIFO-Queue, auto-Reservierung bei Rückgabe |
| **ClassSet** | Eine Sammlung von Medien für eine Schulklasse | Besondere Ausleihregel für Lehrer |
| **Fine** (Aggregate Root) | Mahngebühr für überfällige/beschädigte Medien | Verwaltet Geldbetrag und Status |
| **LoanPolicy** | Regel für Ausleihfristen | `Student: 3 Wochen, Teacher: 8 Wochen` |
| **ReservationPolicy** | Regel für Reservierungsdauer | `Standard: 48h, ClassSet: 1 Woche` |
| **FinePolicy** | Regel für Mahngebühren | `0.50€ pro Tag` |
| **CheckOut** (Event) | Der Moment der Ausleihe | `MediaCheckedOut` Domain Event |
| **Return** (Event) | Der Moment der Rückgabe | `MediaReturned` Domain Event |

#### Geschäftsregeln (Invarianten):

```
┌─────────────────────────────────────────────────────────────┐
│                 LENDING BUSINESS RULES                      │
├─────────────────────────────────────────────────────────────┤
│                                                              │
│ 1. AUSLEIHE (Checkout):                                     │
│    ✓ User muss aktiv sein & keine überfälligen Medien haben │
│    ✓ User-Borrowing-Limit muss überschritten sein           │
│    ✓ Media muss verfügbar sein (Status = Available)         │
│    ✓ DueDate wird per LoanPolicy (Admin Web-App) je UserGroup berechnet │
│    ✓ Domain Event "MediaCheckedOut" wird publiziert         │
│                                                              │
│ 2. RESERVIERUNG (Available Media):                          │
│    ✓ User reserviert ein verfügbares Medium für ReservationPolicy.ttl (Admin Web-App, Default 48h) │
│    ✓ Status wird auf "Reserved" gesetzt                     │
│    ✓ User muss Reservation innerhalb 48h abholen            │
│    ✓ Bei Nichtabholung verfällt die Reservation            │
│    ✓ Domain Event "MediaReserved" wird publiziert           │
│                                                              │
│ 3. VORMERKUNG (PreReservation/Waitlist):                    │
│    ✓ Für verliehene Medien möglich (FIFO-Queue)            │
│    ✓ Bei Rückgabe wird automatisch in Reservation umgewandelt│
│    ✓ User wird sofort notifiziert (über Notification Ctx)  │
│                                                              │
│ 4. KLASSENSATZ-SPEZIAL:                                     │
│    ✓ Nur Lehrer können Klassensätze ausleihen             │
│    ✓ Längere Ausleihdauer per ClassSetPolicy (Admin Web-App, Default: 8 Wochen) │
│    ✓ Besondere Rückgabeprozedur (vollständiger Satz!)     │
│                                                              │
│ 5. RÜCKGABE (Return):                                       │
│    ✓ MediaCopy muss auf einem aktiven Loan sein           │
│    ✓ Status wird auf "Available" gesetzt                    │
│    ✓ Wenn überfällig → Überfälligkeits-Flag setzen         │
│    ✓ Domain Event "MediaReturned" wird publiziert           │
│                                                              │
│ 6. VERLÄNGERUNG (Renewal):                                  │
│    ✓ Maximal gemäß RenewalPolicy (Admin Web-App, Default: 2) │
│    ✓ Nur wenn keine PreReservation vorhanden               │
│    ✓ DueDate wird um RenewalPolicy.durationDays verlängert │
│    ✓ Domain Event "LoanRenewed" wird publiziert             │
│                                                              │
│ 7. MAHNGEBÜHREN (Fines):                                    │
│    ✓ Entstehen bei Rückgabe nach DueDate                    │
│    ✓ Berechnung: Tage überfällig * FinePolicy.amountPerDay  │
│    ✓ Status: Open -> Paid (oder Waived)                     │
│    ✓ Domain Event "FineCreated" wird publiziert             │
│                                                              │
└─────────────────────────────────────────────────────────────┘
```

#### Abhängigkeiten:

- **INPUT (Read):** Catalog Context (Verfügbarkeit), User Context (UserGroup)
- **OUTPUT (Events):** `MediaCheckedOut`, `MediaReturned`, `MediaReserved`, `PreReservationCreated`, `LoanRenewed`
- **CONSUMED BY:** Notification Context, Reminding Context

---

### 4. **NOTIFICATION CONTEXT** (Supporting Subdomain)

**Kategorie:** Supporting Subdomain
**MVP-Status:** Ja
**Kritikalität:** Mittel (Standard-Notification, aber wichtig für UX)

#### Ubiquitous Language (Kernel-Begriffe):

| Begriff | Definition | Typ |
|---------|-----------|------|
| **Notification** | Eine Nachricht an einen User | Entity |
| **NotificationChannel** | Der Kanal (E-Mail, Push) | Value Object: `Email`, `Push` |
| **NotificationTemplate** | HTML-Template für eine Nachricht | Value Object |
| **EventListener** | Subscription auf Domain Events | Infrastructure |
| **Preference** | User-Einstellung für Notification | Tied to User Context |

#### Geschäftsregeln:

- ✅ **Event-Driven:** Listens auf Domain Events aus Lending Context
- ✅ **No State:** Dieser Context speichert nur History, nicht die Geschäftslogik
- ✅ **Multi-Channel:** Push (optional) + E-Mail (Fallback)
- ✅ **Deduplication:** Keine doppelten Notifications bei gleichem Event

#### Abhängigkeiten:

- **INPUT (Events):** Lending Context (`MediaCheckedOut`, `MediaReserved`, `MediaReturned`), Reminding Context (`ReminderTriggered`)
- **OUTPUT:** E-Mail, Push-Nachrichten
- **CONSUMED BY:** User (via Push/E-Mail)

---

### 5. **REMINDING CONTEXT** (Supporting Subdomain)

**Kategorie:** Supporting Subdomain
**MVP-Status:** Ja (für Überfälligkeiten)
**Kritikalität:** Mittel (Geschäftswert, aber Standard-Domain)

#### Ubiquitous Language (Kernel-Begriffe):

| Begriff | Definition | Regel |
|---------|-----------|------|
| **ReminderPolicy** | Regeln für Erinnerungen | z.B. "3 Tage vor Frist" |
| **ReminderCampaign** | Eine ausgelöste Erinnerungswelle | Auto-triggered based on Policy |
| **OverdueReminder** | Erinnerung für überfällige Medien | Sofort nach Fristüberschreitung |
| **UpcomingReminder** | Vorwätzliche Erinnerung | 3 Tage vor Rückgabefrist |

#### Geschäftsregeln:

- ✅ **Automatische Trigger:** Basierend auf DueDate aus Lending Context
- ✅ **Staged Reminders:**
  - **T-3:** "In 3 Tagen fällig" (informativ)
  - **T+0:** "Heute zurückgeben" (reminder)
  - **T+1:** "1 Tag überfällig" (warning)
  - **T+7:** "7 Tage überfällig" (escalation)
- ✅ **publiziert `ReminderTriggered` Event** → Notification Context

#### Abhängigkeiten:

- **INPUT (Read):** Lending Context (alle Loans)
- **OUTPUT (Events):** `ReminderTriggered`, `OverdueDetected`
- **CONSUMED BY:** Notification Context, Mahnwesen (Phase 2)

---

## 🔄 Integration zwischen Contexts

### Integrationsmuster

```
┌──────────────────────────────────────────────────────────────────┐
│                     INTEGRATION PATTERNS                         │
├──────────────────────────────────────────────────────────────────┤
│                                                                   │
│ 1. SYNCHRON (Request-Reply):                                    │
│    ├─ User Context → Lending Context                            │
│    │  Query: "Ist dieser User valid & aktiv?"                  │
│    │  Method: checkUserEligibility(userId)                      │
│    │                                                              │
│    └─ Lending Context → Catalog Context                         │
│       Query: "Ist dieses Media verfügbar?"                      │
│       Method: checkMediaAvailability(mediaId)                    │
│                                                                   │
│ 2. ASYNCHRON (Event-Driven):                                     │
│    ├─ Lending Context publishes:                                │
│    │  • MediaCheckedOut(userId, mediaId, dueDate)              │
│    │  • MediaReturned(mediaId, userId, isOverdue)              │
│    │  • MediaReserved(userId, mediaId, reservationId)          │
│    │  • PreReservationCreated(userId, mediaId, position)       │
│    │  • LoanRenewed(loanId, newDueDate)                        │
│    │                                                              │
│    ├─ Consumed by:                                              │
│    │  • Catalog Context: Updates AvailabilityStatus             │
│    │  • Notification Context: Sends Notifications               │
│    │  • Reminding Context: Schedules Reminders                  │
│    │                                                              │
│    └─ Reminding Context publishes:                              │
│       • ReminderTriggered(userId, loanId, reminderType)        │
│       → Consumed by: Notification Context                       │
│                                                                   │
└──────────────────────────────────────────────────────────────────┘
```

### Query & Command Flowchart

```
USER ACTION                 CONTEXT ORCHESTRATION
═══════════════════════════════════════════════════════════════════

1. SEARCH MEDIA
   User searches            →  CATALOG Context (read-only)
                               ↓
                               Returns: [Media with AvailabilityStatus]

2. CHECKOUT MEDIA
   Admin scans             →  LENDING Context
   barcode                    ├─ sync query: User Context (valid?)
                             ├─ sync query: Catalog Context (available?)
                             ├─ create Loan
                             └─ publishes: MediaCheckedOut
                                ├→ CATALOG: Updates AvailabilityStatus
                                └→ NOTIFICATION: Sends confirmation

3. RESERVE AVAILABLE
   User clicks             →  LENDING Context
   "Reserve"                  ├─ create Reservation (48h TTL)
                             └─ publishes: MediaReserved
                                ├→ CATALOG: Updates AvailabilityStatus
                                └→ NOTIFICATION: Sends pickup info

4. PRE-RESERVE (WAITLIST)
   User clicks             →  LENDING Context
   "Pre-Reserve"             ├─ create PreReservation
                             └─ publishes: PreReservationCreated
                                └→ NOTIFICATION: Sends "added to waitlist"

5. RETURN MEDIA
   Admin scans             →  LENDING Context
   barcode                    ├─ complete Loan
                             ├─ check if overdue → flag
                             ├─ publishes: MediaReturned
                             │  ├→ CATALOG: Updates AvailabilityStatus
                             │  └→ NOTIFICATION: Sends confirmation
                             └─ process PreReservations (if any)
                                ├─ convert first PreReservation
                                │  to Reservation (48h)
                                └─ publishes: PreReservationResolved
                                   └→ NOTIFICATION: Sends pickup notification

6. REMINDING (BATCH PROCESS)
   Scheduled job           →  REMINDING Context
   (hourly/daily)            ├─ queries LENDING (all active Loans)
                             ├─ matches against ReminderPolicy
                             └─ publishes: ReminderTriggered
                                └→ NOTIFICATION: Sends reminder email
```

---

## 📋 Domain Events - Mapping

### Events aus Lending Context

```yaml
EVENT: MediaCheckedOut
├─ Triggered: Beim erfolgreichen Checkout
├─ Payload:
│  ├─ loanId: UUID
│  ├─ userId: UUID
│  ├─ mediaId: UUID
│  ├─ mediaCopyBarcode: String
│  ├─ dueDate: LocalDate
│  └─ userGroup: Enum(Student|Teacher|Librarian)
├─ Handlers:
│  ├─ CatalogContext: Update AvailabilityStatus to "CheckedOut"
│  └─ NotificationContext: Send "Checkout Confirmation"
└─ Synchrony: ASYNCHRONOUS (non-blocking)

EVENT: MediaReturned
├─ Triggered: Beim erfolgreichen Return
├─ Payload:
│  ├─ loanId: UUID
│  ├─ mediaId: UUID
│  ├─ mediaCopyBarcode: String
│  ├─ returnedDate: LocalDate
│  ├─ isOverdue: Boolean
│  └─ overdueDays: Integer (0 if on time)
├─ Handlers:
│  ├─ CatalogContext: Update AvailabilityStatus to "Available"
│  ├─ NotificationContext: Send "Return Confirmation"
│  ├─ LendingContext: Process PreReservations (if any)
│  └─ RemindingContext: Clear pending reminders
└─ Synchrony: ASYNCHRONOUS (non-blocking)

EVENT: MediaReserved
├─ Triggered: Wenn User verfügbares Medium reserviert
├─ Payload:
│  ├─ reservationId: UUID
│  ├─ userId: UUID
│  ├─ mediaId: UUID
│  ├─ expiryDate: LocalDate (NOW + 48h)
│  └─ pickupLocation: String
├─ Handlers:
│  ├─ CatalogContext: Update AvailabilityStatus to "Reserved"
│  └─ NotificationContext: Send "Pickup Information"
└─ Synchrony: ASYNCHRONOUS (non-blocking)

EVENT: PreReservationCreated
├─ Triggered: Wenn User verliehenes Medium vormerkt
├─ Payload:
│  ├─ preReservationId: UUID
│  ├─ userId: UUID
│  ├─ mediaId: UUID
│  ├─ position: Integer (position in Waitlist)
│  └─ estimatedAvailableDate: LocalDate
├─ Handlers:
│  └─ NotificationContext: Send "Added to Waitlist (Position X)"
└─ Synchrony: ASYNCHRONOUS (non-blocking)

EVENT: PreReservationResolved
├─ Triggered: Automatisch wenn Media returned (→ Waitlist abgearbeitet)
├─ Payload:
│  ├─ preReservationId: UUID
│  ├─ reservationId: UUID (newly created)
│  ├─ userId: UUID
│  ├─ mediaId: UUID
│  └─ pickupDeadline: LocalDate
├─ Handlers:
│  └─ NotificationContext: Send "Your reservation is ready for pickup"
└─ Synchrony: ASYNCHRONOUS (non-blocking)

EVENT: LoanRenewed
├─ Triggered: Wenn Nutzer Ausleihe verlängert
├─ Payload:
│  ├─ loanId: UUID
│  ├─ userId: UUID
│  ├─ newDueDate: LocalDate
│  ├─ renewalCount: Integer
│  └─ maxRenewalsAllowed: Integer
├─ Handlers:
│  └─ NotificationContext: Send "Renewal Confirmation"
└─ Synchrony: ASYNCHRONOUS (non-blocking)
```

### Events aus Reminding Context

```yaml
EVENT: ReminderTriggered
├─ Triggered: Von Scheduled Job basierend auf Policy
├─ Payload:
│  ├─ reminderId: UUID
│  ├─ loanId: UUID
│  ├─ userId: UUID
│  ├─ mediaId: UUID
│  ├─ reminderType: Enum(UpcomingReminder|OverdueReminder|EscalationReminder)
│  ├─ daysUntilOrAfterDue: Integer
│  └─ subject: String
├─ Handlers:
│  └─ NotificationContext: Send reminder via email/push
└─ Synchrony: ASYNCHRONOUS (non-blocking)
```

---

## 🛡️ Anti-Corruption Layer (ACL)

### 1. **SSO Integration Anti-Corruption Layer** (User Context ↔ External SSO)

```
┌──────────────────────────────────────────────────────┐
│           EXTERNAL: SCHOOL SSO SYSTEM                 │
│           (Format: OpenID/SAML/OAuth2)               │
└────────────────────┬─────────────────────────────────┘
                     │
                     │ ANTI-CORRUPTION LAYER
                     │
        ┌────────────▼─────────────┐
        │  SSO Adapter / Translator │
        │                          │
        │ • Converts SSO formats   │
        │   to internal DTOs       │
        │ • Maps external attrs    │
        │   to UserGroup enum      │
        │ • Validates schema       │
        │ • Handles versioning     │
        └────────────┬─────────────┘
                     │
                     ▼
        ┌──────────────────────────┐
        │   USER CONTEXT           │
        │   (Internal format)      │
        └──────────────────────────┘
```

**Mapping Rules:**

| External SSO Attribute | Internal UserGroup | Regel |
|-------|--------|------|
| `eduPersonPrimaryAffiliation = student` | `Student` | Standard |
| `eduPersonPrimaryAffiliation = faculty` | `Teacher` | Standard |
| `mail contains "admin" or custom flag` | `Librarian` | Admin-Check |

---

## 🎯 Strategische Entscheidungen dokumentiert

### Entscheidung 1: **Lending + Reservation als INTEGRIERTE Core Domain**

**Begründung:**
- ✅ Kerngeschäft der Bibliothek
- ✅ Komplexe Geschäftslogik (Policies, Waitlists, ClassSets)
- ✅ Wettbewerbsvorteil durch spezialisierte Ausleihrules
- ✅ Änderungen hier beeinflussen viele andere Contexts

**Konsequenzen:**
- Wir investieren die besten Entwickler-Ressourcen
- Ausführliches Testen (Unit + Integration)
- Event-Sourcing als Future-Option

---

### Entscheidung 2: **Catalog Context ist Supporting (nicht Core)**

**Begründung:**
- ❌ Nicht einzigartig für die Schulbibliothek
- ✅ Standard-Produktkatalog-Problem
- ✅ Kann mit Standard-Lösungen implementiert werden
- ❌ Keine differenzierenden Geschäftsregeln (nur CRUD + Suche)

**Konsequenzen:**
- Weniger kritisch bei Auswahl der Technologie
- Könnte später durch externe Lösung ersetzt werden

---

### Entscheidung 3: **User Context ist Generic Subdomain**

**Begründung:**
- ✅ Standard-IAM-Problem
- ✅ SSO ist ein bewährtes Pattern
- ❌ Keine schul-spezifischen Besonderheiten in der Nutzerlogik
- ✅ Kann mit bestehenden SSO-Systemen integriert werden

**Konsequenzen:**
- Minimale Implementierung (Adapter + Mapping)
- Keine Custom-Authentication
- Strong Dependency auf extern verfügbares SSO

---

### Entscheidung 4: **Notification Context ist Event-getrieben (nicht synchron)**

**Begründung:**
- ✅ Notifications sind nicht kritisch für Geschäftsfunktion
- ✅ Bessere Skalierbarkeit & Fehlertoleranz
- ✅ Entkopplung von Lending Context
- ✅ Einfach zu testen (Message-based)

**Konsequenzen:**
- Notifications können verzögert sein (Sekunden)
- Keine Transaktions-Garantien (at-least-once delivery)
- Asynchrone Exception-Handling nötig

---

### Entscheidung 5: **Reminding Context separate von Lending**

**Begründung:**
- ✅ Andere Verantwortung (Time-based Triggers vs. Immediate Actions)
- ✅ Kann unabhängig skaliert werden
- ✅ Später: Eschenator Rules oder BPM-Integration
- ✅ Abgrenzung in Phase 2 (Mahnwesen-Kontext)

**Konsequenzen:**
- Kein direkter Zugriff auf Lending-State (nur Read via Events)
- Scheduling-Infra nötig (Quartz/TaskScheduler)

---

## 📚 Context-zu-User-Story-Mapping

| User Story | Primary Context | Sekundäre Contexts | Domain Events |
|------------|-----------------|-------------------|-----------------|
| US-001: Benutzerkonto & SSO | User Context | - | `UserCreated`, `UserProfileUpdated` |
| US-002: Katalog-Suche | Catalog Context | - | - |
| US-003: Reservierung & Vormerkung | Lending Context | Catalog, Notification | `MediaReserved`, `PreReservationCreated` |
| US-004: Ausleihe (Admin) | Lending Context | Catalog, User, Notification | `MediaCheckedOut` |
| US-005: Benachrichtigungen | Notification Context | Lending, Reminding | - |
| US-006: Rückgabe (Admin) | Lending Context | Catalog, Notification, Reminding | `MediaReturned` |
| US-007: Bestandsverwaltung | Catalog Context | - | - |
| US-008: Mahnwesen | Reminding Context | Notification, Lending | `ReminderTriggered` |
| US-009: Klassensatz | Lending Context | Catalog, User | `ClassSetCheckedOut` |
| US-010: Reporting | (Phase 2) | - | - |
| US-011: Empfehlungslisten | (Phase 2) | - | - |

---

## 🚀 Nächste Phase: Taktisches Design

Mit dieser **strategischen Architektur** haben Sie die Basis für das **Taktische Design** geschaffen:

1. **Aggregate Roots** pro Context definieren
2. **Value Objects** und deren Invarianten
3. **Domain Services** identifizieren
4. **Repositories** und **Factories**
5. **Application Services** (Command Handlers)

➡️ **Nächster Chat-Mode:** `ddd-architect-taktik-design`

