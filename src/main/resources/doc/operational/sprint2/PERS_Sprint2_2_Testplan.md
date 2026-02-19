# PERS Sprint 2 – Testplan (PERS_Sprint2_2_Testplan)

## Status

Geplant – vor Implementierung der Unit- und Integrationstests

---

# 1. Ziel des Testplans

Ziel dieses Testplans ist es, für das Aggregat **Person** eine belastbare, sicherheitsorientierte und architekturkonforme Teststrategie zu definieren.

Der Fokus liegt auf:

* fachlicher Korrektheit
* Record-Level Security (ADR 010)
* Command Permissions (ADR 009)
* Anonymisierungslogik beim Delete
* stabiler Weiterentwicklung durch Service-Tests

Dabei gilt ausdrücklich:

* Keine Test-Inflation
* Äquivalenzklassen statt Variantenexplosion
* Edge-Cases werden gezielt geprüft
* Negativfälle (Exceptions) werden explizit getestet
* Security-by-Design wird durch Tests nachgewiesen

---

# 2. Teststrategie

Primär getestet wird:

* `PersonDomainService`
* `PersonMetadataDomainService`
* RLS-Specifications
* zentrale Sicherheitsregeln

Controller-Tests sind nicht Bestandteil dieses Plans.

---

# 3. Testarchitektur

## 3.1 Integrationstests (Spring Context erforderlich)

Verwendung von:

* `@SpringBootTest`
* H2 InMemory Database
* aktiviertem Spring Security Context

Ziel:

* Prüfung von `@PreAuthorize`
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

Bedingungen:

* User besitzt keine CREATE-Permission

Erwartung:

* AccessDeniedException

---

### A3 – Edge: Gender null

Bedingungen:

* Gender nicht gesetzt

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

Bedingungen:

* Person INACTIVE
* User hat READ, aber nicht READ_INACTIVE

Erwartung:

* Optional.empty()
* Controller würde 404 erzeugen

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

## D. UPDATE

### D1 – Erfolgreiches Update

Bedingungen:

* UPDATE Permission

Erwartung:

* Felder geändert

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

## E. DELETE

### E1 – Hard Delete (keine Referenzen)

Erwartung:

* Entity wird gelöscht

---

### E2 – Anonymisierung bei Referenz

Erwartung:

* firstName null
* lastName = "ANONYMIZED"
* gender null
* birthday null
* Entity weiterhin vorhanden

---

### E3 – DELETE ohne Permission

Erwartung:

* AccessDeniedException

---

### E4 – DELETE INACTIVE ohne READ_INACTIVE

Erwartung:

* EntityNotFoundException

---

# 5. Metadata-Service Tests

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

# 6. Edge-Case-Prüfungen

* Null birthday
* Null gender
* Leere Pagination
* Null Filterparameter
* Replace Metadata überschreibt bestehende Werte

---

# 7. Security-by-Design Nachweis

Durch diese Tests wird nachgewiesen:

* Methoden sind atomar über Permissions geschützt
* Keine Rollenabhängigkeit im Code
* RLS greift bei READ, UPDATE und DELETE
* Nicht sichtbare Datensätze erzeugen 404-Semantik
* Kein Informationsleck über INACTIVE Datensätze

---

# 8. Erwarteter Testumfang

| Bereich  | Anzahl Tests |
| -------- | ------------ |
| Create   | 3            |
| Read     | 5            |
| List     | 4            |
| Update   | 4            |
| Delete   | 4            |
| Metadata | 4            |

Erwartet: ca. 24–26 Tests

Dieser Umfang ist angemessen für ein sicherheitsrelevantes Aggregat ohne Test-Inflation.
