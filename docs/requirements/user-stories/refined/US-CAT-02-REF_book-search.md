# User Story US-CAT-02-REF: Book Search (Refined)

## Story
**As a** student  
**I want to** search for books by title, author, or ISBN  
**So that** I can quickly find specific books I'm looking for

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
| Welche Suchfelder unterstützen? | Titel, Autor, ISBN | Die wichtigsten Suchkriterien für Bibliotheken |
| Soll es eine einzige Suchleiste geben? | Ja, kombinierte Suche | Einfachere UX, Google-ähnlich |
| Soll Suche in Echtzeit erfolgen? | Ja, mit Debouncing (300ms) | Besseres UX, aber Performance-optimiert |
| Fuzzy Search unterstützen? | Ja, Tippfehler-tolerant | Verbessert Treffgenauigkeit |
| Filter nach Kategorien? | Ja, als optionaler Filter | Ermöglicht gezielte Suche |
| Nur verfügbare Bücher zeigen? | Nein, aber Filter-Option anbieten | Benutzer soll auch ausgeliehene Bücher sehen können |
| Suchhistorie speichern? | Ja, lokal (nicht im MVP persistent) | Verbessertes UX für häufige Suchen |
| Auto-Suggest/Autocomplete? | Nein im MVP | Could-Have für spätere Version |

### Tasks

#### Backend
- SearchService mit Multi-Field-Suche implementieren
- Book-Repository mit Custom-Query-Methoden erweitern
- LIKE-Query für Fuzzy-Matching (PostgreSQL ILIKE)
- Filter-Logik für Kategorie und Verfügbarkeit
- Search-API-Endpoint GET /api/v1/books/search
- Query-Parameter: q (query), category, availableOnly
- Pagination für Suchergebnisse
- Performance-Optimierung (Indexes auf Suchfeldern)
- OpenAPI-Dokumentation für Search-Endpoint

#### Frontend Web (Web Admin App – nicht zutreffend)
- Diese Story ist ein User-Flow (Schüler/Lehrkraft) und wird in der Mobile App umgesetzt; die Web Admin App bietet hierfür keine UI.

#### Frontend Mobile
- SearchBar in AppBar integrieren
- Debounced-Search-Input implementieren
- Filter-Bottom-Sheet für Kategorien
- Search-Results-ListView
- Pull-to-Refresh für Suchergebnisse
- Empty-State bei keinen Ergebnissen
- Recent-Searches als Chips anzeigen
- Clear-All-Search-History Button
- Loading-Indicator

#### Testing
- Unit-Tests für SearchService
- Repository-Tests mit verschiedenen Queries
- API-Integration-Tests für Search-Endpoint
- Frontend-Tests für SearchBar-Component
- E2E-Tests für kompletten Suchablauf
- Performance-Tests mit großen Datenmengen
- Fuzzy-Search-Accuracy-Tests

## Akzeptanzkriterien

### Functional
- [ ] Benutzer kann Suchbegriff in Suchleiste eingeben
- [ ] Suchfeld zeigt Placeholder "Titel, Autor oder ISBN suchen..."
- [ ] Suche durchsucht Titel, Autor und ISBN
- [ ] Suchergebnisse werden als Liste/Grid angezeigt (wie Katalog)
- [ ] Ergebnisliste zeigt Kategorie (z.B. "Sachbuch") und Status (z.B. "Verfügbar") pro Buch
- [ ] "Keine Ergebnisse gefunden" wird bei 0 Treffern angezeigt
- [ ] Ergebnisse aktualisieren sich, wenn der Suchbegriff geändert wird

### Non-Functional
- [ ] Suchanfrage < 500ms Response-Zeit
- [ ] Search funktioniert mit Sonderzeichen
- [ ] UI bleibt responsiv während Suche
- [ ] Mobile: On-Screen-Keyboard zeigt "Suchen"-Button
- [ ] Barrierefrei: Screen-Reader-Support
- [ ] Suchhistorie wird lokal gespeichert (Privacy)

### Technical
- [ ] Search-Endpoint: GET /api/v1/books/search?q={query}
- [ ] Case-insensitive Suche (ILIKE in PostgreSQL)
- [ ] Wildcard-Matching für Teilstrings
- [ ] Response-Format identisch zu GET /api/v1/books
- [ ] HTTP-Status: 200 (OK), 400 (Bad Request), 401 (Unauthorized)
- [ ] Optional: Minimum-Zeichen-Anzahl kann clientseitig validiert werden

## Technische Notizen

### Backend-Architektur
**Technologie**: Spring Boot, Spring Data JPA, PostgreSQL

**BookRepository Custom Query**:
```java
@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    
    @Query("SELECT b FROM Book b WHERE " +
           "LOWER(b.title) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(b.author) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "b.isbn LIKE CONCAT('%', :query, '%')")
    Page<Book> searchBooks(@Param("query") String query, Pageable pageable);
    
    @Query("SELECT b FROM Book b WHERE " +
           "(LOWER(b.title) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(b.author) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "b.isbn LIKE CONCAT('%', :query, '%')) AND " +
           "(:category IS NULL OR b.category = :category)")
    Page<Book> searchBooksWithFilters(
        @Param("query") String query,
        @Param("category") BookCategory category,
        Pageable pageable
    );
}
```

**SearchService**:
```java
@Service
public class SearchService {
    
    private final BookRepository bookRepository;
    
    public Page<BookDTO> searchBooks(
        String query,
        BookCategory category,
        Boolean availableOnly,
        Pageable pageable
    ) {
        // Validation
        if (query == null || query.trim().length() < 3) {
            throw new IllegalArgumentException("Query must be at least 3 characters");
        }
        
        Page<Book> books;
        if (category != null) {
            books = bookRepository.searchBooksWithFilters(query, category, pageable);
        } else {
            books = bookRepository.searchBooks(query, pageable);
        }
        
        // Filter by availability if requested
        if (Boolean.TRUE.equals(availableOnly)) {
            books = books.filter(Book::isAvailable);
        }
        
        return books.map(this::toDTO);
    }
}
```

**API Controller**:
```java
@RestController
@RequestMapping("/api/v1/books")
public class BookController {
    
    @GetMapping("/search")
    public ResponseEntity<Page<BookDTO>> searchBooks(
        @RequestParam String q,
        @RequestParam(required = false) BookCategory category,
        @RequestParam(required = false) Boolean availableOnly,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<BookDTO> results = searchService.searchBooks(q, category, availableOnly, pageable);
        return ResponseEntity.ok(results);
    }
}
```

**PostgreSQL-Optimierung**:
```sql
-- Full-Text Search Index (für bessere Performance bei großen Datenmengen)
CREATE INDEX idx_books_search ON books 
USING gin(to_tsvector('german', title || ' ' || author));

-- Einfache LIKE-Indexes
CREATE INDEX idx_books_title_trgm ON books USING gin(title gin_trgm_ops);
CREATE INDEX idx_books_author_trgm ON books USING gin(author gin_trgm_ops);

-- PostgreSQL Extension für Fuzzy-Search
CREATE EXTENSION IF NOT EXISTS pg_trgm;
```

### Frontend-Architektur Web (Optional – kein Teil der Web Admin App)
_Hinweis: Die Web Admin App betrifft nur Admin-Stories. Diese Story ist ein Mobile-User-Flow._
**Technologie**: React, TypeScript, Lodash (debounce)

**SearchBar Component**:
```typescript
import { debounce } from 'lodash';

export const SearchBar: React.FC = () => {
  const [query, setQuery] = useState('');
  const [results, setResults] = useState<Book[]>([]);
  const [loading, setLoading] = useState(false);
  const [category, setCategory] = useState<string | null>(null);
  const [availableOnly, setAvailableOnly] = useState(false);

  // Debounced search function
  const debouncedSearch = useMemo(
    () => debounce(async (searchQuery: string) => {
      if (searchQuery.length < 3) {
        setResults([]);
        return;
      }

      setLoading(true);
      try {
        const response = await apiClient.get<Page<Book>>('/api/v1/books/search', {
          params: {
            q: searchQuery,
            category: category || undefined,
            availableOnly: availableOnly || undefined,
            page: 0,
            size: 20
          }
        });
        setResults(response.data.content);
        
        // Save to search history
        saveToSearchHistory(searchQuery);
      } catch (error) {
        console.error('Search failed', error);
      } finally {
        setLoading(false);
      }
    }, 300),
    [category, availableOnly]
  );

  useEffect(() => {
    debouncedSearch(query);
    return () => debouncedSearch.cancel();
  }, [query, debouncedSearch]);

  const saveToSearchHistory = (searchQuery: string) => {
    const history = JSON.parse(localStorage.getItem('searchHistory') || '[]');
    const updated = [searchQuery, ...history.filter(q => q !== searchQuery)].slice(0, 5);
    localStorage.setItem('searchHistory', JSON.stringify(updated));
  };

  return (
    <Box>
      <TextField
        fullWidth
        placeholder="Search books by title, author, or ISBN..."
        value={query}
        onChange={(e) => setQuery(e.target.value)}
        InputProps={{
          startAdornment: <SearchIcon />,
          endAdornment: query && (
            <IconButton onClick={() => setQuery('')}>
              <ClearIcon />
            </IconButton>
          )
        }}
      />
      
      <FilterBar 
        category={category}
        onCategoryChange={setCategory}
        availableOnly={availableOnly}
        onAvailableOnlyChange={setAvailableOnly}
      />

      {loading && <LinearProgress />}
      
      {query.length >= 3 && !loading && results.length === 0 && (
        <EmptyState message="No books found matching your search" />
      )}
      
      {results.length > 0 && (
        <BookGrid books={results} />
      )}
    </Box>
  );
};
```

### Frontend-Architektur Mobile
**Technologie**: Flutter/Dart, rxdart (for debouncing)

**SearchScreen**:
```dart
class BookSearchScreen extends StatefulWidget {
  @override
  _BookSearchScreenState createState() => _BookSearchScreenState();
}

class _BookSearchScreenState extends State<BookSearchScreen> {
  final _searchController = TextEditingController();
  final _searchSubject = BehaviorSubject<String>();
  List<Book> _results = [];
  bool _isLoading = false;
  String? _selectedCategory;
  bool _availableOnly = false;

  @override
  void initState() {
    super.initState();
    
    // Debounced search
    _searchSubject.stream
      .debounceTime(Duration(milliseconds: 300))
      .where((query) => query.length >= 3)
      .listen((query) => _performSearch(query));
      
    _searchController.addListener(() {
      _searchSubject.add(_searchController.text);
    });
  }

  Future<void> _performSearch(String query) async {
    setState(() => _isLoading = true);
    
    try {
      final response = await apiClient.get('/api/v1/books/search',
        queryParameters: {
          'q': query,
          if (_selectedCategory != null) 'category': _selectedCategory,
          if (_availableOnly) 'availableOnly': true,
          'page': 0,
          'size': 20
        }
      );
      
      final page = PageResponse<Book>.fromJson(response.data);
      setState(() {
        _results = page.content;
      });
      
      // Save to search history
      await _saveSearchHistory(query);
    } catch (e) {
      // Error handling
    } finally {
      setState(() => _isLoading = false);
    }
  }

  Future<void> _saveSearchHistory(String query) async {
    final prefs = await SharedPreferences.getInstance();
    List<String> history = prefs.getStringList('searchHistory') ?? [];
    history.remove(query);
    history.insert(0, query);
    if (history.length > 5) history = history.sublist(0, 5);
    await prefs.setStringList('searchHistory', history);
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: TextField(
          controller: _searchController,
          decoration: InputDecoration(
            hintText: 'Search books...',
            border: InputBorder.none,
            suffixIcon: _searchController.text.isNotEmpty
              ? IconButton(
                  icon: Icon(Icons.clear),
                  onPressed: () {
                    _searchController.clear();
                    setState(() => _results = []);
                  },
                )
              : null,
          ),
        ),
        actions: [
          IconButton(
            icon: Icon(Icons.filter_list),
            onPressed: _showFilterSheet,
          ),
        ],
      ),
      body: _buildBody(),
    );
  }

  Widget _buildBody() {
    if (_isLoading) {
      return Center(child: CircularProgressIndicator());
    }
    
    if (_searchController.text.length >= 3 && _results.isEmpty) {
      return EmptyStateWidget(message: 'No books found');
    }
    
    return ListView.builder(
      itemCount: _results.length,
      itemBuilder: (context, index) => BookListTile(book: _results[index]),
    );
  }

  void _showFilterSheet() {
    showModalBottomSheet(
      context: context,
      builder: (context) => FilterBottomSheet(
        selectedCategory: _selectedCategory,
        availableOnly: _availableOnly,
        onApply: (category, available) {
          setState(() {
            _selectedCategory = category;
            _availableOnly = available;
          });
          _performSearch(_searchController.text);
        },
      ),
    );
  }

  @override
  void dispose() {
    _searchController.dispose();
    _searchSubject.close();
    super.dispose();
  }
}
```

### Performance-Optimierung
**Elasticsearch Integration (für Production mit > 10k Büchern)**:
```java
// Optional: Elasticsearch für bessere Full-Text-Search
@Document(indexName = "books")
public class BookSearchDocument {
    @Id
    private Long id;
    
    @Field(type = FieldType.Text, analyzer = "german")
    private String title;
    
    @Field(type = FieldType.Text, analyzer = "german")
    private String author;
    
    @Field(type = FieldType.Keyword)
    private String isbn;
    
    // ... weitere Felder
}

@Repository
public interface BookSearchRepository extends ElasticsearchRepository<BookSearchDocument, Long> {
    
    @Query("{\"multi_match\": {\"query\": \"?0\", \"fields\": [\"title^2\", \"author\", \"isbn\"]}}")
    Page<BookSearchDocument> searchByQuery(String query, Pageable pageable);
}
```

## Definition of Done
- [ ] Code reviewed und genehmigt
- [ ] Alle Tests bestanden (Unit, Integration, E2E)
- [ ] API-Dokumentation (OpenAPI) aktualisiert
- [ ] User-Dokumentation erstellt
- [ ] Performance-Tests mit 1000+ Büchern durchgeführt
- [ ] Fuzzy-Search-Accuracy getestet
- [ ] Accessibility-Tests bestanden
- [ ] Security-Scan ohne kritische Findings
- [ ] Deployment in Test-Umgebung erfolgreich
- [ ] Product Owner hat Feature abgenommen

## Abhängigkeiten
- User Authentication (US01) muss implementiert sein
- Book Catalog (US02) sollte existieren für konsistente UI
- Datenbank-Indexes müssen erstellt sein
- Test-Daten mit verschiedenen Büchern vorhanden

## Risiken & Offene Punkte
- Performance bei sehr großen Katalogen → Elasticsearch-Migration planen
- Mehrsprachige Suche (z.B. deutsche und englische Bücher) → Analyzer-Konfiguration
- Fuzzy-Search-Genauigkeit muss feingetunt werden (Levenshtein-Distanz)
- Auto-Suggest könnte UX verbessern → Should-Have für nächste Version
- Synonyme (z.B. "Buch" = "Book") → Thesaurus definieren
