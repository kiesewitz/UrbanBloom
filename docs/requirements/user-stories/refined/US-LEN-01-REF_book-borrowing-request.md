# User Story US-LEN-01-REF: Book Borrowing Request (Refined)

## Story
**As a** student  
**I want to** request to borrow an available book  
**So that** I can take it home and read it

## Priority
**Must-Have** | MVP Phase 2

## UI-Prototyp (Mobile)

- HTML: [buch_detailansicht_&_reservierung](../../../ui/prototypes/stitch_schulbibliotheks_app/buch_detailansicht_&_reservierung/code.html)
- Bild:

![buch_detailansicht_&_reservierung](../../../ui/prototypes/stitch_schulbibliotheks_app/buch_detailansicht_&_reservierung/screen.png)

## Refinement Discussion

### Klärungspunkte & Entscheidungen

| Punkt | Entscheidung | Begründung |
|-------|--------------|------------|
| Ausleihprozess: Direkt oder Anfrage? | Direkt für Studenten, Bestätigung durch Librarian | Balance zwischen Self-Service und Kontrolle |
| Ausleihdauer? | Standard 14 Tage, verlängerbar | Übliche Bibliothekspraxis |
| Maximale gleichzeitige Ausleihen? | 3 Bücher pro Student | Verhindert Horten, fair für alle |
| Reservierung bei Nichtverfügbarkeit? | Nein im MVP | Could-Have für spätere Version |
| Erinnerung vor Ablauf? | Ja, 3 Tage vorher | Reduziert verspätete Rückgaben |
| Verlängerungsmöglichkeit? | Ja, einmalig um 7 Tage | Flexibilität für Leser |
| Mahngebühren? | Nein im MVP | Could-Have mit Payment-Integration |
| Welches Exemplar wird zugeteilt? | System wählt automatisch verfügbares | Vereinfacht Prozess |
| Abholort? | Schulbibliothek, Ausgabe am Schalter | Physische Abholung erforderlich |
| Abholbestätigung? | Ja, Librarian markiert als "ausgegeben" | Tracking der physischen Übergabe |

### Tasks

#### Backend
- Borrowing-Entität erstellen (User, BookCopy, dates, status)
- BorrowingService mit Business-Logik
- Validierungen: Verfügbarkeit, User-Limit, bestehende Ausleihen
- POST /api/v1/borrowings Endpoint
- Automatic BookCopy-Zuweisung
- Status-Workflow: REQUESTED → ISSUED → RETURNED
- Due-Date-Berechnung (14 Tage)
- Extension-Logik (einmalig +7 Tage)
- E-Mail-Benachrichtigung bei erfolgreicher Anfrage
- OpenAPI-Dokumentation

#### Frontend Web (Web Admin App – nicht zutreffend)
- Diese Story ist ein User-Flow (Schüler/Lehrkraft) und wird in der Mobile App umgesetzt; die Web Admin App bietet hierfür keine UI.

#### Frontend Mobile
- BorrowRequestScreen oder Bottom-Sheet
- FAB auf Book-Details → Borrow-Bestätigung
- Bestätigungs-Dialog
- Success-Snackbar mit Due-Date
- Error-Handling
- Navigation zu "My Borrowed Books"
- Terms-of-Service-Link

#### Testing
- Unit-Tests für BorrowingService
- Validation-Tests (Limit, Verfügbarkeit)
- API-Integration-Tests
- E2E-Test: Kompletter Ausleih-Flow
- Concurrent-Borrow-Tests (Race-Conditions)
- Email-Notification-Tests (Mock)

## Akzeptanzkriterien

### Functional
- [ ] Benutzer kann auf verfügbarem Buch den Button "Jetzt Reservieren" (Ausleihanfrage) klicken
- [ ] System prüft User-Limit (max. 3 aktive Ausleihen)
- [ ] System prüft Buch-Verfügbarkeit (verfügbares Exemplar)
- [ ] Bei Erfolg: Borrowing wird erstellt mit Status REQUESTED
- [ ] Bei Erfolg: UI zeigt eine Erfolgsbestätigung
- [ ] Bei Erfolg: E-Mail-Benachrichtigung an Benutzer
- [ ] Bei Erfolg: Weiterleitung zu "My Borrowed Books"
- [ ] Bei Limit-Überschreitung: Fehlermeldung "Max 3 books"
- [ ] Bei Nichtverfügbarkeit: Fehlermeldung "No copies available"
- [ ] System wählt automatisch ein verfügbares Exemplar
- [ ] BookCopy-Status wechselt zu BORROWED

### Non-Functional
- [ ] Borrow-Request < 1 Sekunde Response-Time
- [ ] Transaktion ist ACID-compliant (keine Doppelausleihen)
- [ ] Concurrent-Requests werden korrekt gehandhabt
- [ ] E-Mail-Versand asynchron (blockiert nicht UI)
- [ ] Error-Messages sind benutzerfreundlich
- [ ] Bestätigungsdialog ist klar verständlich

### Technical
- [ ] POST /api/v1/borrowings Endpoint
- [ ] Request-Body: { "bookId": 123 }
- [ ] Response: BorrowingDTO mit ID, Buch, Due-Date, Status
- [ ] HTTP-Status: 201 (Created), 400 (Bad Request), 409 (Conflict)
- [ ] Pessimistic Locking für BookCopy-Auswahl
- [ ] Transaction-Management für atomare Operation
- [ ] JWT-Token erforderlich (User-ID aus Token)
- [ ] Audit-Log für Borrowing-Aktionen

## Technische Notizen

### Backend-Architektur
**Technologie**: Spring Boot, Spring Data JPA, Spring Mail

**Borrowing Entity**:
```java
@Entity
@Table(name = "borrowings")
public class Borrowing {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_copy_id", nullable = false)
    private BookCopy bookCopy;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BorrowingStatus status; // REQUESTED, ISSUED, RETURNED, OVERDUE
    
    @Column(nullable = false)
    private LocalDate borrowDate;
    
    @Column(nullable = false)
    private LocalDate dueDate;
    
    private LocalDate returnDate;
    
    private Boolean extended = false;
    
    private LocalDate extendedDueDate;
    
    @CreatedDate
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    private LocalDateTime updatedAt;
    
    @ManyToOne
    @JoinColumn(name = "issued_by_librarian_id")
    private User issuedByLibrarian; // Wer hat physisch ausgegeben
    
    @Transient
    public boolean isOverdue() {
        LocalDate relevantDueDate = extended ? extendedDueDate : dueDate;
        return status == BorrowingStatus.ISSUED && LocalDate.now().isAfter(relevantDueDate);
    }
}
```

**BorrowingService**:
```java
@Service
@Transactional
public class BorrowingService {
    
    private static final int MAX_ACTIVE_BORROWINGS = 3;
    private static final int DEFAULT_BORROW_DAYS = 14;
    
    @Autowired
    private BorrowingRepository borrowingRepository;
    
    @Autowired
    private BookCopyRepository bookCopyRepository;
    
    @Autowired
    private EmailService emailService;
    
    public BorrowingDTO createBorrowRequest(Long userId, Long bookId) {
        // 1. Validate user borrowing limit
        long activeBorrowings = borrowingRepository.countActiveByUserId(userId);
        if (activeBorrowings >= MAX_ACTIVE_BORROWINGS) {
            throw new BorrowingLimitException("Maximum " + MAX_ACTIVE_BORROWINGS + " active borrowings allowed");
        }
        
        // 2. Find available copy with pessimistic lock
        BookCopy availableCopy = bookCopyRepository
            .findFirstAvailableByBookId(bookId, LockModeType.PESSIMISTIC_WRITE)
            .orElseThrow(() -> new NoCopyAvailableException("No copies available for this book"));
        
        // 3. Create borrowing
        Borrowing borrowing = new Borrowing();
        borrowing.setUser(userRepository.findById(userId).orElseThrow());
        borrowing.setBookCopy(availableCopy);
        borrowing.setStatus(BorrowingStatus.REQUESTED);
        borrowing.setBorrowDate(LocalDate.now());
        borrowing.setDueDate(LocalDate.now().plusDays(DEFAULT_BORROW_DAYS));
        borrowing.setExtended(false);
        
        borrowing = borrowingRepository.save(borrowing);
        
        // 4. Update copy status
        availableCopy.setStatus(CopyStatus.BORROWED);
        availableCopy.setCurrentBorrowing(borrowing);
        bookCopyRepository.save(availableCopy);
        
        // 5. Send email notification (async)
        emailService.sendBorrowConfirmation(borrowing);
        
        return toBorrowingDTO(borrowing);
    }
    
    public BorrowingDTO extendBorrowing(Long borrowingId, Long userId) {
        Borrowing borrowing = borrowingRepository.findById(borrowingId)
            .orElseThrow(() -> new BorrowingNotFoundException(borrowingId));
        
        // Validation
        if (!borrowing.getUser().getId().equals(userId)) {
            throw new UnauthorizedAccessException("Not your borrowing");
        }
        
        if (borrowing.getExtended()) {
            throw new ExtensionNotAllowedException("Already extended once");
        }
        
        if (borrowing.isOverdue()) {
            throw new ExtensionNotAllowedException("Cannot extend overdue borrowing");
        }
        
        // Extend
        borrowing.setExtended(true);
        borrowing.setExtendedDueDate(borrowing.getDueDate().plusDays(7));
        borrowing = borrowingRepository.save(borrowing);
        
        emailService.sendExtensionConfirmation(borrowing);
        
        return toBorrowingDTO(borrowing);
    }
}
```

**Repository mit Custom Query**:
```java
@Repository
public interface BookCopyRepository extends JpaRepository<BookCopy, Long> {
    
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT bc FROM BookCopy bc WHERE bc.book.id = :bookId AND bc.status = 'AVAILABLE'")
    Optional<BookCopy> findFirstAvailableByBookId(@Param("bookId") Long bookId);
    
    @Query("SELECT COUNT(b) FROM Borrowing b WHERE b.user.id = :userId AND b.status IN ('REQUESTED', 'ISSUED')")
    long countActiveByUserId(@Param("userId") Long userId);
}
```

**API Controller**:
```java
@RestController
@RequestMapping("/api/v1/borrowings")
public class BorrowingController {
    
    @Autowired
    private BorrowingService borrowingService;
    
    @PostMapping
    public ResponseEntity<BorrowingDTO> createBorrowRequest(
        @RequestBody BorrowRequestDTO request,
        @AuthenticationPrincipal JwtAuthenticationToken token
    ) {
        Long userId = extractUserId(token);
        BorrowingDTO borrowing = borrowingService.createBorrowRequest(userId, request.getBookId());
        return ResponseEntity.status(HttpStatus.CREATED).body(borrowing);
    }
    
    @PostMapping("/{id}/extend")
    public ResponseEntity<BorrowingDTO> extendBorrowing(
        @PathVariable Long id,
        @AuthenticationPrincipal JwtAuthenticationToken token
    ) {
        Long userId = extractUserId(token);
        BorrowingDTO borrowing = borrowingService.extendBorrowing(id, userId);
        return ResponseEntity.ok(borrowing);
    }
    
    private Long extractUserId(JwtAuthenticationToken token) {
        return Long.parseLong(token.getToken().getClaimAsString("sub"));
    }
}
```

**Email Service**:
```java
@Service
public class EmailService {
    
    @Autowired
    private JavaMailSender mailSender;
    
    @Async
    public void sendBorrowConfirmation(Borrowing borrowing) {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        
        helper.setTo(borrowing.getUser().getEmail());
        helper.setSubject("Book Borrow Confirmation");
        helper.setText(
            String.format(
                "Dear %s,\n\n" +
                "Your request to borrow '%s' has been confirmed.\n" +
                "Please pick it up from the library counter.\n\n" +
                "Copy Number: %s\n" +
                "Location: %s\n" +
                "Due Date: %s\n\n" +
                "Happy Reading!",
                borrowing.getUser().getFullName(),
                borrowing.getBookCopy().getBook().getTitle(),
                borrowing.getBookCopy().getCopyNumber(),
                borrowing.getBookCopy().getLocation(),
                borrowing.getDueDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))
            )
        );
        
        mailSender.send(message);
    }
}
```

**Response Beispiel**:
```json
{
  "id": 42,
  "user": {
    "id": 5,
    "name": "Max Mustermann",
    "email": "max@school.com"
  },
  "book": {
    "id": 1,
    "title": "Der Hobbit",
    "author": "J.R.R. Tolkien",
    "coverUrl": "..."
  },
  "copy": {
    "id": 2,
    "copyNumber": "002",
    "location": "Regal A3"
  },
  "status": "REQUESTED",
  "borrowDate": "2026-01-02",
  "dueDate": "2026-01-16",
  "extended": false,
  "canExtend": true
}
```

### Frontend-Architektur Web (Optional – kein Teil der Web Admin App)
_Hinweis: Die Web Admin App betrifft nur Admin-Stories. Diese Story ist ein Mobile-User-Flow._
**Technologie**: React, TypeScript, Material-UI

**BorrowDialog Component**:
```typescript
interface BorrowDialogProps {
  book: BookDetailDTO;
  open: boolean;
  onClose: () => void;
  onSuccess: (borrowing: BorrowingDTO) => void;
}

export const BorrowDialog: React.FC<BorrowDialogProps> = ({
  book,
  open,
  onClose,
  onSuccess
}) => {
  const [loading, setLoading] = useState(false);
  const [accepted, setAccepted] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const handleBorrow = async () => {
    if (!accepted) {
      setError('Please accept the terms and conditions');
      return;
    }

    setLoading(true);
    setError(null);

    try {
      const response = await apiClient.post<BorrowingDTO>('/api/v1/borrowings', {
        bookId: book.id
      });

      onSuccess(response.data);
      onClose();
      
      // Show success notification
      toast.success(
        `Book borrowed successfully! Due date: ${formatDate(response.data.dueDate)}`,
        { duration: 5000 }
      );
    } catch (err: any) {
      if (err.response?.status === 409) {
        setError(err.response.data.message || 'No copies available');
      } else if (err.response?.status === 400) {
        setError(err.response.data.message || 'Maximum borrowing limit reached');
      } else {
        setError('Failed to borrow book. Please try again.');
      }
    } finally {
      setLoading(false);
    }
  };

  const dueDate = new Date();
  dueDate.setDate(dueDate.getDate() + 14);

  return (
    <Dialog open={open} onClose={onClose} maxWidth="sm" fullWidth>
      <DialogTitle>Borrow Book</DialogTitle>
      <DialogContent>
        <Box sx={{ display: 'flex', gap: 2, mb: 2 }}>
          <img 
            src={book.coverUrl} 
            alt={book.title}
            style={{ width: 80, height: 120, objectFit: 'cover' }}
          />
          <Box>
            <Typography variant="h6">{book.title}</Typography>
            <Typography variant="body2" color="text.secondary">
              by {book.author}
            </Typography>
          </Box>
        </Box>

        <Alert severity="info" sx={{ mb: 2 }}>
          <Typography variant="body2">
            <strong>Due Date:</strong> {formatDate(dueDate)} (14 days)
          </Typography>
          <Typography variant="body2">
            You can extend once for 7 more days before the due date.
          </Typography>
        </Alert>

        <Typography variant="body2" paragraph>
          By borrowing this book, you agree to:
        </Typography>
        <ul>
          <li><Typography variant="body2">Pick up the book from the library counter</Typography></li>
          <li><Typography variant="body2">Return the book by the due date</Typography></li>
          <li><Typography variant="body2">Keep the book in good condition</Typography></li>
          <li><Typography variant="body2">Maximum 3 books can be borrowed at once</Typography></li>
        </ul>

        <FormControlLabel
          control={
            <Checkbox 
              checked={accepted} 
              onChange={(e) => setAccepted(e.target.checked)}
            />
          }
          label="I accept the terms and conditions"
        />

        {error && (
          <Alert severity="error" sx={{ mt: 2 }}>
            {error}
          </Alert>
        )}
      </DialogContent>
      <DialogActions>
        <Button onClick={onClose} disabled={loading}>
          Cancel
        </Button>
        <Button 
          onClick={handleBorrow} 
          variant="contained"
          disabled={loading || !accepted}
        >
          {loading ? <CircularProgress size={24} /> : 'Confirm Borrow'}
        </Button>
      </DialogActions>
    </Dialog>
  );
};
```

### Frontend-Architektur Mobile
**Technologie**: Flutter/Dart

**BorrowConfirmationSheet**:
```dart
class BorrowConfirmationSheet extends StatefulWidget {
  final BookDetail book;
  
  const BorrowConfirmationSheet({required this.book});

  @override
  _BorrowConfirmationSheetState createState() => _BorrowConfirmationSheetState();
}

class _BorrowConfirmationSheetState extends State<BorrowConfirmationSheet> {
  bool _accepted = false;
  bool _isLoading = false;

  Future<void> _handleBorrow() async {
    if (!_accepted) {
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text('Please accept the terms')),
      );
      return;
    }

    setState(() => _isLoading = true);

    try {
      final response = await apiClient.post('/api/v1/borrowings', 
        data: {'bookId': widget.book.id}
      );
      
      final borrowing = Borrowing.fromJson(response.data);
      
      Navigator.pop(context, borrowing);
      
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(
          content: Text('Book borrowed! Due: ${DateFormat.yMd().format(borrowing.dueDate)}'),
          backgroundColor: Colors.green,
          duration: Duration(seconds: 5),
        ),
      );
      
      Navigator.pushNamed(context, '/my-books');
    } catch (e) {
      String errorMsg = 'Failed to borrow book';
      if (e is DioError) {
        if (e.response?.statusCode == 409) {
          errorMsg = e.response?.data['message'] ?? 'No copies available';
        } else if (e.response?.statusCode == 400) {
          errorMsg = e.response?.data['message'] ?? 'Borrowing limit reached';
        }
      }
      
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text(errorMsg), backgroundColor: Colors.red),
      );
    } finally {
      setState(() => _isLoading = false);
    }
  }

  @override
  Widget build(BuildContext context) {
    final dueDate = DateTime.now().add(Duration(days: 14));
    
    return Container(
      padding: EdgeInsets.all(16),
      child: Column(
        mainAxisSize: MainAxisSize.min,
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Text(
            'Borrow Book',
            style: Theme.of(context).textTheme.headlineSmall,
          ),
          SizedBox(height: 16),
          
          Row(
            children: [
              CachedNetworkImage(
                imageUrl: widget.book.coverUrl,
                width: 60,
                height: 90,
                fit: BoxFit.cover,
              ),
              SizedBox(width: 12),
              Expanded(
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Text(
                      widget.book.title,
                      style: TextStyle(fontWeight: FontWeight.bold),
                    ),
                    Text(
                      'by ${widget.book.author}',
                      style: TextStyle(color: Colors.grey),
                    ),
                  ],
                ),
              ),
            ],
          ),
          
          SizedBox(height: 16),
          
          Card(
            color: Colors.blue.shade50,
            child: Padding(
              padding: EdgeInsets.all(12),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Text(
                    'Due Date: ${DateFormat.yMd().format(dueDate)} (14 days)',
                    style: TextStyle(fontWeight: FontWeight.bold),
                  ),
                  SizedBox(height: 4),
                  Text(
                    'You can extend once for 7 more days.',
                    style: TextStyle(fontSize: 12),
                  ),
                ],
              ),
            ),
          ),
          
          SizedBox(height: 12),
          
          Text('By borrowing, you agree to:', style: TextStyle(fontWeight: FontWeight.bold)),
          SizedBox(height: 4),
          Text('• Pick up from library counter', style: TextStyle(fontSize: 12)),
          Text('• Return by due date', style: TextStyle(fontSize: 12)),
          Text('• Keep book in good condition', style: TextStyle(fontSize: 12)),
          Text('• Maximum 3 books at once', style: TextStyle(fontSize: 12)),
          
          SizedBox(height: 16),
          
          CheckboxListTile(
            value: _accepted,
            onChanged: (value) => setState(() => _accepted = value ?? false),
            title: Text('I accept the terms', style: TextStyle(fontSize: 14)),
            controlAffinity: ListTileControlAffinity.leading,
            contentPadding: EdgeInsets.zero,
          ),
          
          SizedBox(height: 16),
          
          Row(
            children: [
              Expanded(
                child: OutlinedButton(
                  onPressed: _isLoading ? null : () => Navigator.pop(context),
                  child: Text('Cancel'),
                ),
              ),
              SizedBox(width: 12),
              Expanded(
                child: ElevatedButton(
                  onPressed: (_isLoading || !_accepted) ? null : _handleBorrow,
                  child: _isLoading 
                    ? SizedBox(
                        height: 20,
                        width: 20,
                        child: CircularProgressIndicator(strokeWidth: 2),
                      )
                    : Text('Confirm'),
                ),
              ),
            ],
          ),
        ],
      ),
    );
  }
}
```

### Datenbank-Schema
```sql
CREATE TABLE borrowings (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id),
    book_copy_id BIGINT NOT NULL REFERENCES book_copies(id),
    status VARCHAR(20) NOT NULL,
    borrow_date DATE NOT NULL,
    due_date DATE NOT NULL,
    return_date DATE,
    extended BOOLEAN DEFAULT FALSE,
    extended_due_date DATE,
    issued_by_librarian_id BIGINT REFERENCES users(id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_borrowings_user_id ON borrowings(user_id);
CREATE INDEX idx_borrowings_book_copy_id ON borrowings(book_copy_id);
CREATE INDEX idx_borrowings_status ON borrowings(status);
CREATE INDEX idx_borrowings_due_date ON borrowings(due_date);
```

## Definition of Done
- [ ] Code reviewed und genehmigt
- [ ] Alle Tests bestanden (Unit, Integration, E2E)
- [ ] Concurrent-Borrow-Tests erfolgreich
- [ ] API-Dokumentation (OpenAPI) aktualisiert
- [ ] User-Dokumentation erstellt
- [ ] E-Mail-Templates erstellt und getestet
- [ ] Performance-Tests durchgeführt
- [ ] Security-Scan ohne kritische Findings
- [ ] Deployment in Test-Umgebung erfolgreich
- [ ] Product Owner hat Feature abgenommen

## Abhängigkeiten
- User Authentication (US01) muss implementiert sein
- Book Details View (US04) sollte existieren
- E-Mail-Server muss konfiguriert sein
- Borrowings-Tabelle und BookCopy-Relation
- User-Entität mit E-Mail-Feld

## Risiken & Offene Punkte
- Race-Condition bei gleichzeitigen Borrow-Requests → Pessimistic Locking
- E-Mail-Versand kann fehlschlagen → Retry-Mechanismus und Monitoring
- Physische Ausgabe durch Librarian muss getrackt werden → Status ISSUED
- Rückgabeprozess nicht in dieser Story → Separate Story (Librarian-Feature)
- Mahngebühren-System → Spätere Version mit Payment-Integration
- Reservierungssystem bei Nichtverfügbarkeit → Could-Have Feature
- Reminder-E-Mails 3 Tage vor Ablauf → Scheduled Job (separate Story)
