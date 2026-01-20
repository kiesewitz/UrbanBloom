# Domain

**Zweck:** Strukturpaket für den **catalog Context** (DDD + Ports & Adapters).

- domain/: fachliches Modell, Events, Ports (framework-frei)
- pplication/: Use Cases / Orchestrierung
- dapter/: In/Out Adapter (REST, Persistence, Integrationen)
- pi/: öffentliche Schnittstelle/Facade (falls benötigt)
- config/: Modul-Konfiguration (falls benötigt)
