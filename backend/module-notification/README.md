# Module: Notification Context (Supporting Subdomain)

**Domänen-Kategorie:** Supporting Subdomain  
**Architekturmuster:** Event-Driven, vereinfachte Schichtenarchitektur  
**Kritikalität:** ⭐⭐ (Medium)

## Übersicht

Der **Notification Context** versendet Benachrichtigungen an Nutzer basierend auf Domain Events aus anderen Contexts.

### Verantwortlichkeiten

- ✅ Empfang von Domain Events (Lending, Catalog, Reminding)
- ✅ Versand von Benachrichtigungen via E-Mail (MVP)
- ✅ Benachrichtigungshistorie speichern
- ✅ Retry-Mechanismus bei Fehlern

## Architektur-Struktur

```
module-notification/
├── domain/                      # Notification Entity + Value Objects
├── application/                 # Notification Services
├── adapter/
│   ├── in/event/               # Domain Event Listeners
│   └── out/channel/            # E-Mail-Sender (SMTP)
└── config/                      # Spring Configuration
```

---

## Domain Events (Consumed)

| Event | Trigger | Notification |
|-------|---------|-------------|
| `LoanCreatedEvent` | Ausleihe erstellt | "Ausleihe bestätigt, Fälligkeitsdatum: ..." |
| `LoanReturnedEvent` | Rückgabe | "Rückgabe bestätigt" |
| `ReservationCreatedEvent` | Reservierung | "Reservierung erstellt, bitte in 48h abholen" |
| `ReservationExpiredEvent` | Reservierung verfallen | "Reservierung verfallen" |
| `LoanOverdueEvent` | Überfällig | "Erinnerung: Rückgabe überfällig" |
| `ReminderDueEvent` | Reminder | "Erinnerung: Rückgabe fällig in 3 Tagen" |

---

## Adapter: Event Listener (`adapter/in/event/`)

**Beispiel:**
```java
@Component
@RequiredArgsConstructor
public class LendingEventListener {
    
    private final NotificationService notificationService;
    
    @EventListener
    @Async
    public void onLoanCreated(LoanCreatedEvent event) {
        notificationService.sendNotification(Notification.builder()
            .userId(event.getUserId())
            .type(NotificationType.LOAN_CREATED)
            .subject("Ausleihe bestätigt")
            .message("Ihr Medium wurde ausgeliehen. Fälligkeitsdatum: " + event.getDueDate())
            .build());
    }
    
    @EventListener
    @Async
    public void onLoanOverdue(LoanOverdueEvent event) {
        notificationService.sendNotification(Notification.builder()
            .userId(event.getUserId())
            .type(NotificationType.LOAN_OVERDUE)
            .subject("Rückgabe überfällig")
            .message("Bitte geben Sie das Medium zurück.")
            .build());
    }
}
```

---

## Adapter: E-Mail Channel (`adapter/out/channel/`)

**Beispiel:**
```java
@Component
@RequiredArgsConstructor
public class EmailNotificationChannel {
    
    private final JavaMailSender mailSender;
    
    @Async
    @Retryable(maxAttempts = 3, backoff = @Backoff(delay = 2000))
    public void send(String to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);
        message.setFrom("noreply@schulbib.de");
        
        mailSender.send(message);
    }
}
```

---

## Datenbankschema

Schema: **`notification_schema`**

Tabellen:
- `notifications` - Benachrichtigungshistorie

---

## Referenzen

- 📖 [Strategic Architecture Summary](../../docs/architecture/strategic-architecture-summary.md)
- 📬 [Domain Events & Integrations](../../docs/architecture/domain-events-integrations.md)
