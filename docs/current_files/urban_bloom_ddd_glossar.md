# UrbanBloom — DDD Glossar

| Begriff | Typ | Beschreibung |
|--------|-----|--------------|
| Action | AggregateRoot | Dokumentierte Begrünungsaktion mit Referenz zu User, Standort und Pflanze. |
| AnalyticsService | DomainService | Erzeugt Berichte und Auswertungen für Verwaltung und Stakeholder. |
| AuthService | DomainService | Verantwortlich für Registrierung, Login und Authentifizierung von Usern. |
| Badge | Entity | Auszeichnung, die ein User nach bestimmten Regeln erhält. |
| BadgeAssignmentService | DomainService | Vergibt Badges an User basierend auf Punktestand oder weiteren Kriterien. |
| Challenge | AggregateRoot | Challenge/Kampagne mit Zeitraum, Ziel und Gültigkeitsbereich (z. B. Bezirk). |
| ChallengeEvaluationService | DomainService | Bewertet Fortschritt und Ergebnis einer Challenge. |
| ConsentStatus | VO | Hält Einwilligungsstatus und Version der Datenschutzbestimmungen für einen User. |
| District | AggregateRoot | Verwaltungseinheit (Bezirk) der Stadt, in der Aktionen stattfinden. |
| DistrictComparison | DTO | Datenobjekt zum Vergleich mehrerer Bezirke nach definierten Kriterien. |
| DistrictStats | VO | Aggregierte Kennzahlen für einen Bezirk (Anzahl Aktionen, Punkte, aktive User …). |
| GeoVerificationService | DomainService | Prüft Plausibilität und Gültigkeit eines Standortes (z. B. Geo-Fence). |
| Leaderboard | ReadModel | Auswertungsmodell für Ranglisten, z. B. Top-User pro Bezirk. |
| Location | Entity | Konkreter Standort, an dem Begrünungsaktionen durchgeführt werden. |
| LocationVO | VO | Kompakte Standortinformationen (Geo-Koordinate, Adresse) innerhalb einer Action. |
| NotificationService | DomainService | Versendet Benachrichtigungen auf Basis von Events oder Zeitplänen. |
| NotificationSetting | VO | Benachrichtigungspräferenzen eines Users (Kanäle, Häufigkeit, Ruhezeiten). |
| OfflinePayload | VO | Offline gespeicherte Repräsentation einer Aktion inkl. Medien und Metadaten. |
| Plant | Entity | Eintrag im zentralen Pflanzenkatalog (Art, Pflegehinweise etc.). |
| PlantInfoService | DomainService | Liefert Detailinformationen zu Pflanzen aus dem Katalog. |
| PlantPlausibilityService | DomainService | Bewertet, ob eine gewählte Pflanze zum Standort und Kontext passt. |
| PlantVO | VO | Kompakte Pflanzeninformationen (ID, Gattung, Kategorie) innerhalb einer Action. |
| Points | Entity | Punktestand eines Users im Gamification-Kontext. |
| PointsEngine | DomainService | Berechnet Punkte für Aktionen anhand der Gamification-Regeln. |
| PrivacyService | DomainService | Unterstützt Datenschutz-Funktionen wie Anonymisierung oder Löschanfragen. |
| Report | Entity | Analytischer Bericht mit Kennzahlen, der für Verwaltung und Export genutzt wird. |
| SchedulerService | DomainService | Plant und verwaltet zeitgesteuerte Jobs wie Erinnerungen oder Reports. |
| SyncService | DomainService | Synchronisiert OfflinePayloads mit dem Backend und löst Konflikte auf. |
| User | Entity | Repräsentiert eine registrierte Person, die Begrünungsaktionen durchführt. |