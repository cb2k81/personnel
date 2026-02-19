# PERS_ADR_005_Metadaten

## Status

Proposed

---

## Kontext

Alle Aggregate Roots im Projekt erben von `DomainEntity`. Diese Basisklasse enthält technische Metadaten:

* `id`
* Audit-Attribute (`createdAt`, `createdBy`, `lastModifiedAt`, `lastModifiedBy`)
* `tags`
* `keyValuePairs`

Während Audit-Attribute rein technisch und automatisch gepflegt werden, dienen Tags und Key-Value-Paare der technischen Markierung bzw. Anreicherung von Entitäten.

Bisher wurden Metadaten direkt über fachliche Create-/Update-DTOs transportiert. Dies führt jedoch zu einer Vermischung von:

* fachlicher Verantwortung
* technischer Infrastruktur
* Berechtigungslogik
* API-Semantik

Langfristig entsteht dadurch eine unklare Architektur.

---

## Problemstellung

Metadaten sind keine fachlichen Attribute eines Aggregates.

Wenn Tags und Key-Value-Paare über fachliche POST/PUT/PATCH-Endpunkte manipulierbar sind, entstehen folgende Probleme:

1. Verletzung der fachlichen Konsistenz
2. Fehlende Trennung von Verantwortlichkeiten
3. Erschwerte Rechteverwaltung
4. Unklare API-Semantik
5. Unbeabsichtigte Manipulation technischer Attribute

Metadaten sollen technisch verwaltbar sein, aber fachliche Operationen dürfen nicht automatisch Metadaten verändern.

---

## Entscheidung

Metadaten werden vollständig von fachlichen Operationen getrennt.

### 1. Fachliche Controller

Fachliche Endpunkte (Create, Update, Delete, Read) operieren ausschließlich auf fachlichen DTOs.

Beispiel:

```
POST   /api/personnel/staffing/plan-sets
PUT    /api/personnel/staffing/plan-sets/{id}
GET    /api/personnel/staffing/plan-sets/{id}
```

Diese DTOs enthalten:

* fachliche Attribute
* `id`

Sie enthalten **keine Tags und keine Key-Value-Paare**.

---

### 2. Dedizierte Metadata-Controller

Für jedes Aggregate Root wird ein separater Metadata-Controller bereitgestellt.

Beispiel:

```
GET    /api/personnel/staffing/plan-sets/{id}/metadata
PUT    /api/personnel/staffing/plan-sets/{id}/metadata
PATCH  /api/personnel/staffing/plan-sets/{id}/metadata
```

Diese Controller:

* sind rein technisch
* operieren ausschließlich auf `DomainEntityMetadataDTO`
* können separate Berechtigungen erhalten
* enthalten keine fachliche Logik

---

### 3. Generische Implementierung im system-Package

Das system-Package stellt eine generische Basiskomponente bereit:

* `AbstractMetadataController`
* `MetadataService`
* `MetadataMapper`

Diese Implementierungen sind vollständig domain-unabhängig.

Jedes Aggregate Root kann davon ableiten.

Beispiel:

```
public class StaffingPlanSetMetadataController
        extends AbstractMetadataController<StaffingPlanSet>
```

Damit entsteht:

* Einheitliches Verhalten
* Kein Copy-Paste-Code
* Konsistente API-Struktur
* Zentrale Wartbarkeit

---

### 4. Lesen mit und ohne Metadaten

Standard-GET-Endpunkte liefern keine Metadaten.

Falls Metadaten benötigt werden, erfolgt der Zugriff ausschließlich über:

```
GET /{aggregate}/{id}/metadata
```

Optional kann später ein kombinierter Response-Typ ergänzt werden, ohne das Grundprinzip zu verletzen.

---

## Konsequenzen

### Vorteile

* Saubere Trennung von Fachlichkeit und Technik
* Klare Berechtigungsstruktur
* Einheitliches API-Muster
* Wiederverwendbare system-Komponenten
* Bessere Wartbarkeit
* Keine impliziten Seiteneffekte bei fachlichen Operationen

### Nachteile

* Zusätzliche Endpunkte
* Leicht erhöhter Implementierungsaufwand

Der Mehraufwand ist architektonisch gerechtfertigt.

---

## Architekturprinzip

Metadaten sind Infrastruktur – keine Fachdomäne.

Fachliche Operationen dürfen keine technische Markierung manipulieren.

Die technische Anreicherung eines Aggregates erfolgt ausschließlich über dedizierte Infrastruktur-Endpunkte.

---

## Gültigkeit

Diese Entscheidung gilt für:

* alle bestehenden Aggregate Roots
* alle zukünftigen Aggregate Roots
* alle Apps innerhalb der Plattform

Metadaten-Handling ist damit einheitlich standardisiert.
