# User Story US-CAT-01-REF: Book Catalog Browsing (Refined)

## Story
**As a** student  
**I want to** browse the school library's book catalog  
**So that** I can discover and find books that interest me

## Priority
**Must-Have** | MVP Phase 1

## UI-Prototyp (Mobile)

- HTML: [büchersuche_&_katalog](../../../ui/prototypes/stitch_schulbibliotheks_app/büchersuche_&_katalog/code.html)
- Bild:

![büchersuche_&_katalog](../../../ui/prototypes/stitch_schulbibliotheks_app/büchersuche_&_katalog/screen.png)

## Refinement Discussion

### Klärungspunkte & Entscheidungen

| Punkt | Entscheidung | Begründung |
|-------|--------------|------------|
| Welche Informationen pro Buch anzeigen? | Titel, Autor, Cover-Bild, Verfügbarkeit, Kategorie, Kurzbeschreibung | Grundlegende Informationen für Entscheidungsfindung |
| Soll die Liste paginiert werden? | Ja, 20 Bücher pro Seite | Performance und Übersichtlichkeit |
| Welche Sortieroptionen? | Alphabetisch (A-Z), Neueste zuerst, Beliebteste | Standard-Sortierungen für Bibliothekskataloge |
| Ansichtsmodus (Grid vs. List)? | Beide, umschaltbar | Flexibilität für verschiedene Präferenzen |
| Lazy Loading für Bilder? | Ja, mit Fallback-Icon | Performance-optimiert mit gutem UX-Fallback |
| Filter nach Kategorien? | Nein im MVP | Wird in Search-Story (US03) behandelt |
| Offline-Verfügbarkeit? | Nein im MVP | Kann in späteren Versionen ergänzt werden |

### Tasks

#### Backend
- Book-Entität mit allen Pflichtfeldern definieren
- Book-Repository mit JPA implementieren
- BookService mit Pagination und Sortierung
- API-Endpoint GET /api/v1/books erstellen
- Pagination-Parameter (page, size) implementieren
- Sortierung-Parameter (sortBy, sortOrder) implementieren
- Verfügbarkeitsstatus berechnen (totalCopies vs. activeBorrowings)
- Default-Cover-URL für Bücher ohne Cover
- OpenAPI-Dokumentation für Endpoint

#### Frontend Web (Web Admin App – nicht zutreffend)
- Diese Story ist ein User-Flow (Schüler/Lehrkraft) und wird in der Mobile App umgesetzt; die Web Admin App bietet hierfür keine UI.

#### Frontend Mobile
- BookCatalogScreen erstellen
- BookListTile-Widget für Buchanzeige
- ListView.builder mit Pagination
- Pull-to-Refresh Funktionalität
- Infinite-Scroll für weitere Seiten
- Sortier-Bottom-Sheet
- Loading-Indicators (CircularProgressIndicator)
- Error-States mit Retry-Button
- cached_network_image für Cover
- Empty-State-Widget

#### Testing
- Unit-Tests für BookService
- Repository-Tests (DataJpaTest)
- API-Integration-Tests
- Frontend-Component-Tests
- E2E-Test: Katalog laden und navigieren
- Performance-Test: 1000+ Bücher
- Accessibility-Tests

## Akzeptanzkriterien

### Functional
- [ ] Benutzer sieht eine Liste aller Bücher beim Öffnen des Katalogs
- [ ] Jedes Buch zeigt: Titel, Autor, Cover, Verfügbarkeit, Kategorie
- [ ] Verfügbarkeitsstatus ist klar ersichtlich (Badge/Icon)
- [ ] Fehlende Cover zeigen Platzhalter-Icon
- [ ] Click/Tap auf Buch öffnet Detail-Ansicht (US04)
- [ ] Das UI zeigt ein Suchfeld mit Placeholder "Titel, Autor oder ISBN suchen..."
- [ ] (Mobile) Liste ist scrollbar und lädt Inhalte flüssig nach

### Non-Functional
- [ ] Initiale Ladezeit < 2 Sekunden
- [ ] API-Response-Time < 500ms (100 Bücher)
- [ ] Cover-Bilder < 200KB pro Bild
- [ ] Smooth Scrolling ohne Ruckeln
- [ ] Pagination ohne Full-Page-Reload
- [ ] Mobile: Funktioniert in Portrait und Landscape
- [ ] Barrierefrei: WCAG 2.1 Level AA
- [ ] Screen-Reader-kompatibel

### Technical
- [ ] REST API folgt OpenAPI 3.0 Spezifikation
- [ ] Response enthält eine paginierbare Struktur (z. B. content, page, size, totalElements, totalPages)
- [ ] Cover-URLs als absolute URLs (CDN oder /api/v1/books/{id}/cover)
- [ ] HTTP-Status: 200 (OK), 401 (Unauthorized), 500 (Server Error)
- [ ] JWT-Token erforderlich für API-Zugriff
- [ ] CORS-Header korrekt gesetzt

## Technische Notizen

### Backend-Architektur
**Technologie**: Spring Boot, Spring Data JPA, PostgreSQL

**Book Entity**:
```java
@Entity
@Table(name = "books")
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String title;
    
    @Column(nullable = false)
    private String author;
    
    @Column(unique = true)
    private String isbn;
    
    private String coverUrl;
    
    @Column(length = 1000)
    private String description;
    
    @Enumerated(EnumType.STRING)
    private BookCategory category;
    
    @Column(nullable = false)
    private Integer totalCopies = 1;
    
    private LocalDateTime createdAt;
    
    private Integer borrowCount = 0;
    
    // Derived fields
    @Transient
    public boolean isAvailable() {
        return getAvailableCopies() > 0;
    }
    
    @Transient
    public int getAvailableCopies() {
        // Calculated via query or counter
        return totalCopies - activeBorrowingsCount;
    }
}
```

**BookService**:
```java
@Service
public class BookService {
    
    public Page<BookDTO> getAllBooks(Pageable pageable) {
        Page<Book> books = bookRepository.findAll(pageable);
        return books.map(this::toDTO);
    }
    
    private BookDTO toDTO(Book book) {
        return BookDTO.builder()
            .id(book.getId())
            .title(book.getTitle())
            .author(book.getAuthor())
            .isbn(book.getIsbn())
            .coverUrl(book.getCoverUrl() != null ? book.getCoverUrl() : DEFAULT_COVER_URL)
            .category(book.getCategory())
            .description(book.getDescription())
            .available(book.isAvailable())
            .availableCopies(book.getAvailableCopies())
            .totalCopies(book.getTotalCopies())
            .build();
    }
}
```

**API Controller**:
```java
@RestController
@RequestMapping("/api/v1/books")
public class BookController {
    
    @GetMapping
    public ResponseEntity<Page<BookDTO>> getBooks(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size,
        @RequestParam(defaultValue = "title") String sortBy,
        @RequestParam(defaultValue = "asc") String sortOrder
    ) {
        Sort sort = sortOrder.equalsIgnoreCase("desc") 
            ? Sort.by(sortBy).descending() 
            : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return ResponseEntity.ok(bookService.getAllBooks(pageable));
    }
}
```

**API Response Beispiel**:
```json
{
  "content": [
    {
      "id": 1,
      "title": "Der Hobbit",
      "author": "J.R.R. Tolkien",
      "isbn": "978-3-12-345678-9",
      "coverUrl": "https://cdn.schoollibrary.com/covers/hobbit.jpg",
      "category": "FANTASY",
      "description": "Bilbo Beutlin, ein angesehener Hobbit...",
      "available": true,
      "availableCopies": 2,
      "totalCopies": 3
    }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 20,
    "sort": { "sorted": true, "unsorted": false }
  },
  "totalPages": 8,
  "totalElements": 150,
  "last": false,
  "first": true,
  "size": 20,
  "number": 0
}
```

### Frontend-Architektur Web (Optional – kein Teil der Web Admin App)
_Hinweis: Die Web Admin App betrifft nur Admin-Stories. Diese Story ist ein Mobile-User-Flow._
**Technologie**: React, TypeScript, Material-UI

**BookCatalog Component**:
```typescript
export const BookCatalog: React.FC = () => {
  const [books, setBooks] = useState<Page<Book>>();
  const [loading, setLoading] = useState(true);
  const [page, setPage] = useState(0);
  const [sortBy, setSortBy] = useState('title');
  const [viewMode, setViewMode] = useState<'grid' | 'list'>('grid');

  useEffect(() => {
    loadBooks();
  }, [page, sortBy]);

  const loadBooks = async () => {
    setLoading(true);
    try {
      const response = await apiClient.get<Page<Book>>('/api/v1/books', {
        params: { page, size: 20, sortBy, sortOrder: 'asc' }
      });
      setBooks(response.data);
    } catch (error) {
      // Error handling
    } finally {
      setLoading(false);
    }
  };

  return (
    <Box>
      <BookCatalogToolbar 
        sortBy={sortBy} 
        onSortChange={setSortBy}
        viewMode={viewMode}
        onViewModeChange={setViewMode}
      />
      {loading ? <BookSkeleton /> : (
        viewMode === 'grid' 
          ? <BookGrid books={books.content} />
          : <BookList books={books.content} />
      )}
      <Pagination 
        page={page + 1} 
        count={books.totalPages} 
        onChange={(_, p) => setPage(p - 1)} 
      />
    </Box>
  );
};
```

### Frontend-Architektur Mobile
**Technologie**: Flutter/Dart, Provider

**BookCatalogScreen**:
```dart
class BookCatalogScreen extends StatefulWidget {
  @override
  _BookCatalogScreenState createState() => _BookCatalogScreenState();
}

class _BookCatalogScreenState extends State<BookCatalogScreen> {
  final _scrollController = ScrollController();
  List<Book> _books = [];
  int _currentPage = 0;
  bool _isLoading = false;
  bool _hasMore = true;

  @override
  void initState() {
    super.initState();
    _loadBooks();
    _scrollController.addListener(_onScroll);
  }

  Future<void> _loadBooks() async {
    if (_isLoading || !_hasMore) return;
    
    setState(() => _isLoading = true);
    
    try {
      final response = await apiClient.get('/api/v1/books', 
        queryParameters: {
          'page': _currentPage,
          'size': 20,
          'sortBy': 'title',
          'sortOrder': 'asc'
        }
      );
      
      final page = PageResponse<Book>.fromJson(response.data);
      setState(() {
        _books.addAll(page.content);
        _currentPage++;
        _hasMore = !page.last;
      });
    } catch (e) {
      // Error handling
    } finally {
      setState(() => _isLoading = false);
    }
  }

  void _onScroll() {
    if (_scrollController.position.pixels >= 
        _scrollController.position.maxScrollExtent - 200) {
      _loadBooks();
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: Text('Book Catalog')),
      body: RefreshIndicator(
        onRefresh: _refresh,
        child: ListView.builder(
          controller: _scrollController,
          itemCount: _books.length + (_hasMore ? 1 : 0),
          itemBuilder: (context, index) {
            if (index >= _books.length) {
              return Center(child: CircularProgressIndicator());
            }
            return BookListTile(book: _books[index]);
          },
        ),
      ),
    );
  }
}
```

### Datenbank-Schema
```sql
CREATE TABLE books (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    author VARCHAR(255) NOT NULL,
    isbn VARCHAR(20) UNIQUE,
    cover_url TEXT,
    description TEXT,
    category VARCHAR(50) NOT NULL,
    total_copies INTEGER NOT NULL DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    borrow_count INTEGER DEFAULT 0,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_books_title ON books(title);
CREATE INDEX idx_books_author ON books(author);
CREATE INDEX idx_books_category ON books(category);
CREATE INDEX idx_books_created_at ON books(created_at DESC);
```

## Definition of Done
- [ ] Code reviewed und genehmigt
- [ ] Alle Tests bestanden (Unit, Integration, E2E)
- [ ] API-Dokumentation (OpenAPI) aktualisiert
- [ ] User-Dokumentation erstellt
- [ ] Performance-Tests durchgeführt (1000+ Bücher)
- [ ] Accessibility-Tests bestanden
- [ ] Responsive Design auf allen Target-Geräten getestet
- [ ] Security-Scan ohne kritische Findings
- [ ] Deployment in Test-Umgebung erfolgreich
- [ ] Product Owner hat Feature abgenommen

## Abhängigkeiten
- User Authentication (US01) muss implementiert sein
- Datenbank-Schema muss existieren
- Mindestens Test-Daten (Bücher) in Datenbank
- CDN oder lokaler Storage für Cover-Bilder

## Risiken & Offene Punkte
- Performance bei > 10.000 Büchern muss mit Elasticsearch gelöst werden
- Cover-Upload-Prozess nicht in dieser Story enthalten
- Kategorie-System sollte flexibel/erweiterbar sein
- Mehrsprachigkeit der Buchdaten später (i18n der UI bereits berücksichtigen)
- Caching-Strategie für häufig abgerufene Bücher definieren
