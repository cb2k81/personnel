# PERS Sprint 2 – Testplan (PERS_Sprint2_2_Testplan)

## Status

Überarbeitet – angepasst an getrennte Service-Architektur (Domain / State / Privacy)

---

# 1. Ziel des Testplans

Ziel dieses Testplans ist es, für das Aggregat **Person** eine belastbare, sicherheitsorientierte und architekturkonforme Teststrategie zu definieren.

Die Architektur trennt nun explizit:

* PersonDomainService (CRUD + RLS + Hard Delete)
* PersonStateService (Workflow / Statusänderungen)
* PersonPrivacyService (Anonymisierung / DSGVO-Operation)
* PersonMetadataDomainService (Tags + KeyValue)

Der Fokus liegt auf:

* fachlicher Korrektheit
* Record-Level Security (ADR 010)
* Command Permissions (ADR 009)
* sauberer Trennung von Delete und Anonymisierung
* klarer Workflow-Architektur
* Security-by-Design

Dabei gilt ausdrücklich:

* Keine Test-Inflation
* Äquivalenzklassen statt Variantenexplosion
* Edge-Cases werden gezielt geprüft
* Negativfälle (Exceptions) werden explizit getestet
* Keine implizite Fallback-Logik

---

# 2. Teststrategie

Primär getestet werden:

* PersonDomainService
* PersonStateService
* PersonPrivacyService
* PersonMetadataDomainService
* RLS-Specifications

Controller-Tests sind nicht Bestandteil dieses Plans.

---

# 3. Testarchitektur

## 3.1 Integrationstests (Spring Context erforderlich)

Verwendung von:

* @SpringBootTest
* H2 InMemory Database
* aktiviertem Spring Security Context

Ziel:

* Prüfung von @PreAuthorize
* Prüfung der RLS-Logik
* Prüfung von Transaktionsverhalten
* Prüfung von JPA + Specification Kombination

## 3.2 Native Unit Tests

Verwendung von:

* JUnit 5
* Mockito (bei Bedarf)

Ziel:

* Spezifikationslogik isoliert testen
* Edge-Cases prüfen

---

# 4. Testfälle – PersonDomainService

## A. CREATE

### A1 – Erfolgreiches Erstellen

Bedingungen:

* User besitzt Permission CREATE
* valide Payload

Erwartung:

* Person wird gespeichert
* Status = ACTIVE
* ID gesetzt
* DTO korrekt gemappt

---

### A2 – CREATE ohne Permission

Erwartung:

* AccessDeniedException

---

### A3 – Edge: Gender null

Erwartung:

* Erstellung erfolgreich
* Kein Fehler

---

## B. READ (getPersonById)

### B1 – ACTIVE lesen mit READ

Erwartung:

* DTO wird zurückgegeben

---

### B2 – INACTIVE lesen ohne READ_INACTIVE

Erwartung:

* Optional.empty()

---

### B3 – INACTIVE lesen mit READ_INACTIVE

Erwartung:

* DTO wird zurückgegeben

---

### B4 – READ ohne Permission

Erwartung:

* AccessDeniedException

---

### B5 – Unbekannte ID

Erwartung:

* Optional.empty()

---

## C. LIST (Pagination + Filter + RLS)

### C1 – Nur ACTIVE sichtbar

Setup:

* 2 ACTIVE
* 1 INACTIVE

User:

* READ ohne READ_INACTIVE

Erwartung:

* Nur 2 Datensätze sichtbar

---

### C2 – READ_INACTIVE vorhanden

Erwartung:

* Alle Datensätze sichtbar

---

### C3 – Filterprüfung (Äquivalenzklassen)

* firstName Filter
* Gender Filter
* birthdayBetween Filter

Jeweils:

* Trefferfall
* Kein-Treffer-Fall

---

### C4 – LIST ohne READ

Erwartung:

* AccessDeniedException

---

## D. UPDATE (Attribute only)

### D1 – Erfolgreiches Update

Bedingungen:

* UPDATE Permission

Erwartung:

* Felder geändert
* Status unverändert

---

### D2 – INACTIVE ohne READ_INACTIVE

Erwartung:

* EntityNotFoundException (RLS greift)

---

### D3 – UPDATE ohne Permission

Erwartung:

* AccessDeniedException

---

### D4 – Update unbekannte ID

Erwartung:

* EntityNotFoundException

---

### D5 – Status kann nicht über Update verändert werden

Erwartung:

* Status bleibt unverändert

---

## E. DELETE (Hard Delete only)

### E1 – Hard Delete ohne Referenzen

Erwartung:

* Entity wird gelöscht

---

### E2 – DELETE mit Referenzen

Setup:

* Person mit PositionFilling

Erwartung:

* IllegalStateException
* Entity bleibt unverändert
* Keine Anonymisierung

---

### E3 – DELETE ohne Permission

Erwartung:

* AccessDeniedException

---

### E4 – DELETE INACTIVE ohne READ_INACTIVE

Erwartung:

* EntityNotFoundException (RLS greift)

---

# 5. Testfälle – PersonStateService

## S1 – Activate mit STATE_UPDATE

Erwartung:

* Status = ACTIVE

---

## S2 – Deactivate mit STATE_UPDATE

Erwartung:

* Status = INACTIVE

---

## S3 – STATE_UPDATE ohne Permission

Erwartung:

* AccessDeniedException

---

## S4 – Statuswechsel unbekannte ID

Erwartung:

* EntityNotFoundException

---

# 6. Testfälle – PersonPrivacyService

## P1 – Erfolgreiche Anonymisierung

Erwartung:

* Status = ANONYMIZED
* firstName ersetzt
* middleName ersetzt
* lastName ersetzt
* salutation ersetzt
* academicTitle ersetzt
* gender = null
* birthday = null

---

## P2 – ANONYMIZE ohne Permission

Erwartung:

* AccessDeniedException

---

## P3 – Anonymisierung unbekannte ID

Erwartung:

* EntityNotFoundException

---

## P4 – Idempotente Anonymisierung

Erwartung:

* Zweiter Aufruf führt nicht zu Fehler
* Status bleibt ANONYMIZED

---

# 7. Metadata-Service Tests

### F1 – METADATA_READ erlaubt

Erwartung:

* Metadata wird zurückgegeben

---

### F2 – METADATA_READ nicht erlaubt

Erwartung:

* AccessDeniedException

---

### F3 – METADATA_UPDATE erlaubt

Erwartung:

* Tag + KeyValue gespeichert

---

### F4 – METADATA_UPDATE nicht erlaubt

Erwartung:

* AccessDeniedException

---

# 8. Edge-Case-Prüfungen

* Null birthday
* Null gender
* Leere Pagination
* Null Filterparameter
* Replace Metadata überschreibt bestehende Werte

---

# 9. Security-by-Design Nachweis

Durch diese Tests wird nachgewiesen:

* Methoden sind atomar über Permissions geschützt
* Delete und Anonymisierung sind strikt getrennt
* Workflow ist vom Attribute-Update entkoppelt
* RLS greift bei READ, UPDATE und DELETE
* Nicht sichtbare Datensätze erzeugen 404-Semantik
* Kein Informationsleck über INACTIVE Datensätze

---

# 10. Erwarteter Testumfang

| Bereich  | Anzahl Tests |
| -------- | ------------ |
| Create   | 3            |
| Read     | 5            |
| List     | 4            |
| Update   | 5            |
| Delete   | 4            |
| State    | 4            |
| Privacy  | 4            |
| Metadata | 4            |

Erwartet: ca. 33 Tests

Dieser Umfang ist angemessen für ein sicherheitsrelevantes Aggregat mit getrennter Wo
