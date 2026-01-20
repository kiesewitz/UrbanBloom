# Adapter Layer - Lending Context

**Zweck:** Adapter für externe Systeme (REST, Persistence, Messaging)

---

## Übersicht

Die Adapter Layer implementiert die **Hexagonale Architektur (Ports & Adapters)**. Sie verbindet die Domain Layer mit der Außenwelt:

**Inbound Adapters (`in/`)**: Externe Systeme rufen unsere Anwendung auf
**Outbound Adapters (`out/`)**: Unsere Anwendung ruft externe Systeme auf

```
        ┌──────────────────────────┐
        │   Inbound Adapters (in/) │
        │  - REST Controllers      │
        │  - Event Listeners       │
        └───────────┬──────────────┘
                    │
                    v
        ┌──────────────────────────┐
        │   Application Layer      │
        │   (Use Cases)            │
        └───────────┬──────────────┘
                    │
                    v
        ┌──────────────────────────┐
        │   Outbound Adapters (out)│
        │  - Persistence (JPA)     │
        │  - External APIs         │
        └──────────────────────────┘
```

---

## Unterordner

### 📦 `in/rest/`

**Zweck:** REST API Endpoints (Inbound Adapter)

REST Controllers sind der **Entry Point** für externe Clients (Web-Frontend, Mobile App).

**Beispiel:**
```java
@RestController
@RequestMapping("/api/v1/loans")
@RequiredArgsConstructor
public class LoanController {
    
    private final CheckoutApplicationService checkoutService;
    private final ReturnApplicationService returnService;
    private final LoanQueryService loanQueryService;
    private final LoanMapper mapper;
    
    @PostMapping
    @PreAuthorize("hasAnyRole('STUDENT', 'TEACHER', 'LIBRARIAN')")
    public ResponseEntity<LoanDto> checkout(@Valid @RequestBody CheckoutRequestDto request) {
        CheckoutCommand command = mapper.toCommand(request);
        Loan loan = checkoutService.checkout(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toDto(loan));
    }
    
    @PostMapping("/{id}/return")
    @PreAuthorize("hasAnyRole('STUDENT', 'TEACHER', 'LIBRARIAN')")
    public ResponseEntity<LoanDto> returnMedia(@PathVariable UUID id) {
        ReturnCommand command = new ReturnCommand(id, LocalDate.now());
        Loan loan = returnService.returnMedia(command);
        return ResponseEntity.ok(mapper.toDto(loan));
    }
    
    @GetMapping
    @PreAuthorize("hasAnyRole('STUDENT', 'TEACHER', 'LIBRARIAN')")
    public ResponseEntity<List<LoanDto>> findMyLoans() {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        List<Loan> loans = loanQueryService.findByUserId(new UserId(userId));
        return ResponseEntity.ok(mapper.toDtoList(loans));
    }
}
```

**DTOs (`dto/` Unterordner):**
```java
// Request DTO
@Data
@Builder
public class CheckoutRequestDto {
    @NotBlank
    private String mediaBarcode;
}

// Response DTO
@Data
@Builder
public class LoanDto {
    private UUID id;
    private String userId;
    private String mediaBarcode;
    private LocalDate dueDate;
    private LoanStatus status;
    private int renewalCount;
}
```

**Best Practices:**
- ✅ **Thin Controllers:** Nur Delegation, keine Business-Logik
- ✅ **REST-Konventionen:** `POST /loans`, `GET /loans/{id}`, `POST /loans/{id}/return`
- ✅ **HTTP Status Codes:** 201 (Created), 200 (OK), 404 (Not Found), 400 (Bad Request)
- ✅ **Security:** `@PreAuthorize` für Autorisierung
- ✅ **Validation:** `@Valid` für Request-DTOs
- ✅ **Mapping:** Separate Request/Response DTOs, Mapping über MapStruct

---

### 📦 `out/persistence/`

**Zweck:** Persistenz-Adapter (Outbound Adapter)

Dieser Adapter implementiert die **Repository Ports** aus der Domain Layer und nutzt Spring Data JPA intern.

**Unterordner:**
- `entity/` - JPA Entities (Anemic Model)
- `mapper/` - MapStruct Mapper (Domain ↔ JPA)
- Repository-Implementierungen

**JPA Entity (`entity/`):**
```java
@Entity
@Table(name = "loans", schema = "lending_schema")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoanJpaEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(nullable = false)
    private String userId;
    
    @Column(nullable = false)
    private String mediaBarcode;
    
    @Column(nullable = false)
    private LocalDate dueDate;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LoanStatus status;
    
    @Column(nullable = false)
    private Integer renewalCount;
    
    @Column(nullable = false, updatable = false)
    private Instant createdAt;
    
    @Column(nullable = false)
    private Instant updatedAt;
    
    @PrePersist
    void prePersist() {
        createdAt = Instant.now();
        updatedAt = Instant.now();
    }
    
    @PreUpdate
    void preUpdate() {
        updatedAt = Instant.now();
    }
}
```

**MapStruct Mapper (`mapper/`):**
```java
@Mapper(componentModel = "spring")
public interface LoanPersistenceMapper {
    
    @Mapping(target = "id", expression = "java(new LoanId(entity.getId()))")
    @Mapping(target = "userId", expression = "java(new UserId(entity.getUserId()))")
    @Mapping(target = "mediaBarcode", expression = "java(new Barcode(entity.getMediaBarcode()))")
    @Mapping(target = "dueDate", expression = "java(new DueDate(entity.getDueDate()))")
    Loan toDomain(LoanJpaEntity entity);
    
    @Mapping(target = "id", expression = "java(loan.getId().getValue())")
    @Mapping(target = "userId", expression = "java(loan.getUserId().getValue())")
    @Mapping(target = "mediaBarcode", expression = "java(loan.getMediaBarcode().getValue())")
    @Mapping(target = "dueDate", expression = "java(loan.getDueDate().getDate())")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    LoanJpaEntity toJpaEntity(Loan loan);
}
```

**Repository Adapter:**
```java
@Repository
@RequiredArgsConstructor
public class LoanRepositoryAdapter implements LoanRepository {
    
    private final SpringDataLoanRepository springDataRepository;
    private final LoanPersistenceMapper mapper;
    
    @Override
    public Loan save(Loan loan) {
        LoanJpaEntity entity = mapper.toJpaEntity(loan);
        LoanJpaEntity saved = springDataRepository.save(entity);
        return mapper.toDomain(saved);
    }
    
    @Override
    public Optional<Loan> findById(LoanId id) {
        return springDataRepository.findById(id.getValue())
            .map(mapper::toDomain);
    }
    
    @Override
    public List<Loan> findByUserId(UserId userId) {
        return springDataRepository.findByUserId(userId.getValue())
            .stream()
            .map(mapper::toDomain)
            .toList();
    }
    
    @Override
    public List<Loan> findOverdueLoans(LocalDate today) {
        return springDataRepository.findByDueDateBeforeAndStatus(today, LoanStatus.CHECKED_OUT)
            .stream()
            .map(mapper::toDomain)
            .toList();
    }
}

// Spring Data JPA Repository
interface SpringDataLoanRepository extends JpaRepository<LoanJpaEntity, UUID> {
    List<LoanJpaEntity> findByUserId(String userId);
    List<LoanJpaEntity> findByDueDateBeforeAndStatus(LocalDate date, LoanStatus status);
}
```

**Best Practices:**
- ✅ **Separation:** JPA Entities ≠ Domain Model
- ✅ **Mapping:** MapStruct für Domain ↔ JPA Konvertierung
- ✅ **Adapter-Pattern:** Repository-Adapter implementiert Domain-Repository-Port
- ✅ **Spring Data JPA:** Intern für Queries nutzen
- ✅ **Schema:** `@Table(schema = "lending_schema")` für Multi-Schema-Setup

---

## Dependency Rule

Die Abhängigkeiten zeigen **immer nach innen** (zur Domain Layer):

```
REST Controller (Adapter)
    |
    v
Application Service
    |
    v
Domain Model ← Repository Port (Interface)
    ^
    |
Repository Adapter (Persistence)
```

**Wichtig:**
- ❌ Domain Layer darf **niemals** Adapter Layer kennen
- ✅ Adapter Layer kennt Domain Layer (über Interfaces)

---

## Testing

**REST Controller Tests (MockMvc):**
```java
@WebMvcTest(LoanController.class)
class LoanControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private CheckoutApplicationService checkoutService;
    
    @Test
    void shouldCreateLoan() throws Exception {
        // Given
        CheckoutRequestDto request = CheckoutRequestDto.builder()
            .mediaBarcode("123456")
            .build();
        
        Loan loan = Loan.builder()
            .id(new LoanId(UUID.randomUUID()))
            .userId(new UserId("user123"))
            .mediaBarcode(new Barcode("123456"))
            .dueDate(new DueDate(LocalDate.now().plusDays(21)))
            .status(LoanStatus.CHECKED_OUT)
            .renewalCount(0)
            .build();
        
        when(checkoutService.checkout(any())).thenReturn(loan);
        
        // When & Then
        mockMvc.perform(post("/api/v1/loans")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").exists())
            .andExpect(jsonPath("$.status").value("CHECKED_OUT"));
    }
}
```

**Persistence Tests (Testcontainers):**
```java
@DataJpaTest
@Testcontainers
class LoanRepositoryAdapterIT {
    
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16");
    
    @Autowired
    private SpringDataLoanRepository springDataRepository;
    
    @Test
    void shouldSaveAndFindLoan() {
        // Given
        LoanJpaEntity entity = LoanJpaEntity.builder()
            .userId("user123")
            .mediaBarcode("123456")
            .dueDate(LocalDate.now().plusDays(21))
            .status(LoanStatus.CHECKED_OUT)
            .renewalCount(0)
            .build();
        
        // When
        LoanJpaEntity saved = springDataRepository.save(entity);
        
        // Then
        assertThat(saved.getId()).isNotNull();
        Optional<LoanJpaEntity> found = springDataRepository.findById(saved.getId());
        assertThat(found).isPresent();
    }
}
```

---

## Referenzen

- 📖 [Lending Context README](../README.md)
- 🎯 [Application Layer README](../application/README.md)
- 🔧 [Domain Layer README](../domain/README.md)
