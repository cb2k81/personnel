# ADR PERS_ADR_007 – Error- und Paging-Standards

## Status

Proposed

---

## Kontext

Für eine konsistente API-Nutzung (Swagger, Frontend, DataGrid) müssen Fehler- und Paging-Verhalten über alle Aggregate hinweg standardisiert sein.

Ohne verbindliche Standards entstehen:

* Inkonsistente HTTP-Status-Codes
* Unterschiedliche Error-Response-Strukturen
* Uneinheitliche Paging-Formate
* Erhöhter Aufwand im Frontend

Dieses ADR definiert verbindliche Standards für:

* Fehlerbehandlung
* Error-Response-Struktur
* Paging
* Sortierung

---

# 1. Fehlerbehandlung

## 1.1 Zentrale Verarbeitung

Alle Exceptions werden durch einen globalen `GlobalExceptionHandler` verarbeitet.

Controller enthalten keine try/catch-Blöcke.

---

## 1.2 Standard-HTTP-Statuscodes

| Exception                 | HTTP Status |
| ------------------------- | ----------- |
| EntityNotFoundException   | 404         |
| IllegalArgumentException  | 400         |
| ValidationException       | 400         |
| AccessDeniedException     | 403         |
| AuthenticationException   | 401         |
| ConflictException         | 409         |
| Sonstige RuntimeException | 500         |

---

## 1.3 Standard-Error-Response

Alle Fehlerantworten folgen derselben Struktur:

```
{
  "status": 400,
  "errorType": "ValidationException",
  "message": "Validation failed",
  "timestamp": "2026-02-18T10:15:30Z",
  "errorId": "UUID"
}
```

### Felder

* `status` – HTTP-Statuscode
* `errorType` – Technischer Exception-Typ
* `message` – Fachlich lesbare Nachricht
* `timestamp` – ISO-8601
* `errorId` – Technische Trace-ID

Stacktraces werden nicht ausgeliefert.

---

# 2. Validation-Fehler

Bean Validation Fehler werden aggregiert.

Beispiel:

```
{
  "status": 400,
  "errorType": "ValidationException",
  "message": "Validation failed",
  "violations": [
    { "field": "name", "message": "must not be blank" }
  ]
}
```

---

# 3. Paging-Standard

## 3.1 Verwendung von Pageable

Query-Endpoints unterstützen:

* page
* size
* sort

Beispiel:

```
GET /plan-sets?page=0&size=20&sort=name,asc
```

---

## 3.2 Response-Format

Paging erfolgt über Spring Data `Page<T>`.

Beispielstruktur:

```
{
  "content": [...],
  "pageable": {...},
  "totalElements": 100,
  "totalPages": 5,
  "size": 20,
  "number": 0
}
```

Es wird kein proprietäres Paging-Format eingeführt.

---

# 4. Sortierung

Sortierung erfolgt ausschließlich über Pageable.

Keine separaten Sort-DTOs.

---

# 5. Konsequenzen

## Vorteile

* Einheitliches Fehlerverhalten
* Vorhersehbare API-Struktur
* Frontend-Kompatibilität
* Swagger-Konsistenz

## Nachteile

* Strikte Konventionen
* Begrenzte Flexibilität bei Sonderfällen

Diese Nachteile sind architektonisch akzeptabel.

---

# 6. Gültigkeit

Diese Standards gelten für:

* alle REST-Controller
* alle Aggregate
* alle zukünftigen Endpunkte

Error- und Paging-Verhalten sind verbindlich definiert.
