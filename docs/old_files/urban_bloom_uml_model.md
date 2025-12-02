# UrbanBloom — UML Domain Model (PlantUML)

## 1) Gesamtmodell (ohne Attribute)
```plantuml
@startuml OverallDomainModel
package "UrbanBloom" {
  class User
  class Action
  class Location
  class District
  class Plant
  class Points
  class Badge
  class Challenge
  class NotificationSetting
  class Report

  User --> Action
  Action --> Location
  Action --> Plant
  Location --> District
  User --> Points
  Points --> Badge
  Challenge --> User
  NotificationSetting --> User
  Report ..> District
}
@enduml
```

## 2) User Domain
```plantuml
@startuml UserDomain
package "User Domain" {
  class User <<Entity>>
  class ConsentStatus <<VO>>
  class AuthService <<DomainService>>
  User --> ConsentStatus
  AuthService ..> User
}
@enduml
```

## 3) Action Domain
```plantuml
@startuml ActionDomain
package "Action Domain" {
  class Action <<AggregateRoot>>
  class PlantVO <<VO>>
  class LocationVO <<VO>>
  class GeoVerificationService <<DomainService>>
  class PointsEngine <<DomainService>>
  Action --> PlantVO
  Action --> LocationVO
  Action ..> GeoVerificationService
  Action ..> PointsEngine
}
@enduml
```

## 4) Location Domain
```plantuml
@startuml LocationDomain
package "Location" {
  class Location <<Entity>>
  class District <<AggregateRoot>>
  class DistrictStats <<VO>>
  Location --> District
}
@enduml
```

## 5) Plant Domain
```plantuml
@startuml PlantDomain
package "Plant Catalog" {
  class Plant <<Entity>>
  class PlantInfoService <<DomainService>>
  PlantInfoService ..> Plant
}
@enduml
```

## 6) Gamification Domain
```plantuml
@startuml GamificationDomain
package "Gamification" {
  class Points <<Entity>>
  class Badge <<Entity>>
  class Leaderboard <<ReadModel>>
  Points --> Badge
  Leaderboard ..> Points
}
@enduml
```

## 7) Challenge Domain
```plantuml
@startuml ChallengeDomain
package "Challenge" {
  class Challenge <<AggregateRoot>>
  class ChallengeEvaluationService <<DomainService>>
  Challenge ..> ChallengeEvaluationService
}
@enduml
```

## 8) Notification Domain
```plantuml
@startuml NotificationDomain
package "Notification" {
  class NotificationSetting <<VO>>
  class NotificationService <<DomainService>>
  NotificationService ..> NotificationSetting
}
@enduml
```

## 9) Sync Domain
```plantuml
@startuml SyncDomain
package "Sync" {
  class OfflinePayload
  class SyncService <<DomainService>>
  OfflinePayload ..> SyncService
}
@enduml
```

