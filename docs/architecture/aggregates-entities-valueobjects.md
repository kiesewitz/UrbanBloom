# Aggregate Roots, Entities & Value Objects - Taktisches Design

**Version:** 1.0  
**Datum:** 2025-12-17  
**Phase:** Taktisches Design (Phase 2)  
**Status:** Draft für Geschäftslogik-Zuordnung

---

## 📋 Übersicht

Dieses Dokument beschreibt alle DDD-Bausteine (Aggregate Roots, Entities, Value Objects) je Bounded Context mit ihren Attributen, Beziehungen und Invarianten.

---

## 🎯 LENDING CONTEXT (Core Domain)

### Aggregat 1: Loan (Aggregate Root)

**Identität:** `loanId: UUID`  
**Lebenszyklus:** Active → Returned | Overdue  
**Verantwortung:** Verwaltung eines Ausleihvorgangs mit Fristberechnung und Verlängerungslogik

#### Attribute:

| Attribut | Typ | Constraints | Beschreibung |
|----------|-----|-----------|-------------|
| `loanId` | UUID | NOT NULL, UNIQUE, PK | Eindeutige Kennung |
| `userId` | UUID | NOT NULL, FK | Nutzer (aus User Context) |
| `mediaId` | UUID | NOT NULL | Medium (aus Catalog Context) |
| `mediaCopyBarcode` | String | NOT NULL | Spezifisches Exemplar (z.B. "SCH-12345") |
| `checkoutDate` | LocalDate | NOT NULL | Ausleih-Datum |
| `dueDate` | LocalDate | NOT NULL, > checkoutDate | Rückgabefrist (berechnet aus LoanPolicy) |
| `returnDate` | LocalDate | Nullable | Tatsächliches Rückgabe-Datum |
| `status` | Enum(LoanStatus) | NOT NULL | Active, Returned, Overdue |
| `renewalCount` | Integer | 0-2 (Default: 0) | Wie oft verlängert |
| `isOverdue` | Boolean | Default: false | Überfällig-Flag |
| `overdueDays` | Integer | >= 0 (Default: 0) | Tage über Frist |
| `createdAt` | Instant | NOT NULL | Erstellungs-Zeitstempel |
| `lastModifiedAt` | Instant | NOT NULL | Letzter Änderungs-Zeitstempel |

#### Enthaltene Entities:
- **KEINE** (1:1 Mapping zu MediaCopy)

#### Referenzierte Aggregate (über ID):
- **User** (userId) → Query: `checkEligibility()`, `getBorrowingLimit()`
- **Media** (mediaId) → Query: `getTitle()` (für Notifications)
- **MediaCopy** (mediaCopyBarcode) → Query: `checkAvailability()`

#### Value Objects:
- **DueDate** → LocalDate mit Validierung (> checkoutDate)
- **LoanPolicy** → Regeln pro UserGroup (studentDays, teacherDays, librarianDays)
- **CheckoutDate** → LocalDate
- **ReturnDate** → Optional<LocalDate>
- **OverdueDays** → NonNegativeInteger
- **RenewalCount** → Integer (0-2)
- **LoanStatus** → Enum

#### Geschäftsregeln (Invarianten):

```
INVARIANTE 1: Zeitlogik
  ✓ checkoutDate < dueDate (immer!)
  ✓ returnDate >= checkoutDate (wenn gesetzt)
  ✓ returnDate <= TODAY (Rückgabedatum darf nicht in Zukunft liegen)

INVARIANTE 2: Renewal-Regeln
  ✓ renewalCount <= RenewalPolicy.maxRenewals (Default: 2)
  ✓ Renewal nur möglich wenn: renewalCount < max UND keine PreReservation existiert UND status = Active
  ✓ Renewal nur möglich BEVOR dueDate überschritten ist

INVARIANTE 3: Overdue-Flag
  ✓ isOverdue = TRUE IFF (TODAY > dueDate AND status = Active)
  ✓ isOverdue wird beim Return automatisch berechnet
  ✓ overdueDays = max(0, TODAY - dueDate)

INVARIANTE 4: Status-Übergänge
  ✓ Active → Returned (via return())
  ✓ Active → Overdue (via scheduled job wenn TODAY > dueDate)
  ✓ Returned ist terminal (keine weitere Änderung)

INVARIANTE 5: Return-Invarianten
  ✓ Return nur möglich wenn status = Active
  ✓ returnDate wird auf TODAY gesetzt
  ✓ MediaCopy-Status wird auf Available aktualisiert
```

#### Methoden:

**Factory Methods:**
- `static checkout(userId, mediaId, mediaCopyBarcode, loanPolicy, userGroup) → Loan`
  - Berechnet dueDate basierend auf LoanPolicy + UserGroup
  - Setzt checkoutDate = TODAY, status = Active, renewalCount = 0
  - Validiert alle Invarianten
  - Use Case: US-004; Domain Event: MediaCheckedOut

**Core Business Methods:**
- `return(returnDate = TODAY) → void`
  - Prüft status == Active, setzt returnDate
  - Berechnet isOverdue und overdueDays
  - Setzt status = Returned
  - Use Case: US-006; Domain Event: MediaReturned

- `renew(renewalPolicy, loanPolicy, existingPreReservations) → void`
  - Guards: renewalCount < max, keine PreReservations, TODAY < dueDate, status == Active
  - Verlängert dueDate um renewalPolicy.durationDays
  - Erhöht renewalCount
  - Use Case: US-003; Domain Event: LoanRenewed

- `isEligibleForRenewal(renewalPolicy, existingPreReservations) → Boolean`
  - Prüft alle Renewal-Bedingungen

- `isOverdue() → Boolean`
  - Gibt TRUE zurück wenn TODAY > dueDate AND status = Active

- `calculateOverdueDays() → Integer`
  - Berechnet max(0, TODAY - dueDate)

**Queries:**
- `getDueDate() → LocalDate`
- `getRenewalCount() → Integer`
- `getStatus() → LoanStatus`
- `getIsOverdue() → Boolean`

---

### Aggregat 2: Reservation (Aggregate Root)

**Identität:** `reservationId: UUID`  
**Lebenszyklus:** Pending → Collected | Expired  
**Verantwortung:** Verwaltung verfügbarer Medien-Reservierungen mit 48h-TTL

#### Attribute:

| Attribut | Typ | Constraints | Beschreibung |
|----------|-----|-----------|-------------|
| `reservationId` | UUID | NOT NULL, UNIQUE, PK | Eindeutige Kennung |
| `userId` | UUID | NOT NULL, FK | Nutzer (aus User Context) |
| `mediaId` | UUID | NOT NULL | Medium (aus Catalog Context) |
| `mediaCopyBarcode` | String | NOT NULL | Spezifisches Exemplar |
| `createdAt` | Instant | NOT NULL | Reservierungs-Zeitstempel |
| `expiryDate` | LocalDate | NOT NULL | Abholdeadline (NOW + ReservationPolicy.ttl) |
| `pickupLocation` | String | NOT NULL | Abhol-Standort (z.B. "Main Desk") |
| `status` | Enum(ReservationStatus) | NOT NULL | Pending, Collected, Expired |
| `collectedAt` | Instant | Nullable | Zeitpunkt der Abholung |
| `notificationSent` | Boolean | Default: false | User notifiziert? |

#### Enthaltene Entities:
- **KEINE**

#### Referenzierte Aggregate (über ID):
- **User** (userId)
- **Media** (mediaId)
- **MediaCopy** (mediaCopyBarcode)

#### Value Objects:
- **ExpiryDate** → LocalDate (auto-berechnet: createdAt + ReservationPolicy.ttl)
- **ReservationPolicy** → TTL in Stunden/Tagen (Default: 48h)
- **PickupDeadline** → LocalDateTime
- **ReservationStatus** → Enum

#### Geschäftsregeln (Invarianten):

```
INVARIANTE 1: Timing
  ✓ expiryDate = createdAt + ReservationPolicy.ttl (Default: 48h)
  ✓ expiryDate > createdAt

INVARIANTE 2: Status-Übergänge
  ✓ Pending → Collected (via pickup())
  ✓ Pending → Expired (via scheduled job wenn TODAY > expiryDate)
  ✓ Collected und Expired sind terminal

INVARIANTE 3: Eindeutigkeit
  ✓ Nur 1 aktive (Pending) Reservation pro User-Media-Kombination erlaubt
  ✓ Ein User kann maximal 3 offene Reservationen haben

INVARIANTE 4: Automation
  ✓ Cron-Job setzt status = Expired wenn expiryDate überschritten
  ✓ Nach Collection: Reservation → Loan Transformation (oder manuell)
  ✓ notificationSent = true NACH erfolgreicher Mail

INVARIANTE 5: Collection
  ✓ collectedAt darf nur gesetzt sein wenn status = Collected
  ✓ collectedAt >= createdAt
```

#### Methoden:

**Factory Methods:**
- `static reserve(userId, mediaId, mediaCopyBarcode, reservationPolicy) → Reservation`
  - Erstellt Reservation mit TTL aus reservationPolicy
  - Setzt status = Pending, berechnet expiryDate
  - Use Case: US-003; Domain Event: MediaReserved

**Core Business Methods:**
- `collect() → void`
  - Prüft status == Pending
  - Setzt status = Collected, collectedAt = NOW
  - Use Case: US-003

- `expire() → void`
  - Setzt status = Expired
  - Wird vom Scheduler aufgerufen wenn expiryDate überschritten
  - Use Case: US-003 (TTL)

- `cancel() → void`
  - Prüft status == Pending
  - Setzt status = Cancelled
  - Use Case: US-003

**Queries:**
- `isPending() → Boolean`
- `isExpired() → Boolean`
- `getExpiryDate() → LocalDate`
- `getPickupLocation() → String`

---

### Aggregat 3: PreReservation (Aggregate Root)

**Identität:** `preReservationId: UUID`  
**Lebenszyklus:** Waiting → Resolved | Cancelled  
**Verantwortung:** FIFO-Waitlist für verliehene Medien

#### Attribute:

| Attribut | Typ | Constraints | Beschreibung |
|----------|-----|-----------|-------------|
| `preReservationId` | UUID | NOT NULL, UNIQUE, PK | Eindeutige Kennung |
| `userId` | UUID | NOT NULL, FK | Nutzer |
| `mediaId` | UUID | NOT NULL | Das verliehene Medium |
| `position` | Integer | > 0 | Position in FIFO-Queue |
| `createdAt` | Instant | NOT NULL | Vormerkdatum |
| `estimatedAvailableDate` | LocalDate | Nullable | Geschätzte Verfügbarkeit |
| `status` | Enum(PreReservationStatus) | NOT NULL | Waiting, Resolved, Cancelled |
| `resolvedAt` | Instant | Nullable | Zeitpunkt der Auflösung |

#### Enthaltene Entities:
- **KEINE**

#### Referenzierte Aggregate (über ID):
- **User** (userId)
- **Media** (mediaId)

#### Value Objects:
- **WaitlistPosition** → PositiveInteger
- **EstimatedAvailableDate** → Optional<LocalDate>
- **PreReservationStatus** → Enum

#### Geschäftsregeln (Invarianten):

```
INVARIANTE 1: Position
  ✓ position >= 1
  ✓ position wird neu berechnet wenn höher liegende Einträge gelöscht werden

INVARIANTE 2: Eindeutigkeit & Limits
  ✓ Nur 1 aktive PreReservation pro User-Media-Kombination
  ✓ Max. 3 PreReservations pro User (global über alle Medien)

INVARIANTE 3: Status-Übergänge
  ✓ Waiting → Resolved (via autoResolve beim MediaReturn)
  ✓ Waiting → Cancelled (via cancel())
  ✓ Resolved und Cancelled sind terminal

INVARIANTE 4: Auto-Resolution
  ✓ Bei MediaReturned: Erste PreReservation in Waiting → Resolved
  ✓ Neue Reservation wird automatisch erstellt (48h TTL)
  ✓ resolvedAt wird gesetzt

INVARIANTE 5: FIFO-Garantie
  ✓ Position ist sortierend (createdAt)
  ✓ Keine Position-Sprünge möglich
```

#### Methoden:

**Factory Methods:**
- `static preReserve(userId, mediaId, currentWaitlistSize) → PreReservation`
  - Berechnet position = currentWaitlistSize + 1
  - Setzt createdAt = NOW, status = Waiting
  - estimatedAvailableDate wird später geschätzt
  - Use Case: US-003; Domain Event: PreReservationCreated

**Core Business Methods:**
- `resolve() → void`
  - Prüft status == Waiting
  - Setzt status = Resolved, resolvedAt = NOW
  - Wird intern bei MediaReturn aufgerufen
  - Use Case: Internal; Domain Event: PreReservationResolved

- `cancel() → void`
  - Prüft status == Waiting
  - Setzt status = Cancelled
  - Andere PreReservations mit höherer Position werden nachgeschoben
  - Use Case: US-003

- `updatePosition(newPosition) → void`
  - Nur intern nach cancel() aufrufen
  - Aktualisiert position

- `setEstimatedAvailableDate(estimatedDate) → void`
  - Setzt geschätztes Verfügbarkeitsdatum

**Queries:**
- `getPosition() → Integer`
- `isWaiting() → Boolean`
- `getEstimatedAvailableDate() → LocalDate`

---

### Aggregat 4: ClassSet (Aggregate Root)

**Identität:** `classSetId: UUID`  
**Lebenszyklus:** Active → Returned | PartiallyReturned  
**Verantwortung:** Verwaltung von Klassensatz-Ausleihen mit Multi-Media-Handling

#### Attribute:

| Attribut | Typ | Constraints | Beschreibung |
|----------|-----|-----------|-------------|
| `classSetId` | UUID | NOT NULL, UNIQUE, PK | Eindeutige Kennung |
| `classSetLoanId` | UUID | NOT NULL | Referenz zum Ausleihvorgang |
| `teacherUserId` | UUID | NOT NULL, FK | Nur Lehrer (UserGroup = Teacher) |
| `className` | String | NOT NULL (max 10 chars) | Klasse (z.B. "8a", "10b") |
| `checkoutDate` | LocalDate | NOT NULL | Ausleih-Datum |
| `dueDate` | LocalDate | NOT NULL | Längerfrist (ClassSetPolicy, Default: 8 Wochen) |
| `returnDate` | LocalDate | Nullable | Rückgabedatum |
| `status` | Enum(ClassSetStatus) | NOT NULL | Active, Returned, PartiallyReturned |
| `isComplete` | Boolean | Default: false | Alle SetMembers zurück? |
| `createdAt` | Instant | NOT NULL | Erstellungszeitstempel |
| `lastModifiedAt` | Instant | NOT NULL | Letzter Änderungszeitstempel |

#### Enthaltene Entities:

**SetMember (Entity):**
| Attribut | Typ | Constraints | Beschreibung |
|----------|-----|-----------|-------------|
| `setMemberId` | UUID | NOT NULL | ID des Set-Eintrags |
| `mediaCopyBarcode` | String | NOT NULL | Referenz zu MediaCopy |
| `mediaTitle` | String | NOT NULL (denormalisiert) | Titel für schnelle Anzeige |
| `returnedAt` | Instant | Nullable | Rückgabezeitstempel |
| `status` | Enum(SetMemberStatus) | NOT NULL | CheckedOut, Returned, Missing |

#### Referenzierte Aggregate (über ID):
- **User** (teacherUserId) → MUSS UserGroup = Teacher sein
- **MediaCopy** (SetMember.mediaCopyBarcode, mehrere) ← KORRIGIERT: Spezifische Exemplare!

#### Value Objects:
- **ClassSetPolicy** → Ausleihdauer (Default: 8 Wochen)
- **SetCompleteness** → Boolean mit Validierung
- **ClassSetStatus** → Enum
- **SetMemberStatus** → Enum

#### Geschäftsregeln (Invarianten):

```
INVARIANTE 1: Zugriff & Validierung
  ✓ teacherUserId MUSS User mit UserGroup = Teacher sein
  ✓ setMembers.count >= 1 (mindestens 1 Medium im Set)
  ✓ Keine Duplikate in SetMembers (barcode-eindeutig)

INVARIANTE 2: Completeness-Status
  ✓ isComplete = TRUE IFF alle SetMembers.status = Returned
  ✓ status = Returned IFF isComplete = TRUE
  ✓ status = PartiallyReturned IFF mindestens 1 SetMember returned ABER nicht alle

INVARIANTE 3: Checkout-Invarianten
  ✓ Alle SetMember-MediaCopies MÜSSEN verfügbar sein (availabilityStatus = Available)
  ✓ dueDate wird aus ClassSetPolicy berechnet (länger als normale Loans)
  ✓ Keine Gleichzeitigkeit: Teacher kann nur 1 aktives ClassSet haben

INVARIANTE 4: Return-Invarianten
  ✓ Return nur möglich wenn isComplete = TRUE (alle Exemplare zurück)
  ✓ IF NOT isComplete: Admin-Flag setzen für manuelles Folgeup
  ✓ returnDate = TODAY bei erfolgreicher Rückgabe

INVARIANTE 5: Status-Übergänge
  ✓ Active → PartiallyReturned (wenn einzelne SetMembers zurückkommen)
  ✓ Active → Returned (wenn alle zurück)
  ✓ PartiallyReturned → Returned (wenn letztes Member zurückkommt)
  ✓ Returned ist terminal
```

#### Methoden:

**Factory Methods:**
- `static checkoutClassSet(teacherUserId, className, setMembers, classSetPolicy) → ClassSet`
  - Validiert teacherUserId ist Teacher
  - Validiert setMembers.count >= 1, keine Duplikate
  - Berechnet dueDate = TODAY + classSetPolicy.duration
  - Setzt status = Active, isComplete = false
  - Use Case: US-009; Domain Event: ClassSetCheckedOut

**Core Business Methods:**
- `markSetMemberReturned(barcode, returnDate = TODAY) → void`
  - Findet SetMember mit barcode
  - Validiert status == CheckedOut
  - Setzt setMember.status = Returned, returnedAt = returnDate
  - Ruft recalculateCompleteness() auf
  - Use Case: US-006

- `markSetMemberMissing(barcode) → void`
  - Findet SetMember mit barcode
  - Setzt setMember.status = Missing
  - Flag für Admin-Nachverfolgung
  - Ruft recalculateCompleteness() auf

- `recalculateCompleteness() → void` (private)
  - Berechnet isComplete = alle SetMembers.status == Returned
  - Aktualisiert ClassSet.status entsprechend

- `returnClassSet() → void`
  - Validiert isComplete == true
  - Setzt returnDate = TODAY, status = Returned
  - Domain Event: ClassSetReturned

**Queries:**
- `isComplete() → Boolean`
- `getTotalSetMembers() → Integer`
- `getReturnedSetMembers() → Integer`
- `getMissingSetMembers() → List<SetMember>`
- `getDueDate() → LocalDate`

### Aggregat 5: Fine (Aggregate Root)

**Identität:** `fineId: UUID`
**Lebenszyklus:** Open → Paid | Waived
**Verantwortung:** Verwaltung von Mahngebühren für überfällige oder beschädigte Medien

#### Attribute:

| Attribut | Typ | Constraints | Beschreibung |
|----------|-----|-----------|-------------|
| `fineId` | UUID | NOT NULL, UNIQUE, PK | Eindeutige Kennung |
| `userId` | UUID | NOT NULL, FK | Nutzer (aus User Context) |
| `loanId` | UUID | NOT NULL, FK | Zugehörige Ausleihe |
| `amount` | Money | NOT NULL, > 0 | Betrag der Gebühr |
| `reason` | Enum(FineReason) | NOT NULL | Overdue, Damaged, Lost |
| `status` | Enum(FineStatus) | NOT NULL | Open, Paid, Waived |
| `createdAt` | Instant | NOT NULL | Erstellungs-Zeitstempel |
| `paidAt` | Instant | Nullable | Bezahl-Zeitstempel |
| `waivedReason` | String | Nullable | Grund für Erlass (bei Waived) |

#### Enthaltene Entities:
- **KEINE**

#### Referenzierte Aggregate (über ID):
- **User** (userId)
- **Loan** (loanId)

#### Value Objects:
- **Money** → Betrag und Währung (EUR)
- **FineReason** → Enum (Overdue, Damaged, Lost)
- **FineStatus** → Enum (Open, Paid, Waived)

#### Geschäftsregeln (Invarianten):

```
INVARIANTE 1: Betrag
  ✓ amount muss immer positiv sein (> 0)
  ✓ Währung ist immer EUR

INVARIANTE 2: Status-Übergänge
  ✓ Open → Paid (via pay())
  ✓ Open → Waived (via waive())
  ✓ Paid und Waived sind terminal (keine Änderung mehr möglich)

INVARIANTE 3: Bezahlung
  ✓ paidAt wird gesetzt wenn status auf Paid wechselt
  ✓ waivedReason muss gesetzt sein wenn status auf Waived wechselt
```

#### Methoden:

**Factory Methods:**
- `static create(userId, loanId, amount, reason) → Fine`
  - Setzt status = Open, createdAt = NOW
  - Validiert amount > 0
  - Domain Event: FineCreated

**Core Business Methods:**
- `pay() → void`
  - Prüft status == Open
  - Setzt status = Paid, paidAt = NOW
  - Domain Event: FinePaid

- `waive(reason) → void`
  - Prüft status == Open
  - Setzt status = Waived, waivedReason = reason
  - Domain Event: FineWaived

- `isPaid() → Boolean`

---

## 📚 CATALOG CONTEXT (Supporting Subdomain)

### Aggregat 5: Media (Aggregate Root)

**Identität:** `mediaId: UUID`  
**Lebenszyklus:** Created → Available/Archived (stabiler als Lending)  
**Verantwortung:** Katalog-Eintrag für Werke (abstrakt)

#### Attribute:

| Attribut | Typ | Constraints | Beschreibung |
|----------|-----|-----------|-------------|
| `mediaId` | UUID | NOT NULL, UNIQUE, PK | Eindeutige Kennung |
| `title` | String | NOT NULL, max 500 | Titel des Werks |
| `author` | String | max 200 | Autor/in |
| `isbn` | String | Nullable, ISBN-13 format | ISBN (für Bücher) |
| `publisher` | String | max 200 | Verlag |
| `publicationYear` | Integer | Nullable, 1000-9999 | Erscheinungsjahr |
| `category` | Enum(MediaCategory) | NOT NULL | Fiction, NonFiction, Reference |
| `language` | String | Default: "DE" | Sprache |
| `description` | String | Nullable, max 2000 | Klappentext |
| `coverImageUrl` | String | Nullable, URL format | Link zum Bild |
| `totalCopies` | Integer | >= 0 | Physische Exemplare |
| `availableCopies` | Integer | 0 - totalCopies | Verfügbare Exemplare |
| `createdAt` | Instant | NOT NULL | Erstellungszeitstempel |
| `lastModifiedAt` | Instant | NOT NULL | Letzter Änderungszeitstempel |

#### Enthaltene Entities:
- **KEINE** (MediaCopies sind separate Aggregate)

#### Referenzierte Aggregate:
- **KEINE** (wird von anderen referenziert)

#### Value Objects:
- **MediaMetadata** → Zusammenfassung (title, author, isbn, publisher, year)
- **ISBN** → String mit ISBN-13-Validierung
- **MediaCategory** → Enum (Immutable nach Erstellung)
- **PublicationYear** → Integer (1000-9999)

#### Geschäftsregeln (Invarianten):

```
INVARIANTE 1: Datenvalidierung
  ✓ title DARF NICHT leer sein
  ✓ isbn MUSS ISBN-13 Format haben (wenn gesetzt)
  ✓ publicationYear <= aktuelles Jahr
  ✓ description max 2000 chars

INVARIANTE 2: Bestandskonsistenz
  ✓ totalCopies >= availableCopies (immer!)
  ✓ availableCopies >= 0
  ✓ availableCopies wird von Catalog-Event-Handlern aktualisiert

INVARIANTE 3: Immutability
  ✓ category ist NICHT änderbar nach Erstellung
  ✓ isbn ist NICHT änderbar
  ✓ author/title können geändert werden (für Fehlerkorrektur)

INVARIANTE 4: Katalogverwaltung
  ✓ Medien können nur als Archiv markiert, nicht gelöscht werden
  ✓ totalCopies = 0 möglich (für vergriffene Werke)
```

#### Methoden:

**Factory Methods:**
- `static addMedia(title, author, isbn, category, publicationYear) → Media`
  - Validiert title NOT NULL
  - Validiert ISBN-13 Format (falls gesetzt)
  - Validiert publicationYear <= aktuelles Jahr
  - Generiert mediaId, setzt totalCopies = 0, availableCopies = 0
  - Use Case: US-007 (Admin-Portal)

**Core Business Methods:**
- `addCopy() → void`
  - Erhöht totalCopies++
  - Erhöht availableCopies++ (neue Kopie ist verfügbar)
  - Use Case: US-007

- `removeCopy() → void`
  - Validiert totalCopies > 0, availableCopies > 0
  - Verringert totalCopies--, availableCopies--

- `updateAvailableCopies(newCount) → void`
  - Validiert 0 <= newCount <= totalCopies
  - Setzt availableCopies = newCount
  - Use Case: Event-Handler (MediaCheckedOut, MediaReturned)

**Queries:**
- `isAvailable() → Boolean`
- `getAvailableCopies() → Integer`
- `getTitle() → String`
- `getAuthor() → String`

---

### Aggregat 6: MediaCopy (Aggregate Root)

**Identität:** `copyId: UUID`  
**Business Key:** `barcode` (UNIQUE)  
**Lebenszyklus:** Available → CheckedOut → Available → Damaged  
**Verantwortung:** Physisches Exemplar mit Verfügbarkeitsstatus

#### Attribute:

| Attribut | Typ | Constraints | Beschreibung |
|----------|-----|-----------|-------------|
| `copyId` | UUID | NOT NULL, UNIQUE, PK | Eindeutige Kennung |
| `mediaId` | UUID | NOT NULL, FK | Referenz zu Media |
| `barcode` | String | NOT NULL, UNIQUE | Physische ID (z.B. "SCH-12345") |
| `shelfLocation` | String | Nullable | Regal-Standort (z.B. "C2-R3-H5") |
| `availabilityStatus` | Enum | NOT NULL | Available, CheckedOut, Reserved, OnHold, Damaged |
| `condition` | Enum(MediaCondition) | NOT NULL | Excellent, Good, Fair, Poor |
| `acquisitionDate` | LocalDate | NOT NULL | Ankaufsdatum |
| `lastInventoryCheck` | LocalDate | Nullable | Letzte Inventur |
| `notes` | String | Nullable, max 500 | Admin-Notizen |
| `createdAt` | Instant | NOT NULL | Erstellungszeitstempel |
| `lastModifiedAt` | Instant | NOT NULL | Letzter Änderungszeitstempel |

#### Enthaltene Entities:
- **KEINE**

#### Referenzierte Aggregate (über ID):
- **Media** (mediaId)

#### Value Objects:
- **Barcode** → String mit Custom-Format-Validierung
- **ShelfLocation** → String (Pattern: "C\d+-R\d+-H\d+")
- **AvailabilityStatus** → Enum (State Machine)
- **MediaCondition** → Enum (escalating: Excellent → Good → Fair → Poor)

#### Geschäftsregeln (Invarianten):

```
INVARIANTE 1: Identität
  ✓ barcode MUSS eindeutig sein (UNIQUE constraint)
  ✓ barcode ist IMMUTABLE
  ✓ barcode Format: custom (z.B. "SCH-" + sequential number)

INVARIANTE 2: Verfügbarkeitsstatus State Machine
  Available    ──Checkout──>  CheckedOut
             ──Reserve──>    Reserved

  CheckedOut  ──Return──>     Available (falls on-time)
             ──Return──>     Available (falls overdue)

  Reserved    ──Collect──>   CheckedOut
             ──Expire──>    Available (48h TTL)

  Jeder Status  ──Mark Damaged──>  Damaged (terminal)

  Damaged  (terminal, kein Checkout möglich)

INVARIANTE 3: Zustandsübergänge
  ✓ Status-Übergänge ERFOLGEN NUR via Domain Events aus Lending Context
  ✓ MediaCopy kann sich nicht selbst ändern
  ✓ Damaged ist terminal (kein Zurück)

INVARIANTE 4: Condition Escalation
  ✓ Condition kann sich verschlechtern (Excellent → Good → Fair → Poor)
  ✓ Condition wird NIE besser (keine Downgrade)
  ✓ Damaged-MediaCopies DÜRFEN NICHT ausgeliehen werden

INVARIANTE 5: Inventur & Lagerung
  ✓ shelfLocation ist optional (nicht alle Medien haben festen Standort)
  ✓ lastInventoryCheck wird regelmäßig aktualisiert
  ✓ notes enthalten Besonderheiten (Verschädigungen, Reparaturen, etc.)
```

#### Methoden:

**Factory Methods:**
- `static addCopy(mediaId, barcode, condition = Excellent) → MediaCopy`
  - Validiert barcode ist UNIQUE
  - Setzt mediaId, barcode (IMMUTABLE)
  - Setzt availabilityStatus = Available, condition, acquisitionDate = TODAY
  - Use Case: US-007 (Admin-Portal)

**Core Business Methods:**
- `updateAvailabilityStatus(newStatus) → void`
  - Validiert State Transition erlaubt
  - Setzt availabilityStatus = newStatus, lastModifiedAt = NOW
  - Use Case: Event-Handler (MediaCheckedOut, MediaReturned, etc.)

- `markDamaged(notes) → void`
  - Setzt availabilityStatus = Damaged (terminal)
  - Setzt condition = Poor
  - Fügt notes hinzu mit "Marked damaged: " + notes
  - Domain Event: MediaCopyDamaged (optional)

- `updateCondition(newCondition) → void`
  - Validiert newCondition >= current condition (kann nur verschlechtern)
  - Setzt condition = newCondition
  - Wenn newCondition == Poor: Suggestion Damaged markieren

- `updateShelfLocation(location) → void`
  - Setzt shelfLocation, lastModifiedAt = NOW

- `updateInventory(lastCheckDate) → void`
  - Setzt lastInventoryCheck = lastCheckDate, lastModifiedAt = NOW

**Queries:**
- `isAvailable() → Boolean` (status == Available AND condition != Poor)
- `isDamaged() → Boolean`
- `getStatus() → AvailabilityStatus`
- `getBarcode() → String`

---

## 👤 USER CONTEXT (Generic Subdomain)

### Aggregat 7: User (Aggregate Root)

**Identität:** `userId: UUID`  
**Business Key:** `schoolIdentity` (UNIQUE)  
**Lebenszyklus:** Created → Active → Inactive (via Admin)  
**Verantwortung:** Nutzer-Identität mit SSO-Mapping

#### Attribute:

| Attribut | Typ | Constraints | Beschreibung |
|----------|-----|-----------|-------------|
| `userId` | UUID | NOT NULL, UNIQUE, PK | Eindeutige Kennung |
| `schoolIdentity` | String | NOT NULL, UNIQUE | SSO-Email (z.B. "max.mustermann@schulbib.de") |
| `firstName` | String | max 100 | Vorname |
| `lastName` | String | max 100 | Nachname |
| `email` | String | RFC5322 format | Kontakt-Email |
| `userGroup` | Enum(UserGroup) | NOT NULL | Student, Teacher, Librarian (vom SSO) |
| `borrowingLimit` | Integer | >= 1 | Max. gleichzeitige Loans |
| `isActive` | Boolean | Default: true | Aktiv/Gesperrt |
| `registrationDate` | LocalDate | NOT NULL | Erste Anmeldung |
| `lastLoginAt` | Instant | Nullable | Letzter Login |
| `createdAt` | Instant | NOT NULL | Erstellungszeitstempel |
| `lastModifiedAt` | Instant | NOT NULL | Letzter Änderungszeitstempel |

#### Enthaltene Entities:
- **KEINE**

#### Referenzierte Aggregate:
- **KEINE**

#### Value Objects:
- **UserProfile** → Zusammenfassung (firstName, lastName, email, registrationDate) - IMMUTABLE nach Erstellung
- **SchoolIdentity** → Email mit SSO-Format-Validierung - IMMUTABLE
- **UserGroup** → Enum (Student, Teacher, Librarian) - READ-ONLY vom SSO
- **BorrowingLimit** → PositiveInteger (abhängig von UserGroup)
- **ContactInfo** → Email + optional Phone

#### Geschäftsregeln (Invarianten):

```
INVARIANTE 1: SSO-Integration
  ✓ schoolIdentity MUSS eindeutig sein (vom SSO garantiert)
  ✓ schoolIdentity ist IMMUTABLE (kann nicht geändert werden)
  ✓ userGroup wird VOM SSO BESTIMMT, NICHT manuell editierbar
  ✓ userGroup-Änderung erfolgt nur über SSO-Sync

INVARIANTE 2: UserProfile Immutability
  ✓ UserProfile ist IMMUTABLE nach Erstellung
  ✓ firstName/lastName können nachträglich korrigiert werden (für Admin-Fehlerkorrektur)
  ✓ email kann sich ändern (Kontakt-Adresse)

INVARIANTE 3: Borrowing Limits
  ✓ borrowingLimit hängt von userGroup ab:
    - Student: 5 (konfigurierbar)
    - Teacher: 10 (konfigurierbar)
    - Librarian: 999 (unbegrenzt)
  ✓ borrowingLimit wird automatisch angepasst wenn userGroup ändert

INVARIANTE 4: Activity Status
  ✓ isActive = false → Keine Ausleihe möglich
  ✓ isActive = true → volle Funktionalität
  ✓ Nur Admin darf isActive ändern

INVARIANTE 5: Email-Validierung
  ✓ email MUSS RFC5322-konform sein
  ✓ schoolIdentity und email können unterschiedlich sein
  ✓ schoolIdentity ist primäre Authentifizierungs-ID
```

#### Methoden:

**Factory Methods:**
- `static createUserFromSSO(schoolIdentity, firstName, lastName, email, userGroup) → User`
  - Validiert schoolIdentity ist UNIQUE
  - Validiert userGroup ist gültig (Student, Teacher, Librarian)
  - Generiert userId, setzt schoolIdentity (IMMUTABLE)
  - Setzt userGroup (READ-ONLY vom SSO)
  - Berechnet borrowingLimit basierend auf userGroup
  - Setzt isActive = true, registrationDate = TODAY
  - Use Case: US-001 (SSO First Login)

**Core Business Methods:**
- `updateProfile(firstName, lastName, email) → void`
  - Nur Admin darf ändern (Authorization außerhalb dieses Aggregats)
  - Aktualisiert firstName, lastName, email (mit RFC5322-Validierung)
  - Setzt lastModifiedAt = NOW
  - Use Case: US-001 (Admin-Portal)

- `deactivate() → void`
  - Setzt isActive = false, lastModifiedAt = NOW
  - Effekt: Keine neuen Loans möglich
  - Use Case: Admin-Portal

- `activate() → void`
  - Setzt isActive = true, lastModifiedAt = NOW

- `recordLogin() → void`
  - Setzt lastLoginAt = NOW, lastModifiedAt = NOW
  - Use Case: SSO-Authentifizierung

- `syncWithSSO(userGroupFromSSO, firstNameFromSSO, lastNameFromSSO) → void`
  - Wenn userGroup geändert: aktualisiert und berechnet borrowingLimit neu
  - Aktualisiert firstName/lastName falls vom SSO geliefert
  - schoolIdentity bleibt IMMUTABLE
  - Use Case: SSO-Sync (regelmäßig)

- `calculateBorrowingLimit(userGroup) → Integer` (private)
  - Student → 5, Teacher → 10, Librarian → 999

**Queries:**
- `isActive() → Boolean`
- `getBorrowingLimit() → Integer`
- `getUserGroup() → UserGroup`
- `getSchoolIdentity() → String`
- `getEmail() → String`

---

## 🔔 NOTIFICATION CONTEXT (Supporting Subdomain)

### Aggregat 8: Notification (Aggregate Root)

**Identität:** `notificationId: UUID`  
**Business Key:** `eventId + userId` (kombiniert, für Deduplication)  
**Lebenszyklus:** Pending → Sent | Failed  
**Verantwortung:** Nachrichtenversand mit Retry-Logik und Audit

#### Attribute:

| Attribut | Typ | Constraints | Beschreibung |
|----------|-----|-----------|-------------|
| `notificationId` | UUID | NOT NULL, UNIQUE, PK | Eindeutige Kennung |
| `userId` | UUID | NOT NULL, FK | Empfänger (User Context) |
| `recipientEmail` | String | NOT NULL, RFC5322 | Wohin senden |
| `channel` | Enum(NotificationChannel) | NOT NULL | Email, Push |
| `type` | Enum(NotificationType) | NOT NULL | CheckoutConfirmation, ReservationReady, ReminderUpcoming, etc. |
| `subject` | String | max 200 | Email-Betreff |
| `body` | String | max 5000 | HTML-Inhalt (aus Template) |
| `status` | Enum(NotificationStatus) | NOT NULL | Pending, Sent, Failed |
| `sentAt` | Instant | Nullable | Zeitpunkt Versand |
| `failureReason` | String | Nullable, max 500 | Fehler-Details |
| `retryCount` | Integer | Default: 0 | Anzahl Versuche |
| `maxRetries` | Integer | Default: 3 | Max. Versuche |
| `eventId` | String | NOT NULL | Ref. zum auslösenden Event (Deduplication) |
| `createdAt` | Instant | NOT NULL | Erstellungszeitstempel |

#### Enthaltene Entities:
- **KEINE**

#### Referenzierte Aggregate (über ID):
- **User** (userId)

#### Value Objects:
- **NotificationChannel** → Enum (Email, Push)
- **NotificationType** → Enum (CheckoutConfirmation, ReservationReady, ReminderUpcoming, ReminderOverdue, ReminderEscalation)
- **NotificationTemplate** → String mit Placeholders ({{mediaTitle}}, {{dueDate}}, {{pickupDeadline}})
- **RecipientInfo** → userId + email
- **NotificationStatus** → Enum

#### Geschäftsregeln (Invarianten):

```
INVARIANTE 1: Retry-Logik
  ✓ retryCount <= maxRetries
  ✓ status = Failed IFF retryCount = maxRetries AND letzte Sendung fehlgeschlagen
  ✓ Exponential backoff: retry_delay = 2^retryCount Minuten

INVARIANTE 2: Deduplication
  ✓ Nur 1 Notification pro (eventId + userId + type) erlaubt
  ✓ Mehrfaches Auftreten des gleichen Events → nur 1 Mail

INVARIANTE 3: Status-Übergänge
  ✓ Pending → Sent (bei erfolgreichem Versand)
  ✓ Pending → Failed (nach maxRetries Versuche)
  ✓ Sent und Failed sind terminal

INVARIANTE 4: Timestamps
  ✓ sentAt DARF NUR gesetzt sein wenn status = Sent
  ✓ sentAt >= createdAt
  ✓ failureReason DARF NUR gesetzt sein wenn status = Failed

INVARIANTE 5: Fallback-Kanal
  ✓ channel = Email ist immer verfügbar (Fallback)
  ✓ Push ist optional (kann deaktiviert sein)
```

#### Methoden:

**Factory Methods:**
- `static createNotification(userId, recipientEmail, channel, type, subject, body, eventId) → Notification`
  - Validiert keine Notification für (eventId, userId, type) existiert
  - Validiert recipientEmail RFC5322 format
  - Generiert notificationId
  - Setzt status = Pending, retryCount = 0, maxRetries = 3
  - Use Case: US-005 (Event-Handler)

**Core Business Methods:**
- `markSent() → void`
  - Validiert status == Pending
  - Setzt status = Sent, sentAt = NOW, failureReason = null
  - Use Case: Mail-Service Success Callback

- `recordFailure(reason) → void`
  - Erhöht retryCount++
  - Setzt failureReason = reason
  - Wenn retryCount >= maxRetries: status = Failed, sonst bleibt Pending
  - Use Case: Mail-Service Error Callback

- `retry() → void`
  - Validiert status == Pending AND retryCount < maxRetries
  - Retry-Logic wird vom Infrastructure Layer gehandlet
  - Use Case: Retry-Job

**Queries:**
- `isPending() → Boolean`
- `isFailed() → Boolean`
- `canRetry() → Boolean` (status == Pending AND retryCount < maxRetries)
- `getEventId() → String`

---

## ⏰ REMINDING CONTEXT (Supporting Subdomain)

### Aggregat 9: ReminderCampaign (Aggregate Root)

**Identität:** `campaignId: UUID`  
**Business Key:** `executionDate` (quasi-UNIQUE pro Tag)  
**Lebenszyklus:** Running → Completed | Failed  
**Verantwortung:** Batch-Execution von täglichen Reminder-Checks

#### Attribute:

| Attribut | Typ | Constraints | Beschreibung |
|----------|-----|-----------|-------------|
| `campaignId` | UUID | NOT NULL, UNIQUE, PK | Eindeutige Kennung |
| `executionDate` | LocalDate | NOT NULL | Wann ausgeführt |
| `executionTime` | LocalTime | NOT NULL | Uhrzeit (z.B. 08:00) |
| `totalLoansChecked` | Integer | >= 0 | Geprüfte Loans |
| `remindersTriggered` | Integer | >= 0 | Ausgelöste Reminders |
| `status` | Enum(CampaignStatus) | NOT NULL | Running, Completed, Failed |
| `startedAt` | Instant | NOT NULL | Start-Zeitstempel |
| `completedAt` | Instant | Nullable | End-Zeitstempel |
| `errorMessage` | String | Nullable, max 1000 | Fehler-Details |

#### Enthaltene Entities:
- **KEINE** (ReminderRecords sind Teil von Notification oder separate Entity)

#### Referenzierte Aggregate:
- **KEINE** (liest Loans, erstellt Notifications)

#### Value Objects:
- **ReminderPolicy** → Regeln (upcomingDays, overdueDays, escalationDays) - Admin-konfiguriert
- **ReminderSchedule** → Cron-Expression (Daily 08:00 etc.)
- **ReminderType** → Enum (Upcoming, Overdue, Escalation)
- **CampaignStatus** → Enum

#### Geschäftsregeln (Invarianten):

```
INVARIANTE 1: Eindeutigkeit & Timing
  ✓ Nur 1 Campaign pro Tag (executionDate quasi-UNIQUE)
  ✓ completedAt > startedAt
  ✓ executionTime ist konfigurierbar in Admin Web-App

INVARIANTE 2: Status-Übergänge
  ✓ Running → Completed (wenn alle Loans geprüft UND alle Events publiziert)
  ✓ Running → Failed (bei unbehebbarem Fehler)
  ✓ Completed und Failed sind terminal

INVARIANTE 3: Metrics
  ✓ totalLoansChecked >= remindersTriggered (nicht alle Loans triggern Reminder)
  ✓ remindersTriggered >= 0 (kann auch 0 sein)

INVARIANTE 4: Deduplication
  ✓ Kein doppelter Reminder für gleichen Loan am gleichen Tag
  ✓ Deduplication via eventId in Notification Context

INVARIANTE 5: Error Handling
  ✓ errorMessage DARF NUR gesetzt sein wenn status = Failed
  ✓ Bei Fehler: Campaign.status = Failed, errorMessage geloggt
  ✓ Fehler in Event-Publishing führt zu Retry (Outbox Pattern)
```

#### Methoden:

**Factory Methods:**
- `static startCampaign(executionDate = TODAY, executionTime, reminderPolicy) → ReminderCampaign`
  - Generiert campaignId
  - Setzt executionDate, status = Running, startedAt = NOW
  - Initialisiert totalLoansChecked = 0, remindersTriggered = 0
  - Use Case: US-008 (Scheduled Job täglich um 08:00)

**Core Business Methods:**
- `addLoanChecked() → void`
  - Erhöht totalLoansChecked++
  - Use Case: Internal (pro Loan gescannt)

- `addReminderTriggered() → void`
  - Erhöht remindersTriggered++
  - Use Case: Internal (pro getriggertem Reminder)

- `complete() → void`
  - Validiert status == Running
  - Setzt status = Completed, completedAt = NOW
  - Use Case: Job-Ende

- `fail(errorMessage) → void`
  - Validiert status == Running
  - Setzt status = Failed, errorMessage, completedAt = NOW
  - Loggt Error für Admin
  - Use Case: Job-Fehler

**Queries:**
- `isRunning() → Boolean`
- `getTotalChecked() → Integer`
- `getTotalTriggered() → Integer`
- `getDurationInMinutes() → Integer` (berechnet aus completedAt - startedAt)

---

## 📊 Zusammenfassung: Aggregate-Übersicht

| Context | Aggregate Root | PK | Business Key | Entities | Value Objects |
|---------|---|---|---|---|---|
| **Lending** | Loan | loanId | (userId, mediaId) | - | DueDate, LoanPolicy, RenewalPolicy |
| **Lending** | Reservation | reservationId | (userId, mediaId) | - | ExpiryDate, ReservationPolicy |
| **Lending** | PreReservation | preReservationId | (userId, mediaId) | - | WaitlistPosition |
| **Lending** | ClassSet | classSetId | (teacherUserId, className) | SetMember | ClassSetPolicy, SetCompleteness |
| **Catalog** | Media | mediaId | title + author | - | MediaMetadata, ISBN, MediaCategory |
| **Catalog** | MediaCopy | copyId | barcode | - | Barcode, ShelfLocation, AvailabilityStatus |
| **User** | User | userId | schoolIdentity | - | UserProfile, SchoolIdentity, UserGroup, BorrowingLimit |
| **Notification** | Notification | notificationId | (eventId, userId, type) | - | NotificationChannel, NotificationType |
| **Reminding** | ReminderCampaign | campaignId | executionDate | - | ReminderPolicy, ReminderSchedule |

---

## 🔗 Cross-Aggregate Referenzen

### Synchrone Queries (Request-Reply):

```
Loan → User (Query: checkEligibility, getBorrowingLimit)
Loan → Media (Query: getTitle)
Loan → MediaCopy (Query: checkAvailability)

Reservation → User (Query: verify user exists)
Reservation → Media (Query: get metadata)
Reservation → MediaCopy (Query: check status)

PreReservation → User (Query: verify user)
PreReservation → Media (Query: estimate availability)

ClassSet → User (Query: verify Teacher)
ClassSet → MediaCopy (Query: check all available)

Notification → User (Query: getEmail)

MediaCopy → Media (Query: getMetadata)
```

### Asynchrone Events (Event-Driven):

```
Lending Context publishes:
  - MediaCheckedOut (→ Catalog, Notification, Reminding)
  - MediaReturned (→ Catalog, Notification, Reminding)
  - MediaReserved (→ Catalog, Notification)
  - PreReservationCreated (→ Notification)
  - PreReservationResolved (→ Notification)
  - LoanRenewed (→ Notification, Reminding)
  - ClassSetCheckedOut (→ Notification)
  - ClassSetReturned (→ Notification)

Reminding Context publishes:
  - ReminderTriggered (→ Notification)

Catalog Context:
  - Subscribes zu: MediaCheckedOut, MediaReturned, MediaReserved (updates AvailabilityStatus)

Notification Context:
  - Subscribes zu: alle Events aus Lending & Reminding
  - Creates & sends Notifications
```

---

## 🔧 Domain Services

Diese Domain Services kapseln domänenlogische Operationen, die mehrere Aggregate (und teils mehrere Bounded Contexts) betreffen. Sie sind zustandslos, orchestrieren Aggregate-Aufrufe und publizieren Domain Events.

### ReservationWaitlistService (zusammengeführt)

- Zweck: Einheitliche Orchestrierung von Reservierungen verfügbarer Medien und Vormerkungen (Waitlist) bei ausgeliehenen Medien inkl. automatischer Promotion bei Rückgabe.
- Beteiligte Aggregate: Reservation, PreReservation, MediaCopy, User, (read: Loan)
- Methoden:
  - reserveOrQueue(userId, mediaId, now):
    → Falls verfügbare Kopie: Reservation anlegen (TTL aus ReservationPolicy), MediaCopy-Status „Reserved“, Event „MediaReserved“.
    → Sonst: PreReservation anlegen mit FIFO-Position, Event „PreReservationCreated“.
    → Use Case: US-003
  - cancel(userId, reservationId?, preReservationId?):
    → Pending-Reservation oder Waitlist-Eintrag stornieren; bei Waitlist: nachfolgende Positionen nachrücken.
    → Use Case: US-003
  - promoteOnReturn(mediaId, now):
    → Bei Rückgabe: ersten Waitlist-Eintrag (PreReservation) zu Reservation promoten (48h TTL), Event „PreReservationResolved“ (+ optional erneut „MediaReserved“), Positions-Update übriger Einträge.
    → Use Cases: US-006, US-003
  - collect(reservationId, userId):
    → Reservation als „Collected“ markieren; nachgelagert optional Übergabe an LoanCheckoutService für tatsächlichen Checkout.
    → Use Case: US-003 (Abholung)
  - expirePending(now):
    → Abgelaufene Reservations auf „Expired“ setzen, MediaCopy freigeben.
    → Use Case: US-003 (TTL)
- Geschäftsregeln: Ein User hat max. 1 aktive Reservation/Vormerkung je Medium; FIFO-Garantie bei PreReservations; Idempotenz bei mehrfachen Events; Konsistenz via transaktionaler Outbox.

### LoanCheckoutService

- Zweck: Ausleihe orchestrieren (Eligibility, Verfügbarkeit, Fälligkeit)
- Beteiligte Aggregate: Loan, MediaCopy, User, Media
- Methoden: checkout(userId, mediaCopyBarcode, now), validateUserEligibility(userId), calculateDueDate(userGroup, loanPolicy)
- Use Cases: US-004; Events: MediaCheckedOut

### LoanReturnService

- Zweck: Rückgabe verbuchen, Überfälligkeit berechnen, Waitlist-Promotion anstoßen
- Beteiligte Aggregate: Loan, MediaCopy, (ReservationWaitlistService)
- Methoden: returnByBarcode(barcode, returnDate), finalizeLoanReturn(loan), processWaitlist(mediaId)
- Use Cases: US-006, US-003; Events: MediaReturned

### LoanRenewalService

- Zweck: Verlängerungen gem. Policy/Waitlist prüfen und durchführen
- Beteiligte Aggregate: Loan, PreReservation (read via Service)
- Methoden: renew(loanId, renewalPolicy, loanPolicy), isEligible(loanId)
- Use Cases: US-003; Events: LoanRenewed

### ClassSetOrchestrationService

- Zweck: Spezielle Flows für Klassensätze inkl. Teilrückgaben
- Beteiligte Aggregate: ClassSet, MediaCopy, User
- Methoden: checkoutClassSet(teacherUserId, className, barcodes), markMemberReturned(barcode), markMemberMissing(barcode), returnClassSet(classSetId)
- Use Cases: US-009, US-006; Events: ClassSetCheckedOut, ClassSetReturned

### RemindingEvaluationService

- Zweck: Aktive Loans gegen ReminderPolicy auswerten, Kampagnen führen
- Beteiligte Aggregate: ReminderCampaign, Loan (read)
- Methoden: runDailyCampaign(policy, atTime), evaluateLoan(loan, policy), completeCampaign(campaignId), failCampaign(campaignId, error)
- Use Cases: US-008; Events: ReminderTriggered

### NotificationComposerService

- Zweck: Notifications aus Domain-Events erstellen (Dedup, Templates)
- Beteiligte Aggregate: Notification, User (Präferenzen)
- Methoden: composeFromEvent(event, type, templates), deduplicate(eventId, userId, type), queue(notification)
- Use Cases: US-005

### CatalogInventoryService

- Zweck: Bestandsverwaltung, Schäden, Archivierung, Vorschläge
- Beteiligte Aggregate: Media, MediaCopy
- Methoden: addMediaWithCopies(metadata, copies), addCopy(mediaId, barcode), markDamaged(barcode, notes), archiveMedia(mediaId), suggestWeedingCandidates(since)
- Use Cases: US-007

### SSOUserProvisioningService

- Zweck: Provisionierung/Sync mit Schul-SSO inkl. Domänen-Validierung
- Beteiligte Aggregate: User
- Methoden: provisionOnFirstLogin(schoolIdentity, attrs), syncFromSSO(userId, attrs), validateEmailDomain(email, allowedDomains)
- Use Cases: US-001

### PolicyConfigurationService

- Zweck: Pflege von Ausleih-/Reservierungs-/Reminder-Policies
- Beteiligte Value Objects: LoanPolicy, ReservationPolicy, RenewalPolicy, ReminderPolicy
- Methoden: updateLoanPolicy(values), updateReservationPolicy(values), updateRenewalPolicy(values), updateReminderPolicy(values), validatePolicyChanges(changes)
- Use Cases: US-012

### ReportingQueryService

- Zweck: Read-Only-Reports über Ausleihe, Bestand, Überfälligkeit
- Beteiligte (read-only): Loan, Media, MediaCopy, User, ReminderCampaign
- Methoden: topBorrowedTitles(range, filters), leastBorrowedTitles(range, filters), usageByGroup(range), overdueSummary(range), export(format)
- Use Cases: US-010

### RecommendationListService

- Zweck: Modellierung und Freigabe von Lehrer-Empfehlungslisten
- Beteiligte Aggregate: (künftiges) RecommendationList, Media, User
- Methoden: createList(teacherId, title), addMedia(listId, mediaId), publishToClass(listId, className), reorder(listId, positions)
- Use Cases: US-011

---

## ✅ Nächster Schritt

**→ Schritt 2.5: Dokumentation der DDD-Bausteine konsolidieren & visualisieren**

Die Domain Services sind identifiziert (inkl. zusammengeführter Reservation/Waitlist). Als Nächstes bereite ich die vollständige Dokumentation zur Visualisierung in Phase 3 auf.
