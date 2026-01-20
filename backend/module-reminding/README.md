# Module: Reminding Context (Supporting Subdomain)

**Domänen-Kategorie:** Supporting Subdomain  
**Architekturmuster:** Scheduler-basiert, vereinfachte Schichtenarchitektur  
**Kritikalität:** ⭐⭐ (Medium)

## Übersicht

Der **Reminding Context** verwaltet automatische Erinnerungen für bevorstehende und überfällige Rückgaben.

### Verantwortlichkeiten

- ✅ Scheduled Job für tägliche Prüfung
- ✅ Erinnerungen 3 Tage vor Fälligkeit
- ✅ Erinnerungen bei Überfälligkeit
- ✅ ReminderPolicy-Verwaltung (konfigurierbar)
- ✅ Publizieren von `ReminderDueEvent` und `LoanOverdueEvent`

## Architektur-Struktur

```
module-reminding/
├── domain/                      # ReminderPolicy, ReminderCampaign
├── application/                 # ReminderService
├── adapter/
│   ├── in/scheduler/           # Spring Scheduled Jobs
│   └── out/persistence/        # JPA Repository
└── config/                      # Spring Configuration
```

---

## Domain Events (Published)

| Event | Trigger | Subscriber |
|-------|---------|-----------|
| `ReminderDueEvent` | 3 Tage vor Fälligkeit | Notification Context |
| `LoanOverdueEvent` | Fälligkeit überschritten | Notification Context |

---

## Scheduler (`adapter/in/scheduler/`)

**Beispiel:**
```java
@Component
@RequiredArgsConstructor
public class DailyReminderJob {
    
    private final ReminderService reminderService;
    
    @Scheduled(cron = "0 0 8 * * *") // Täglich 08:00 Uhr
    public void sendDueReminders() {
        reminderService.sendDueReminders(LocalDate.now().plusDays(3));
    }
    
    @Scheduled(cron = "0 0 9 * * *") // Täglich 09:00 Uhr
    public void sendOverdueReminders() {
        reminderService.sendOverdueReminders(LocalDate.now());
    }
}
```

---

## Geschäftsregeln

- ✅ Erinnerung 3 Tage vor Fälligkeit (konfigurierbar)
- ✅ Täglich 1 Erinnerung bei Überfälligkeit
- ✅ Max. 3 Erinnerungen bei Überfälligkeit

---

## Datenbankschema

Schema: **`reminding_schema`**

Tabellen:
- `reminder_campaigns` - Versendete Erinnerungen (Historie)

---

## Referenzen

- 📖 [Strategic Architecture Summary](../../docs/architecture/strategic-architecture-summary.md)
- 📬 [Domain Events & Integrations](../../docs/architecture/domain-events-integrations.md)
