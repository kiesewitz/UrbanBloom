# Architektur


## Mulit-modales Maven-Projekt

schulbibliothek/
├── pom.xml                                   # Maven Multi-Module Project
│
├── schulbibliothek-app/                      # Host-Anwendung (Einstiegspunkt)
│   ├── pom.xml
│   └── src/main/
│       ├── java/com/schulbib/
│       │   ├── SchulbibliothekApplication.java
│       │   └── config/
│       │       └── SecurityConfig.java
│       └── resources/
│           ├── application.yml
│           └── db/migration/                 # Flyway Migrations
│               ├── V1__Init_Ausleihe_Schema.sql
│               ├── V1__Init_Anschaffung_Schema.sql
│               ├── V1__Init_Nutzerprofil_Schema.sql
│               └── V1__Init_Shared_Data.sql
│
├── module-ausleihe/                          # Ausleih-Modul (Core Domain)
│   ├── pom.xml
│   └── src/main/java/com/schulbib/ausleihe/
│       ├── domain/                           # Domänenmodell (Hexagon-Kern)
│       │   ├── model/
│       │   │   ├── Ausleiher.java            # Aggregate Root
│       │   │   ├── AusleihExemplar.java      # Aggregate Root
│       │   │   ├── Ausleihe.java             # Entity
│       │   │   └── AusleiheId.java           # Value Object
│       │   ├── service/
│       │   │   └── MahnService.java          # Domain Service
│       │   ├── event/
│       │   │   └── BuchAusgeliehenEvent.java # Domain Event
│       │   └── port/
│       │       ├── in/                       # Use Case Ports
│       │       │   └── BuchAusleihenUseCase.java
│       │       └── out/                      # Repository Ports
│       │           ├── AusleiheRepository.java
│       │           └── EventPublisher.java
│       ├── application/                      # Application Layer
│       │   ├── service/
│       │   │   └── AusleiheApplicationService.java
│       │   ├── command/
│       │   │   └── BuchAusleihenCommand.java
│       │   └── query/
│       │       └── AusleihenAbfragenQuery.java
│       ├── adapter/                          # Adapter (außen)
│       │   ├── in/rest/
│       │   │   ├── AusleiheController.java   # REST Adapter
│       │   │   └── AusleiheMapper.java       # DTO Mapping
│       │   └── out/persistence/
│       │       ├── AusleiheJpaRepository.java
│       │       ├── AusleiheEntity.java       # JPA Entity
│       │       └── AusleiheRepositoryAdapter.java
│       ├── config/                           # Datenbankconfig für dieses Modul
│       │   └── AusleiheDataSourceConfig.java # Schema: ausleihe_*
│       └── api/                              # Öffentliche Schnittstelle
│           └── AusleiheModuleFacade.java
│
├── module-anschaffung/                       # Anschaffungs-Modul (Supporting - CRUD)
│   ├── pom.xml
│   └── src/main/java/com/schulbib/anschaffung/
│       ├── controller/                       # Modul API Controller (CRUD)
│       │   ├── BuchkatalogController.java
│       │   └── BestellungController.java
│       ├── entity/                           # JPA Entities (anämisch)
│       │   ├── BuchkatalogEintrag.java
│       │   └── Bestellung.java
│       ├── repository/                       # Spring Data JPA Repositories
│       │   ├── BuchkatalogRepository.java
│       │   └── BestellungRepository.java
│       ├── dto/                              # Data Transfer Objects
│       │   ├── BuchkatalogDto.java
│       │   └── BestellungDto.java
│       └── config/
│           └── AnschaffungDataSourceConfig.java # Schema: anschaffung_*
│
└── module-nutzerprofil/                      # Nutzerprofil-Modul (Generic)
    ├── pom.xml
    └── src/main/java/com/schulbib/nutzerprofil/
        ├── domain/
        │   └── model/
        │       └── Benutzerkonto.java        # Aggregate Root
        ├── application/
        ├── adapter/
        │   └── out/keycloak/
        │       └── KeycloakAdapter.java      # ACL zu Keycloak
        ├── config/
        │   └── NutzerprofilDataSourceConfig.java # Schema: nutzerprofil_*
        └── api/
            └── NutzerprofilModuleFacade.java

└── Shared/                                   # Gemeinsame Infrastruktur (kein Domänenwissen!)
    └── SharedKernel/                         # Value Objects, die mehrfach verwendet werden
        └── Geldbetrag.java
│
└── tests/
    ├── Schulbibliothek.Ausleihe.Tests/
    ├── Schulbibliothek.Anschaffung.Tests/
    └── Schulbibliothek.Nutzerprofil.Tests/
```
