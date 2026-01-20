# Domain Events & Integrations - Digitale Schulbibliothek

**Version:** 1.0  
**Datum:** 2024-12-16  
**Status:** Architektur-Referenz für Implementierung

---

## 📬 Domain Events Katalog

### Event-Emission Governance

```
Regel 1: Events werden NUR von Aggregate Roots publiziert
         (nicht von Value Objects oder Entities)

Regel 2: Events sind IMMUTABLE nach Erstellung

Regel 3: Events enthalten Minimal-Payload
         (nur Information notwendig für Consumer)

Regel 4: Event-Publishing ist Teil der Write-Transaction
         (Atomarität: Command → State Change → Event Publish)

Regel 5: Events verwenden Naming-Convention: <Aggregate><Verb>ed
         Beispiel: MediaCheckedOut, LoanRenewed
```

### Admin-konfigurierbare Policies (wirken auf Events)

- **LoanPolicy:** Ausleihdauer pro UserGroup (Defaults: Student 21 Tage, Teacher 56 Tage, Librarian 90 Tage) + optional Sonderfälle (Reference-Bestand).
- **ReservationPolicy:** Abholfrist für Reservierungen / Auto-Reservations aus Waitlist (Default: 48h).
- **RenewalPolicy:** Max. Anzahl Verlängerungen und Verlängerungsdauer (Default: 2 Verlängerungen, gleiche Dauer wie LoanPolicy der UserGroup).
- **ReminderPolicy:** Zeitpunkte für Upcoming/Overdue/Eskalation sowie Job-Ausführungszeit (Defaults: T-3, T+1, T+7; Scheduler 08:00).

Alle Werte werden in der Admin Web-App gepflegt und fließen in Payloads (z.B. `dueDate`, `expiryDate`, `pickupDeadline`) sowie in die Reminder-Auslösung ein.

---

## 🎬 LENDING CONTEXT - Events

### Event 1: MediaCheckedOut

**Semantik:** Ein Medium wurde gerade ausgecheckt und ist kein verfügbar mehr

```yaml
Event Name: MediaCheckedOut
Aggregate: Loan
Triggered: Beim erfolgreichen Checkout via Admin
Timing: SYNCHRONOUS (blocking → State Change first)

Payload:
  loanId: UUID
  userId: UUID
  userGroup: Enum(Student|Teacher|Librarian)
  mediaId: UUID
  mediaCopyBarcode: String (e.g., "SCH-12345")
  dueDate: LocalDate (berechnet aus LoanPolicy, Admin Web-App konfigurierbar)
  checkoutDate: LocalDate
  createdAt: Instant
  context: 
    ipAddress: String (optional)
    deviceId: String (optional)

Handlers (Subscribers):
  1. CatalogContext:
     Action: Update MediaCopy.availabilityStatus = CheckedOut
     Query: "Get MediaCopy by barcode"
     Update: Set status, lastModified
     Sync/Async: ASYNCHRONOUS (non-blocking)
     
  2. NotificationContext:
     Action: Send "Checkout Confirmation" Email
     Content: Media title, due date, renewal info
     Sync/Async: ASYNCHRONOUS (non-blocking)
     Retry: 3x with exponential backoff
     
  3. RemindingContext:
     Action: Schedule upcoming reminders (T-3, T+1, etc)
     Sync/Async: ASYNCHRONOUS (non-blocking)

Failure Handling:
  - Event ist immutable
  - Handlers sind idempotent (at-least-once delivery)
  - Dead-Letter-Queue bei persistenter Fehlerquote

Example Event JSON:
{
  "eventId": "evt-2024-001",
  "eventType": "MediaCheckedOut",
  "aggregateId": "loan-uuid-1234",
  "aggregateType": "Loan",
  "timestamp": "2024-12-16T10:30:00Z",
  "version": 1,
  "payload": {
    "loanId": "loan-uuid-1234",
    "userId": "user-uuid-5678",
    "userGroup": "Student",
    "mediaId": "media-uuid-9999",
    "mediaCopyBarcode": "SCH-12345",
    "dueDate": "2025-01-06",
    "checkoutDate": "2024-12-16"
  }
}
```

---

### Event 2: MediaReturned

**Semantik:** Ein Medium wurde gerade zurückgegeben und ist wieder verfügbar (oder überfällig)

```yaml
Event Name: MediaReturned
Aggregate: Loan
Triggered: Beim erfolgreichen Return via Admin
Timing: SYNCHRONOUS (blocking → State Change first)

Payload:
  loanId: UUID
  userId: UUID
  mediaId: UUID
  mediaCopyBarcode: String
  returnDate: LocalDate
  dueDate: LocalDate (original due date)
  isOverdue: Boolean
  overdueDays: Integer (0 if on-time)
  createdAt: Instant

Handlers (Subscribers):
  1. CatalogContext:
     Action: Update MediaCopy.availabilityStatus = Available
     Process: Delete Reserved status if exists
     Sync/Async: ASYNCHRONOUS (non-blocking)
     
  2. LendingContext (internal):
     Action: Process PreReservations (Waitlist)
     Logic:
       - IF PreReservations exist for this media:
         1. Take first PreReservation from queue
         2. Create new Reservation (48h TTL)
         3. Publish: PreReservationResolved event
         4. Remove PreReservation from Waitlist
       - ELSE: Just update availability
     Sync/Async: SYNCHRONOUS (must process in same transaction)
     
  3. NotificationContext:
     Action: Send "Return Confirmation" Email
     Conditional: Include overdue info if isOverdue=true
     Sync/Async: ASYNCHRONOUS (non-blocking)
     
  4. RemindingContext:
     Action: Clear all pending reminders for this loan
     Sync/Async: ASYNCHRONOUS (non-blocking)

Failure Handling:
  - Waitlist processing must complete even if notification fails
  - Retry logic for notification delivery
  - Event is idempotent (same loanId → same result)

Example Event JSON:
{
  "eventId": "evt-2024-002",
  "eventType": "MediaReturned",
  "aggregateId": "loan-uuid-1234",
  "aggregateType": "Loan",
  "timestamp": "2024-12-16T14:15:00Z",
  "version": 1,
  "payload": {
    "loanId": "loan-uuid-1234",
    "userId": "user-uuid-5678",
    "mediaId": "media-uuid-9999",
    "mediaCopyBarcode": "SCH-12345",
    "returnDate": "2024-12-16",
    "dueDate": "2025-01-06",
    "isOverdue": false,
    "overdueDays": 0
  }
}
```

---

### Event 3: MediaReserved

**Semantik:** Ein verfügbares Medium wurde soeben reserviert mit 48h Abholfrist

```yaml
Event Name: MediaReserved
Aggregate: Reservation
Triggered: Wenn User "Reserve" Button klickt auf verfügbarem Medium
Timing: SYNCHRONOUS (blocking)

Payload:
  reservationId: UUID
  userId: UUID
  mediaId: UUID
  mediaCopyBarcode: String
  expiryDate: LocalDate (NOW + ReservationPolicy.ttl configured in Admin Web-App, Default: 48h)
  pickupLocation: String (e.g., "Main Desk")
  createdAt: Instant
  notificationType: String (e.g., "Email")

Handlers (Subscribers):
  1. CatalogContext:
     Action: Update MediaCopy.availabilityStatus = Reserved
     Sync/Async: ASYNCHRONOUS
     
  2. NotificationContext:
     Action: Send "Reservation Confirmed" Email
     Content: 
       - Media title, author
       - Pickup deadline (48h from now)
       - Pickup location
       - Cancellation link
     Sync/Async: ASYNCHRONOUS

Expiry Handling:
  - Scheduled Job (daily, time configurable in Admin Web-App): Check all Reservations with expiryDate < TODAY
  - IF not collected: Delete Reservation, release Media
  - Publish: ReservationExpired event (optional)

Example Event JSON:
{
  "eventId": "evt-2024-003",
  "eventType": "MediaReserved",
  "aggregateId": "res-uuid-5555",
  "aggregateType": "Reservation",
  "timestamp": "2024-12-16T15:20:00Z",
  "version": 1,
  "payload": {
    "reservationId": "res-uuid-5555",
    "userId": "user-uuid-5678",
    "mediaId": "media-uuid-9999",
    "mediaCopyBarcode": "SCH-12345",
    "expiryDate": "2024-12-18",
    "pickupLocation": "Main Desk"
  }
}
```

---

### Event 4: PreReservationCreated

**Semantik:** Ein User hat ein verliehenes Medium vorgemerkt (Waitlist Entry)

```yaml
Event Name: PreReservationCreated
Aggregate: PreReservation (part of Loan aggregate)
Triggered: Wenn User "Pre-Reserve" klickt auf bereits CheckedOut Media
Timing: SYNCHRONOUS

Payload:
  preReservationId: UUID
  userId: UUID
  mediaId: UUID
  position: Integer (position in Waitlist, 1-based)
  estimatedAvailableDate: LocalDate (educated guess)
  createdAt: Instant

Handlers (Subscribers):
  1. NotificationContext:
     Action: Send "Added to Waitlist" Email
     Content:
       - Media title
       - Position in queue (e.g., "You are 2nd in line")
       - Estimated available date
       - Will notify when ready
     Sync/Async: ASYNCHRONOUS

Example Event JSON:
{
  "eventId": "evt-2024-004",
  "eventType": "PreReservationCreated",
  "aggregateId": "pre-res-uuid-6666",
  "aggregateType": "PreReservation",
  "timestamp": "2024-12-16T16:00:00Z",
  "version": 1,
  "payload": {
    "preReservationId": "pre-res-uuid-6666",
    "userId": "user-uuid-5678",
    "mediaId": "media-uuid-9999",
    "position": 2,
    "estimatedAvailableDate": "2024-12-20"
  }
}
```

---

### Event 5: PreReservationResolved

**Semantik:** Ein vorgemerktes Medium ist soeben verfügbar geworden → Auto-Reservation erstellt

```yaml
Event Name: PreReservationResolved
Aggregate: Reservation (newly created from PreReservation)
Triggered: AUTOMATISCH bei MediaReturned wenn Waitlist existiert
Timing: SYNCHRONOUS (part of Return transaction)

Payload:
  preReservationId: UUID
  reservationId: UUID (newly created)
  userId: UUID
  mediaId: UUID
  mediaCopyBarcode: String
  pickupDeadline: LocalDate (NOW + ReservationPolicy.ttl configured in Admin Web-App, Default: 48h)
  createdAt: Instant

Handlers (Subscribers):
  1. NotificationContext:
     Action: Send "Ready for Pickup" Email
     Content:
       - "Your reservation is ready!"
       - Media title
       - Pickup deadline (48h)
       - Pickup location
     Priority: HIGH
     Sync/Async: ASYNCHRONOUS

Workflow:
  1. Media returned
  2. MediaReturned event published
  3. LendingContext (internally) processes PreReservations
  4. First PreReservation taken from Waitlist
  5. New Reservation created (pickup deadline per ReservationPolicy.ttl)
  6. PreReservationResolved event published
  7. NotificationContext sends immediate notification

Example Event JSON:
{
  "eventId": "evt-2024-005",
  "eventType": "PreReservationResolved",
  "aggregateId": "res-uuid-7777",
  "aggregateType": "Reservation",
  "timestamp": "2024-12-16T14:15:05Z",
  "version": 1,
  "payload": {
    "preReservationId": "pre-res-uuid-6666",
    "reservationId": "res-uuid-7777",
    "userId": "user-uuid-5678",
    "mediaId": "media-uuid-9999",
    "mediaCopyBarcode": "SCH-12345",
    "pickupDeadline": "2024-12-18"
  }
}
```

---

### Event 6: LoanRenewed

**Semantik:** Eine Ausleihe wurde gerade verlängert

```yaml
Event Name: LoanRenewed
Aggregate: Loan
Triggered: Wenn User "Renew" Button klickt
Timing: SYNCHRONOUS

Payload:
  loanId: UUID
  userId: UUID
  mediaId: UUID
  oldDueDate: LocalDate
  newDueDate: LocalDate
  renewalCount: Integer (1, 2)
  maxRenewalsAllowed: Integer (RenewalPolicy from Admin Web-App, Default: 2)
  createdAt: Instant

Handlers (Subscribers):
  1. NotificationContext:
     Action: Send "Renewal Confirmation" Email
     Content:
       - Media title
       - New due date
       - Renewal count (e.g., "1 of 2 allowed")
     Sync/Async: ASYNCHRONOUS
     
  2. RemindingContext:
     Action: Reschedule reminders based on new due date
     Sync/Async: ASYNCHRONOUS

Example Event JSON:
{
  "eventId": "evt-2024-006",
  "eventType": "LoanRenewed",
  "aggregateId": "loan-uuid-1234",
  "aggregateType": "Loan",
  "timestamp": "2024-12-20T09:30:00Z",
  "version": 2,
  "payload": {
    "loanId": "loan-uuid-1234",
    "userId": "user-uuid-5678",
    "mediaId": "media-uuid-9999",
    "oldDueDate": "2025-01-06",
    "newDueDate": "2025-01-27",
    "renewalCount": 1,
    "maxRenewalsAllowed": 2
  }
}
```

---

### Event 7: ClassSetCheckedOut

**Semantik:** Eine Klassensatz wurde gerade von einem Lehrer ausgecheckt

```yaml
Event Name: ClassSetCheckedOut
Aggregate: ClassSetLoan
Triggered: Beim Checkout eines Klassensatzes
Timing: SYNCHRONOUS

Payload:
  classSetLoanId: UUID
  classSetId: UUID
  userId: UUID (Teacher)
  className: String (e.g., "8a")
  dueDate: LocalDate (usually 8 weeks out)
  mediaCopyCnt: Integer (how many copies in set)
  createdAt: Instant

Handlers (Subscribers):
  1. CatalogContext:
     Action: Update ALL SetMembers.availabilityStatus = CheckedOut
     Sync/Async: ASYNCHRONOUS
     
  2. NotificationContext:
     Action: Send confirmation with Checklist
     Sync/Async: ASYNCHRONOUS

Example Event JSON:
{
  "eventId": "evt-2024-007",
  "eventType": "ClassSetCheckedOut",
  "aggregateId": "classset-loan-uuid-8888",
  "aggregateType": "ClassSetLoan",
  "timestamp": "2024-12-16T11:00:00Z",
  "version": 1,
  "payload": {
    "classSetLoanId": "classset-loan-uuid-8888",
    "classSetId": "classset-uuid-1111",
    "userId": "user-uuid-teacher",
    "className": "8a",
    "dueDate": "2025-02-10",
    "mediaCopyCnt": 25
  }
}
```

---

### Event 8: FineCreated

**Semantik:** Eine Mahngebühr wurde erhoben (z.B. bei Rückgabe eines überfälligen Mediums)

```yaml
Event Name: FineCreated
Aggregate: Fine
Triggered: Bei Rückgabe (Overdue) oder manuell (Damaged/Lost)
Timing: SYNCHRONOUS

Payload:
  fineId: UUID
  userId: UUID
  loanId: UUID
  amount: Money
  reason: Enum(Overdue|Damaged|Lost)
  createdAt: Instant

Handlers (Subscribers):
  1. NotificationContext:
     Action: Send "Fine Notification" Email
     Content: Amount, Reason, Payment Instructions
     Sync/Async: ASYNCHRONOUS
```

### Event 9: FinePaid

**Semantik:** Eine Mahngebühr wurde bezahlt

```yaml
Event Name: FinePaid
Aggregate: Fine
Triggered: Wenn Zahlung registriert wird
Timing: SYNCHRONOUS

Payload:
  fineId: UUID
  userId: UUID
  amount: Money
  paidAt: Instant

Handlers (Subscribers):
  1. NotificationContext:
     Action: Send "Payment Receipt" Email
     Sync/Async: ASYNCHRONOUS
```

---

## 🔔 REMINDING CONTEXT - Events

### Event 10: ReminderTriggered

**Semantik:** Eine automatische Erinnerung wurde gerade ausgelöst basierend auf Policy

```yaml
Event Name: ReminderTriggered
Aggregate: ReminderCampaign (internal state)
Triggered: Von Scheduled Job (Cron)
Timing: ASYNCHRONOUS (event-driven, non-blocking)

Payload:
  reminderId: UUID
  loanId: UUID
  userId: UUID
  mediaId: UUID
  mediaTitle: String
  reminderType: Enum(UpcomingReminder|OverdueReminder|EscalationReminder)
  daysUntilOrAfterDue: Integer
  dueDate: LocalDate
  createdAt: Instant
  subject: String (templated)

Reminder Types:
  1. UpcomingReminder: ReminderPolicy.upcomingDays BEFORE due date (Admin Web-App, Default: 3 Tage)
     Subject: "Your media is due in 3 days"
     Severity: INFO
     
  2. OverdueReminder: Exactly on due date OR ReminderPolicy.overdueDays after (Admin Web-App, Default: 1 Tag)
     Subject: "Please return your media today"
     Severity: WARNING
     
  3. EscalationReminder: ReminderPolicy.escalationDays AFTER due date (Admin Web-App, Default: 7 Tage)
     Subject: "Final reminder: Media is 7 days overdue"
     Severity: CRITICAL
     Action: Include admin CC

Handlers (Subscribers):
  1. NotificationContext:
     Action: Send reminder Email
     Retry: Yes, 3x with exponential backoff
     Sync/Async: ASYNCHRONOUS
     
  2. LendingContext (optional feedback):
     Action: Update Loan.overdueFlagUpdated (if escalation)
     Sync/Async: ASYNCHRONOUS

Scheduling:
  - Job runs daily at 08:00 (configurable in Admin Web-App)
  - Queries: getAllActiveLoans() from LendingContext
  - For each Loan: Check against ReminderPolicy (konfigurierbar)
  - Only trigger once per Loan per reminder type
  - Deduplication: Check if Reminder already sent today

Example Event JSON - Upcoming Reminder:
{
  "eventId": "evt-2024-008-reminder-1",
  "eventType": "ReminderTriggered",
  "aggregateId": "reminder-uuid-9999",
  "aggregateType": "ReminderCampaign",
  "timestamp": "2024-12-13T08:30:00Z",
  "version": 1,
  "payload": {
    "reminderId": "reminder-uuid-9999",
    "loanId": "loan-uuid-1234",
    "userId": "user-uuid-5678",
    "mediaId": "media-uuid-9999",
    "mediaTitle": "Der Herr der Ringe",
    "reminderType": "UpcomingReminder",
    "daysUntilOrAfterDue": 3,
    "dueDate": "2025-01-06",
    "subject": "Reminder: Return '{{mediaTitle}}' in 3 days"
  }
}

Example Event JSON - Overdue Reminder:
{
  "eventId": "evt-2024-008-reminder-2",
  "eventType": "ReminderTriggered",
  "aggregateId": "reminder-uuid-9999",
  "aggregateType": "ReminderCampaign",
  "timestamp": "2025-01-07T08:30:00Z",
  "version": 1,
  "payload": {
    "reminderId": "reminder-uuid-9999-overdue",
    "loanId": "loan-uuid-1234",
    "userId": "user-uuid-5678",
    "mediaId": "media-uuid-9999",
    "mediaTitle": "Der Herr der Ringe",
    "reminderType": "OverdueReminder",
    "daysUntilOrAfterDue": 1,
    "dueDate": "2025-01-06",
    "subject": "Warning: '{{mediaTitle}}' is 1 day overdue!"
  }
}
```

---

## 🔄 Integration Patterns

### Pattern 1: Synchronous Query (Request-Reply)

**Use Case:** Validierung vor Aktion (z.B. Checkout)

```
CLIENT REQUEST
  ├─ Action: CheckOut media
  │
  └─→ LENDING CONTEXT
      ├─ SYNC QUERY: User Context.checkUserEligibility(userId)
      │   └─→ USER CONTEXT
      │       ├─ Validate: User.isActive
      │       ├─ Validate: No Overdues
      │       ├─ Validate: Count(ActiveLoans) < BorrowingLimit
      │       └─→ Response: {eligible: true, borrowingLimit: 5}
      │
      ├─ SYNC QUERY: Catalog Context.checkMediaAvailability(mediaId)
      │   └─→ CATALOG CONTEXT
      │       ├─ Validate: MediaCopy.status == Available
      │       ├─ Load: Media.title, author (for audit)
      │       └─→ Response: {available: true, mediaTitle: "..."}
      │
      ├─ IF all validations pass:
      │   ├─ Create Loan aggregate
      │   ├─ Calculate DueDate
      │   ├─ Publish: MediaCheckedOut event (async)
      │   └─→ Return: Success(loanId, dueDate)
      │
      └─ ELSE:
          ├─ Rollback
          └─→ Return: Error(reason)
```

**Code Pattern:**

```java
@Service
public class CheckoutUseCase {
  private UserService userService;
  private CatalogService catalogService;
  private LoanRepository loanRepo;
  private EventPublisher publisher;
  
  @Transactional
  public CheckoutResult execute(CheckoutCommand cmd) {
    // 1. Synchronous validation
    var userEligible = userService.checkEligibility(cmd.userId);
    if (!userEligible) throw new IneligibleUserException();
    
    var mediaAvailable = catalogService.isAvailable(cmd.mediaId);
    if (!mediaAvailable) throw new MediaNotAvailableException();
    
    // 2. Create aggregate
    var loan = new Loan(cmd.userId, cmd.mediaId, cmd.dueDate);
    
    // 3. Persist
    var savedLoan = loanRepo.save(loan);
    
    // 4. Publish event (async, but within transaction)
    var event = new MediaCheckedOutEvent(savedLoan);
    publisher.publish(event);  // transactional publisher
    
    return CheckoutResult.success(savedLoan);
  }
}
```

---

### Pattern 2: Asynchronous Event-Driven (Pub-Sub)

**Use Case:** Notification nach Aktion (MediaCheckedOut → Email)

```
LENDING CONTEXT publishes
  ├─ Event: MediaCheckedOut
  │  ├─ loanId, userId, mediaId, dueDate
  │  └─ Published to Message Broker (Kafka, RabbitMQ)
  │
  └─→ MESSAGE BROKER (Event Hub)
      ├─→ CATALOG CONTEXT subscribes
      │   ├─ Update MediaCopy.status = CheckedOut
      │   └─ No response needed
      │
      └─→ NOTIFICATION CONTEXT subscribes
          ├─ Create notification record
          ├─ Load template
          ├─ Send email asynchronously
          ├─ Retry on failure
          └─ Log delivery status

Benefits:
  • Decouple: Notification failure ≠ Checkout failure
  • Scale: Multiple handlers can process in parallel
  • Retry: Automatic retry logic
  • Audit: Event store provides complete history
```

**Code Pattern:**

```java
// Lending Context
@Service
public class CheckoutService {
  private EventPublisher eventPublisher;
  
  public void checkout(User user, MediaCopy media) {
    var loan = new Loan(user, media);
    var event = new MediaCheckedOutEvent(loan);
    
    // Publish async (non-blocking)
    eventPublisher.publishAsync(event);
  }
}

// Notification Context (Event Handler)
@Component
public class CheckoutNotificationHandler {
  @EventListener(MediaCheckedOutEvent.class)
  public void onMediaCheckedOut(MediaCheckedOutEvent event) {
    var notification = new Notification(
      event.userId,
      "Checkout Confirmation",
      "Your media is due on " + event.dueDate
    );
    notificationService.send(notification);
  }
}

// Catalog Context (Event Handler)
@Component
public class CatalogAvailabilityHandler {
  @EventListener(MediaCheckedOutEvent.class)
  public void onMediaCheckedOut(MediaCheckedOutEvent event) {
    var mediaCopy = mediaRepository.findByBarcode(event.mediaCopyBarcode);
    mediaCopy.setStatus(AvailabilityStatus.CHECKED_OUT);
    mediaRepository.save(mediaCopy);
  }
}
```

---

### Pattern 3: Orchestrated Saga (Multi-Step Process)

**Use Case:** Komplexer Prozess mit mehreren Context-Interaktionen (ClassSet Return)

```
RETURN CLASSSET Flow (Orchestrated)
══════════════════════════════════════════════════════════════════

1. INITIATE
   Admin scans classset barcode
   └─→ LendingContext: returnClassSet(classSetLoanId)
   
2. VALIDATE
   Lending: Check all SetMembers are scanned
   └─→ IF incomplete: Mark as "Incomplete", Flag admin
       ELSE: Continue
   
3. PROCESS RETURN
   Lending: Update ClassSetLoan.status = Returned
   Event: ClassSetReturned published
   
4. ASYNC HANDLERS
   
   a) Catalog Context:
      Update ALL MediaCopy.status = Available
      Clear Reserved flags
   
   b) Notification Context:
      Send "Return Confirmation" email to teacher
      CC: Librarian
   
   c) Reminding Context:
      Clear all pending reminders
   
5. RESULT
   IF all async handlers succeed:
     Display: "ClassSet successfully returned"
   
   IF some handlers fail:
     Event replay ensures consistency
     Admin notified of issues

Failure Scenarios:
  • Incomplete return: Manual intervention needed
  • Notification failure: Retry logic handles
  • Catalog update timeout: Event re-processing
```

---

## 📊 Integration Matrix (Context Dependencies)

```
                 ┌──────────┬──────────┬──────────┬──────────┬──────────┐
                 │  User    │ Catalog  │ Lending  │Notif.    │ Remind.  │
                 │ Context  │ Context  │ Context  │ Context  │ Context  │
┌────────────────┼──────────┼──────────┼──────────┼──────────┼──────────┤
│ User Context   │   ---    │    Q     │    Q     │    Q     │    -     │
│                │          │checkAvl  │eligib.   │ email    │          │
├────────────────┼──────────┼──────────┼──────────┼──────────┼──────────┤
│ Catalog        │    -     │   ---    │    Q     │    Q     │    -     │
│ Context        │          │          │mediaTitle│ mediaInfo│          │
├────────────────┼──────────┼──────────┼──────────┼──────────┼──────────┤
│ Lending        │   Q      │    Q     │   ---    │    E     │    Q     │
│ Context        │eligib.   │avail.    │          │pub.Events│ read only│
│  (Core)        │          │          │          │          │          │
├────────────────┼──────────┼──────────┼──────────┼──────────┼──────────┤
│ Notification   │    -     │    -     │    -     │   ---    │    E     │
│ Context        │          │          │          │          │subscr.   │
├────────────────┼──────────┼──────────┼──────────┼──────────┼──────────┤
│ Reminding      │    -     │    -     │    Q     │    E     │   ---    │
│ Context        │          │          │read-only │pub.Events│          │
└────────────────┴──────────┴──────────┴──────────┴──────────┴──────────┘

Legend:
Q = Synchronous Query (Request-Reply)
E = Asynchronous Event (Publish-Subscribe)
- = No dependency
```

---

## 🛡️ Error Handling & Resilience

### Event Publishing Resilience

```
Event Publishing Transaction:
┌─────────────────────────────────────────────────────────────┐
│  1. Create Aggregate                                         │
│  2. Validate State                                           │
│  3. Persist to Database                    (commit point 1)  │
│  4. Publish Event                          (commit point 2)  │
│                                                               │
│  CASE A: Step 3 fails                                        │
│    └─ Rollback everything, return error                     │
│                                                               │
│  CASE B: Step 3 succeeds, Step 4 fails                      │
│    └─ Event record written to DB (outbox pattern)           │
│    └─ Separate job polls outbox → publishes to broker       │
│    └─ Ensures at-least-once delivery                        │
└─────────────────────────────────────────────────────────────┘

Outbox Pattern Implementation:
┌──────────────────────────┐
│  LENDING SERVICE         │
│                          │
│  1. Create Loan          │
│     Update loan table    │
│  2. Insert into OUTBOX   │ ← Same Transaction
│     (event_type, event_  │
│      payload, published= │
│      false)              │
│  3. Commit or Rollback   │
│     (atomic)             │
└────────────┬─────────────┘
             │
             └─→ OUTBOX POLL JOB (runs every 10s)
                 ├─ Read unpublished events
                 ├─ Publish to Message Broker
                 ├─ Mark as published in DB
                 └─ Retry on failure

Benefits:
  ✓ No lost events
  ✓ Handles DB-to-Broker network failures
  ✓ Automatic retry on broker unavailability
```

### Handler Idempotency

```
Principle: Each handler must produce same result when called 2x

Example: NotificationHandler.onMediaCheckedOut()
┌──────────────────────────────────────────────────────┐
│  Handler receives: MediaCheckedOutEvent(loanId=123)  │
│                                                       │
│  1. Check if Notification exists for this loanId    │
│     SELECT * FROM notifications WHERE loanId=123    │
│                                                       │
│  2. IF exists:                                       │
│     └─ Skip (idempotent: no duplicate)              │
│     └─ Return success                               │
│                                                       │
│  3. IF not exists:                                  │
│     ├─ Create notification                          │
│     ├─ Send email                                   │
│     ├─ Save to DB                                   │
│     └─ Return success                               │
│                                                       │
│  Result: 1st call = notification sent               │
│         2nd call (replay) = no new notification     │
└──────────────────────────────────────────────────────┘

Implementation Pattern:
@EventListener(MediaCheckedOutEvent.class)
public synchronized void onMediaCheckedOut(MediaCheckedOutEvent event) {
  // Idempotency key: combination of eventType + loanId
  var key = "checkout_notification_" + event.loanId;
  
  if (hasProcessed(key)) {
    logger.info("Event already processed, skipping");
    return;
  }
  
  // Process
  sendNotification(event);
  markAsProcessed(key);
}
```

---

## 📡 Event Broker Configuration (for MVP)

**Recommended Technology Stack:**

```
Option 1: Spring Cloud Stream + RabbitMQ
  ✓ Lightweight
  ✓ Good for MVP
  ✓ Built-in retry logic
  
Option 2: Kafka
  ✓ Event store built-in
  ✓ Better for scalability
  ✓ Overkill for MVP

Option 3: Database Polling (Simple, no broker)
  ✓ No new infrastructure
  ✓ Works with existing DB
  ✓ Slower but reliable
  ✓ Good for MVP phase
```

**MVP Recommendation: Option 3 (Database Polling) + Spring Events**

```java
// application.yml
spring:
  jpa:
    hibernate:
      ddl-auto: validate
    show-sql: false

// Outbox Table
@Entity
@Table(name = "event_outbox")
public class EventOutbox {
  @Id
  private UUID eventId;
  private String eventType;
  @Column(columnDefinition = "TEXT")
  private String eventPayload;
  private Boolean published = false;
  private LocalDateTime createdAt;
  private LocalDateTime publishedAt;
}

// Event Publisher (with Outbox)
@Service
public class OutboxEventPublisher {
  @Transactional
  public void publish(DomainEvent event) {
    // 1. Persist event to outbox
    var outbox = new EventOutbox(event);
    outboxRepository.save(outbox);
    
    // 2. Publish locally (synchronous)
    applicationEventPublisher.publishEvent(event);
  }
}

// Poll Job (Scheduled)
@Component
public class OutboxPoller {
  @Scheduled(fixedDelay = 10000) // Every 10 seconds
  public void pollUnpublished() {
    var unpublished = outboxRepository.findByPublishedFalse();
    for (var outbox : unpublished) {
      try {
        eventPublisher.publishToExternalBroker(outbox.getEvent());
        outbox.setPublished(true);
        outboxRepository.save(outbox);
      } catch (Exception e) {
        logger.warn("Failed to publish event: " + outbox.eventId, e);
        // Retry on next poll
      }
    }
  }
}
```

