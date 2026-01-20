# Module: Catalog Context (Supporting Subdomain)

**Domänen-Kategorie:** Supporting Subdomain  
**Architekturmuster:** Vereinfachte Schichtenarchitektur (Domain + Application + Adapter)  
**Kritikalität:** ⭐⭐ (Medium)

## Übersicht

Der **Catalog Context** verwaltet den Medienbestand der Schulbibliothek. Er bietet CRUD-Operationen für Medien und deren Exemplare sowie Such- und Filterfunktionen.

### Verantwortlichkeiten

- ✅ Verwaltung von Medienwerken (`Media`)
- ✅ Verwaltung von physischen Exemplaren (`MediaCopy`)
- ✅ Kategorisierung und Klassifikation
- ✅ Suche und Filterung (Titel, Autor, ISBN, Kategorie)
- ✅ Verfügbarkeitsstatus-Verwaltung

## Architektur-Struktur

Dieser Context nutzt eine **vereinfachte Schichtenarchitektur** (keine volle Hexagonale Architektur):

```
module-catalog/
├── domain/                      # Domain Model (einfach)
│   ├── Media (Entity)
│   ├── MediaCopy (Entity)
│   └── Category (Value Object)
│
├── application/                 # Application Services (CRUD)
│   └── CatalogService
│
├── adapter/
│   ├── in/rest/                # REST API
│   └── out/persistence/        # JPA Repository
│
└── config/                      # Spring Configuration
```

**Warum vereinfacht?**
- ❌ Keine komplexe Business-Logik
- ❌ Keine speziellen Domain Services
- ✅ Standard CRUD-Operationen
- ✅ Fokus auf schnelle Umsetzung

---

## Domain Layer

### 📦 `domain/`

**Wichtige Klassen:**
- `Media` (Entity) - Medienwerk (Buch, DVD, etc.)
- `MediaCopy` (Entity) - Physisches Exemplar
- `Category` (Value Object) - Kategorie (Belletristik, Sachbuch, etc.)
- `ISBN` (Value Object) - ISBN-Nummer
- `AvailabilityStatus` (Enum) - `AVAILABLE`, `CHECKED_OUT`, `RESERVED`

**Beispiel:**
```java
@Data
@Builder
public class Media {
    private UUID id;
    private String title;
    private String author;
    private ISBN isbn;
    private Category category;
    private List<MediaCopy> copies;
    
    public int getAvailableCopiesCount() {
        return (int) copies.stream()
            .filter(copy -> copy.getStatus() == AvailabilityStatus.AVAILABLE)
            .count();
    }
}

@Data
@Builder
public class MediaCopy {
    private UUID id;
    private Barcode barcode;
    private UUID mediaId;
    private AvailabilityStatus status;
    
    public void markAsCheckedOut() {
        this.status = AvailabilityStatus.CHECKED_OUT;
    }
    
    public void markAsAvailable() {
        this.status = AvailabilityStatus.AVAILABLE;
    }
}
```

**Hinweis:** Domain-Logik ist minimal, da es hauptsächlich CRUD ist.

---

## Application Layer

### 📦 `application/`

**Zweck:** CRUD-Operationen für Medien und Exemplare

**Wichtige Services:**
- `CatalogService` - Media CRUD
- `MediaCopyService` - MediaCopy CRUD
- `SearchService` - Suche und Filterung

**Beispiel:**
```java
@Service
@RequiredArgsConstructor
public class CatalogService {
    
    private final MediaRepository mediaRepository;
    
    @Transactional
    public Media createMedia(CreateMediaCommand command) {
        Media media = Media.builder()
            .id(UUID.randomUUID())
            .title(command.getTitle())
            .author(command.getAuthor())
            .isbn(new ISBN(command.getIsbn()))
            .category(new Category(command.getCategory()))
            .copies(new ArrayList<>())
            .build();
        
        return mediaRepository.save(media);
    }
    
    @Transactional
    public Media updateMedia(UUID id, UpdateMediaCommand command) {
        Media media = mediaRepository.findById(id)
            .orElseThrow(() -> new MediaNotFoundException(id));
        
        media.setTitle(command.getTitle());
        media.setAuthor(command.getAuthor());
        media.setCategory(new Category(command.getCategory()));
        
        return mediaRepository.save(media);
    }
    
    public Optional<Media> findById(UUID id) {
        return mediaRepository.findById(id);
    }
    
    public List<Media> searchByTitle(String title) {
        return mediaRepository.findByTitleContainingIgnoreCase(title);
    }
}
```

---

## Adapter Layer

### 📦 `adapter/in/rest/`

**Zweck:** REST API für Medien und Exemplare

**Endpoints:**
- `GET /api/v1/media` - Liste aller Medien
- `GET /api/v1/media/{id}` - Medium nach ID
- `POST /api/v1/media` - Neues Medium erstellen
- `PUT /api/v1/media/{id}` - Medium aktualisieren
- `DELETE /api/v1/media/{id}` - Medium löschen
- `GET /api/v1/media/search?title=...` - Suche nach Titel

**Beispiel:**
```java
@RestController
@RequestMapping("/api/v1/media")
@RequiredArgsConstructor
public class MediaController {
    
    private final CatalogService catalogService;
    private final MediaMapper mapper;
    
    @GetMapping
    @PreAuthorize("hasAnyRole('STUDENT', 'TEACHER', 'LIBRARIAN')")
    public ResponseEntity<List<MediaDto>> findAll() {
        List<Media> media = catalogService.findAll();
        return ResponseEntity.ok(mapper.toDtoList(media));
    }
    
    @PostMapping
    @PreAuthorize("hasRole('LIBRARIAN')")
    public ResponseEntity<MediaDto> create(@Valid @RequestBody CreateMediaRequestDto request) {
        CreateMediaCommand command = mapper.toCommand(request);
        Media media = catalogService.createMedia(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toDto(media));
    }
    
    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('STUDENT', 'TEACHER', 'LIBRARIAN')")
    public ResponseEntity<List<MediaDto>> search(@RequestParam String title) {
        List<Media> media = catalogService.searchByTitle(title);
        return ResponseEntity.ok(mapper.toDtoList(media));
    }
}
```

### 📦 `adapter/out/persistence/`

**Zweck:** JPA Persistence

**Beispiel:**
```java
@Entity
@Table(name = "media", schema = "catalog_schema")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MediaJpaEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(nullable = false)
    private String title;
    
    @Column(nullable = false)
    private String author;
    
    @Column(unique = true)
    private String isbn;
    
    @Column(nullable = false)
    private String category;
    
    @OneToMany(mappedBy = "media", cascade = CascadeType.ALL)
    private List<MediaCopyJpaEntity> copies = new ArrayList<>();
}

@Repository
public interface MediaJpaRepository extends JpaRepository<MediaJpaEntity, UUID> {
    List<MediaJpaEntity> findByTitleContainingIgnoreCase(String title);
    List<MediaJpaEntity> findByAuthorContainingIgnoreCase(String author);
    Optional<MediaJpaEntity> findByIsbn(String isbn);
    List<MediaJpaEntity> findByCategory(String category);
}
```

---

## Geschäftsregeln

- ✅ ISBN muss eindeutig sein
- ✅ Jedes Medium hat mindestens 1 Exemplar
- ✅ Exemplare haben eindeutige Barcodes
- ✅ Status wird von Lending Context gesteuert (via Events)

---

## Domain Events

### Consumed Events (von anderen Contexts)

| Event | Trigger | Action |
|-------|---------|--------|
| `LoanCreatedEvent` | Ausleihe erstellt | MediaCopy-Status auf `CHECKED_OUT` setzen |
| `LoanReturnedEvent` | Medium zurückgegeben | MediaCopy-Status auf `AVAILABLE` setzen |
| `ReservationCreatedEvent` | Reservierung erstellt | MediaCopy-Status auf `RESERVED` setzen |

**Event Listener Beispiel:**
```java
@Component
@RequiredArgsConstructor
public class LendingEventListener {
    
    private final MediaCopyService mediaCopyService;
    
    @EventListener
    @Transactional
    public void onLoanCreated(LoanCreatedEvent event) {
        mediaCopyService.markAsCheckedOut(new Barcode(event.getMediaBarcode()));
    }
    
    @EventListener
    @Transactional
    public void onLoanReturned(LoanReturnedEvent event) {
        mediaCopyService.markAsAvailable(new Barcode(event.getMediaBarcode()));
    }
}
```

---

## Datenbankschema

Dieses Modul verwendet das Schema: **`catalog_schema`**

Flyway-Migrationen: `host-application/src/main/resources/db/migration/V1__catalog_*.sql`

---

## Testing

- **Unit Tests:** Für einfache Domain-Logik
- **Integration Tests:** Mit Testcontainers für Repository
- **REST Tests:** MockMvc für Controller

---

## Abhängigkeiten

- **Erlaubt:** `shared` Modul
- **Verboten:** Keine Abhängigkeiten zu anderen Modulen

---

## Referenzen

- 📖 [Strategic Architecture Summary](../../docs/architecture/strategic-architecture-summary.md)
- 🗺️ [Bounded Contexts Map](../../docs/architecture/bounded-contexts-map.md)
