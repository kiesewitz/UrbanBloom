# Context Map Visualisierungen - Digital School Library

**Version:** 1.0  
**Format:** Mermaid Diagrams  
**Verwendung:** Druckbar, HTML-Export, Wiki-Integration

---

## 📊 Diagramm 1: Bounded Contexts Übersicht

```mermaid
graph TB
    subgraph "🎯 CORE DOMAIN"
        LENDING["<b>LENDING CONTEXT</b><br/>────────<br/>Loan<br/>Reservation<br/>PreReservation<br/>ClassSet"]
        style LENDING fill:#fee2e2,stroke:#dc2626,stroke-width:3px,color:#000
    end
    
    subgraph "🛠️ SUPPORTING SUBDOMAINS"
        CATALOG["<b>CATALOG CONTEXT</b><br/>────────<br/>Media<br/>MediaCopy<br/>Inventory<br/>AvailabilityStatus"]
        NOTIFICATION["<b>NOTIFICATION CONTEXT</b><br/>────────<br/>Notification<br/>NotificationChannel<br/>EventListener"]
        REMINDING["<b>REMINDING CONTEXT</b><br/>────────<br/>ReminderPolicy<br/>ReminderCampaign"]
        style CATALOG fill:#dbeafe,stroke:#0284c7,stroke-width:2px,color:#000
        style NOTIFICATION fill:#dbeafe,stroke:#0284c7,stroke-width:2px,color:#000
        style REMINDING fill:#dbeafe,stroke:#0284c7,stroke-width:2px,color:#000
    end
    
    subgraph "📋 GENERIC SUBDOMAINS"
        USER["<b>USER CONTEXT</b><br/>────────<br/>User<br/>UserProfile<br/>SchoolIdentity"]
        style USER fill:#e5e7eb,stroke:#6b7280,stroke-width:1px,color:#000
    end
    
    subgraph "🔐 EXTERNAL"
        SSO["<b>School SSO</b><br/>────────<br/>OpenID/OAuth2<br/>SAML"]
        style SSO fill:#f3f4f6,stroke:#9ca3af,stroke-width:1px,color:#000,stroke-dasharray:5 5
    end
    
    %% Arrows with Sync/Async labels
    SSO -->|authenticates| USER
    USER -->|Q: eligibility| LENDING
    CATALOG -->|Q: availability| LENDING
    LENDING -->|E: MediaCheckedOut| CATALOG
    LENDING -->|E: MediaReturned| CATALOG
    LENDING -->|E: MediaReserved| NOTIFICATION
    LENDING -->|E: ReminderTriggered| NOTIFICATION
    LENDING -->|Q: read-only| REMINDING
    REMINDING -->|E: ReminderTriggered| NOTIFICATION
    
    classDef syncStyle stroke:#7c3aed,stroke-width:2px,color:#fff
    classDef asyncStyle stroke:#16a34a,stroke-width:2px,color:#fff
```

---

## 📊 Diagramm 2: Integration Flows (Detailed)

```mermaid
sequenceDiagram
    participant User
    participant Admin
    participant LendingCtx as Lending Context<br/>(CORE)
    participant CatalogCtx as Catalog Context
    participant NotifCtx as Notification Context
    participant RemindCtx as Reminding Context
    
    User->>LendingCtx: Reserve available media
    activate LendingCtx
    LendingCtx->>LendingCtx: Create Reservation<br/>(48h TTL)
    LendingCtx-xCatalogCtx: Update status=Reserved
    LendingCtx-xNotifCtx: Send "Ready for pickup"
    deactivate LendingCtx
    
    Note over Admin,RemindCtx: ──── 3 days later ────
    
    Admin->>LendingCtx: Scan media barcode
    activate LendingCtx
    LendingCtx->>LendingCtx: Create Loan<br/>DueDate = NOW + 21d
    LendingCtx-xCatalogCtx: Update status=CheckedOut
    LendingCtx-xNotifCtx: Send confirmation
    LendingCtx-xRemindCtx: Schedule reminders
    deactivate LendingCtx
    
    Note over Admin,RemindCtx: ──── 18 days later ────
    
    RemindCtx->>RemindCtx: Check policy<br/>3 days before due?
    RemindCtx-xNotifCtx: Trigger ReminderTriggered
    NotifCtx->>User: Send reminder email
    
    Note over Admin,RemindCtx: ──── 3 more days ────
    
    Admin->>LendingCtx: Scan media barcode
    activate LendingCtx
    LendingCtx->>LendingCtx: Complete Loan
    LendingCtx-xCatalogCtx: Update status=Available<br/>Process PreReservations?
    LendingCtx-xNotifCtx: Send confirmation
    LendingCtx-xRemindCtx: Clear reminders
    deactivate LendingCtx
    
    Note over User,RemindCtx: ✓ Cycle complete
```

---

## 📊 Diagramm 3: Domain Events Chain

```mermaid
graph LR
    A["👤 User<br/>clicks<br/>CHECKOUT"] -->|triggers| B["LENDING<br/>CONTEXT"]
    
    B -->|validates| B1["✓ User<br/>✓ Media<br/>✓ Limit"]
    B1 -->|creates| B2["Loan<br/>Aggregate"]
    B2 -->|publishes| E1["📬 MediaCheckedOut<br/>Event"]
    
    E1 -->|async| C["CATALOG<br/>CONTEXT"]
    C -->|updates| C1["MediaCopy.status<br/>= CheckedOut"]
    
    E1 -->|async| D["NOTIFICATION<br/>CONTEXT"]
    D -->|sends| D1["📧 Email:<br/>Confirmation"]
    
    E1 -->|async| R["REMINDING<br/>CONTEXT"]
    R -->|schedules| R1["⏰ Reminder<br/>T-3, T+1, T+7"]
    
    style B fill:#fee2e2,stroke:#dc2626,stroke-width:2px
    style E1 fill:#dcfce7,stroke:#16a34a,stroke-width:2px
    style C fill:#dbeafe,stroke:#0284c7,stroke-width:2px
    style D fill:#dbeafe,stroke:#0284c7,stroke-width:2px
    style R fill:#dbeafe,stroke:#0284c7,stroke-width:2px
```

---

## 📊 Diagramm 4: Waitlist / PreReservation Resolution

```mermaid
sequenceDiagram
    participant User1
    participant User2
    participant LendingCtx as Lending Context
    participant CatalogCtx as Catalog Context
    participant NotifCtx as Notification Context
    
    User1->>LendingCtx: Checkout media
    LendingCtx->>LendingCtx: status = CheckedOut
    
    User2->>LendingCtx: Pre-Reserve media
    LendingCtx->>LendingCtx: Create PreReservation<br/>position = 1<br/>Waitlist = [User2]
    LendingCtx-xNotifCtx: Send "Added to waitlist"
    
    Note over User1,NotifCtx: ──── User1 returns media ────
    
    User1->>LendingCtx: Scan barcode
    activate LendingCtx
    LendingCtx->>LendingCtx: status = Active → Returned
    LendingCtx->>LendingCtx: Process Waitlist:<br/>Take first<br/>Convert to<br/>Reservation (48h)
    LendingCtx-xCatalogCtx: status = Reserved
    LendingCtx-xNotifCtx: MediaReserved Event
    NotifCtx->>User2: 🔔 "Ready for pickup!"
    deactivate LendingCtx
    
    Note over User1,NotifCtx: ✓ User2 automatically gets slot
```

---

## 📊 Diagramm 5: Klassensatz Special Handling

```mermaid
graph TD
    T["👨‍🏫 Teacher<br/>initiates"]
    T -->|checkout| L["LENDING<br/>CONTEXT"]
    
    L -->|validate| L1["✓ UserGroup=Teacher<br/>✓ ALL SetMembers<br/>available"]
    L1 -->|create| L2["ClassSetLoan<br/>────<br/>Aggregate"]
    L2 -->|set| L3["DueDate = NOW+56d<br/>ALL copies=CheckedOut"]
    L3 -->|publish| L4["📬 ClassSetCheckedOut<br/>Event"]
    
    L4 -->|update| C["CATALOG:<br/>Mark all copies<br/>as CheckedOut"]
    L4 -->|notify| N["NOTIFICATION:<br/>Send checklist<br/>to Teacher"]
    
    T -->|later| T2["👨‍🏫 Return media"]
    T2 -->|scan all copies| L
    
    L -->|validate| L5["✓ Complete set<br/>scanned?"]
    L5 -->|yes| L6["Mark as Returned"]
    L5 -->|NO| L7["⚠️ Incomplete:<br/>Flag for Admin"]
    
    L6 -->|publish| L8["📬 ClassSetReturned<br/>Event"]
    L8 -->|notify| N2["NOTIFICATION:<br/>Receipt email"]
    
    style L fill:#fee2e2,stroke:#dc2626,stroke-width:2px
    style L2 fill:#fef3c7,stroke:#eab308,stroke-width:2px
    style L7 fill:#fee2e2,stroke:#dc2626,stroke-width:2px
```

---

## 📊 Diagramm 6: State Machines

### MediaCopy Availability State Machine

```mermaid
stateDiagram-v2
    [*] --> Available
    
    Available --> Reserved: User reserves
    Available --> CheckedOut: Admin checkout
    
    Reserved --> Available: Expires (48h)
    Reserved --> CheckedOut: Admin checkout
    
    CheckedOut --> Available: Return on-time
    CheckedOut --> Available: Return overdue
    
    note right of CheckedOut
        PreReservation
        queue active
        (no state change)
    end note
    
    note right of Available
        Auto-process
        PreReservations:
        First → Reserve
    end note
```

### Loan State Machine

```mermaid
stateDiagram-v2
    [*] --> Active
    
    Active --> Renewed: Renew (max 2x)
    Active --> Returned: Return on-time
    Active --> Returned: Return overdue
    
    Renewed --> Returned: Return on-time
    Renewed --> Returned: Return overdue
    
    Returned --> [*]
    
    note right of Active
        DueDate calculated
        Reminders scheduled
        Renewal counter = 0
    end note
    
    note right of Renewed
        DueDate extended
        Renewal counter++
        Reminders rescheduled
    end note
    
    note right of Returned
        Marked with:
        - ReturnDate
        - IsOverdue flag
        - OverdueDays count
    end note
```

### Reservation State Machine

```mermaid
stateDiagram-v2
    [*] --> Reserved
    
    Reserved --> Collected: User picks up
    Reserved --> Expired: Expires after 48h
    
    Collected --> [*]
    Expired --> [*]
    
    note right of Reserved
        TTL: 48 hours
        Notification sent
        Auto-cleanup job
    end note
```

---

## 📊 Diagramm 7: Context Dependency Matrix

```mermaid
graph TB
    subgraph Matrix["Context Dependencies"]
        direction LR
        
        subgraph Legend
            direction LR
            Q["Q = Sync Query"]
            E["E = Async Event"]
            D["- = None"]
            style Q fill:#7c3aed,color:#fff
            style E fill:#16a34a,color:#fff
            style D fill:#e5e7eb,color:#000
        end
        
        subgraph User["👥 USER"]
            USER["User<br/>Eligibility"]
        end
        
        subgraph Catalog["📚 CATALOG"]
            CAT["Media<br/>Availability"]
        end
        
        subgraph Lending["💳 LENDING<br/>(Core)"]
            LEND["Loan<br/>Reservation<br/>PreReservation<br/>ClassSet"]
        end
        
        subgraph Notif["🔔 NOTIFICATION"]
            NOT["Notification<br/>Templates"]
        end
        
        subgraph Remind["⏰ REMINDING"]
            REM["Reminder<br/>Policies"]
        end
        
        USER -->|Q| LEND
        CAT -->|Q| LEND
        LEND -->|E| CAT
        LEND -->|E| NOT
        LEND -->|E| REM
        REM -->|Q| LEND
        REM -->|E| NOT
        
        style USER fill:#e5e7eb
        style CAT fill:#dbeafe
        style LEND fill:#fee2e2
        style NOT fill:#dbeafe
        style REM fill:#dbeafe
    end
```

---

## 📊 Diagramm 8: MVP Scope vs. Future

```mermaid
graph TB
    subgraph MVP["✅ MVP SCOPE (This Design)"]
        M1["Lending Context<br/>(Core)"]
        M2["Catalog Context"]
        M3["User Context"]
        M4["Notification Context"]
        M5["Reminding Context"]
        
        style M1 fill:#fee2e2,stroke:#dc2626
        style M2 fill:#dbeafe,stroke:#0284c7
        style M3 fill:#e5e7eb,stroke:#6b7280
        style M4 fill:#dbeafe,stroke:#0284c7
        style M5 fill:#dbeafe,stroke:#0284c7
    end
    
    subgraph Future["🚀 FUTURE PHASES"]
        F1["Analytics Context<br/>(Reporting & Stats)"]
        F2["Recommendation Context<br/>(ML-based suggestions)"]
        F3["Compliance Context<br/>(Privacy, GDPR)"]
        F4["Dunning Context<br/>(Payment reminders)"]
        
        style F1 fill:#dbeafe,stroke:#0284c7,stroke-dasharray:5 5
        style F2 fill:#dbeafe,stroke:#0284c7,stroke-dasharray:5 5
        style F3 fill:#f3e8ff,stroke:#7c3aed,stroke-dasharray:5 5
        style F4 fill:#dbeafe,stroke:#0284c7,stroke-dasharray:5 5
    end
    
    M5 -.->|subscribes to Events| F1
    M5 -.->|subscribes to Events| F4
    M1 -.->|provides data for| F2
    
    Note1["Phase 2+: Add new Contexts<br/>without modifying MVP Contexts<br/>(Open/Closed Principle)"]
```

---

## 🎨 Legende

```
┌─────────────────────────────────────────────────────────────┐
│                    FARB-CODIERUNG                           │
├─────────────────────────────────────────────────────────────┤
│                                                              │
│  🔴 ROT (#fee2e2)      = CORE DOMAIN                        │
│     ├─ Höchste Komplexität                                 │
│     ├─ Beste Ressourcen                                    │
│     ├─ Detailliertes Testen                                │
│     └─ Zentraler Punkt aller Integrationen                 │
│                                                              │
│  🔵 BLAU (#dbeafe)     = SUPPORTING SUBDOMAIN               │
│     ├─ Mittlere Komplexität                                │
│     ├─ Unterstützen Core Domain                            │
│     ├─ Teilweise Standard-Lösungen                         │
│     └─ Mehrere Contexts möglich                            │
│                                                              │
│  ⚫ GRAU (#e5e7eb)     = GENERIC SUBDOMAIN                   │
│     ├─ Niedrige Komplexität                                │
│     ├─ Standard-Probleme                                   │
│     ├─ Kaufen statt Bauen möglich                          │
│     └─ Externe Dependencies                                │
│                                                              │
│  🟡 GELB               = SPECIAL HANDLING                    │
│     ├─ ClassSet (spezialisierte Regel)                     │
│     ├─ Klassensatz-spezifische Logik                       │
│     └─ Part of Lending Context aber besondere Rules        │
│                                                              │
│  ──────────────────────────────────────────────────────────  │
│                                                              │
│  ⚡ BOLD ARROW        = Synchrone Query (Request-Reply)     │
│     ├─ Blocking                                            │
│     ├─ Real-time Response                                  │
│     └─ Must succeed for action                             │
│                                                              │
│  ─ ─ DASHED ARROW     = Asynchrone Event (Pub-Sub)         │
│     ├─ Non-blocking                                        │
│     ├─ Fire-and-forget                                     │
│     ├─ Eventual consistency                                │
│     └─ Handler can fail independently                      │
│                                                              │
└─────────────────────────────────────────────────────────────┘
```

---

## 📐 Verwendung dieser Diagramme

### For Documentation:
- Kopieren Sie die Mermaid-Code-Blöcke in Markdown
- Verwenden Sie in README, Wiki, oder Docs

### For Presentation:
- Nutzen Sie Mermaid Live Editor (mermaid.live)
- Export als SVG/PNG für Slides

### For Code Comments:
```java
// Flow documented in: docs/architecture/context-map-visualizations.md
// Diagram 1: Bounded Contexts Overview
// Diagram 3: Domain Events Chain
public void checkout(User user, Media media) {
  // ...
}
```

