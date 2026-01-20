# Lessons Learned: Keycloak DPoP-Fehler und Authentifizierungs-Flow

**Datum:** 2026-01-11  
**Kontext:** Backend-Login-Feature / Keycloak OAuth2 Integration  
**Beteiligte:** Uwe Kondert (Product Owner), GitHub Copilot (AI Agent)

## 1. Problemübersicht (Situation)
Beim Implementieren des User-Login-Features für die mobile App trat ein Authentifizierungsfehler auf:  
- Nach erfolgreicher Registrierung schlug der Login mit einem 400 BAD_REQUEST fehl.  
- Die Fehlermeldung lautete: `{"error":"invalid_request","error_description":"DPoP proof is missing"}`  
- Der Fehler trat auf, nachdem der Keycloak-Client korrekt konfiguriert und das Secret im Backend hinterlegt wurde.  
- Die Entwicklung wurde blockiert, da kein User-Login möglich war.

## 2. Kernursachen (Root Cause Analysis)
- [x] Technisches Hindernis: Keycloak verlangte einen DPoP-Proof, obwohl das Backend diesen nicht unterstützt.
- [x] Wissenslücke: Die Bedeutung und Auswirkung der DPoP-Einstellung im Keycloak-Client war zunächst unklar.
- [ ] Prozessfehler: Keine
- *Detailbeschreibung der Ursachen:*  
  Die DPoP-Einstellung im Keycloak-Client war aktiviert. Dadurch erwartete Keycloak einen DPoP-Header im Token-Request, den Standard-REST-Clients (wie Spring Boot RestTemplate) nicht liefern. Die Fehlermeldung war irreführend, da die Client-Authentifizierung und das Secret korrekt gesetzt waren.

## 3. Umgesetzte Lösung (Action Taken)
- Keycloak-Client `schoollibrary-app` in der Admin-Konsole geöffnet.
- Im Bereich "Capability config" die Option "DPoP" auf "Off" gesetzt.
- Änderungen gespeichert und Keycloak-Container neu gestartet (`docker-compose restart keycloak`).
- Backend erneut getestet: Login funktionierte sofort.

## 4. Konkrete Referenzen (Code & Docs)
- Backend-Log: [backend/schoollibrary-app/logs/schoollibrary.log](../../backend/schoollibrary-app/logs/schoollibrary.log#L533)
- Keycloak Setup-Doku: [docs/configurations/keycloak-setup.md](../../docs/configurations/keycloak-setup.md)
- AuthController: [backend/module-user/src/main/java/com/schoollibrary/user/api/AuthController.java](../../backend/module-user/src/main/java/com/schoollibrary/user/api/AuthController.java)

## 5. Empfehlungen & Lessons Learned
- **Stop doing:** DPoP aktivieren, wenn Backend/Client keine DPoP-Unterstützung bietet.
- **Start doing:** Bei OAuth2-Fehlern immer die Keycloak-Client-Konfiguration (insbesondere DPoP und Authentifizierung) prüfen.
- **Keep doing:** Fehler systematisch analysieren, Logs und Keycloak-Events auswerten, Doku aktuell halten.

## 6. Alternative Ansätze
- Ansatz A: DPoP-Implementierung im Backend (Spring Security).  
  Grund für Ablehnung: Unnötig komplex, da DPoP für klassische Web-/Mobile-Apps nicht erforderlich ist.

## 7. Fazit / TL;DR
DPoP ist für Standard-Login-Flows mit Keycloak und Spring Boot nicht notwendig. Die Aktivierung kann zu schwer verständlichen Fehlern führen. Die Lösung: DPoP im Keycloak-Client deaktivieren.
