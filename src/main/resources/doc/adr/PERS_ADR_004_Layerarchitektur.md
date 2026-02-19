# ADR PERS_ADR_004 – Layerarchitektur und DDD-Umsetzung

## Status

Accepted

---

## Kontext

Im Projekt „Personnel – Stellenplan“ wird eine mehrschichtige Architektur implementiert, die folgende Anforderungen erfüllen muss:

* Klare Trennung zwischen fachlicher Logik und technischer Infrastruktur
* Saubere DDD-Aggregatgrenzen
* Unterstützung von Paging, Sorting und Filtering für DataGrid-Anwendungen
* Einheitliche DTO-Struktur
* Zentrale Fehlerbehandlung über GlobalExceptionHandler
* Transaktionssicherheit
* Erweiterbarkeit und Wartbarkeit

Dieses ADR dokumentiert die verbindliche Layerarchitektur.

---

# 1. Schichtenmodell

Die Applikation folgt einem vierstufigen Schichtenmodell:

```
Controller
   ↓
Domain Service (Use Case Layer)
   ↓
Entity Service
   ↓
Repository (Spring Data JPA)
```

---

# 2. Verantwortlichkeiten der Schichten

## 2.1 Controller

* REST-Endpunkte
* Annahme und Validierung von Request DTOs
* Übergabe an Domain Service
* Rückgabe von Response DTOs
* Keine Business-Logik
* Keine Repository-Zugriffe

Benennung: `*Controller`

---

## 2.2 Domain Service (Use Case Layer)

* Implementiert fachliche Use Cases
* Definiert Transaktionsgrenzen (@Transactional)
* Führt DTO ↔ Entity Mapping durch
* Ruft Entity Services auf
* Enthält Logging (@Slf4j)
* Wirft fachliche Exceptions
* Kommuniziert ausschließlich über DTOs nach außen

Wichtig:

* Domain Services kennen DTOs und Entities
* Domain Services kennen keine HTTP-Details
* Domain Services enthalten keine direkte Repository-Logik

---

## 2.3 Entity Service

* Arbeitet ausschließlich mit Entities
* Nutzt Repositories
* Keine DTOs
* Keine HTTP-Logik
* Keine Berechtigungslogik
* Keine Transaktionsdefinition (kommt vom Domain Service)

Zweck:

* Kapselung von Persistenzoperationen
* Vorbereitung für komplexere Invarianten

---

## 2.4 Repository

* Spring Data JPA Repository
* Zugriff ausschließlich über Aggregate Root
* Paging und Sorting über Pageable
* Keine Business-Logik

Beispiel:

```
public interface StaffingPlanSetRepository
    extends JpaRepository<StaffingPlanSet, String> {
}
```

---

# 3. Aggregate-Regeln

Repositories existieren ausschließlich für Aggregate Roots.

Beispiel:

* StaffingPlanSet → Repository vorhanden
* PlannedPosition → kein eigenes Repository
* PositionShare → kein eigenes Repository

Manipulation erfolgt immer über das Aggregate Root.

---

# 4. DTO-Strategie

Fachliche DTOs transportieren ausschließlich Use-Case-relevante Daten. Technische Infrastruktur-Daten werden getrennt behandelt.

## 4.1 Response DTOs

* Implementieren DataTransferObject
* Serialisierbar
* Enthalten keine Business-Logik

Beispiel:

* StaffingPlanSetDTO

---

## 4.2 Inbound DTOs

* Eigene DTOs pro Use Case
* Gemeinsame Nutzdaten in PayloadDTO

Namenskonvention:

* StaffingPlanSetPayloadDTO
* StaffingPlanSetCreateDTO
* StaffingPlanSetUpdateDTO

---

# 5. Mapping

* Verwendung von MapStruct
* ID-Generierung erfolgt nicht im Mapper
* ID-Generierung erfolgt über DomainEntityListener
* Mapper enthalten keine Business-Logik

---

# 6. Transaktionsmodell

* @Transactional auf Domain Service
* readOnly = true für Query-Use-Cases
* Entity Services sind transaktionsfrei

---

# 7. Logging

* Verwendung von @Slf4j (Lombok)
* Logging auf Domain-Service-Ebene
* Keine Logging-Logik in Repositories

---

# 8. Fehlerbehandlung

Exceptions werden durch GlobalExceptionHandler zentral verarbeitet.

| Exception                     | HTTP Status |
| ----------------------------- | ----------- |
| EntityNotFoundException       | 404         |
| ValidationException           | 400         |
| PermissionDeniedException     | 403         |
| AuthenticationException       | 401         |
| ConflictException (zukünftig) | 409         |

---

# 9. DataGrid-Unterstützung

* Pagination über Pageable
* Sortierung über Pageable
* Filterung wird später über Specification oder Query-Parameter erweitert

Controller bleiben schlank.
Domain Service koordiniert Paging.

---

# 10. Konsequenzen

Diese Architektur stellt sicher:

* Saubere DDD-Trennung
* Klare Verantwortlichkeiten
* Testbarkeit
* Erweiterbarkeit
* Einheitliche Fehlerbehandlung
* Vorbereitung für komplexe Invarianten

Dieses Muster dient als Referenz für alle weiteren Aggregate.

---

## Fazit

Die Layerarchitektur ist verbindlich definiert.
Alle weiteren Aggregate müssen diesem Muster folgen.

Dieses ADR ist Grundlage für die weitere Implementierung.
