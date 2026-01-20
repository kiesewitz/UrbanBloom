# Application Layer - Lending Context

**Zweck:** Use Cases und Orchestrierung der Domain-Logik

---

## Übersicht

Die Application Layer orchestriert Domain-Objekte und koordiniert komplexe Use Cases. Sie ist die **Brücke** zwischen den Adaptern (REST, Persistence) und der Domain Layer.

**Verantwortlichkeiten:**
- ✅ Use Case-Implementierung (Commands & Queries)
- ✅ Orchestrierung von Aggregates und Domain Services
- ✅ Transaktionsgrenzen definieren (`@Transactional`)
- ✅ Domain Events publizieren
- ❌ **KEINE Business-Logik** (die gehört ins Domain Model)

---

## Unterordner

### 📦 `command/`

**Zweck:** Write Commands (DTOs für Schreib-Operationen)

Commands sind **immutable DTOs**, die Schreib-Operationen repräsentieren.

**Beispiele:**
```java
@Value
@Builder
public class CheckoutCommand {
    String userId;
    String mediaBarcode;
}

@Value
@Builder
public class RenewLoanCommand {
    UUID loanId;
}

@Value
@Builder
public class ReturnCommand {
    UUID loanId;
    LocalDate returnDate;
}
```

**Best Practices:**
- ✅ Immutable (`@Value`)
- ✅ Validierung mit Bean Validation (`@NotNull`, `@NotBlank`)
- ✅ Business-Intent im Namen (`CheckoutCommand`, nicht `CreateLoanCommand`)

---

### 📦 `query/`

**Zweck:** Read Queries (DTOs für Lese-Operationen)

Queries sind **immutable DTOs** für Lese-Operationen (CQRS-Pattern).

**Beispiele:**
```java
@Value
@Builder
public class FindLoansByUserQuery {
    String userId;
}

@Value
@Builder
public class FindOverdueLoansQuery {
    LocalDate asOfDate;
}

@Value
@Builder
public class FindActiveReservationsQuery {
    String userId;
}
```

**Best Practices:**
- ✅ Separate Queries von Commands (CQRS)
- ✅ Read-only Intention klar im Namen
- ✅ Optional: Pagination-Parameter (`page`, `size`)

---

### 📦 `service/`

**Zweck:** Application Services (Use Case Orchestration)

Application Services sind **Entry Points** für Use Cases. Sie orchestrieren Domain-Objekte und Domain Services.

**Beispiel:**
```java
@Service
@RequiredArgsConstructor
public class CheckoutApplicationService {
    
    private final LoanRepository loanRepository;
    private final UserRepository userRepository;
    private final MediaRepository mediaRepository;
    private final LoanEligibilityChecker eligibilityChecker;
    private final ApplicationEventPublisher eventPublisher;
    
    @Transactional
    public Loan checkout(CheckoutCommand command) {
        // 1. Load Aggregates
        User user = userRepository.findById(new UserId(command.getUserId()))
            .orElseThrow(() -> new UserNotFoundException(command.getUserId()));
        
        MediaCopy copy = mediaRepository.findByBarcode(new Barcode(command.getMediaBarcode()))
            .orElseThrow(() -> new MediaCopyNotFoundException(command.getMediaBarcode()));
        
        // 2. Delegate to Domain Service (Business Rule Check)
        if (!eligibilityChecker.canCheckout(user, copy)) {
            throw new CheckoutNotAllowedException("User not eligible");
        }
        
        // 3. Create Aggregate (Business Logic in Domain Model)
        Loan loan = Loan.checkout(user.getId(), copy.getBarcode(), user.getLoanPolicy());
        
        // 4. Persist
        Loan savedLoan = loanRepository.save(loan);
        
        // 5. Publish Domain Event
        eventPublisher.publishEvent(LoanCreatedEvent.builder()
            .loanId(savedLoan.getId().getValue())
            .userId(user.getId().getValue())
            .mediaBarcode(copy.getBarcode().getValue())
            .dueDate(savedLoan.getDueDate().getDate())
            .occurredAt(Instant.now())
            .build());
        
        return savedLoan;
    }
}
```

**Best Practices:**
- ✅ **Eine Methode pro Use Case** (nicht mehrere Use Cases in einem Service)
- ✅ **`@Transactional`** auf Service-Methoden (Application Service = Transaction Boundary)
- ✅ **Orchestration only:** Delegiere Business-Logik an Domain-Objekte
- ✅ **Event Publishing:** Nach erfolgreicher Transaktion
- ✅ **Exception Handling:** Wirf Domain Exceptions (aus `shared` Modul)

---

## CQRS Pattern

Diese Layer trennt **Commands** (Write) und **Queries** (Read):

```
Commands (Write)                    Queries (Read)
     |                                   |
     v                                   v
CheckoutApplicationService      LoanQueryService
     |                                   |
     v                                   v
LoanRepository (write)          LoanRepository (read-optimized)
```

**Vorteile:**
- ✅ Separate Read/Write-Optimierung
- ✅ Klare Intent-Trennung
- ✅ Einfachere Evolution (z.B. Event Sourcing später)

---

## Transaction Boundaries

Application Services definieren **Transaktionsgrenzen**:

```java
@Transactional // ← Transaction Boundary
public Loan checkout(CheckoutCommand command) {
    // Alles innerhalb dieser Methode läuft in einer Transaktion
    // Bei Exception: Rollback
}
```

**Wichtig:**
- ✅ Eine Transaktion pro Use Case
- ✅ `@Transactional` nur auf Application Service-Methoden
- ❌ Keine `@Transactional` im Domain Layer
- ❌ Keine `@Transactional` in Controllern

---

## Event Publishing

Application Services sind verantwortlich für das Publizieren von Domain Events **nach erfolgreicher Persistierung**:

```java
@Transactional
public Loan returnMedia(ReturnCommand command) {
    // 1. Business Logic
    Loan loan = loanRepository.findById(command.getLoanId())
        .orElseThrow(...);
    loan.returnMedia(command.getReturnDate());
    loanRepository.save(loan);
    
    // 2. Process Waitlist (Domain Service)
    List<PreReservation> waitlist = waitlistRepository.findByMediaBarcode(loan.getMediaBarcode());
    Optional<Reservation> reservation = waitlistProcessor.processNextInQueue(waitlist, mediaCopy);
    reservation.ifPresent(reservationRepository::save);
    
    // 3. Publish Events (after successful transaction)
    eventPublisher.publishEvent(LoanReturnedEvent.builder()
        .loanId(loan.getId().getValue())
        .userId(loan.getUserId().getValue())
        .mediaBarcode(loan.getMediaBarcode().getValue())
        .returnDate(command.getReturnDate())
        .wasOverdue(loan.wasOverdue(command.getReturnDate()))
        .occurredAt(Instant.now())
        .build());
    
    return loan;
}
```

**Event Subscribers:**
Events werden von anderen Contexts konsumiert (z.B. Notification, Reminding).

---

## Testing

**Integration Tests mit Testcontainers:**
```java
@SpringBootTest
@Testcontainers
class CheckoutApplicationServiceIT {
    
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16");
    
    @Autowired
    private CheckoutApplicationService checkoutService;
    
    @Test
    void shouldCreateLoanWhenUserEligible() {
        // Given
        CheckoutCommand command = CheckoutCommand.builder()
            .userId("user123")
            .mediaBarcode("123456")
            .build();
        
        // When
        Loan loan = checkoutService.checkout(command);
        
        // Then
        assertThat(loan).isNotNull();
        assertThat(loan.getStatus()).isEqualTo(LoanStatus.CHECKED_OUT);
    }
}
```

---

## Anti-Patterns (vermeiden!)

❌ **Business-Logik im Application Service:**
```java
// FALSCH: Geschäftsregel im Application Service
@Transactional
public Loan renewLoan(RenewLoanCommand command) {
    Loan loan = loanRepository.findById(command.getLoanId()).orElseThrow();
    
    // ❌ Business-Logik gehört ins Domain Model
    if (loan.getRenewalCount() >= 2) {
        throw new RenewalNotAllowedException();
    }
    loan.setDueDate(loan.getDueDate().plusDays(21));
    loan.setRenewalCount(loan.getRenewalCount() + 1);
    
    return loanRepository.save(loan);
}
```

✅ **Richtig: Delegation an Domain Model:**
```java
// RICHTIG: Application Service orchestriert nur
@Transactional
public Loan renewLoan(RenewLoanCommand command) {
    Loan loan = loanRepository.findById(command.getLoanId()).orElseThrow();
    LoanPolicy policy = policyRepository.findByUserGroup(loan.getUserGroup());
    
    loan.renew(policy); // ✅ Business-Logik im Domain Model
    
    return loanRepository.save(loan);
}
```

---

## Referenzen

- 📖 [Lending Context README](../README.md)
- 🎯 [Domain Layer README](../domain/README.md)
