# Traceability Matrix - User Stories ↔ Domain Model

**Version:** 2.0  
**Erstellt:** 2025-01-27  
**Autor:** GitHub Copilot (Claude Sonnet 4.5)  
**Status:** ✅ Complete  
**Letzte Änderung:** 2025-01-27

---

## 1. Übersicht

Diese Traceability Matrix zeigt die Zuordnung zwischen User Stories und Domain Model-Elementen (Aggregaten, Domain Services, Methoden). Sie dient der:

- **Impact-Analyse:** Welche Komponenten sind bei Story-Änderungen betroffen?
- **Vollständigkeits-Validierung:** Sind alle User Stories im Domain-Modell abgedeckt?
- **Kommunikation mit Stakeholdern:** Wie werden fachliche Anforderungen technisch umgesetzt?

**Legende:**
- ✅ **Modelliert:** Vollständig im Domain-Modell abgebildet
- ⏳ **Geplant:** Noch nicht modelliert, für spätere Phase geplant
- 🚧 **In Arbeit:** Teilweise modelliert, wird ergänzt

---

## 2. Traceability Matrix

### 2.1 LENDING CONTEXT (Core Domain)

| User Story ID | Titel | Bounded Context | Aggregate | Methode/Service | Operation | Status |
|---------------|-------|-----------------|-----------|-----------------|-----------|--------|
| **US-004** | Ausleihe durch Administrator | Lending | **Loan** | `Loan.checkout()` | Command | ✅ Modelliert |
| US-004 | | Lending | **LoanCheckoutService** | `checkout(userId, mediaCopyId, policy)` | Command | ✅ Modelliert |
| US-004 | | Lending | | → Validiert User eligibility | Business Rule | ✅ Modelliert |
| US-004 | | Lending | | → Prüft BorrowingLimit | Business Rule | ✅ Modelliert |
| US-004 | | Lending | | → Berechnet DueDate | Business Logic | ✅ Modelliert |
| US-004 | | Lending | | → Publiziert MediaCheckedOut Event | Event | ✅ Modelliert |
| US-004 | | Catalog | **MediaCopy** | `markAsOnLoan()` | Event Handler | ✅ Modelliert |
| | | | | | | |
| **US-006** | Rückgabe durch Administrator | Lending | **Loan** | `markReturned()` | Command | ✅ Modelliert |
| US-006 | | Lending | **LoanReturnService** | `returnLoan(loanId, condition)` | Command | ✅ Modelliert |
| US-006 | | Lending | | → Markiert Loan als returned | Business Logic | ✅ Modelliert |
| US-006 | | Lending | | → Publiziert MediaReturned Event | Event | ✅ Modelliert |
| US-006 | | Lending | | → Triggert promoteOnReturn() | Orchestration | ✅ Modelliert |
| US-006 | | Lending | **ReservationWaitlistService** | `promoteOnReturn(mediaId)` | Command | ✅ Modelliert |
| US-006 | | Lending | | → Holt älteste PreReservation (FIFO) | Business Logic | ✅ Modelliert |
| US-006 | | Lending | | → Promoted zu Reservation | State Transition | ✅ Modelliert |
| US-006 | | Catalog | **MediaCopy** | `markAsReturned(condition)` | Event Handler | ✅ Modelliert |
| US-006 | | Catalog | | → Aktualisiert MediaCondition | Business Logic | ✅ Modelliert |
| | | | | | | |
| **US-003** | Reservierung & Vormerkung | Lending | **Reservation** | `Reservation.create()` | Command | ✅ Modelliert |
| US-003 | | Lending | **PreReservation** | `PreReservation.create()` | Command | ✅ Modelliert |
| US-003 | | Lending | **ReservationWaitlistService** | `reserveOrQueue(userId, mediaId, policy)` | Command | ✅ Modelliert |
| US-003 | | Lending | | → Wenn verfügbar: Reservation.create() | Business Logic | ✅ Modelliert |
| US-003 | | Lending | | → Wenn ausgeliehen: PreReservation.create() | Business Logic | ✅ Modelliert |
| US-003 | | Lending | | → Publiziert MediaReserved Event | Event | ✅ Modelliert |
| US-003 | | Lending | **Reservation** | `collect()` | Command | ✅ Modelliert |
| US-003 | | Lending | **ReservationWaitlistService** | `collect(reservationId)` | Command | ✅ Modelliert |
| US-003 | | Lending | | → Triggert LoanCheckoutService | Orchestration | ✅ Modelliert |
| US-003 | | Lending | **Reservation** | `cancel()` | Command | ✅ Modelliert |
| US-003 | | Lending | **PreReservation** | `cancel()` | Command | ✅ Modelliert |
| US-003 | | Lending | **ReservationWaitlistService** | `cancel(reservationId/preReservationId)` | Command | ✅ Modelliert |
| US-003 | | Lending | **Reservation** | `expire()` | Command | ✅ Modelliert |
| US-003 | | Lending | **ReservationWaitlistService** | `expirePending()` | Batch Job | ✅ Modelliert |
| US-003 | | Catalog | **MediaCopy** | `markAsReserved()` | Event Handler | ✅ Modelliert |
| US-003 | | Catalog | **MediaCopy** | `markAsAvailable()` | Event Handler | ✅ Modelliert |
| | | | | | | |
| **US-009** | Klassensatz-Verwaltung | Lending | **ClassSet** | `ClassSet.create()` | Command | ✅ Modelliert |
| US-009 | | Lending | **ClassSetOrchestrationService** | `checkoutClassSet(teacherId, mediaCopyIds, subject, policy)` | Command | ✅ Modelliert |
| US-009 | | Lending | | → Validiert teacherId ist Teacher | Business Rule | ✅ Modelliert |
| US-009 | | Lending | | → Prüft alle MediaCopies verfügbar | Business Rule | ✅ Modelliert |
| US-009 | | Lending | | → Publiziert ClassSetCheckedOut Event | Event | ✅ Modelliert |
| US-009 | | Lending | **ClassSet** | `returnCopy(mediaCopyId, condition)` | Command | ✅ Modelliert |
| US-009 | | Lending | **ClassSet** | `returnAll(conditions)` | Command | ✅ Modelliert |
| US-009 | | Lending | **ClassSetOrchestrationService** | `returnClassSet(classSetId, conditions)` | Command | ✅ Modelliert |
| US-009 | | Lending | **ClassSetOrchestrationService** | `returnPartial(classSetId, mediaCopyId, condition)` | Command | ✅ Modelliert |
| | | | | | | |
| **US-004** | Ausleihe - Verlängerung | Lending | **Loan** | `renew(policy)` | Command | ✅ Modelliert |
| US-004 | | Lending | **LoanRenewalService** | `renew(loanId, policy)` | Command | ✅ Modelliert |
| US-004 | | Lending | | → Prüft RenewalPolicy.maxRenewals | Business Rule | ✅ Modelliert |
| US-004 | | Lending | | → Prüft keine aktive Reservation | Business Rule | ✅ Modelliert |
| US-004 | | Lending | | → Berechnet neue DueDate | Business Logic | ✅ Modelliert |
| US-004 | | Lending | | → Publiziert LoanRenewed Event | Event | ✅ Modelliert |
| | | | | | | |

---

### 2.2 CATALOG CONTEXT (Supporting Subdomain)

| User Story ID | Titel | Bounded Context | Aggregate | Methode/Service | Operation | Status |
|---------------|-------|-----------------|-----------|-----------------|-----------|--------|
| **US-002** | Katalog-Suche & Detailansicht | Catalog | **Media** | `Media.getTotalCopies()` | Query | ✅ Modelliert |
| US-002 | | Catalog | **Media** | `Media.getAvailableCopies()` | Query | ✅ Modelliert |
| US-002 | | Catalog | **MediaSearchService** | `search(query, filters)` | Query | ✅ Modelliert |
| US-002 | | Catalog | **MediaSearchService** | `findByISBN(isbn)` | Query | ✅ Modelliert |
| US-002 | | Catalog | **MediaCopy** | `isAvailableForLoan()` | Query | ✅ Modelliert |
| US-002 | | Catalog | | → Zeigt Verfügbarkeitsstatus | Query | ✅ Modelliert |
| US-002 | | Catalog | | → Zeigt ShelfLocation | Query | ✅ Modelliert |
| | | | | | | |
| **US-007** | Bestandsverwaltung | Catalog | **Media** | `Media.create()` | Command | ✅ Modelliert |
| US-007 | | Catalog | **MediaCopy** | `MediaCopy.create()` | Command | ✅ Modelliert |
| US-007 | | Catalog | **CatalogInventoryService** | `acquireMedia(isbn, title, author, copyCount)` | Command | ✅ Modelliert |
| US-007 | | Catalog | | → Erstellt Media (falls ISBN neu) | Business Logic | ✅ Modelliert |
| US-007 | | Catalog | | → Erstellt MediaCopies mit Barcodes | Business Logic | ✅ Modelliert |
| US-007 | | Catalog | | → Publiziert MediaAcquired Event | Event | ✅ Modelliert |
| US-007 | | Catalog | **CatalogInventoryService** | `addCopy(mediaId, barcode, shelfLocation)` | Command | ✅ Modelliert |
| US-007 | | Catalog | **MediaCopy** | `updateShelfLocation(newLocation)` | Command | ✅ Modelliert |
| US-007 | | Catalog | **MediaCopy** | `markDamaged(condition)` | Command | ✅ Modelliert |
| US-007 | | Catalog | **MediaCopy** | `markLost()` | Command | ✅ Modelliert |
| US-007 | | Catalog | **CatalogInventoryService** | `withdrawMedia(mediaCopyId, reason)` | Command | ✅ Modelliert |
| | | | | | | |

---

### 2.3 USER CONTEXT (Generic Subdomain)

| User Story ID | Titel | Bounded Context | Aggregate | Methode/Service | Operation | Status |
|---------------|-------|-----------------|-----------|-----------------|-----------|--------|
| **US-001** | Benutzerkonto & SSO | User | **User** | `User.createFromSSO()` | Command | ✅ Modelliert |
| US-001 | | User | **SSOUserProvisioningService** | `authenticateUser(ssoToken)` | Command | ✅ Modelliert |
| US-001 | | User | | → Validiert SSO-Token via SSOAdapter | ACL | ✅ Modelliert |
| US-001 | | User | | → Erstellt/aktualisiert User | Business Logic | ✅ Modelliert |
| US-001 | | User | | → Publiziert UserAuthenticated Event | Event | ✅ Modelliert |
| US-001 | | User | **SSOUserProvisioningService** | `provisionUser(ssoUserId)` | Command | ✅ Modelliert |
| US-001 | | User | **SSOUserProvisioningService** | `syncUserGroup(userId, ssoData)` | Command | ✅ Modelliert |
| US-001 | | User | **User** | `syncWithSSO(ssoData)` | Command | ✅ Modelliert |
| | | | | | | |
| **US-004** | Ausleihe - User Eligibility | User | **User** | `canBorrow()` | Query | ✅ Modelliert |
| US-004 | | User | | → Prüft UserStatus.Active | Business Rule | ✅ Modelliert |
| US-004 | | User | | → Prüft keine Overdues | Business Rule | ✅ Modelliert |
| US-004 | | User | **User** | `getRemainingBorrowingCapacity(activeLoans)` | Query | ✅ Modelliert |
| US-004 | | User | | → Berechnet BorrowingLimit - activeLoans | Business Logic | ✅ Modelliert |
| | | | | | | |
| **US-001** | Admin - User Suspend | User | **User** | `suspend()` | Command | ✅ Modelliert |
| US-001 | | User | **User** | `reactivate()` | Command | ✅ Modelliert |
| | | | | | | |

---

### 2.4 NOTIFICATION CONTEXT (Supporting Subdomain)

| User Story ID | Titel | Bounded Context | Aggregate | Methode/Service | Operation | Status |
|---------------|-------|-----------------|-----------|-----------------|-----------|--------|
| **US-005** | Benachrichtigungen | Notification | **Notification** | `Notification.create()` | Command | ✅ Modelliert |
| US-005 | | Notification | **NotificationComposerService** | `composeFromEvent(event, userId)` | Command | ✅ Modelliert |
| US-005 | | Notification | | → Dedupliziert anhand (eventId, userId, type) | Business Logic | ✅ Modelliert |
| US-005 | | Notification | | → Rendert Subject/Body aus Template | Business Logic | ✅ Modelliert |
| US-005 | | Notification | | → Wählt Channel (Email/Push) | Business Logic | ✅ Modelliert |
| US-005 | | Notification | **NotificationDeliveryService** | `send(notificationId)` | Command | ✅ Modelliert |
| US-005 | | Notification | | → Ruft EmailAdapter/PushAdapter auf | External Integration | ✅ Modelliert |
| US-005 | | Notification | **Notification** | `markSent()` | Command | ✅ Modelliert |
| US-005 | | Notification | **Notification** | `retry()` | Command | ✅ Modelliert |
| US-005 | | Notification | **Notification** | `markFailed()` | Command | ✅ Modelliert |
| US-005 | | Notification | **Notification** | `canRetry()` | Query | ✅ Modelliert |
| | | | | | | |
| **US-005** | Event Subscriptions | Notification | **NotificationEventHandler** | Subscribes zu Domain Events | Event Handler | ✅ Modelliert |
| US-005 | | Notification | | → MediaCheckedOut → "Ausleihe bestätigt" | Event → Notification | ✅ Modelliert |
| US-005 | | Notification | | → LoanRenewed → "Verlängerung bestätigt" | Event → Notification | ✅ Modelliert |
| US-005 | | Notification | | → MediaReturned → "Rückgabe bestätigt" | Event → Notification | ✅ Modelliert |
| US-005 | | Notification | | → MediaReserved → "Reservierung bestätigt" | Event → Notification | ✅ Modelliert |
| US-005 | | Notification | | → ReservationExpired → "Reservierung abgelaufen" | Event → Notification | ✅ Modelliert |
| US-005 | | Notification | | → ReminderTriggered → "Erinnerung fällig" | Event → Notification | ✅ Modelliert |
| US-005 | | Notification | | → ClassSetCheckedOut → "Klassensatz ausgeliehen" | Event → Notification | ✅ Modelliert |
| US-005 | | Notification | | → ClassSetReturned → "Klassensatz zurückgegeben" | Event → Notification | ✅ Modelliert |
| US-005 | | Notification | | → PreReservationPromoted → "Vormerkung verfügbar" | Event → Notification | ✅ Modelliert |
| | | | | | | |

---

### 2.5 REMINDING CONTEXT (Supporting Subdomain)

| User Story ID | Titel | Bounded Context | Aggregate | Methode/Service | Operation | Status |
|---------------|-------|-----------------|-----------|-----------------|-----------|--------|
| **US-008** | Mahnwesen | Reminding | **ReminderCampaign** | `ReminderCampaign.startCampaign()` | Command | ✅ Modelliert |
| US-008 | | Reminding | **RemindingEvaluationService** | `runDailyCampaign(policy, atTime)` | Command | ✅ Modelliert |
| US-008 | | Reminding | | → Startet Campaign | Business Logic | ✅ Modelliert |
| US-008 | | Reminding | | → Query alle aktiven Loans | Query | ✅ Modelliert |
| US-008 | | Reminding | | → Für jeden Loan: evaluateLoan() | Business Logic | ✅ Modelliert |
| US-008 | | Reminding | | → Complete Campaign | Business Logic | ✅ Modelliert |
| US-008 | | Reminding | **RemindingEvaluationService** | `evaluateLoan(loan, policy)` | Query | ✅ Modelliert |
| US-008 | | Reminding | | → Prüft gegen ReminderPolicy | Business Rule | ✅ Modelliert |
| US-008 | | Reminding | | → Bei Match: Publiziert ReminderTriggered Event | Event | ✅ Modelliert |
| US-008 | | Reminding | **ReminderCampaign** | `addLoanChecked()` | Command | ✅ Modelliert |
| US-008 | | Reminding | **ReminderCampaign** | `addReminderTriggered()` | Command | ✅ Modelliert |
| US-008 | | Reminding | **ReminderCampaign** | `complete()` | Command | ✅ Modelliert |
| US-008 | | Reminding | **ReminderCampaign** | `fail(errorMessage)` | Command | ✅ Modelliert |
| | | | | | | |
| **US-008** | Reminder Policy | Reminding | **ReminderPolicyService** | `getActivePolicy()` | Query | ✅ Modelliert |
| US-008 | | Reminding | **ReminderPolicyService** | `updatePolicy(policy)` | Command | ✅ Modelliert |
| US-008 | | Reminding | **ReminderPolicyService** | `calculateReminderDates(loan, policy)` | Query | ✅ Modelliert |
| | | | | | | |
| **US-008** | Scheduled Job | Reminding | **ReminderScheduler** | `scheduleDaily(policy)` | Command | ✅ Modelliert |
| US-008 | | Reminding | | → Cron: "0 8 * * *" (täglich 08:00) | Scheduler | ✅ Modelliert |
| US-008 | | Reminding | | → Triggert RemindingEvaluationService | Orchestration | ✅ Modelliert |
| | | | | | | |

---

### 2.6 ADMIN & CONFIGURATION (Cross-Cutting)

| User Story ID | Titel | Bounded Context | Aggregate | Methode/Service | Operation | Status |
|---------------|-------|-----------------|-----------|-----------------|-----------|--------|
| **US-012** | Admin-Konfiguration Regeln | Lending | **PolicyConfigurationService** | `updateLoanPolicy(policy)` | Command | ✅ Modelliert |
| US-012 | | Lending | **PolicyConfigurationService** | `updateReservationPolicy(policy)` | Command | ✅ Modelliert |
| US-012 | | Lending | **PolicyConfigurationService** | `updateRenewalPolicy(policy)` | Command | ✅ Modelliert |
| US-012 | | Lending | **PolicyConfigurationService** | `updateClassSetPolicy(policy)` | Command | ✅ Modelliert |
| US-012 | | Lending | **PolicyConfigurationService** | `getLoanPolicy()` | Query | ✅ Modelliert |
| US-012 | | Lending | **PolicyConfigurationService** | `getReservationPolicy()` | Query | ✅ Modelliert |
| US-012 | | Lending | **PolicyConfigurationService** | `getRenewalPolicy()` | Query | ✅ Modelliert |
| US-012 | | Reminding | **ReminderPolicyService** | `updatePolicy(policy)` | Command | ✅ Modelliert |
| US-012 | | Reminding | **ReminderPolicyService** | `getActivePolicy()` | Query | ✅ Modelliert |
| US-012 | | Reminding | **ReminderScheduler** | `updateSchedule(cronExpression)` | Command | ✅ Modelliert |
| | | | | | | |

---

### 2.7 REPORTING (Read-Only)

| User Story ID | Titel | Bounded Context | Aggregate | Methode/Service | Operation | Status |
|---------------|-------|-----------------|-----------|-----------------|-----------|--------|
| **US-010** | Reporting und Statistik | Lending | **ReportingQueryService** | `getTopBorrowedMedia(period)` | Query | ✅ Modelliert |
| US-010 | | Lending | **ReportingQueryService** | `getLeastBorrowedMedia(period)` | Query | ✅ Modelliert |
| US-010 | | Lending | **ReportingQueryService** | `getBorrowingsByUserGroup(period)` | Query | ✅ Modelliert |
| US-010 | | Lending | **ReportingQueryService** | `getOverdueLoans()` | Query | ✅ Modelliert |
| US-010 | | Catalog | **MediaSearchService** | `getAvailabilityStatus()` | Query | ✅ Modelliert |
| US-010 | | Reminding | **ReportingQueryService** | `getReminderCampaignStatistics(period)` | Query | ✅ Modelliert |
| | | | | | | |

---

### 2.8 RECOMMENDATION LISTS

| User Story ID | Titel | Bounded Context | Aggregate | Methode/Service | Operation | Status |
|---------------|-------|-----------------|-----------|-----------------|-----------|--------|
| **US-011** | Empfehlungslisten | Lending | **RecommendationListService** | `createList(teacherId, title, description)` | Command | ⏳ Geplant |
| US-011 | | Lending | **RecommendationListService** | `addMediaToList(listId, mediaId, note)` | Command | ⏳ Geplant |
| US-011 | | Lending | **RecommendationListService** | `shareWithClass(listId, classId)` | Command | ⏳ Geplant |
| US-011 | | Lending | **RecommendationListService** | `getListsForUser(userId)` | Query | ⏳ Geplant |
| | | | | | | |
| **Note:** | Empfehlungslisten wurden noch nicht als eigenes Aggregat modelliert. | | | | | |
| | Vorschlag: Eigenes Aggregat `RecommendationList` mit Entity `ListItem`. | | | | | |
| | Bounded Context: Lending oder neuer "Curation Context". | | | | | |

---

## 3. Vollständigkeits-Validierung

### 3.1 Vollständigkeit nach User Story

| User Story | Vollständig modelliert? | Fehlende Elemente |
|------------|-------------------------|-------------------|
| **US-001** | ✅ Ja | - |
| **US-002** | ✅ Ja | - |
| **US-003** | ✅ Ja | - |
| **US-004** | ✅ Ja | - |
| **US-005** | ✅ Ja | - |
| **US-006** | ✅ Ja | - |
| **US-007** | ✅ Ja | - |
| **US-008** | ✅ Ja | - |
| **US-009** | ✅ Ja | - |
| **US-010** | ✅ Ja | - |
| **US-011** | ⏳ Geplant | RecommendationList Aggregat fehlt |
| **US-012** | ✅ Ja | - |

**Fazit:** 11 von 12 User Stories vollständig modelliert (92%).

### 3.2 Vollständigkeit nach Bounded Context

| Bounded Context | User Stories abgedeckt | Status |
|----------------|------------------------|--------|
| **Lending** | US-003, US-004, US-006, US-009, US-012 | ✅ Complete |
| **Catalog** | US-002, US-007 | ✅ Complete |
| **User** | US-001 | ✅ Complete |
| **Notification** | US-005 | ✅ Complete |
| **Reminding** | US-008, US-012 | ✅ Complete |
| **Reporting** | US-010 | ✅ Complete |
| **Recommendation** | US-011 | ⏳ Nicht modelliert |

### 3.3 Command/Query/Event Übersicht

**Commands (State-Changing Operations):**
- Loan: `checkout()`, `renew()`, `markReturned()`
- Reservation: `create()`, `collect()`, `cancel()`, `expire()`
- PreReservation: `create()`, `cancel()`, `promote()`
- ClassSet: `create()`, `returnCopy()`, `returnAll()`
- Media: `create()`, `updateMetadata()`
- MediaCopy: `create()`, `markAsOnLoan()`, `markAsReturned()`, `markDamaged()`, `markLost()`, `updateShelfLocation()`
- User: `createFromSSO()`, `syncWithSSO()`, `suspend()`, `reactivate()`
- Notification: `create()`, `markSent()`, `retry()`, `markFailed()`
- ReminderCampaign: `startCampaign()`, `complete()`, `fail()`

**Queries (Read-Only Operations):**
- Loan: `isOverdue()`, `canRenew()`, `getDaysUntilDue()`
- Reservation: `isExpired()`
- ClassSet: `getAllCopies()`, `getUnreturnedCopies()`
- Media: `getTotalCopies()`, `getAvailableCopies()`
- MediaCopy: `isAvailableForLoan()`
- User: `canBorrow()`, `getRemainingBorrowingCapacity()`
- Notification: `canRetry()`
- ReminderCampaign: `isRunning()`, `getTotalChecked()`, `getTotalTriggered()`
- **Reporting**: `getTopBorrowedMedia()`, `getLeastBorrowedMedia()`, `getBorrowingsByUserGroup()`, `getOverdueLoans()`, etc.

**Domain Events:**
- `MediaCheckedOut(loanId, userId, mediaCopyId, dueDate)`
- `LoanRenewed(loanId, newDueDate, renewalCount)`
- `MediaReturned(loanId, mediaCopyId, returnedAt)`
- `MediaReserved(reservationId, userId, mediaCopyId, expiresAt)`
- `ReservationCollected(reservationId, loanId)`
- `ReservationExpired(reservationId, mediaCopyId)`
- `PreReservationCreated(preReservationId, userId, mediaId, position)`
- `PreReservationPromoted(preReservationId, reservationId)`
- `ClassSetCheckedOut(classSetId, teacherId, mediaCopyIds, dueDate)`
- `ClassSetReturned(classSetId, returnedCopies)`
- `ReminderTriggered(reminderId, loanId, userId, type, daysDelta)`

---

## 4. Impact-Analyse: Beispiele

### 4.1 Änderung an US-004 (Ausleihe)

**Szenario:** Neue Anforderung "Ausleihsperren bei überfälligen Gebühren"

**Betroffene Komponenten:**
- **Aggregate:** Loan (neue Methode `hasOverdueFees()`)
- **Service:** LoanCheckoutService (erweiterte Validierung in `checkout()`)
- **Business Rule:** User eligibility (neue Bedingung)
- **Domain Event:** Neues Event `LoanRejected(userId, reason)`
- **Notification:** Neue NotificationType "LoanRejected"

**Geschätzter Aufwand:** 2-3 PT (+ Testing)

### 4.2 Änderung an US-008 (Mahnwesen)

**Szenario:** Neue Mahnstufe T+14 mit SMS-Versand

**Betroffene Komponenten:**
- **Value Object:** ReminderPolicy (neues Feld `escalationLevel2Days`)
- **Service:** RemindingEvaluationService (neue Evaluationslogik)
- **Domain Event:** ReminderTriggered (neuer Type "EscalationLevel2")
- **Notification:** NotificationChannel (neuer Channel "SMS")
- **Admin UI:** Policy Configuration (neues Eingabefeld)

**Geschätzter Aufwand:** 5 PT (+ SMS-Provider-Integration)

---

## 5. Nächste Schritte

### 5.1 Offene Modellierungen

**US-011: Empfehlungslisten**
- Aggregat `RecommendationList` mit Entity `ListItem`
- Bounded Context: Lending oder neuer "Curation Context"
- Methoden: `createList()`, `addMedia()`, `shareWithClass()`, `updateSortOrder()`
- Domain Events: `ListCreated`, `ListShared`, `ListUpdated`

### 5.2 Technische Schulden

- **Event Sourcing:** Vollständige Event Store-Implementierung
- **CQRS:** Separate Read Models für Reporting
- **Monitoring:** Domain Events für Audit Log und Analytics
- **Performance:** Caching für häufige Queries (z.B. `getAvailableCopies()`)

### 5.3 Dokumentations-Updates

- PlantUML-Diagramme bei Änderungen aktualisieren
- Ubiquitous Language Glossar erweitern bei neuen Begriffen
- Traceability Matrix bei neuen User Stories ergänzen

---

## 6. Referenzen

- **User Stories:** `/docs/requirements/user-stories/*.md`
- **Domain Model:** `/docs/architecture/domain-model.md`
- **Aggregates & Entities:** `/docs/architecture/aggregates-entities-valueobjects.md`
- **Ubiquitous Language:** `/docs/architecture/ubiquitous-language-glossar-complete.md`
- **PlantUML Diagrams:** `/docs/architecture/domain-models/*.puml`

---

**Autor:** GitHub Copilot (Claude Sonnet 4.5)  
**Version:** 2.0  
**Datum:** 2025-01-27
