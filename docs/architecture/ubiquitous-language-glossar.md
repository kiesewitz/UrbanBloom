# Ubiquitous Language Glossar - Digitale Schulbibliothek

**Version:** 1.0  
**Datum:** 2024-12-16  
**Status:** Draft für Taktisches Design

---

## 📖 Allgemeine Domänen-Begriffe

Diese Begriffe sind **domänenweit** bekannt und haben in jedem Context die gleiche Bedeutung:

| Begriff | Definition | Kontext | Kategorisierung |
|---------|----------|---------|-----------------|
| **User** | Eine eindeutig identifizierte Person im System | Alle | Entity |
| **Media** | Ein Medienwerk im Katalog (Buch, DVD, etc.) | Catalog, Lending | Aggregate Root |
| **MediaCopy** | Ein physisches Exemplar eines Media-Werks | Catalog, Lending | Entity |
| **Loan** | Ein Ausleihvorgang | Lending | Aggregate Root |
| **Reservation** | Reservierung eines verfügbaren Mediums | Lending | Aggregate Root |
| **Barcode** | Eindeutige Kennung für User oder Media | Alle | Value Object |
| **UserGroup** | Rolle des Users (Student, Teacher, Librarian) | User, Lending | Enum / Value Object |

---

## 🎯 USER CONTEXT - Ubiquitous Language

### Nomen (Substantive)

| Begriff | Synonym | Definition | Type | Constraints |
|---------|---------|-----------|------|-------------|
| **User** | - | Eine authentifizierte Person | Entity | PK: userId, eindeutig |
| **UserProfile** | Nutzerprofil | Sammlung der Benutzerdaten | Entity | Tied to User (1:1) |
| **SchoolIdentity** | Schulkennung | Die eindeutige ID vom SSO | Value Object | Format: `vorname.nachname@schulbib.de` |
| **UserGroup** | Nutzergruppe, Rolle | Kategorisierung des Users | Enum | Student, Teacher, Librarian |
| **BorrowingLimit** | Ausleihgrenze | Max. Anzahl gleichzeitig ausgeliehener Medien | Value Object | Typ: Integer, > 0 |
| **ContactInfo** | Kontaktdaten | E-Mail, Optional: Telefon | Value Object | E-Mail: RFC5322 |
| **RegistrationDate** | - | Datum der ersten Anmeldung | Value Object | Type: LocalDate |

### Verben (Aktionen)

| Verb | Aktion | Kontext | Resultat |
|-----|--------|---------|----------|
| **Authenticate** | SSO-Authentifizierung | User Login | User ist validated |
| **CreateUser** | Neuer Benutzer wird erstellt | Bei First Login | User existiert im System |
| **UpdateProfile** | Nutzerdaten ändern | User selbst oder Admin | UserProfile updated |
| **CheckEligibility** | Prüfung ob User ausleihen darf | Vor Checkout | Boolean (valid/invalid) |
| **GetBorrowingLimit** | Grenze abrufen | Lending Context | Integer |

### Geschäftsregeln (Invarianten)

- ✅ **Authentifizierung:** Nur SSO erlaubt, keine lokalen Passwörter
- ✅ **Eindeutigkeit:** SchoolIdentity ist eindeutig pro User
- ✅ **UserGroup:** Wird vom SSO gelesen, nicht manuell geändert
- ✅ **BorrowingLimit:** Abhängig von UserGroup (Student: 5, Teacher: 10, Librarian: unlimited)
- ✅ **Aktiv-Sein:** User muss aktiv sein um auszuleihen
- ✅ **SSO-Sync:** UserProfile wird mit SSO synchronisiert (bei jedem Login)

### Integrationen

| Richtung | Partner Context | Muster | Payload |
|----------|-----------------|--------|---------|
| ← Input | External SSO | Synchron (ACL) | SchoolIdentity, UserGroup, mail |
| → Output | Lending Context | Synchron (Query) | userId, BorrowingLimit, UserGroup |
| → Output | Notification Context | Synchron (Query) | userId, mail, preferences |

---

## 🏷️ CATALOG CONTEXT - Ubiquitous Language

### Nomen (Substantive)

| Begriff | Synonym | Definition | Type | Constraints |
|---------|---------|-----------|------|-------------|
| **Media** | Medienwerk | Ein abstraktes Werk (z.B. "Der Herr der Ringe") | Aggregate Root | PK: mediaId, eindeutig |
| **MediaCopy** | Exemplar, Band | Ein konkretes physisches Medium | Entity | PK: copyId, Barcode eindeutig |
| **Inventory** | Bestand | Die Gesamtheit aller MediaCopies | Aggregate Root | Sammlung von MediaCopy |
| **MediaCategory** | Medienart, Gattung | Klassifizierung (Fiction, NonFiction, Reference) | Enum | Fixed List (MVP) |
| **MediaMetadata** | Metadaten | Beschreibende Infos (Titel, Autor, etc.) | Value Object | Immutable |
| **AvailabilityStatus** | Verfügbarkeitsstatus | Der aktuelle Status | Enum | Available, CheckedOut, Reserved, OnHold |
| **MediaBarcode** | Media-Barcode | Eindeutige ID für Scanning | Value Object | Format: EAN13 oder custom |
| **CopyBarcode** | Copy-Barcode | Eindeutige ID pro Exemplar | Value Object | Format: Custom (z.B. SCH-12345) |
| **ShelfLocation** | Standort, Regal | Physischer Ort im Regal | Value Object | z.B. "C2-R3-H5" (Category-Regal-Höhe) |
| **ISBN** | - | Bücher-Identifikation | Value Object | Format: ISBN-13 |
| **PublicationYear** | Erscheinungsjahr | Jahr der Veröffentlichung | Value Object | Type: Year |

### Verben (Aktionen)

| Verb | Aktion | Kontext | Resultat |
|-----|--------|---------|----------|
| **SearchMedia** | Suche nach Medien | Katalog-Abfrage | [Media] mit AvailabilityStatus |
| **GetMediaDetails** | Detailansicht abrufen | Nutzer-Ansicht | Media + alle MetaData |
| **CheckAvailability** | Prüfung ob verfügbar | Vor Checkout/Reservation | Boolean + ETA |
| **UpdateAvailabilityStatus** | Status ändern | Event Handler | AvailabilityStatus updated |
| **AddMediaCopy** | Neues Exemplar hinzufügen | Admin Bestandsverwaltung | MediaCopy erstellt |
| **RemoveMediaCopy** | Exemplar aussortieren | Admin Bestandsverwaltung | MediaCopy gelöscht/archived |

### Geschäftsregeln (Invarianten)

- ✅ **Availability-Update:** Nur via Events aus Lending Context
- ✅ **Read-Only für Lending:** Dieser Context verwaltet keine Checkout/Return-Logik
- ✅ **Metadata-Immutability:** Beschreibungsdaten ändern sich nicht zwischen Abfragen
- ✅ **Copy-Eindeutigkeit:** Barcode pro Exemplar ist eindeutig
- ✅ **Status-Konsistenz:** Status muss gültig sein (Valid State Machine)

### Integrationen

| Richtung | Partner Context | Muster | Payload |
|----------|-----------------|--------|---------|
| ← Input | Lending Context (Events) | Asynchron | MediaCheckedOut, MediaReturned, MediaReserved |
| → Output | Lending Context (Query) | Synchron | mediaId, AvailabilityStatus, ShelfLocation |
| → Output | Notification Context (Query) | Synchron | mediaId, Media.title, Media.author |

---

## 💳 LENDING CONTEXT - Ubiquitous Language (CORE DOMAIN)

### Nomen (Substantive) - Kernaggregate

| Begriff | Synonym | Definition | Type | Constraints |
|---------|---------|-----------|------|-------------|
| **Loan** | Ausleihe | Ein Ausleihvorgang (Aggregate Root) | AR | PK: loanId, Immutable nach Erstellung |
| **LoanItem** | Ausleih-Zeile | Eine Zeile in einem Loan (ein Medium) | Entity | Part of Loan |
| **DueDate** | Rückgabefrist | Datum bis wann zurückzugeben | Value Object | Type: LocalDate, > today |
| **LoanPolicy** | Ausleih-Regel | Regeln für Ausleihfristen | Value Object | Admin Web-App konfigurierbar (Defaults: Student 21 Tage, Teacher 56 Tage, Librarian 90 Tage) |
| **Renewal** | Verlängerung | Verlängerung der Ausleihfrist | Entity | Max. 2x pro Loan |
| **RenewalCount** | Verlängerungszähler | Wie oft bereits verlängert | Value Object | Type: Integer, max=2 |

### Nomen (Substantive) - Reservierung & Waitlist

| Begriff | Synonym | Definition | Type | Constraints |
|---------|---------|-----------|------|-------------|
| **Reservation** | Reservierung | Reservierung verfügbares Medium (AR) | AR | PK: reservationId, TTL: 48h |
| **PreReservation** | Vormerkung, Wartelisten-Eintrag | Vormerkung verliehenes Medium | Entity | FIFO-Queue, auto→Reservation |
| **Waitlist** | Warteliste | Sammlung aller PreReservations | Value Object | Ordered by creation |
| **ReservationPolicy** | Reservierungs-Regel | Gültigkeitsdauer für Reservation | Value Object | Admin Web-App konfigurierbar (Default: 48h) |
| **WaitlistPosition** | Wartelisten-Position | Position in der Queue | Value Object | Type: Integer, > 0 |
| **PickupDeadline** | Abholzeit | Deadline für Abholung | Value Object | Type: LocalDateTime |

### Nomen (Substantive) - Klassensatz

| Begriff | Synonym | Definition | Type | Constraints |
|---------|---------|-----------|------|-------------|
| **ClassSet** | Klassensatz, Klassenpakete | Sammlung von Medien für eine Klasse | AR | PK: classSetId, für Lehrer |
| **SetMember** | Satz-Exemplar | Ein Medium im ClassSet | Entity | Part of ClassSet |
| **ClassSetPolicy** | Klassensatz-Regel | Spezialregeln (längere Ausleihe) | Value Object | Admin Web-App konfigurierbar (Default: 8 Wochen für Lehrer) |
| **SetCompleteness** | Satz-Vollständigkeit | Prüfung ob alle Exemplare zurück | Value Object | Boolean (vollständig/unvollständig) |

### Nomen (Substantive) - Status & Flags

| Begriff | Synonym | Definition | Type | Constraints |
|---------|---------|-----------|------|-------------|
| **LoanStatus** | Ausleih-Status | Lebenszyklus (Active, Returned, Overdue) | Enum | Gültige States |
| **OverdueFlag** | Überfällig-Flag | Ist Rückgabefrist überschritten? | Boolean | Default: false |
| **OverdueDays** | Überfällig-Tage | Wie viele Tage überfällig | Integer | ≥ 0 |
| **CheckoutDate** | Ausleih-Datum | Wann ausgeliehen | LocalDate | < DueDate |
| **ReturnDate** | Rückgabe-Datum | Wann tatsächlich zurückgegeben | LocalDate | ≥ CheckoutDate |

### Verben (Aktionen)

| Verb | Aktion | Bedingungen | Resultat |
|-----|--------|----------|----------|
| **CheckOut** | Medium ausleihen | User eligible, Media available | Loan created, `MediaCheckedOut` Event |
| **Return** | Medium zurückgeben | Loan active, Media scanned | Loan.status = Returned, `MediaReturned` Event |
| **Reserve** | Verfügbar Medium reservieren | Media available, User not borrowing | Reservation created, `MediaReserved` Event |
| **PreReserve** | Verliehenes Medium vormerken | Media CheckedOut | PreReservation created, added to Waitlist |
| **Renew** | Ausleihe verlängern | RenewalCount < 2, no PreReservation | DueDate extended, `LoanRenewed` Event |
| **CheckoutClassSet** | Klassensatz ausleihen | User.userGroup = Teacher, Set available | ClassSetLoan created |
| **ReturnClassSet** | Klassensatz zurückgeben | All SetMembers scanned | ClassSetLoan.status = Returned |

### Geschäftsregeln (Invarianten) - INVARIANTEN der CORE DOMAIN

```
═══════════════════════════════════════════════════════════════════════════════
INVARIANTE 1: CHECKOUT GUARD RULES
═══════════════════════════════════════════════════════════════════════════════
Bedingung MUSS ALLE erfüllt sein:
  ✓ User.isActive() == true
  ✓ User hat keine OverdueItems
  ✓ countActiveLoansByUser(userId) < User.borrowingLimit
  ✓ Media.availabilityStatus == Available
  ✓ MediaCopy hat keine defects (optional für MVP)
Aktion: Create Loan, update MediaCopy.status = CheckedOut
Event: MediaCheckedOut(loanId, userId, mediaId, dueDate)

═══════════════════════════════════════════════════════════════════════════════
INVARIANTE 2: DUE DATE CALCULATION
═══════════════════════════════════════════════════════════════════════════════
DueDate wird über LoanPolicy (Admin Web-App konfigurierbar, Defaults: Student 21 Tage, Teacher 56 Tage, Librarian 90 Tage) berechnet:
  IF User.userGroup == Student:
    dueDate = TODAY + LoanPolicy.studentDays
  ELSE IF User.userGroup == Teacher:
    dueDate = TODAY + LoanPolicy.teacherDays
  ELSE IF User.userGroup == Librarian:
    dueDate = TODAY + LoanPolicy.librarianDays
  
  IF Media.category == Reference:
    dueDate = TODAY + LoanPolicy.referenceDays (Default: 1 Tag)

═══════════════════════════════════════════════════════════════════════════════
INVARIANTE 3: RETURN PROCESSING
═══════════════════════════════════════════════════════════════════════════════
Return MUSS follow rules:
  ✓ Loan.status == Active (nicht bereits returned)
  ✓ MediaCopy wird aktualisiert
  ✓ Check: TODAY > Loan.dueDate → Overdue Flag setzen
  ✓ Process PreReservations (if exists):
    - Take first from Waitlist
    - Create new Reservation with 48h TTL
    - Event: PreReservationResolved + MediaReserved
  ✓ Publish: MediaReturned(loanId, mediaId, isOverdue, overdueDays)

═══════════════════════════════════════════════════════════════════════════════
INVARIANTE 4: RESERVATION (Available Media)
═══════════════════════════════════════════════════════════════════════════════
Bedingung:
  ✓ Media.availabilityStatus == Available
  ✓ User nicht bereits owner of dieses Media
Aktion:
  - Create Reservation mit expiryDate = NOW + ReservationPolicy.ttl (Admin Web-App konfigurierbar, Default: 48h)
  - Media.availabilityStatus = Reserved
  - Publish: MediaReserved(reservationId, userId, mediaId, pickupDeadline)
Verfallsprozess:
  - Cron-Job prüft täglich
  - IF NOW > expiryDate AND Reservation not collected:
    → Delete Reservation, Media.status = Available

═══════════════════════════════════════════════════════════════════════════════
INVARIANTE 5: PRE-RESERVATION (Waitlist)
═══════════════════════════════════════════════════════════════════════════════
Bedingung:
  ✓ Media.availabilityStatus == CheckedOut
  ✓ User darf maximal 3x auf Waitlist stehen (pro User)
Aktion:
  - Create PreReservation, add to Waitlist (FIFO)
  - Set position = Waitlist.size()
  - Publish: PreReservationCreated(preResId, userId, mediaId, position)
Auflösung:
  - TRIGGER: MediaReturned Event
  - Take first PreReservation
  - Auto-create Reservation (ReservationPolicy.ttl) + convert status
  - Publish: PreReservationResolved + MediaReserved
  - Send Notification: "Your turn! Media is ready for pickup"

═══════════════════════════════════════════════════════════════════════════════
INVARIANTE 6: RENEWAL
═══════════════════════════════════════════════════════════════════════════════
Bedingung:
  ✓ Loan.renewalCount < RenewalPolicy.maxRenewals (Admin Web-App konfigurierbar, Default: 2)
  ✓ NO PreReservation für diesen Media existiert
  ✓ Loan.status == Active
  ✓ TODAY < Loan.dueDate (vor Rückgabefrist)
Aktion:
  - Increment Loan.renewalCount
  - newDueDate = Loan.dueDate + RenewalPolicy.durationDays (Default: identisch zur LoanPolicy-Dauer der UserGroup)
  - Update Loan.dueDate
  - Clear any pending Reminders
  - Publish: LoanRenewed(loanId, userId, newDueDate)

═══════════════════════════════════════════════════════════════════════════════
INVARIANTE 7: CLASSSET SPECIAL HANDLING
═══════════════════════════════════════════════════════════════════════════════
Bedingung (nur Lehrer):
  ✓ User.userGroup == Teacher
  ✓ ALL SetMembers sind Available
  ✓ Teacher hat BorrowingLimit für Satz
Aktion:
  - Create ClassSetLoan (als spezieller Loan-Typ)
  - dueDate = TODAY + 56 days  (längerer Zeitraum)
  - ALL SetMembers.status = CheckedOut
  - Publish: ClassSetCheckedOut(setId, teacherId, dueDate)
Return:
  - Prüfe SetCompleteness (alle Exemplare müssen zurück sein)
  - IF incomplete: Flag setzen, Admin-Benachrichtigung
  - IF complete: Mark as Returned, Publish: ClassSetReturned

═══════════════════════════════════════════════════════════════════════════════
INVARIANTE 8: MEDIA-COPY AVAILABILITY STATE MACHINE
═══════════════════════════════════════════════════════════════════════════════
Valid Transitions:

Available ──CheckOut──> CheckedOut
           ──Reserve──> Reserved

CheckedOut ──Return──> Available (if on-time)
           ──Return──> Available (if overdue, but still available)
           ──PreReserve added──> CheckedOut (no change, in queue)

Reserved ──Collect(→ CheckOut)──> CheckedOut
         ──Expire (48h)──> Available

Waitlist Entry:
  - NOT a state, but queue while in CheckedOut
  - When Return happens → first Waitlist → convert to Reserved (48h)
```

### Integrationen

| Richtung | Partner Context | Muster | Payload |
|----------|-----------------|--------|---------|
| → Output | Catalog Context (Query) | Synchron | mediaId, check availability |
| → Output | User Context (Query) | Synchron | userId, check eligibility |
| → Output | Both Contexts (Events) | Asynchron | MediaCheckedOut, MediaReturned, etc. |

---

## 🔔 NOTIFICATION CONTEXT - Ubiquitous Language

### Nomen (Substantive)

| Begriff | Synonym | Definition | Type | Constraints |
|---------|---------|-----------|------|-------------|
| **Notification** | Benachrichtigung | Eine Nachricht an User | Entity | PK: notificationId |
| **NotificationChannel** | Kanal | E-Mail oder Push | Enum | Email, Push |
| **NotificationTemplate** | Template | HTML-Vorlage | Value Object | Customizable |
| **EventListener** | Event-Listener | Subscription auf Domain Event | Infrastructure | Auto-triggered |
| **NotificationHistory** | Verlauf | Log aller versendeten Notifications | Entity | Audit-Trail |

### Verben (Aktionen)

| Verb | Aktion | Trigger | Resultat |
|-----|--------|---------|----------|
| **OnMediaCheckedOut** | Checkout-Bestätigung | MediaCheckedOut Event | Send confirmation email |
| **OnMediaReserved** | Reservierungs-Info | MediaReserved Event | Send pickup info + deadline |
| **OnPreReservationCreated** | Wartelisten-Info | PreReservationCreated Event | Send position + estimated date |
| **OnReminderTriggered** | Erinnerungs-Email | ReminderTriggered Event | Send reminder email |

### Geschäftsregeln

- ✅ **Event-Driven:** Keine Geschäftslogik, nur Reaction
- ✅ **Deduplication:** Keine doppelten Mails für gleiches Event
- ✅ **Channel:** Email ist Fallback, Push optional
- ✅ **Audit:** Alle Notifications werden geloggt

### Integrationen

| Richtung | Partner Context | Muster | Payload |
|----------|-----------------|--------|---------|
| ← Input | Lending Context (Events) | Asynchron | MediaCheckedOut, MediaReserved, etc. |
| ← Input | Reminding Context (Events) | Asynchron | ReminderTriggered |
| → Output | External: Mail-Service | Asynchron | E-Mail mit Recipient + Template |

---

## ⏰ REMINDING CONTEXT - Ubiquitous Language

### Nomen (Substantive)

| Begriff | Synonym | Definition | Type | Constraints |
|---------|---------|-----------|------|-------------|
| **ReminderPolicy** | Erinnerungs-Regel | Wann Reminders triggered | Value Object | Admin Web-App konfigurierbar (Defaults: T-3, T+1, T+7) |
| **ReminderCampaign** | Erinnerungs-Kampagne | Eine ausgelöste Welle | Entity | Batch Job Result |
| **OverdueReminder** | Überfälligkeits-Erinnerung | Warning für Überfälligkeit | Value Object | Auto-triggered |
| **UpcomingReminder** | Voraus-Erinnerung | Info vor Fristende | Value Object | ReminderPolicy.upcomingDays (Default: 3) |
| **ReminderSchedule** | Zeitplan | Cron-Job Definition | Value Object | Admin Web-App konfigurierbar (Default: daily 09:00) |

### Verben (Aktionen)

| Verb | Aktion | Zeitpunkt | Resultat |
|-----|--------|----------|----------|
| **QueryAllActiveLoans** | Alle aktiven Loans abrufen | Täglich, Scheduler in Admin Web-App konfigurierbar (Default: 08:00) | List[Loan] |
| **CheckReminderPolicy** | Prüfe ob Reminder triggered | Per Loan | Boolean |
| **TriggerReminder** | Erinnerung auslösen | Bei Match | ReminderTriggered Event |
| **EscalateReminder** | Eskalation (7 Tage überfällig) | Nach 7 Tagen | Escalation Event |

### Geschäftsregeln

```
REMINDER POLICY DEFINITION:
═══════════════════════════════════════════════════════════════════════════════

Stage 1 - UPCOMING REMINDER (T-3 Tage):
  ├─ Bedingung: Loan.dueDate == TODAY + ReminderPolicy.upcomingDays (Default: 3 Tage)
  ├─ Message: "Your media is due in 3 days"
  ├─ Channel: Email (optional: Push)
  └─ Frequency: Daily (max 1x per Loan)

Stage 2 - DUE DATE WARNING (T-0, TODAY):
  ├─ Bedingung: Loan.dueDate == TODAY
  ├─ Message: "Please return today!"
  ├─ Channel: Email + Push (if configured)
  └─ Frequency: Daily (max 1x per Loan)

Stage 3 - OVERDUE NOTICE (T+1 Tage):
  ├─ Bedingung: TODAY > Loan.dueDate + ReminderPolicy.overdueDays AND Loan.status == Active (Default: 1 Tag)
  ├─ Message: "Media is 1 day overdue. Please return immediately."
  ├─ Channel: Email (mandatory)
  ├─ Set: Loan.overdueFlag = true
  └─ Frequency: Once

Stage 4 - ESCALATION NOTICE (T+7 Tage):
  ├─ Bedingung: TODAY > Loan.dueDate + ReminderPolicy.escalationDays (Default: 7 Tage)
  ├─ Message: "Media is 7 days overdue. Final reminder!"
  ├─ Channel: Email + Admin CC
  ├─ Action: Create ReminderRecord for Admin Follow-up
  └─ Frequency: Once
```

### Integrationen

| Richtung | Partner Context | Muster | Payload |
|----------|-----------------|--------|---------|
| → Query | Lending Context | Synchron (Read-Only) | getAllActiveLoans() |
| → Output | Notification Context (Events) | Asynchron | ReminderTriggered |

---

## 🔗 Cross-Context Definitionen

### Value Objects (used in multiple Contexts)

| VO | Used in | Definition |
|----|---------|-----------|
| **Barcode** | User, Catalog, Lending | EAN13 oder custom format |
| **DueDate** | Lending, Reminding | LocalDate, immutable |
| **Email** | User, Notification | RFC5322 format |
| **UserGroup** | User, Lending | Enum: Student, Teacher, Librarian |

### Aggregate Roots (pro Context isoliert)

| AR | Context | Responsibility |
|-------|---------|---------------|
| **User** | User Context | Identität & Profil |
| **Media** | Catalog Context | Medienverwaltung |
| **Loan** | Lending Context | Ausleihlogik |
| **Reservation** | Lending Context | Reservierungslogik |
| **ClassSet** | Lending Context | Klassensatz-Verwaltung |

---

## 📝 Glossar-Update-Plan

Diese Version dokumentiert die **Semantik** für das **Taktische Design**:

**Phase 2 (Taktisches Design):**
- Jedes Aggregate Root erhält ein Kapitel
- Value Objects werden mit Validierungsregeln definiert
- Domain Services werden explizit aufgelistet
- Factories und Repositories werden benannt

**Phase 3 (Implementierung):**
- Code-Kommentare referenzieren dieses Glossar
- Tests prüfen Invarianten aus diesem Dokument

