# UrbanBloom Dokumentation

Dieser Ordner enthält die zentrale Projektdokumentation für UrbanBloom.

## Inhalt

### Domain-Driven Design Dokumentation

- **[domain-model-description-urbanbloom.md](domain-model-description-urbanbloom.md)**: Detaillierte Beschreibung des Domain Models mit allen Bounded Contexts, Aggregates, Entities, Value Objects und Domain Services
- **[urban_bloom_ddd_glossar.md](urban_bloom_ddd_glossar.md)**: DDD-Glossar mit allen Begriffen und deren Typen
- **[urban_bloom_domains.md](urban_bloom_domains.md)**: Übersicht über alle Domains mit typischen Use Cases und Methoden

### UML Modelle

- **[urban_bloom_uml_model_f-2.md](urban_bloom_uml_model_f-2.md)**: PlantUML-Diagramme für alle Domains

### User Stories

- **[urban_bloom_user_stories_with_domains.md](urban_bloom_user_stories_with_domains.md)**: Refined User Stories mit Domain-Zuordnung und MoSCoW-Priorisierung

## Bounded Contexts (Domains)

UrbanBloom ist nach Domain-Driven Design strukturiert und besteht aus folgenden Bounded Contexts:

1. **User / Identity** - Registrierung, Login, Identitätsverwaltung
2. **Action / Observation** - Dokumentation und Verifizierung von Begrünungsaktionen
3. **Plant Catalog** - Zentraler Pflanzenkatalog
4. **Location / District** - Standorte und Bezirksverwaltung
5. **Gamification** - Punkte, Badges und Ranglisten
6. **Challenge** - Challenges und Kampagnen
7. **Notification / Reminder** - Benachrichtigungen und Erinnerungen
8. **Admin / Analytics** - Auswertungen für die Stadtverwaltung
9. **Sync / Offline** - Offline-Speicherung und Synchronisation

## Verwendung

Diese Dokumentation dient als Single Source of Truth für:

- Entwickler (technische Implementierung)
- Product Owner (fachliche Anforderungen)
- Stakeholder (Projektübersicht)

Bei Änderungen am Domain Model oder an User Stories diese Dokumentation aktualisieren.
