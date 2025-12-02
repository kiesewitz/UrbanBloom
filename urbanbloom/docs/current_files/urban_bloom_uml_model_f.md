# UrbanBloom — UML Domain Model (PlantUML)

## 1) Gesamtmodell mit Domaingrenzen und Kommunikation
```plantuml
@startuml OverallDomainModel
skinparam packageStyle rectangle

package "User / Identity" as UserDomain {
  class User
  class ConsentStatus
}

package "Action / Observation" as ActionDomain {
  class Action
}

package "Location / District" as LocationDomain {
  class Location
  class District
  class DistrictStats
}

package "Plant Catalog" as PlantDomain {
  class Plant
}

package "Gamification" as GamificationDomain {
  class Points
  class Badge
  class Leaderboard
}

package "Challenge" as ChallengeDomain {
  class Challenge
}

package "Notification / Reminder" as NotificationDomain {
  class NotificationSetting
}

package "Admin / Analytics" as AnalyticsDomain {
  class Report
}

package "Sync / Offline" as SyncDomain {
  class OfflinePayload
}

User --> Action : "führt aus"
Action --> Location : "findet statt an"
Action --> Plant : "bezieht sich auf"
Location --> District : "liegt in"
User --> Points : "erhält"
Points --> Badge : "führt zu"
Challenge --> User : "zielt auf"
NotificationSetting --> User : "gilt für"
Report ..> District : "wertet aus"
OfflinePayload ..> Action : "offline Kopie"

ActionDomain ..> GamificationDomain : "Event: ActionRegistered"
ActionDomain ..> LocationDomain : "nutzt Geo-Daten"
GamificationDomain ..> AnalyticsDomain : "liefert KPIs"
SyncDomain ..> ActionDomain : "synchronisiert Aktionen"
NotificationDomain ..> UserDomain : "sendet Nachrichten"

@enduml
```

## 2) User / Identity Domain
```plantuml
@startuml UserDomain
package "User / Identity Domain" {
  class User <<Entity>> {
    +userId: UUID
    +name: String
    +email: String
    +createdAt: DateTime
    +totalPoints: int

    +register(name: String, email: String)
    +addPoints(amount: int)
    +anonymize()
  }

  class ConsentStatus <<VO>> {
    +consentGiven: boolean
    +consentVersion: String
    +consentDate: DateTime

    +isValidForVersion(version: String): boolean
  }

  class AuthService <<DomainService>> {
    +register(email: String, password: String): User
    +authenticate(email: String, password: String): User
  }

  class PrivacyService <<DomainService>> {
    +requestDeletion(userId: UUID)
    +anonymizeUser(user: User)
  }

  User --> ConsentStatus : "has"
  AuthService ..> User
  PrivacyService ..> User
}
@enduml
```

## 3) Action / Observation Domain
```plantuml
@startuml ActionDomain
package "Action / Observation Domain" {
  class Action <<AggregateRoot>> {
    +actionId: UUID
    +userId: UUID
    +photoUrl: String
    +status: String
    +createdAt: DateTime

    +registerAction()
    +validate()
    +assignPoints(points: int)
    +markSynced()
  }

  class PlantVO <<VO>> {
    +plantId: UUID
    +speciesName: String
    +category: String
  }

  class LocationVO <<VO>> {
    +latitude: double
    +longitude: double
    +address: String
  }

  class GeoVerificationService <<DomainService>> {
    +verify(location: LocationVO): boolean
  }

  class PlantPlausibilityService <<DomainService>> {
    +check(plant: PlantVO, location: LocationVO): boolean
  }

  class PointsEngine <<DomainService>> {
    +calculatePoints(action: Action): int
  }

  Action --> PlantVO
  Action --> LocationVO
  Action ..> GeoVerificationService
  Action ..> PlantPlausibilityService
  Action ..> PointsEngine
}
@enduml
```

## 4) Plant Catalog Domain
```plantuml
@startuml PlantDomain
package "Plant Catalog Domain" {
  class Plant <<Entity>> {
    +plantId: UUID
    +latinName: String
    +commonName: String
    +category: String
    +sunExposure: String
    +waterNeed: String
  }

  class PlantInfoService <<DomainService>> {
    +lookup(plantId: UUID): Plant
    +searchByCriteria(sun: String, water: String, category: String): List
  }

  PlantInfoService ..> Plant
}
@enduml
```

## 5) Location / District Domain
```plantuml
@startuml LocationDomain
package "Location / District Domain" {
  class Location <<Entity>> {
    +locationId: UUID
    +address: String
    +latitude: double
    +longitude: double

    +register(address: String, latitude: double, longitude: double)
  }

  class District <<AggregateRoot>> {
    +districtId: UUID
    +name: String
    +population: int

    +getStats(): DistrictStats
  }

  class DistrictStats <<VO>> {
    +totalActions: int
    +totalPoints: int
    +activeUsers: int
  }

  Location --> District
  District --> DistrictStats
}
@enduml
```

## 6) Gamification Domain
```plantuml
@startuml GamificationDomain
package "Gamification Domain" {
  class Points <<Entity>> {
    +userId: UUID
    +total: int
    +lastUpdated: DateTime

    +add(amount: int)
    +getTotal(): int
  }

  class Badge <<Entity>> {
    +badgeId: UUID
    +name: String
    +description: String
    +threshold: int

    +isAchieved(totalPoints: int): boolean
  }

  class Leaderboard <<ReadModel>> {
    +scope: String
    +entries: List

    +updateFor(userId: UUID, points: int)
    +getTop(limit: int)
  }

  class BadgeAssignmentService <<DomainService>> {
    +assignBadgesFor(user: User)
  }

  Points --> Badge : "unlocks"
  Leaderboard ..> Points
  BadgeAssignmentService ..> Points
  BadgeAssignmentService ..> Badge
}
@enduml
```

## 7) Challenge Domain
```plantuml
@startuml ChallengeDomain
package "Challenge Domain" {
  class Challenge <<AggregateRoot>> {
    +challengeId: UUID
    +name: String
    +scope: String
    +startDate: Date
    +endDate: Date

    +schedule(start: Date, end: Date, scope: String)
  }

  class ChallengeEvaluationService <<DomainService>> {
    +evaluateProgress(challengeId: UUID)
    +finishChallenge(challengeId: UUID)
  }

  Challenge ..> ChallengeEvaluationService
}
@enduml
```

## 8) Notification / Reminder Domain
```plantuml
@startuml NotificationDomain
package "Notification / Reminder Domain" {
  class NotificationSetting <<VO>> {
    +userId: UUID
    +channels: String
    +frequency: String
    +quietHours: String

    +updatePreferences(channels: String, frequency: String, quietHours: String)
  }

  class NotificationService <<DomainService>> {
    +sendNotification(userId: UUID, message: String)
  }

  class SchedulerService <<DomainService>> {
    +scheduleReminder(userId: UUID, trigger: String)
    +cancelReminder(reminderId: UUID)
  }

  NotificationService ..> NotificationSetting
  SchedulerService ..> NotificationSetting
}
@enduml
```

## 9) Sync / Offline Domain
```plantuml
@startuml SyncDomain
package "Sync / Offline Domain" {
  class OfflinePayload <<VO>> {
    +payloadId: UUID
    +userId: UUID
    +actionJson: String
    +createdAt: DateTime

    +toAction(): Action
  }

  class SyncService <<DomainService>> {
    +enqueuePayload(payload: OfflinePayload)
    +processQueue()
    +resolveConflict(serverAction: Action, clientAction: Action)
  }

  OfflinePayload ..> SyncService
}
@enduml
```
