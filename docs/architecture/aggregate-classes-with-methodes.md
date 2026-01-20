# Aggregatklassen mit Methoden

## LENDING CONTEXT - Methoden-Zuordnung

### Aggregat: Loan

**Prinzip**: Die Logik sitzt SO NAH wie möglich bei den Daten, die sie verändert.

```java
class Loan {
  
  // ============ FACTORY METHODS (Konstruktion) ============
  
  static Loan checkout(
    userId: UUID, 
    mediaId: UUID, 
    mediaCopyBarcode: String,
    loanPolicy: LoanPolicy,
    userGroup: UserGroup
  ) → Loan {
    // Geschäftslogik:
    //  1. dueDate berechnen basierend auf LoanPolicy + UserGroup
    //  2. checkoutDate = TODAY
    //  3. status = Active
    //  4. renewalCount = 0
    //  5. Validierung: Alle Invarianten prüfen
    // 
    // Use Case: US-004 (Ausleihe Admin)
    // Domain Event: MediaCheckedOut (wird vom ApplicationService publiziert)
  }

  // ============ CORE BUSINESS METHODS ============
  
  void return(
    returnDate: LocalDate = TODAY
  ) {
    // Geschäftslogik:
    //  1. Prüfung: status == Active? (sonst Exception)
    //  2. this.returnDate = returnDate
    //  3. Berechne: isOverdue = (returnDate > dueDate)
    //  4. Falls overdue: overdueDays = returnDate - dueDate
    //  5. this.status = Returned
    //
    // Invarianten:
    //  ✓ returnDate >= checkoutDate
    //  ✓ returnDate <= TODAY
    //
    // Use Case: US-006 (Rückgabe Admin)
    // Domain Event: MediaReturned (wird vom ApplicationService publiziert)
  }

  void renew(
    renewalPolicy: RenewalPolicy,
    loanPolicy: LoanPolicy,
    existingPreReservations: List<PreReservation>
  ) {
    // Geschäftslogik:
    //  1. Guard: renewalCount < renewalPolicy.maxRenewals? → Exception
    //  2. Guard: existingPreReservations.isEmpty()? → Exception
    //  3. Guard: TODAY < this.dueDate? → Exception
    //  4. Guard: status == Active? → Exception
    //  5. newDueDate = dueDate + renewalPolicy.durationDays
    //  6. this.dueDate = newDueDate
    //  7. this.renewalCount++
    //
    // Invarianten:
    //  ✓ Neue dueDate > alte dueDate
    //  ✓ renewalCount bleibt <= max
    //
    // Use Case: US-003 (implizit in Renewal-Flow)
    // Domain Event: LoanRenewed (wird vom ApplicationService publiziert)
  }

  boolean isEligibleForRenewal(
    renewalPolicy: RenewalPolicy,
    existingPreReservations: List<PreReservation>
  ) → Boolean {
    // Rückgabe: renewalCount < renewalPolicy.maxRenewals
    //        && existingPreReservations.isEmpty()
    //        && status == Active
    //        && TODAY < dueDate
  }

  boolean isOverdue() → Boolean {
    return TODAY > dueDate && status == Active;
  }

  Integer calculateOverdueDays() → Integer {
    return max(0, TODAY - dueDate);
  }

  // ============ QUERIES (Read-Only) ============
  
  LocalDate getDueDate() → LocalDate {
    return this.dueDate;
  }

  Integer getRenewalCount() → Integer {
    return this.renewalCount;
  }

  LoanStatus getStatus() → LoanStatus {
    return this.status;
  }

  Boolean getIsOverdue() → Boolean {
    return this.isOverdue();
  }
}
```

### Aggregat: PreReservation

```java
class PreReservation {

  // ============ FACTORY METHODS ============
  
  static PreReservation preReserve(
    userId: UUID,
    mediaId: UUID,
    currentWaitlistSize: Integer
  ) → PreReservation {
    // Geschäftslogik:
    //  1. position = currentWaitlistSize + 1
    //  2. createdAt = NOW
    //  3. status = Waiting
    //  4. estimatedAvailableDate = null (wird später geschätzt)
    //
    // Use Case: US-003 (Vormerkung verliehen)
    // Domain Event: PreReservationCreated
  }

  // ============ CORE BUSINESS METHODS ============
  
  void resolve() {
    // Geschäftslogik:
    //  1. Prüfung: status == Waiting? (sonst Exception)
    //  2. this.status = Resolved
    //  3. this.resolvedAt = NOW
    //
    // Use Case: Internal (bei MediaReturn)
    // Domain Event: PreReservationResolved
  }

  void cancel() {
    // Geschäftslogik:
    //  1. Prüfung: status == Waiting? (sonst Exception)
    //  2. this.status = Cancelled
    //  3. Andere PreReservations mit höherer Position: position-- (Nachschieben)
    //
    // Use Case: User cancels waitlist entry
    // Domain Event: PreReservationCancelled (optional)
  }

  void updatePosition(newPosition: Integer) {
    // Geschäftslogik:
    //  1. Nur intern nach cancel() aufrufen
    //  2. this.position = newPosition
  }

  void setEstimatedAvailableDate(estimatedDate: LocalDate) {
    this.estimatedAvailableDate = estimatedDate;
  }

  // ============ QUERIES ============
  
  Integer getPosition() → Integer {
    return this.position;
  }

  Boolean isWaiting() → Boolean {
    return status == Waiting;
  }
}
```

### Aggregat: ClassSet

```java
class ClassSet {

  // ============ FACTORY METHODS ============
  
  static ClassSet checkoutClassSet(
    teacherUserId: UUID,
    className: String,
    setMembers: List<SetMember>,  // SetMember mit mediaCopyBarcode
    classSetPolicy: ClassSetPolicy
  ) → ClassSet {
    // Geschäftslogik:
    //  1. Validierung: teacherUserId muss Teacher sein
    //  2. Validierung: setMembers.count >= 1
    //  3. Validierung: Keine Duplikate in barcodes
    //  4. this.setMembers = setMembers
    //  5. checkoutDate = TODAY
    //  6. dueDate = TODAY + classSetPolicy.duration
    //  7. status = Active
    //  8. isComplete = false
    //
    // Use Case: US-009 (Klassensatz Ausleihe)
    // Domain Event: ClassSetCheckedOut
  }

  // ============ CORE BUSINESS METHODS ============
  
  void markSetMemberReturned(
    barcode: String,
    returnDate: LocalDate = TODAY
  ) {
    // Geschäftslogik:
    //  1. Finde SetMember mit barcode
    //  2. Validierung: status == CheckedOut? (sonst Exception)
    //  3. setMember.status = Returned
    //  4. setMember.returnedAt = returnDate
    //  5. recalculateCompleteness()
    //
    // Use Case: US-006 (Rückgabe Klassensatz)
  }

  void markSetMemberMissing(barcode: String) {
    // Geschäftslogik:
    //  1. Finde SetMember mit barcode
    //  2. setMember.status = Missing
    //  3. Flag für Admin-Nachverfolgung
    //  4. recalculateCompleteness()
  }

  private void recalculateCompleteness() {
    // Geschäftslogik:
    //  1. isComplete = setMembers.all(m → m.status == Returned)?
    //  2. status = (isComplete) ? Returned : PartiallyReturned
  }

  void returnClassSet() {
    // Geschäftslogik:
    //  1. Validierung: isComplete == true? (sonst Exception)
    //  2. returnDate = TODAY
    //  3. status = Returned
    //
    // Domain Event: ClassSetReturned
  }

  // ============ QUERIES ============
  
  Boolean isComplete() → Boolean {
    return this.isComplete;
  }

  Integer getTotalSetMembers() → Integer {
    return this.setMembers.size();
  }

  Integer getReturnedSetMembers() → Integer {
    return this.setMembers.filter(m → m.status == Returned).size();
  }

  List<SetMember> getMissingSetMembers() → List<SetMember> {
    return this.setMembers.filter(m → m.status != Returned);
  }

  LocalDate getDueDate() → LocalDate {
    return this.dueDate;
  }
}
```

### Aggregat: Fine

```java
class Fine {

  // ============ FACTORY METHODS ============

  static Fine create(
    userId: UUID,
    loanId: UUID,
    amount: Money,
    reason: FineReason
  ) → Fine {
    // Geschäftslogik:
    //  1. Validierung: amount > 0
    //  2. status = Open
    //  3. createdAt = NOW
    //
    // Domain Event: FineCreated
  }

  // ============ CORE BUSINESS METHODS ============

  void pay() {
    // Geschäftslogik:
    //  1. Guard: status == Open? → Exception
    //  2. status = Paid
    //  3. paidAt = NOW
    //
    // Domain Event: FinePaid
  }

  void waive(reason: String) {
    // Geschäftslogik:
    //  1. Guard: status == Open? → Exception
    //  2. status = Waived
    //  3. waivedReason = reason
    //
    // Domain Event: FineWaived
  }

  // ============ QUERIES ============

  boolean isPaid() → Boolean {
    return status == Paid;
  }

  boolean isOpen() → Boolean {
    return status == Open;
  }
}
```

## CATALOG CONTEXT - Methodenzuordnung

### Aggregat: Media


```java
class Media {

  // ============ FACTORY METHODS ============
  
  static Media addMedia(
    title: String,
    author: String,
    isbn: String?,
    category: MediaCategory,
    publicationYear: Integer?
  ) → Media {
    // Geschäftslogik:
    //  1. Validierung: title NOT NULL
    //  2. Validierung: ISBN-13 format (falls gesetzt)
    //  3. Validierung: publicationYear <= aktuelles Jahr
    //  4. mediaId = UUID.randomUUID()
    //  5. totalCopies = 0 (wird von außen erhöht)
    //  6. availableCopies = 0
    //
    // Use Case: Admin-Portal (Katalog pflegen)
  }

  // ============ CORE BUSINESS METHODS ============
  
  void addCopy() {
    // Geschäftslogik:
    //  1. this.totalCopies++
    //  2. this.availableCopies++ (neue Kopie ist verfügbar)
    //
    // Use Case: Admin-Portal
  }

  void removeCopy() {
    // Geschäftslogik:
    //  1. Validierung: totalCopies > 0?
    //  2. Validierung: availableCopies > 0?
    //  3. this.totalCopies--
    //  4. this.availableCopies--
  }

  void updateAvailableCopies(newCount: Integer) {
    // Geschäftslogik:
    //  1. Validierung: 0 <= newCount <= totalCopies
    //  2. this.availableCopies = newCount
    //
    // Use Case: Event-Handler (MediaCheckedOut, MediaReturned)
  }

  // ============ QUERIES ============
  
  Boolean isAvailable() → Boolean {
    return availableCopies > 0;
  }

  Integer getAvailableCopies() → Integer {
    return this.availableCopies;
  }

  String getTitle() → String {
    return this.title;
  }

  String getAuthor() → String {
    return this.author;
  }
}
```

### Aggregat: MediaCopy

```java
class MediaCopy {

  // ============ FACTORY METHODS ============
  
  static MediaCopy addCopy(
    mediaId: UUID,
    barcode: String,
    condition: MediaCondition = Excellent
  ) → MediaCopy {
    // Geschäftslogik:
    //  1. Validierung: barcode ist UNIQUE
    //  2. this.mediaId = mediaId
    //  3. this.barcode = barcode
    //  4. this.availabilityStatus = Available
    //  5. this.condition = condition
    //  6. this.acquisitionDate = TODAY
    //
    // Use Case: Admin-Portal
  }

  // ============ CORE BUSINESS METHODS ============
  
  void updateAvailabilityStatus(newStatus: AvailabilityStatus) {
    // Geschäftslogik:
    //  1. Validierung: Valid State Transition erlaubt?
    //  2. this.availabilityStatus = newStatus
    //  3. this.lastModifiedAt = NOW
    //
    // Use Case: Event-Handler (MediaCheckedOut, MediaReturned, etc.)
  }

  void markDamaged(notes: String?) {
    // Geschäftslogik:
    //  1. this.availabilityStatus = Damaged (terminal)
    //  2. this.condition = Poor
    //  3. this.notes += "Marked damaged: " + notes
    //  4. this.lastModifiedAt = NOW
    //
    // Domain Event: MediaCopyDamaged (optional)
  }

  void updateCondition(newCondition: MediaCondition) {
    // Geschäftslogik:
    //  1. Validierung: newCondition >= current condition (kann nur verschlechtern)
    //  2. this.condition = newCondition
    //  3. IF newCondition == Poor: Suggestion Damaged markieren
  }

  void updateShelfLocation(location: String?) {
    // Geschäftslogik:
    //  1. this.shelfLocation = location
    //  2. this.lastModifiedAt = NOW
  }

  void updateInventory(lastCheckDate: LocalDate) {
    // Geschäftslogik:
    //  1. this.lastInventoryCheck = lastCheckDate
    //  2. this.lastModifiedAt = NOW
  }

  // ============ QUERIES ============
  
  Boolean isAvailable() → Boolean {
    return availabilityStatus == Available && condition != Poor;
  }

  Boolean isDamaged() → Boolean {
    return availabilityStatus == Damaged;
  }

  AvailabilityStatus getStatus() → AvailabilityStatus {
    return this.availabilityStatus;
  }

  String getBarcode() → String {
    return this.barcode;
  }
}
```

## USER CONTEXT - Methodenzuordnung

### Aggregat: User

```java
class User {

  // ============ FACTORY METHODS ============
  
  static User createUserFromSSO(
    schoolIdentity: String,  // z.B. max.mustermann@schulbib.de
    firstName: String,
    lastName: String,
    email: String,
    userGroup: UserGroup
  ) → User {
    // Geschäftslogik:
    //  1. Validierung: schoolIdentity ist UNIQUE
    //  2. Validierung: userGroup ist gültig (Student, Teacher, Librarian)
    //  3. userId = UUID.randomUUID()
    //  4. this.schoolIdentity = schoolIdentity (IMMUTABLE)
    //  5. this.userGroup = userGroup (READ-ONLY vom SSO)
    //  6. this.borrowingLimit = calculateBorrowingLimit(userGroup)
    //  7. this.isActive = true
    //  8. this.registrationDate = TODAY
    //
    // Use Case: SSO-Integration (First Login)
  }

  // ============ CORE BUSINESS METHODS ============
  
  void updateProfile(
    firstName: String?,
    lastName: String?,
    email: String?
  ) {
    // Geschäftslogik:
    //  1. Nur Admin darf ändern (Authorization außerhalb dieses Aggregats!)
    //  2. IF firstName: this.firstName = firstName
    //  3. IF lastName: this.lastName = lastName
    //  4. IF email: Validierung RFC5322, dann this.email = email
    //  5. this.lastModifiedAt = NOW
    //
    // Use Case: Admin-Portal (Profil-Korrektur)
  }

  void deactivate() {
    // Geschäftslogik:
    //  1. this.isActive = false
    //  2. this.lastModifiedAt = NOW
    //  3. Effekt: Keine neuen Loans möglich
    //
    // Use Case: Admin-Portal (User sperren)
  }

  void activate() {
    // Geschäftslogik:
    //  1. this.isActive = true
    //  2. this.lastModifiedAt = NOW
  }

  void recordLogin() {
    // Geschäftslogik:
    //  1. this.lastLoginAt = NOW
    //  2. this.lastModifiedAt = NOW
    //
    // Use Case: SSO-Authentifizierung
  }

  void syncWithSSO(
    userGroupFromSSO: UserGroup,
    firstNameFromSSO: String?,
    lastNameFromSSO: String?
  ) {
    // Geschäftslogik:
    //  1. IF userGroupFromSSO changed: this.userGroup = newGroup
    //  2. IF newGroup changed: recalculate borrowingLimit
    //  3. IF firstName from SSO: this.firstName = firstName (Update)
    //  4. IF lastName from SSO: this.lastName = lastName (Update)
    //  5. schoolIdentity bleibt IMMUTABLE
    //
    // Use Case: SSO-Sync (regelmäßig)
  }

  // ============ QUERIES ============
  
  Boolean isActive() → Boolean {
    return this.isActive;
  }

  Integer getBorrowingLimit() → Integer {
    return this.borrowingLimit;
  }

  UserGroup getUserGroup() → UserGroup {
    return this.userGroup;
  }

  String getSchoolIdentity() → String {
    return this.schoolIdentity;
  }

  String getEmail() → String {
    return this.email;
  }

  private Integer calculateBorrowingLimit(userGroup: UserGroup) → Integer {
    return switch(userGroup) {
      Student → 5,
      Teacher → 10,
      Librarian → 999
    };
  }
}
```

## NOTIFICATION CONTEXT - Methodenzuordnung

### Aggregat: Notification

```java
class Notification {

  // ============ FACTORY METHODS ============
  
  static Notification createNotification(
    userId: UUID,
    recipientEmail: String,
    channel: NotificationChannel,
    type: NotificationType,
    subject: String,
    body: String,
    eventId: String
  ) → Notification {
    // Geschäftslogik:
    //  1. Validierung: Noch KEINE Notification für (eventId, userId, type)?
    //  2. Validierung: recipientEmail RFC5322 format
    //  3. notificationId = UUID.randomUUID()
    //  4. this.status = Pending
    //  5. this.retryCount = 0
    //  6. this.maxRetries = 3
    //
    // Use Case: Event-Handler (MediaCheckedOut, etc.)
  }

  // ============ CORE BUSINESS METHODS ============
  
  void markSent() {
    // Geschäftslogik:
    //  1. Validierung: status == Pending?
    //  2. this.status = Sent
    //  3. this.sentAt = NOW
    //  4. this.failureReason = null
    //
    // Use Case: Mail-Service Success Callback
  }

  void recordFailure(reason: String) {
    // Geschäftslogik:
    //  1. this.retryCount++
    //  2. this.failureReason = reason
    //  3. IF retryCount >= maxRetries:
    //       this.status = Failed
    //     ELSE:
    //       status bleibt Pending (für Retry)
    //
    // Use Case: Mail-Service Error Callback
  }

  void retry() {
    // Geschäftslogik:
    //  1. Validierung: status == Pending && retryCount < maxRetries?
    //  2. Retry-Logic wird vom Infrastructure Layer gehandlet
    //
    // Use Case: Retry-Job
  }

  // ============ QUERIES ============
  
  Boolean isPending() → Boolean {
    return status == Pending;
  }

  Boolean isFailed() → Boolean {
    return status == Failed;
  }

  Boolean canRetry() → Boolean {
    return status == Pending && retryCount < maxRetries;
  }

  String getEventId() → String {
    return this.eventId;
  }
}
```

## REMINDING CONTEXT - Methoden-Zuordnung

### Aggregat: ReminderCampaign

```java
class ReminderCampaign {

  // ============ FACTORY METHODS ============
  
  static ReminderCampaign startCampaign(
    executionDate: LocalDate = TODAY,
    executionTime: LocalTime,
    reminderPolicy: ReminderPolicy
  ) → ReminderCampaign {
    // Geschäftslogik:
    //  1. campaignId = UUID.randomUUID()
    //  2. this.executionDate = executionDate
    //  3. this.status = Running
    //  4. this.startedAt = NOW
    //  5. this.totalLoansChecked = 0
    //  6. this.remindersTriggered = 0
    //
    // Use Case: Scheduled Job (täglich um 08:00)
  }

  // ============ CORE BUSINESS METHODS ============
  
  void addLoanChecked() {
    // Geschäftslogik:
    //  1. totalLoansChecked++
    //
    // Use Case: Internal (pro Loan gescannt)
  }

  void addReminderTriggered() {
    // Geschäftslogik:
    //  1. remindersTriggered++
    //
    // Use Case: Internal (pro getriggertem Reminder)
  }

  void complete() {
    // Geschäftslogik:
    //  1. Validierung: status == Running?
    //  2. this.status = Completed
    //  3. this.completedAt = NOW
    //
    // Use Case: Job-Ende
  }

  void fail(errorMessage: String) {
    // Geschäftslogik:
    //  1. Validierung: status == Running?
    //  2. this.status = Failed
    //  3. this.errorMessage = errorMessage
    //  4. this.completedAt = NOW
    //  5. Log Error für Admin
    //
    // Use Case: Job-Fehler
  }

  // ============ QUERIES ============
  
  Boolean isRunning() → Boolean {
    return status == Running;
  }

  Integer getTotalChecked() → Integer {
    return this.totalLoansChecked;
  }

  Integer getTotalTriggered() → Integer {
    return this.remindersTriggered;
  }

  Integer getDurationInMinutes() → Integer {
    IF completedAt: return (completedAt - startedAt).toMinutes()
    ELSE: return 0
  }
}
```