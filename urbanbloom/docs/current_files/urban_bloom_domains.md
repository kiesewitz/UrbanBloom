# UrbanBloom — Domains

Die folgenden Domänen bilden die Bounded Contexts von UrbanBloom. Begriffe (Entities, Value Objects, Domain Services …) sind im **DDD-Glossar** als Single Source of Truth definiert.

## 1) User / Identity Domain
Verantwortlich für Registrierung, Login, Identitäts- und Einwilligungsverwaltung der Bürger:innen.

- **Aggregate Root:** User
- **Entities:** User
- **Value Objects:** ConsentStatus
- **Domain Services:** AuthService, PrivacyService
- **Typische Methoden / Use Cases:**
  - `User.register()` – neuen User registrieren.
  - `User.anonymize()` – personenbezogene Daten unwiderruflich löschen/anonymisieren.
  - `AuthService.authenticate(credentials)` – Login prüfen.
  - `PrivacyService.requestDeletion(userId)` – DSGVO-konforme Löschung anstoßen.

## 2) Action / Observation Domain
Dokumentiert und verifiziert Begrünungsaktionen (z. B. Pflanzen setzen, Baumscheibe begrünen) und bildet die Grundlage für Punktevergabe.

- **Aggregate Root:** Action
- **Entities:** Action
- **Value Objects:** PlantVO, LocationVO
- **Domain Services:** GeoVerificationService, PlantPlausibilityService, PointsEngine
- **Typische Methoden / Use Cases:**
  - `Action.registerAction(userId, plant, location, mediaRefs)` – neue Aktion erfassen.
  - `Action.validate()` – Aktion fachlich/geo-basiert validieren.
  - `Action.assignPoints(points)` – Punkte nach Gamification-Regeln zuordnen.
  - `Action.markSynced()` – Aktion als erfolgreich synchronisiert markieren.
  - `GeoVerificationService.verify(locationVO)` – Standort plausibilisieren.
  - `PlantPlausibilityService.check(plantVO, locationVO)` – Pflanzenwahl bewerten.
  - `PointsEngine.calculatePoints(action)` – Punktzahl berechnen.

## 3) Plant Catalog Domain
Hält den zentralen Pflanzenkatalog, der für Aktionen, Empfehlungen und Plausibilitätsprüfungen verwendet wird.

- **Aggregate Root:** Plant
- **Entities:** Plant
- **Domain Services:** PlantInfoService
- **Typische Methoden / Use Cases:**
  - `PlantInfoService.lookup(plantId)` – Pflanze nach ID finden.
  - `PlantInfoService.searchByCriteria(sun, water, type)` – geeignete Pflanzen suchen.
  - `Plant.isSuitableFor(locationType)` – prüfen, ob Pflanze zum Standort passt.

## 4) Location / District Domain
Verwaltet Standorte, zugehörige Bezirke und aggregierte Kennzahlen pro Bezirk.

- **Aggregates:** District
- **Entities:** Location, District
- **Value Objects:** DistrictStats
- **Typische Methoden / Use Cases:**
  - `Location.register(address, geoPoint)` – neuen Standort anlegen.
  - `Location.assignDistrict(district)` – Standort einem Bezirk zuordnen.
  - `DistrictStats.recalculateFor(district)` – Kennzahlen für einen Bezirk neu berechnen (z. B. Anzahl Aktionen, Punkte).
  - `District.getStats()` – aktuelle Kennzahlen für Dashboard/Reports liefern.

## 5) Gamification Domain
Regelt Punkte, Badges und Ranglisten, die aus Aktionen der User entstehen.

- **Entities:** Points, Badge
- **Read Models:** Leaderboard
- **Domain Services:** BadgeAssignmentService
- **Typische Methoden / Use Cases:**
  - `Points.add(points)` – Punktestand eines Users erhöhen.
  - `Points.getTotalForUser(userId)` – Gesamtpunkte eines Users ermitteln.
  - `BadgeAssignmentService.assignBadgesFor(user)` – Badges nach Regeln vergeben.
  - `Leaderboard.updateFor(action)` – Leaderboard nach neuer Aktion aktualisieren.
  - `Leaderboard.getTopUsersByDistrict(districtId)` – Rangliste pro Bezirk abrufen.

## 6) Challenge Domain
Definiert Challenges (z. B. „Grünster Bezirk“) und bewertet den Fortschritt.

- **Aggregate Root:** Challenge
- **Entities:** Challenge
- **Domain Services:** ChallengeEvaluationService
- **Typische Methoden / Use Cases:**
  - `Challenge.schedule(start, end, scope)` – Challenge anlegen/planen.
  - `ChallengeEvaluationService.evaluateProgress(challengeId)` – Fortschritt berechnen.
  - `ChallengeEvaluationService.finishChallenge(challengeId)` – Challenge abschließen & Gewinner bestimmen.

## 7) Notification / Reminder Domain
Verwaltet Benachrichtigungseinstellungen und das Versenden von Erinnerungen/Push-Notifications.

- **Value Objects:** NotificationSetting
- **Domain Services:** NotificationService, SchedulerService
- **Typische Methoden / Use Cases:**
  - `NotificationSetting.updatePreferences(userId, channels, frequency)` – Präferenzen des Users anpassen.
  - `SchedulerService.scheduleReminder(userId, trigger)` – Erinnerung (z. B. Pflegehinweis) einplanen.
  - `NotificationService.send(notification)` – Benachrichtigung ausliefern.
  - `SchedulerService.cancelReminder(reminderId)` – Terminierte Erinnerung abbrechen.

## 8) Admin / Analytics Domain
Bietet Auswertungen für die Stadtverwaltung und andere Stakeholder (z. B. Bericht je Bezirk).

- **Entities:** Report
- **Value Objects / DTOs:** DistrictComparison
- **Domain Services:** AnalyticsService
- **Typische Methoden / Use Cases:**
  - `AnalyticsService.generateDistrictReport(districtId, period)` – Report für einen Bezirk erzeugen.
  - `AnalyticsService.exportCSV(filter)` – Datenexport für weitere Analysen.
  - `AnalyticsService.compareDistricts(criteria)` – Bezirke vergleichen (z. B. pro Kopf begrünte Fläche).
  - `Report.renderAsPdf()` – Bericht als PDF für Download/Versand generieren.

## 9) Sync / Offline Domain
Kümmert sich um Offline-Speicherung von Aktionen und deren spätere Synchronisation mit dem Backend.

- **Value Objects:** OfflinePayload
- **Domain Services:** SyncService
- **Typische Methoden / Use Cases:**
  - `SyncService.enqueuePayload(offlinePayload)` – Offline-Daten in Queue aufnehmen.
  - `SyncService.processQueue()` – gespeicherte Aktionen ins Backend übertragen.
  - `SyncService.resolveConflict(serverAction, clientAction)` – Konflikte bei unterschiedlichem Stand auflösen.
  - `OfflinePayload.toAction()` – Payload in eine valide Action des Action-Domain-Kontexts überführen.
