# Ubiquitous Language Glossar - Digital School Library (Vollständig)

**Version:** 2.0  
**Datum:** 2025-12-17  
**Status:** Complete (Phase 3)  
**Autor:** DDD Software Architect

---

## Versionshistorie

| Datum | Version | Autor | Kommentar |
|---|---:|---|---|
| 2024-12-16 | 0.1 | Team | Erstfassung aus Domain Modeling |
| 2025-12-17 | 2.0 | DDD Architect | Vollständiges Glossar mit allen Bausteinen (Phase 3) |

---

## 📖 Verwendungshinweise

- **Single Source of Truth**: Diese Datei ist die offizielle Quelle für Projektbegriffe
- **Owner**: Jedes Glossar‑Item hat eine verantwortliche Person oder Team
- **Status**: `draft / review / accepted / deprecated`
- **Review-Pflicht**: Domänenexperten müssen Glossar-Updates reviewen
- **Bounded Context Zuordnung**: Jeder Begriff ist einem Context zugeordnet

---

## 🎯 USER CONTEXT (Generic Subdomain)

### Nomen (Substantive)

#### Begriff: User
- **Definition**: Eine eindeutig identifizierte Person im System, authentifiziert über Schul-SSO
- **Bounded Context**: User Context
- **Rolle im System**: Aggregate Root
- **Synonyme / Verwechslungsgefahr**: Nutzer, Benutzer
- **Beispiel(e)**: Max Mustermann (Schüler), Frau Schmidt (Lehrerin)
- **Repräsentation im Code**: class `User`, DB-Tabelle `users`
- **Owner**: User Management Team
- **Status**: accepted
- **Letzte Änderung**: 2025-12-17
- **Referenzen**: US-001, US-004

#### Begriff: SchoolIdentity
- **Definition**: Die eindeutige Schulidentität vom SSO-System (IMMUTABLE)
- **Bounded Context**: User Context
- **Rolle im System**: Value Object
- **Synonyme / Verwechslungsgefahr**: SSO-Email, Schulkennung
- **Beispiel(e)**: `max.mustermann@schulbib.de`
- **Repräsentation im Code**: class `SchoolIdentity`, String-Format Email
- **Owner**: User Management Team
- **Status**: accepted
- **Letzte Änderung**: 2025-12-17
- **Referenzen**: US-001

#### Begriff: UserGroup
- **Definition**: Die Rolle/Kategorie des Users (Student, Teacher, Librarian), vom SSO bestimmt
- **Bounded Context**: User Context
- **Rolle im System**: Value Object (Enum)
- **Synonyme / Verwechslungsgefahr**: Nutzergruppe, Rolle
- **Beispiel(e)**: `Student` (Schüler), `Teacher` (Lehrer), `Librarian` (Bibliothekar)
- **Repräsentation im Code**: enum `UserGroup`
- **Owner**: User Management Team
- **Status**: accepted
- **Letzte Änderung**: 2025-12-17
- **Referenzen**: US-001, US-004

#### Begriff: BorrowingLimit
- **Definition**: Maximale Anzahl gleichzeitig ausgeliehener Medien, abhängig von UserGroup
- **Bounded Context**: User Context
- **Rolle im System**: Value Object
- **Synonyme / Verwechslungsgefahr**: Ausleihgrenze, Ausleih-Limit
- **Beispiel(e)**: Student: 5, Teacher: 10, Librarian: 999
- **Repräsentation im Code**: class `BorrowingLimit`, Integer > 0
- **Owner**: User Management Team
- **Status**: accepted
- **Letzte Änderung**: 2025-12-17
- **Referenzen**: US-004

#### Begriff: UserProfile
- **Definition**: Sammlung aller Metadaten eines Nutzers (firstName, lastName, email, registrationDate)
- **Bounded Context**: User Context
- **Rolle im System**: Value Object
- **Synonyme / Verwechslungsgefahr**: Nutzerprofil, Benutzerdaten
- **Beispiel(e)**: Max Mustermann, max.mustermann@example.com, 2024-01-15
- **Repräsentation im Code**: class `UserProfile`
- **Owner**: User Management Team
- **Status**: accepted
- **Letzte Änderung**: 2025-12-17
- **Referenzen**: US-001

---

### Verben (Aktionen)

#### Begriff: Authenticate
- **Definition**: SSO-Authentifizierung beim Login
- **Bounded Context**: User Context
- **Rolle im System**: Domain Service Method (SSOUserProvisioningService)
- **Beispiel(e)**: `ssoService.authenticate(schoolIdentity, ssoToken)`
- **Repräsentation im Code**: Methode `authenticate()` in `SSOUserProvisioningService`
- **Owner**: User Management Team
- **Status**: accepted
- **Letzte Änderung**: 2025-12-17
- **Referenzen**: US-001

#### Begriff: CreateUserFromSSO
- **Definition**: Neuen User beim First Login anlegen
- **Bounded Context**: User Context
- **Rolle im System**: Factory Method (Aggregat User)
- **Beispiel(e)**: `User.createUserFromSSO(schoolIdentity, firstName, lastName, email, userGroup)`
- **Repräsentation im Code**: static method `createUserFromSSO()` in `User`
- **Owner**: User Management Team
- **Status**: accepted
- **Letzte Änderung**: 2025-12-17
- **Referenzen**: US-001

#### Begriff: UpdateProfile
- **Definition**: Nutzerdaten ändern (nur Admin)
- **Bounded Context**: User Context
- **Rolle im System**: Methode (Aggregat User)
- **Beispiel(e)**: `user.updateProfile(firstName, lastName, email)`
- **Repräsentation im Code**: method `updateProfile()` in `User`
- **Owner**: User Management Team
- **Status**: accepted
- **Letzte Änderung**: 2025-12-17
- **Referenzen**: US-001

#### Begriff: SyncWithSSO
- **Definition**: UserProfile mit SSO synchronisieren (bei jedem Login)
- **Bounded Context**: User Context
- **Rolle im System**: Methode (Aggregat User)
- **Beispiel(e)**: `user.syncWithSSO(userGroupFromSSO, firstNameFromSSO, lastNameFromSSO)`
- **Repräsentation im Code**: method `syncWithSSO()` in `User`
- **Owner**: User Management Team
- **Status**: accepted
- **Letzte Änderung**: 2025-12-17
- **Referenzen**: US-001

---

## 📚 CATALOG CONTEXT (Supporting Subdomain)

### Nomen (Substantive)

#### Begriff: Media
- **Definition**: Ein abstraktes Medienwerk im Katalog (z.B. "Der Herr der Ringe")
- **Bounded Context**: Catalog Context
- **Rolle im System**: Aggregate Root
- **Synonyme / Verwechslungsgefahr**: Medienwerk, Titel
- **Beispiel(e)**: "Der Herr der Ringe" (Buch), "Planet Erde" (DVD)
- **Repräsentation im Code**: class `Media`, DB-Tabelle `media`
- **Owner**: Catalog Team
- **Status**: accepted
- **Letzte Änderung**: 2025-12-17
- **Referenzen**: US-002, US-007

#### Begriff: MediaCopy
- **Definition**: Ein physisches Exemplar eines Media-Werks mit eigenem Barcode
- **Bounded Context**: Catalog Context
- **Rolle im System**: Aggregate Root
- **Synonyme / Verwechslungsgefahr**: Exemplar, Band, Kopie
- **Beispiel(e)**: "Der Herr der Ringe" Exemplar 1 (Barcode SCH-12345)
- **Repräsentation im Code**: class `MediaCopy`, DB-Tabelle `media_copies`
- **Owner**: Catalog Team
- **Status**: accepted
- **Letzte Änderung**: 2025-12-17
- **Referenzen**: US-004, US-006, US-007

#### Begriff: Barcode
- **Definition**: Eindeutige physische Kennung für Scanning (MediaCopy oder User)
- **Bounded Context**: Catalog Context (primär), alle Contexts (verwendet)
- **Rolle im System**: Value Object
- **Synonyme / Verwechslungsgefahr**: Strichcode, ID
- **Beispiel(e)**: `SCH-12345` (custom Format), EAN-13 für ISBN
- **Repräsentation im Code**: class `Barcode`, String mit Validierung
- **Owner**: Catalog Team
- **Status**: accepted
- **Letzte Änderung**: 2025-12-17
- **Referenzen**: US-004, US-006

#### Begriff: AvailabilityStatus
- **Definition**: Aktueller Verfügbarkeitsstatus einer MediaCopy
- **Bounded Context**: Catalog Context
- **Rolle im System**: Value Object (Enum)
- **Synonyme / Verwechslungsgefahr**: Verfügbarkeitsstatus, Status
- **Beispiel(e)**: `Available`, `CheckedOut`, `Reserved`, `OnHold`, `Damaged`
- **Repräsentation im Code**: enum `AvailabilityStatus`
- **Owner**: Catalog Team
- **Status**: accepted
- **Letzte Änderung**: 2025-12-17
- **Referenzen**: US-002, US-004, US-006

#### Begriff: MediaCategory
- **Definition**: Klassifizierung des Mediums (Fiction, NonFiction, Reference)
- **Bounded Context**: Catalog Context
- **Rolle im System**: Value Object (Enum)
- **Synonyme / Verwechslungsgefahr**: Medienart, Gattung, Kategorie
- **Beispiel(e)**: `Fiction` (Belletristik), `NonFiction` (Sachbuch), `Reference` (Nachschlagewerk)
- **Repräsentation im Code**: enum `MediaCategory`
- **Owner**: Catalog Team
- **Status**: accepted
- **Letzte Änderung**: 2025-12-17
- **Referenzen**: US-002, US-007

#### Begriff: ShelfLocation
- **Definition**: Physischer Standort im Regal (z.B. "C2-R3-H5" = Kategorie-Regal-Höhe)
- **Bounded Context**: Catalog Context
- **Rolle im System**: Value Object
- **Synonyme / Verwechslungsgefahr**: Standort, Regalplatz
- **Beispiel(e)**: `C2-R3-H5`, `Fiction-A-1`
- **Repräsentation im Code**: class `ShelfLocation`, String mit Pattern
- **Owner**: Catalog Team
- **Status**: accepted
- **Letzte Änderung**: 2025-12-17
- **Referenzen**: US-002, US-007

#### Begriff: ISBN
- **Definition**: International Standard Book Number (ISBN-13 Format)
- **Bounded Context**: Catalog Context
- **Rolle im System**: Value Object
- **Beispiel(e)**: `978-3-16-148410-0`
- **Repräsentation im Code**: class `ISBN`, String mit ISBN-13 Validierung
- **Owner**: Catalog Team
- **Status**: accepted
- **Letzte Änderung**: 2025-12-17
- **Referenzen**: US-002, US-007

#### Begriff: MediaMetadata
- **Definition**: Beschreibende Informationen eines Mediums (Titel, Autor, Verlag, Jahr, etc.)
- **Bounded Context**: Catalog Context
- **Rolle im System**: Value Object
- **Beispiel(e)**: Titel: "Der Herr der Ringe", Autor: "J.R.R. Tolkien", Jahr: 1954
- **Repräsentation im Code**: class `MediaMetadata`
- **Owner**: Catalog Team
- **Status**: accepted
- **Letzte Änderung**: 2025-12-17
- **Referenzen**: US-002, US-007

#### Begriff: MediaCondition
- **Definition**: Zustand eines Exemplars (Excellent, Good, Fair, Poor)
- **Bounded Context**: Catalog Context
- **Rolle im System**: Value Object (Enum)
- **Beispiel(e)**: `Excellent` (neuwertig), `Poor` (stark abgenutzt)
- **Repräsentation im Code**: enum `MediaCondition`
- **Owner**: Catalog Team
- **Status**: accepted
- **Letzte Änderung**: 2025-12-17
- **Referenzen**: US-007

---

### Verben (Aktionen)

#### Begriff: SearchMedia
- **Definition**: Suche nach Medien im Katalog (Freitext, Filter)
- **Bounded Context**: Catalog Context
- **Rolle im System**: Query (Repository)
- **Beispiel(e)**: `mediaRepository.searchMedia(query, filters)`
- **Repräsentation im Code**: method `searchMedia()` in `MediaRepository`
- **Owner**: Catalog Team
- **Status**: accepted
- **Letzte Änderung**: 2025-12-17
- **Referenzen**: US-002

#### Begriff: AddMedia
- **Definition**: Neues Medium zum Katalog hinzufügen
- **Bounded Context**: Catalog Context
- **Rolle im System**: Factory Method (Aggregat Media)
- **Beispiel(e)**: `Media.addMedia(title, author, isbn, category, publicationYear)`
- **Repräsentation im Code**: static method `addMedia()` in `Media`
- **Owner**: Catalog Team
- **Status**: accepted
- **Letzte Änderung**: 2025-12-17
- **Referenzen**: US-007

#### Begriff: AddCopy
- **Definition**: Neues Exemplar zu einem Medium hinzufügen
- **Bounded Context**: Catalog Context
- **Rolle im System**: Factory Method (Aggregat MediaCopy) + Methode (Media)
- **Beispiel(e)**: `MediaCopy.addCopy(mediaId, barcode, condition)` + `media.addCopy()`
- **Repräsentation im Code**: static method `addCopy()` in `MediaCopy`, method `addCopy()` in `Media`
- **Owner**: Catalog Team
- **Status**: accepted
- **Letzte Änderung**: 2025-12-17
- **Referenzen**: US-007

#### Begriff: UpdateAvailabilityStatus
- **Definition**: Verfügbarkeitsstatus einer MediaCopy ändern (nur via Events)
- **Bounded Context**: Catalog Context
- **Rolle im System**: Methode (Aggregat MediaCopy)
- **Beispiel(e)**: `mediaCopy.updateAvailabilityStatus(newStatus)`
- **Repräsentation im Code**: method `updateAvailabilityStatus()` in `MediaCopy`
- **Owner**: Catalog Team
- **Status**: accepted
- **Letzte Änderung**: 2025-12-17
- **Referenzen**: US-004, US-006

#### Begriff: MarkDamaged
- **Definition**: MediaCopy als beschädigt markieren (terminal Status)
- **Bounded Context**: Catalog Context
- **Rolle im System**: Methode (Aggregat MediaCopy)
- **Beispiel(e)**: `mediaCopy.markDamaged(notes)`
- **Repräsentation im Code**: method `markDamaged()` in `MediaCopy`
- **Owner**: Catalog Team
- **Status**: accepted
- **Letzte Änderung**: 2025-12-17
- **Referenzen**: US-007

---

## 💳 LENDING CONTEXT (Core Domain)

### Nomen (Substantive) - Loan Aggregate

#### Begriff: Loan
- **Definition**: Ein Ausleihvorgang für einen User und ein Medium mit Fälligkeit
- **Bounded Context**: Lending Context
- **Rolle im System**: Aggregate Root
- **Synonyme / Verwechslungsgefahr**: Ausleihe, Ausleihvorgang
- **Beispiel(e)**: Max Mustermann leiht "Der Herr der Ringe" vom 01.12. bis 22.12.
- **Repräsentation im Code**: class `Loan`, DB-Tabelle `loans`
- **Owner**: Lending Team
- **Status**: accepted
- **Letzte Änderung**: 2025-12-17
- **Referenzen**: US-004, US-006

#### Begriff: DueDate
- **Definition**: Datum bis wann das Medium zurückzugeben ist, berechnet aus LoanPolicy
- **Bounded Context**: Lending Context
- **Rolle im System**: Value Object
- **Synonyme / Verwechslungsgefahr**: Rückgabefrist, Fälligkeitsdatum
- **Beispiel(e)**: 22.12.2024
- **Repräsentation im Code**: class `DueDate`, LocalDate mit Validierung (> checkoutDate)
- **Owner**: Lending Team
- **Status**: accepted
- **Letzte Änderung**: 2025-12-17
- **Referenzen**: US-004, US-005, US-008

#### Begriff: LoanPolicy
- **Definition**: Regeln für Ausleihfristen je UserGroup (konfigurierbar in Admin Web-App)
- **Bounded Context**: Lending Context
- **Rolle im System**: Value Object
- **Synonyme / Verwechslungsgefahr**: Ausleih-Regel, Leihfrist-Regel
- **Beispiel(e)**: Student: 21 Tage, Teacher: 56 Tage, Librarian: 90 Tage (Defaults)
- **Repräsentation im Code**: class `LoanPolicy`
- **Owner**: Lending Team
- **Status**: accepted
- **Letzte Änderung**: 2025-12-17
- **Referenzen**: US-004, US-012

#### Begriff: LoanStatus
- **Definition**: Lebenszyklus-Status eines Loans (Active, Returned, Overdue)
- **Bounded Context**: Lending Context
- **Rolle im System**: Value Object (Enum)
- **Beispiel(e)**: `Active` (ausgeliehen), `Returned` (zurückgegeben), `Overdue` (überfällig)
- **Repräsentation im Code**: enum `LoanStatus`
- **Owner**: Lending Team
- **Status**: accepted
- **Letzte Änderung**: 2025-12-17
- **Referenzen**: US-004, US-006, US-008

#### Begriff: RenewalCount
- **Definition**: Wie oft ein Loan bereits verlängert wurde (max 2x laut RenewalPolicy)
- **Bounded Context**: Lending Context
- **Rolle im System**: Value Object
- **Beispiel(e)**: 0, 1, 2
- **Repräsentation im Code**: class `RenewalCount`, Integer (0-2)
- **Owner**: Lending Team
- **Status**: accepted
- **Letzte Änderung**: 2025-12-17
- **Referenzen**: US-003

#### Begriff: RenewalPolicy
- **Definition**: Regeln für Verlängerungen (maxRenewals, durationDays), konfigurierbar in Admin Web-App
- **Bounded Context**: Lending Context
- **Rolle im System**: Value Object
- **Synonyme / Verwechslungsgefahr**: Verlängerungs-Regel
- **Beispiel(e)**: maxRenewals: 2, durationDays: 21 (Default)
- **Repräsentation im Code**: class `RenewalPolicy`
- **Owner**: Lending Team
- **Status**: accepted
- **Letzte Änderung**: 2025-12-17
- **Referenzen**: US-003, US-012

#### Begriff: OverdueFlag
- **Definition**: Boolean-Flag ob Loan überfällig ist (TODAY > dueDate AND status = Active)
- **Bounded Context**: Lending Context
- **Rolle im System**: Value Object
- **Synonyme / Verwechslungsgefahr**: Überfällig-Markierung
- **Beispiel(e)**: true, false
- **Repräsentation im Code**: Boolean `isOverdue`
- **Owner**: Lending Team
- **Status**: accepted
- **Letzte Änderung**: 2025-12-17
- **Referenzen**: US-006, US-008

#### Begriff: OverdueDays
- **Definition**: Anzahl Tage über Frist (max(0, TODAY - dueDate))
- **Bounded Context**: Lending Context
- **Rolle im System**: Value Object
- **Beispiel(e)**: 3 Tage
- **Repräsentation im Code**: Integer >= 0
- **Owner**: Lending Team
- **Status**: accepted
- **Letzte Änderung**: 2025-12-17
- **Referenzen**: US-006, US-008

---

### Nomen (Substantive) - Reservation Aggregate

#### Begriff: Reservation
- **Definition**: Reservierung eines verfügbaren Mediums mit 48h TTL (ReservationPolicy)
- **Bounded Context**: Lending Context
- **Rolle im System**: Aggregate Root
- **Synonyme / Verwechslungsgefahr**: Reservierung
- **Beispiel(e)**: Max reserviert "Der Herr der Ringe" für Abholung bis 03.12. 14:00
- **Repräsentation im Code**: class `Reservation`, DB-Tabelle `reservations`
- **Owner**: Lending Team
- **Status**: accepted
- **Letzte Änderung**: 2025-12-17
- **Referenzen**: US-003

#### Begriff: ReservationPolicy
- **Definition**: Regeln für Reservierungsdauer (TTL in Stunden/Tagen), konfigurierbar in Admin Web-App
- **Bounded Context**: Lending Context
- **Rolle im System**: Value Object
- **Synonyme / Verwechslungsgefahr**: Reservierungs-Regel
- **Beispiel(e)**: TTL: 48h (Default)
- **Repräsentation im Code**: class `ReservationPolicy`
- **Owner**: Lending Team
- **Status**: accepted
- **Letzte Änderung**: 2025-12-17
- **Referenzen**: US-003, US-012

#### Begriff: ReservationStatus
- **Definition**: Status einer Reservation (Pending, Collected, Expired)
- **Bounded Context**: Lending Context
- **Rolle im System**: Value Object (Enum)
- **Beispiel(e)**: `Pending` (offen), `Collected` (abgeholt), `Expired` (verfallen)
- **Repräsentation im Code**: enum `ReservationStatus`
- **Owner**: Lending Team
- **Status**: accepted
- **Letzte Änderung**: 2025-12-17
- **Referenzen**: US-003

#### Begriff: PickupDeadline
- **Definition**: Deadline bis wann eine Reservation abgeholt werden muss
- **Bounded Context**: Lending Context
- **Rolle im System**: Value Object
- **Synonyme / Verwechslungsgefahr**: Abholzeit, Abholfrist
- **Beispiel(e)**: 03.12.2024 14:00
- **Repräsentation im Code**: class `PickupDeadline`, LocalDateTime
- **Owner**: Lending Team
- **Status**: accepted
- **Letzte Änderung**: 2025-12-17
- **Referenzen**: US-003

---

### Nomen (Substantive) - PreReservation (Waitlist)

#### Begriff: PreReservation
- **Definition**: Vormerkung auf ein verliehenes Medium (FIFO-Queue), wird bei Rückgabe zu Reservation
- **Bounded Context**: Lending Context
- **Rolle im System**: Aggregate Root
- **Synonyme / Verwechslungsgefahr**: Vormerkung, Wartelisten-Eintrag
- **Beispiel(e)**: Max ist Position 2 in Waitlist für "Der Herr der Ringe"
- **Repräsentation im Code**: class `PreReservation`, DB-Tabelle `pre_reservations`
- **Owner**: Lending Team
- **Status**: accepted
- **Letzte Änderung**: 2025-12-17
- **Referenzen**: US-003

#### Begriff: WaitlistPosition
- **Definition**: Position in der FIFO-Queue (> 0)
- **Bounded Context**: Lending Context
- **Rolle im System**: Value Object
- **Synonyme / Verwechslungsgefahr**: Wartelisten-Position
- **Beispiel(e)**: Position 1, Position 5
- **Repräsentation im Code**: class `WaitlistPosition`, Integer > 0
- **Owner**: Lending Team
- **Status**: accepted
- **Letzte Änderung**: 2025-12-17
- **Referenzen**: US-003

#### Begriff: PreReservationStatus
- **Definition**: Status einer PreReservation (Waiting, Resolved, Cancelled)
- **Bounded Context**: Lending Context
- **Rolle im System**: Value Object (Enum)
- **Beispiel(e)**: `Waiting` (wartend), `Resolved` (aufgelöst → Reservation), `Cancelled` (storniert)
- **Repräsentation im Code**: enum `PreReservationStatus`
- **Owner**: Lending Team
- **Status**: accepted
- **Letzte Änderung**: 2025-12-17
- **Referenzen**: US-003

---

### Nomen (Substantive) - ClassSet

#### Begriff: ClassSet
- **Definition**: Sammlung von Medien für eine Schulklasse mit längerer Ausleihfrist (nur Lehrer)
- **Bounded Context**: Lending Context
- **Rolle im System**: Aggregate Root
- **Synonyme / Verwechslungsgefahr**: Klassensatz, Klassenpakete
- **Beispiel(e)**: Frau Schmidt leiht 25x "Faust" für Klasse 10a
- **Repräsentation im Code**: class `ClassSet`, DB-Tabelle `class_sets`
- **Owner**: Lending Team
- **Status**: accepted
- **Letzte Änderung**: 2025-12-17
- **Referenzen**: US-009

#### Begriff: SetMember
- **Definition**: Ein Medium im ClassSet mit eigenem Status (CheckedOut, Returned, Missing)
- **Bounded Context**: Lending Context
- **Rolle im System**: Entity (Teil von ClassSet)
- **Synonyme / Verwechslungsgefahr**: Satz-Exemplar
- **Beispiel(e)**: "Faust" Exemplar SCH-12345 in Klassensatz 10a
- **Repräsentation im Code**: class `SetMember`
- **Owner**: Lending Team
- **Status**: accepted
- **Letzte Änderung**: 2025-12-17
- **Referenzen**: US-009, US-006

#### Begriff: ClassSetPolicy
- **Definition**: Spezialregeln für Klassensätze (längere Ausleihdauer), konfigurierbar in Admin Web-App
- **Bounded Context**: Lending Context
- **Rolle im System**: Value Object
- **Synonyme / Verwechslungsgefahr**: Klassensatz-Regel
- **Beispiel(e)**: Ausleihdauer: 8 Wochen (Default für Lehrer)
- **Repräsentation im Code**: class `ClassSetPolicy`
- **Owner**: Lending Team
- **Status**: accepted
- **Letzte Änderung**: 2025-12-17
- **Referenzen**: US-009, US-012

#### Begriff: SetCompleteness
- **Definition**: Prüfung ob alle SetMembers zurückgegeben wurden
- **Bounded Context**: Lending Context
- **Rolle im System**: Value Object
- **Synonyme / Verwechslungsgefahr**: Satz-Vollständigkeit
- **Beispiel(e)**: vollständig (alle 25 zurück), unvollständig (23 von 25)
- **Repräsentation im Code**: class `SetCompleteness`, Boolean
- **Owner**: Lending Team
- **Status**: accepted
- **Letzte Änderung**: 2025-12-17
- **Referenzen**: US-009, US-006

---

### Verben (Aktionen) - Lending

#### Begriff: CheckOut
- **Definition**: Medium ausleihen (User eligible, Media available)
- **Bounded Context**: Lending Context
- **Rolle im System**: Factory Method (Aggregat Loan) + Domain Service (LoanCheckoutService)
- **Beispiel(e)**: `Loan.checkout(userId, mediaId, barcode, loanPolicy, userGroup)` + `loanCheckoutService.checkout()`
- **Repräsentation im Code**: static method `checkout()` in `Loan`, service `LoanCheckoutService`
- **Owner**: Lending Team
- **Status**: accepted
- **Letzte Änderung**: 2025-12-17
- **Referenzen**: US-004

#### Begriff: Return
- **Definition**: Medium zurückgeben, Overdue berechnen, Waitlist-Promotion
- **Bounded Context**: Lending Context
- **Rolle im System**: Methode (Aggregat Loan) + Domain Service (LoanReturnService)
- **Beispiel(e)**: `loan.return(returnDate)` + `loanReturnService.returnByBarcode(barcode)`
- **Repräsentation im Code**: method `return()` in `Loan`, service `LoanReturnService`
- **Owner**: Lending Team
- **Status**: accepted
- **Letzte Änderung**: 2025-12-17
- **Referenzen**: US-006

#### Begriff: Renew
- **Definition**: Ausleihe verlängern (max 2x, keine PreReservation)
- **Bounded Context**: Lending Context
- **Rolle im System**: Methode (Aggregat Loan) + Domain Service (LoanRenewalService)
- **Beispiel(e)**: `loan.renew(renewalPolicy, loanPolicy, existingPreReservations)` + `loanRenewalService.renew(loanId)`
- **Repräsentation im Code**: method `renew()` in `Loan`, service `LoanRenewalService`
- **Owner**: Lending Team
- **Status**: accepted
- **Letzte Änderung**: 2025-12-17
- **Referenzen**: US-003

#### Begriff: Reserve
- **Definition**: Verfügbares Medium reservieren (48h TTL)
- **Bounded Context**: Lending Context
- **Rolle im System**: Factory Method (Aggregat Reservation) + Domain Service (ReservationWaitlistService)
- **Beispiel(e)**: `Reservation.reserve(userId, mediaId, barcode, reservationPolicy)` + `reservationWaitlistService.reserveOrQueue()`
- **Repräsentation im Code**: static method `reserve()` in `Reservation`, service `ReservationWaitlistService`
- **Owner**: Lending Team
- **Status**: accepted
- **Letzte Änderung**: 2025-12-17
- **Referenzen**: US-003

#### Begriff: PreReserve
- **Definition**: Verliehenes Medium vormerken (Waitlist FIFO)
- **Bounded Context**: Lending Context
- **Rolle im System**: Factory Method (Aggregat PreReservation) + Domain Service (ReservationWaitlistService)
- **Beispiel(e)**: `PreReservation.preReserve(userId, mediaId, currentWaitlistSize)` + `reservationWaitlistService.reserveOrQueue()`
- **Repräsentation im Code**: static method `preReserve()` in `PreReservation`, service `ReservationWaitlistService`
- **Owner**: Lending Team
- **Status**: accepted
- **Letzte Änderung**: 2025-12-17
- **Referenzen**: US-003

#### Begriff: PromoteOnReturn
- **Definition**: Ersten Waitlist-Eintrag zu Reservation promoten bei Rückgabe
- **Bounded Context**: Lending Context
- **Rolle im System**: Domain Service Method (ReservationWaitlistService)
- **Beispiel(e)**: `reservationWaitlistService.promoteOnReturn(mediaId, now)`
- **Repräsentation im Code**: method `promoteOnReturn()` in `ReservationWaitlistService`
- **Owner**: Lending Team
- **Status**: accepted
- **Letzte Änderung**: 2025-12-17
- **Referenzen**: US-003, US-006

#### Begriff: CheckoutClassSet
- **Definition**: Klassensatz ausleihen (nur Lehrer, längere Frist)
- **Bounded Context**: Lending Context
- **Rolle im System**: Factory Method (Aggregat ClassSet) + Domain Service (ClassSetOrchestrationService)
- **Beispiel(e)**: `ClassSet.checkoutClassSet(teacherUserId, className, setMembers, classSetPolicy)` + `classSetService.checkoutClassSet()`
- **Repräsentation im Code**: static method `checkoutClassSet()` in `ClassSet`, service `ClassSetOrchestrationService`
- **Owner**: Lending Team
- **Status**: accepted
- **Letzte Änderung**: 2025-12-17
- **Referenzen**: US-009

---

## 🔔 NOTIFICATION CONTEXT (Supporting Subdomain)

### Nomen (Substantive)

#### Begriff: Notification
- **Definition**: Eine Nachricht an einen User (E-Mail/Push) mit Retry-Logik
- **Bounded Context**: Notification Context
- **Rolle im System**: Aggregate Root
- **Synonyme / Verwechslungsgefahr**: Benachrichtigung, Nachricht
- **Beispiel(e)**: "Dein Medium ist in 3 Tagen fällig"
- **Repräsentation im Code**: class `Notification`, DB-Tabelle `notifications`
- **Owner**: Notification Team
- **Status**: accepted
- **Letzte Änderung**: 2025-12-17
- **Referenzen**: US-005

#### Begriff: NotificationChannel
- **Definition**: Kanal über den notifiziert wird (Email, Push)
- **Bounded Context**: Notification Context
- **Rolle im System**: Value Object (Enum)
- **Beispiel(e)**: `Email` (Standard), `Push` (optional)
- **Repräsentation im Code**: enum `NotificationChannel`
- **Owner**: Notification Team
- **Status**: accepted
- **Letzte Änderung**: 2025-12-17
- **Referenzen**: US-005

#### Begriff: NotificationType
- **Definition**: Art der Benachrichtigung (CheckoutConfirmation, ReminderUpcoming, etc.)
- **Bounded Context**: Notification Context
- **Rolle im System**: Value Object (Enum)
- **Beispiel(e)**: `CheckoutConfirmation`, `ReservationReady`, `ReminderUpcoming`, `ReminderOverdue`
- **Repräsentation im Code**: enum `NotificationType`
- **Owner**: Notification Team
- **Status**: accepted
- **Letzte Änderung**: 2025-12-17
- **Referenzen**: US-005

#### Begriff: NotificationStatus
- **Definition**: Status einer Notification (Pending, Sent, Failed)
- **Bounded Context**: Notification Context
- **Rolle im System**: Value Object (Enum)
- **Beispiel(e)**: `Pending` (wartend), `Sent` (gesendet), `Failed` (fehlgeschlagen)
- **Repräsentation im Code**: enum `NotificationStatus`
- **Owner**: Notification Team
- **Status**: accepted
- **Letzte Änderung**: 2025-12-17
- **Referenzen**: US-005

#### Begriff: NotificationTemplate
- **Definition**: HTML-Template mit Placeholders ({{mediaTitle}}, {{dueDate}}, etc.)
- **Bounded Context**: Notification Context
- **Rolle im System**: Value Object
- **Beispiel(e)**: "Hallo {{firstName}}, dein Medium {{mediaTitle}} ist am {{dueDate}} fällig."
- **Repräsentation im Code**: class `NotificationTemplate`, String
- **Owner**: Notification Team
- **Status**: accepted
- **Letzte Änderung**: 2025-12-17
- **Referenzen**: US-005

---

### Verben (Aktionen)

#### Begriff: CreateNotification
- **Definition**: Notification aus Domain-Event erstellen (mit Deduplication)
- **Bounded Context**: Notification Context
- **Rolle im System**: Factory Method (Aggregat Notification) + Domain Service (NotificationComposerService)
- **Beispiel(e)**: `Notification.createNotification(userId, email, channel, type, subject, body, eventId)` + `notificationComposerService.composeFromEvent()`
- **Repräsentation im Code**: static method `createNotification()` in `Notification`, service `NotificationComposerService`
- **Owner**: Notification Team
- **Status**: accepted
- **Letzte Änderung**: 2025-12-17
- **Referenzen**: US-005

#### Begriff: MarkSent
- **Definition**: Notification als erfolgreich gesendet markieren
- **Bounded Context**: Notification Context
- **Rolle im System**: Methode (Aggregat Notification)
- **Beispiel(e)**: `notification.markSent()`
- **Repräsentation im Code**: method `markSent()` in `Notification`
- **Owner**: Notification Team
- **Status**: accepted
- **Letzte Änderung**: 2025-12-17
- **Referenzen**: US-005

#### Begriff: RecordFailure
- **Definition**: Fehler bei Versand dokumentieren, Retry-Logik
- **Bounded Context**: Notification Context
- **Rolle im System**: Methode (Aggregat Notification)
- **Beispiel(e)**: `notification.recordFailure(reason)`
- **Repräsentation im Code**: method `recordFailure()` in `Notification`
- **Owner**: Notification Team
- **Status**: accepted
- **Letzte Änderung**: 2025-12-17
- **Referenzen**: US-005

---

## ⏰ REMINDING CONTEXT (Supporting Subdomain)

### Nomen (Substantive)

#### Begriff: ReminderCampaign
- **Definition**: Batch-Execution von täglichen Reminder-Checks (1 pro Tag)
- **Bounded Context**: Reminding Context
- **Rolle im System**: Aggregate Root
- **Synonyme / Verwechslungsgefahr**: Reminder-Kampagne, Reminder-Job
- **Beispiel(e)**: Campaign vom 17.12.2024 um 08:00, 150 Loans geprüft, 12 Reminders ausgelöst
- **Repräsentation im Code**: class `ReminderCampaign`, DB-Tabelle `reminder_campaigns`
- **Owner**: Reminding Team
- **Status**: accepted
- **Letzte Änderung**: 2025-12-17
- **Referenzen**: US-008

#### Begriff: ReminderPolicy
- **Definition**: Regeln für Reminder-Zeitpunkte (upcomingDays, overdueDays, escalationDays), konfigurierbar in Admin Web-App
- **Bounded Context**: Reminding Context
- **Rolle im System**: Value Object
- **Synonyme / Verwechslungsgefahr**: Reminder-Regel, Mahnregel
- **Beispiel(e)**: upcomingDays: -3 (T-3), overdueDays: +1 (T+1), escalationDays: +7 (T+7) (Defaults)
- **Repräsentation im Code**: class `ReminderPolicy`
- **Owner**: Reminding Team
- **Status**: accepted
- **Letzte Änderung**: 2025-12-17
- **Referenzen**: US-008, US-012

#### Begriff: ReminderType
- **Definition**: Art des Reminders (Upcoming, Overdue, Escalation)
- **Bounded Context**: Reminding Context
- **Rolle im System**: Value Object (Enum)
- **Beispiel(e)**: `Upcoming` (T-3), `Overdue` (T+1), `Escalation` (T+7)
- **Repräsentation im Code**: enum `ReminderType`
- **Owner**: Reminding Team
- **Status**: accepted
- **Letzte Änderung**: 2025-12-17
- **Referenzen**: US-008

#### Begriff: CampaignStatus
- **Definition**: Status einer ReminderCampaign (Running, Completed, Failed)
- **Bounded Context**: Reminding Context
- **Rolle im System**: Value Object (Enum)
- **Beispiel(e)**: `Running` (läuft), `Completed` (abgeschlossen), `Failed` (fehlgeschlagen)
- **Repräsentation im Code**: enum `CampaignStatus`
- **Owner**: Reminding Team
- **Status**: accepted
- **Letzte Änderung**: 2025-12-17
- **Referenzen**: US-008

#### Begriff: ReminderSchedule
- **Definition**: Cron-Expression für tägliche Ausführung (z.B. 08:00)
- **Bounded Context**: Reminding Context
- **Rolle im System**: Value Object
- **Beispiel(e)**: "0 8 * * *" (täglich um 08:00)
- **Repräsentation im Code**: class `ReminderSchedule`, Cron-String
- **Owner**: Reminding Team
- **Status**: accepted
- **Letzte Änderung**: 2025-12-17
- **Referenzen**: US-008

---

### Verben (Aktionen)

#### Begriff: StartCampaign
- **Definition**: ReminderCampaign starten (täglich via Scheduler)
- **Bounded Context**: Reminding Context
- **Rolle im System**: Factory Method (Aggregat ReminderCampaign) + Domain Service (RemindingEvaluationService)
- **Beispiel(e)**: `ReminderCampaign.startCampaign(executionDate, executionTime, reminderPolicy)` + `remindingService.runDailyCampaign()`
- **Repräsentation im Code**: static method `startCampaign()` in `ReminderCampaign`, service `RemindingEvaluationService`
- **Owner**: Reminding Team
- **Status**: accepted
- **Letzte Änderung**: 2025-12-17
- **Referenzen**: US-008

#### Begriff: EvaluateLoan
- **Definition**: Einzelnen Loan gegen ReminderPolicy prüfen
- **Bounded Context**: Reminding Context
- **Rolle im System**: Domain Service Method (RemindingEvaluationService)
- **Beispiel(e)**: `remindingService.evaluateLoan(loan, policy)`
- **Repräsentation im Code**: method `evaluateLoan()` in `RemindingEvaluationService`
- **Owner**: Reminding Team
- **Status**: accepted
- **Letzte Änderung**: 2025-12-17
- **Referenzen**: US-008

---

## 🎯 Domain Events

### Event: MediaCheckedOut
- **Definition**: Ein Medium wurde ausgeliehen
- **Bounded Context**: Lending Context (Publisher)
- **Auslöser**: LoanCheckoutService nach `Loan.checkout()`
- **Payload**: loanId, userId, mediaId, mediaCopyBarcode, dueDate, userGroup
- **Subscriber**: Catalog Context (update AvailabilityStatus), Notification Context (send confirmation)
- **Repräsentation im Code**: class `MediaCheckedOutEvent`
- **Owner**: Lending Team
- **Status**: accepted
- **Letzte Änderung**: 2025-12-17
- **Referenzen**: US-004

### Event: MediaReturned
- **Definition**: Ein Medium wurde zurückgegeben
- **Bounded Context**: Lending Context (Publisher)
- **Auslöser**: LoanReturnService nach `loan.return()`
- **Payload**: loanId, mediaId, mediaCopyBarcode, returnedDate, isOverdue, overdueDays
- **Subscriber**: Catalog Context (update AvailabilityStatus → Available), Notification Context (confirmation), Reminding Context (clear reminders), Lending Context (process PreReservations)
- **Repräsentation im Code**: class `MediaReturnedEvent`
- **Owner**: Lending Team
- **Status**: accepted
- **Letzte Änderung**: 2025-12-17
- **Referenzen**: US-006, US-003

### Event: MediaReserved
- **Definition**: Ein verfügbares Medium wurde reserviert
- **Bounded Context**: Lending Context (Publisher)
- **Auslöser**: ReservationWaitlistService nach `Reservation.reserve()`
- **Payload**: reservationId, userId, mediaId, mediaCopyBarcode, expiryDate, pickupLocation
- **Subscriber**: Catalog Context (update AvailabilityStatus → Reserved), Notification Context (send pickup info)
- **Repräsentation im Code**: class `MediaReservedEvent`
- **Owner**: Lending Team
- **Status**: accepted
- **Letzte Änderung**: 2025-12-17
- **Referenzen**: US-003

### Event: PreReservationCreated
- **Definition**: Ein verliehenes Medium wurde vorgemerkt
- **Bounded Context**: Lending Context (Publisher)
- **Auslöser**: ReservationWaitlistService nach `PreReservation.preReserve()`
- **Payload**: preReservationId, userId, mediaId, position, estimatedAvailableDate
- **Subscriber**: Notification Context (send waitlist confirmation)
- **Repräsentation im Code**: class `PreReservationCreatedEvent`
- **Owner**: Lending Team
- **Status**: accepted
- **Letzte Änderung**: 2025-12-17
- **Referenzen**: US-003

### Event: PreReservationResolved
- **Definition**: Erste PreReservation wurde zu Reservation promoviert
- **Bounded Context**: Lending Context (Publisher)
- **Auslöser**: ReservationWaitlistService.promoteOnReturn() nach MediaReturned
- **Payload**: preReservationId, reservationId (new), userId, mediaId, pickupDeadline
- **Subscriber**: Notification Context (send pickup notification)
- **Repräsentation im Code**: class `PreReservationResolvedEvent`
- **Owner**: Lending Team
- **Status**: accepted
- **Letzte Änderung**: 2025-12-17
- **Referenzen**: US-003, US-006

### Event: LoanRenewed
- **Definition**: Ausleihe wurde verlängert
- **Bounded Context**: Lending Context (Publisher)
- **Auslöser**: LoanRenewalService nach `loan.renew()`
- **Payload**: loanId, userId, newDueDate, renewalCount, maxRenewalsAllowed
- **Subscriber**: Notification Context (send renewal confirmation), Reminding Context (update reminder schedule)
- **Repräsentation im Code**: class `LoanRenewedEvent`
- **Owner**: Lending Team
- **Status**: accepted
- **Letzte Änderung**: 2025-12-17
- **Referenzen**: US-003

### Event: ClassSetCheckedOut
- **Definition**: Klassensatz wurde ausgeliehen
- **Bounded Context**: Lending Context (Publisher)
- **Auslöser**: ClassSetOrchestrationService nach `ClassSet.checkoutClassSet()`
- **Payload**: classSetId, teacherUserId, className, setMembers[], dueDate
- **Subscriber**: Notification Context (send confirmation)
- **Repräsentation im Code**: class `ClassSetCheckedOutEvent`
- **Owner**: Lending Team
- **Status**: accepted
- **Letzte Änderung**: 2025-12-17
- **Referenzen**: US-009

### Event: ClassSetReturned
- **Definition**: Klassensatz vollständig zurückgegeben
- **Bounded Context**: Lending Context (Publisher)
- **Auslöser**: ClassSetOrchestrationService nach `classSet.returnClassSet()`
- **Payload**: classSetId, teacherUserId, className, returnDate, isComplete
- **Subscriber**: Notification Context (send completion confirmation)
- **Repräsentation im Code**: class `ClassSetReturnedEvent`
- **Owner**: Lending Team
- **Status**: accepted
- **Letzte Änderung**: 2025-12-17
- **Referenzen**: US-009, US-006

### Event: ReminderTriggered
- **Definition**: Reminder wurde ausgelöst (Upcoming, Overdue, Escalation)
- **Bounded Context**: Reminding Context (Publisher)
- **Auslöser**: RemindingEvaluationService.evaluateLoan() während ReminderCampaign
- **Payload**: reminderId, loanId, userId, mediaId, reminderType, daysUntilOrAfterDue, subject
- **Subscriber**: Notification Context (send reminder email/push)
- **Repräsentation im Code**: class `ReminderTriggeredEvent`
- **Owner**: Reminding Team
- **Status**: accepted
- **Letzte Änderung**: 2025-12-17
- **Referenzen**: US-008

---

## 📊 Geschäftsregeln - Übersicht

### Regel: User muss aktiv sein für Ausleihe
- **Definition**: Ein User mit isActive = false kann keine neuen Loans/Reservations erstellen
- **Bounded Context**: Lending Context
- **Betroffene Aggregate**: Loan, Reservation
- **Implementierung**: Guard in `LoanCheckoutService.validateUserEligibility()` und `ReservationWaitlistService.reserveOrQueue()`
- **Owner**: Lending Team
- **Status**: accepted
- **Letzte Änderung**: 2025-12-17
- **Referenzen**: US-004, AC: "User muss aktiv sein"

### Regel: BorrowingLimit darf nicht überschritten werden
- **Definition**: countActiveLoansByUser(userId) < User.borrowingLimit
- **Bounded Context**: Lending Context
- **Betroffene Aggregate**: Loan, User
- **Implementierung**: Guard in `LoanCheckoutService.validateUserEligibility()`
- **Owner**: Lending Team
- **Status**: accepted
- **Letzte Änderung**: 2025-12-17
- **Referenzen**: US-004, AC: "max. Anzahl erreicht"

### Regel: DueDate-Berechnung nach LoanPolicy
- **Definition**: dueDate = TODAY + LoanPolicy.durationDays (je UserGroup: Student 21 Tage, Teacher 56 Tage, Librarian 90 Tage - konfigurierbar in Admin Web-App)
- **Bounded Context**: Lending Context
- **Betroffene Aggregate**: Loan
- **Implementierung**: In `LoanCheckoutService.calculateDueDate(userGroup, loanPolicy)`
- **Owner**: Lending Team
- **Status**: accepted
- **Letzte Änderung**: 2025-12-17
- **Referenzen**: US-004, US-012

### Regel: Renewal nur bei fehlender PreReservation
- **Definition**: Verlängerung nur möglich wenn keine aktive PreReservation für dieses Medium existiert
- **Bounded Context**: Lending Context
- **Betroffene Aggregate**: Loan, PreReservation
- **Implementierung**: Guard in `loan.renew()` und `LoanRenewalService.isEligible()`
- **Owner**: Lending Team
- **Status**: accepted
- **Letzte Änderung**: 2025-12-17
- **Referenzen**: US-003, AC: "Keine Vormerkung vorhanden"

### Regel: Max. 2 Verlängerungen pro Loan
- **Definition**: renewalCount < RenewalPolicy.maxRenewals (Default: 2, konfigurierbar in Admin Web-App)
- **Bounded Context**: Lending Context
- **Betroffene Aggregate**: Loan
- **Implementierung**: Guard in `loan.renew()`
- **Owner**: Lending Team
- **Status**: accepted
- **Letzte Änderung**: 2025-12-17
- **Referenzen**: US-003, US-012

### Regel: Reservation TTL 48h (ReservationPolicy)
- **Definition**: expiryDate = createdAt + ReservationPolicy.ttl (Default: 48h, konfigurierbar in Admin Web-App)
- **Bounded Context**: Lending Context
- **Betroffene Aggregate**: Reservation
- **Implementierung**: In `Reservation.reserve()` Factory Method
- **Owner**: Lending Team
- **Status**: accepted
- **Letzte Änderung**: 2025-12-17
- **Referenzen**: US-003, US-012

### Regel: FIFO-Garantie bei PreReservations
- **Definition**: position wird sortiert nach createdAt, keine Position-Sprünge
- **Bounded Context**: Lending Context
- **Betroffene Aggregate**: PreReservation
- **Implementierung**: In `PreReservation.preReserve()` und `ReservationWaitlistService.promoteOnReturn()`
- **Owner**: Lending Team
- **Status**: accepted
- **Letzte Änderung**: 2025-12-17
- **Referenzen**: US-003

### Regel: ClassSet nur für Lehrer
- **Definition**: teacherUserId MUSS User mit UserGroup = Teacher sein
- **Bounded Context**: Lending Context
- **Betroffene Aggregate**: ClassSet, User
- **Implementierung**: Guard in `ClassSet.checkoutClassSet()`
- **Owner**: Lending Team
- **Status**: accepted
- **Letzte Änderung**: 2025-12-17
- **Referenzen**: US-009, AC: "Nur Lehrkräfte"

### Regel: ClassSet Return nur wenn vollständig
- **Definition**: classSet.returnClassSet() nur möglich wenn isComplete = true (alle SetMembers zurück)
- **Bounded Context**: Lending Context
- **Betroffene Aggregate**: ClassSet
- **Implementierung**: Guard in `classSet.returnClassSet()`
- **Owner**: Lending Team
- **Status**: accepted
- **Letzte Änderung**: 2025-12-17
- **Referenzen**: US-009, US-006

### Regel: Notification Deduplication
- **Definition**: Nur 1 Notification pro (eventId + userId + type)
- **Bounded Context**: Notification Context
- **Betroffene Aggregate**: Notification
- **Implementierung**: Guard in `Notification.createNotification()`
- **Owner**: Notification Team
- **Status**: accepted
- **Letzte Änderung**: 2025-12-17
- **Referenzen**: US-005

### Regel: Notification Retry-Logik
- **Definition**: retryCount <= maxRetries (Default: 3), exponential backoff
- **Bounded Context**: Notification Context
- **Betroffene Aggregate**: Notification
- **Implementierung**: In `notification.recordFailure()` und Retry-Job
- **Owner**: Notification Team
- **Status**: accepted
- **Letzte Änderung**: 2025-12-17
- **Referenzen**: US-005

### Regel: 1 ReminderCampaign pro Tag
- **Definition**: executionDate ist quasi-UNIQUE, nur 1 Campaign pro Tag
- **Bounded Context**: Reminding Context
- **Betroffene Aggregate**: ReminderCampaign
- **Implementierung**: DB-Constraint + Guard in `ReminderCampaign.startCampaign()`
- **Owner**: Reminding Team
- **Status**: accepted
- **Letzte Änderung**: 2025-12-17
- **Referenzen**: US-008

---

## 🔧 Governance & Best Practices

### Review-Prozess
- ✅ **Owner festlegen**: Für jeden neuen Begriff eine verantwortliche Person/Team benennen
- ✅ **Definition of Done**: Glossar‑Updates sind Teil der DoD für Features mit neuen Begriffen
- ✅ **Review-Pflicht**: Domänenexperten müssen Glossar-Updates reviewen (PR mit Label `glossary-update`)
- ✅ **Code-Beispiele**: Kurze Code‑Snippets zeigen, wie Begriffe im Code repräsentiert werden

### Status-Management
- ✅ **Status-Tags**: Nutze `draft / review / accepted / deprecated` und halte Änderungsdatum aktuell
- ✅ **Bounded Context Zuordnung**: Jeder Begriff muss einem oder mehreren Contexts zugeordnet sein
- ✅ **Konsistenz prüfen**: Bei Context-übergreifenden Begriffen auf unterschiedliche Bedeutungen achten
- ✅ **Versionierung**: Änderungshistorie pflegen für Nachvollziehbarkeit

### Tooling
- ✅ **CSV/Excel-Export**: Für nicht‑technische Stakeholder
- ✅ **JSON-Export**: Für automatisierte Prüfungen und Generierung (glossary.json)

---

## 📞 Hilfe & Unterstützung

Bei Fragen zur Pflege des Glossars:
- **Domain Owner**: Lending Team (Core Domain), Catalog Team, User Management Team
- **Tech Lead**: Software Architect
- **Prozess**: Siehe `.github/instructions/` für Coding Guidelines

---

**Letzte Aktualisierung**: 2025-12-17
**Status**: Complete (Phase 3)
