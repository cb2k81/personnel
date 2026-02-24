# PERS – Sprint 2.3 Review

## Kontext

Ziel dieses Sprint-Abschnitts war ursprünglich die Stabilisierung und Umstrukturierung der Testbasis im Bereich `person` (insbesondere Delete-, State- und Metadata-Tests).

Im Verlauf der Arbeiten wurden jedoch strukturelle Inkonsistenzen im technischen Basiskern (`system`-Package) sichtbar, die zu folgenden Problemen führten:

* Build-Fehlern
* DataIntegrityViolationExceptions
* Lombok-bedingten Equality-Problemen
* inkonsistentem Metadatenverhalten

Die Erweiterung des Scopes war daher fachlich und architektonisch notwendig, um eine stabile, deterministische und langfristig wartbare Codebasis sicherzustellen.

---

# Durchgeführte Maßnahmen

## 1. Bereinigung der Lombok-Nutzung in JPA-Entities

### Ausgangsproblem

* Verwendung von `@Data` in Entities
* Verwendung von `@EqualsAndHashCode(callSuper = true)`
* implizite equals/hashCode-Generierung unter Einbeziehung von Relationen
* Proxy-Unsicherheit unter Hibernate

### Maßnahmen

* Entfernen von `@Data` aus allen JPA-Entities
* Entfernen globaler `@EqualsAndHashCode`-Annotationen
* Einführung expliziter, proxy-sicherer equals/hashCode-Implementierungen
* Verbindliche Lombok-Policy über ADR-001 Version 3 festgeschrieben

### Ergebnis

* Keine Lombok-bedingten Equality-Probleme mehr
* Keine Proxy-/Lazy-Loading-Seiteneffekte
* Deterministisches Vergleichsverhalten

---

## 2. Konsolidierung der DomainEntity-Strategie

### Problem

* Equality-Verhalten war nicht normativ geregelt
* Subklassen konnten potenziell equals/hashCode überschreiben
* Technische Basisklassen waren architektonisch nicht vollständig fixiert

### Maßnahmen

* Equality-Strategie verbindlich über ADR-001 Version 3 definiert
* Klare Trennung zwischen technischer Identität und fachlicher Logik
* Technischer Shared Kernel explizit geschärft

### Ergebnis

* Stabiler Shared Kernel
* Klare Governance für zukünftige Entities
* Keine impliziten Gleichheitsregeln mehr

---

## 3. Metadaten-Modell (KeyValuePair) strukturell korrigiert

### Ursprüngliches Problem

`KeyValuePair` enthielt:

* `@ManyToOne domainEntity`
* zusätzlich redundantes Feld `entityId`
* NOT NULL-Constraint auf `entityId`

Dies führte zu:

* Inkonsistenzen
* Synchronisationsfehlern
* DataIntegrityViolationExceptions in Tests

### Entscheidung

Das redundante Feld `entityId` wurde vollständig entfernt.

Die Relation ist nun:

> Single Source of Truth

Der Unique-Constraint basiert ausschließlich auf:

```
(domain_entity_id, key_name)
```

### Ergebnis

* Keine Synchronisationslogik erforderlich
* Keine Redundanz
* Keine Persistenzfehler mehr
* Konsistentes relationales Modell

---

## 4. Tests neu strukturiert und stabilisiert

### Betroffene Bereiche

* `PersonDomainServiceDeleteTest`
* `PersonMetadataDomainServiceTest`
* `PersonStateServiceTest`
* `PersonDomainServiceReadTest`

### Maßnahmen

* Trennung von Setup-Rechten (CREATE) und fachlicher Testlogik (READ/DELETE)
* Bereinigung ungültiger Setter-Verwendungen nach Entity-Refactoring
* Anpassung der Testlogik an die neue Metadaten-Relation
* Sicherstellung deterministischer Persistenz

### Ergebnis

* `mvn clean install` läuft vollständig erfolgreich
* Keine Errors, keine Failures
* Keine DataIntegrityViolationExceptions mehr

---

# Architekturelle Erweiterung des Scopes

Obwohl initial nur Testanpassungen geplant waren, war die Erweiterung des Scopes korrekt und notwendig, weil:

1. Tests strukturelle Architekturfehler sichtbar gemacht haben
2. Lombok-Fehlkonfigurationen das Persistenzmodell destabilisiert haben
3. Redundante FK-Modelle zu inkonsistentem Verhalten führten
4. Equality-Strategie nicht normativ festgelegt war

Ein isoliertes "Test-Flicken" hätte:

* die Ursachen nicht beseitigt
* technische Schulden erhöht
* spätere Refactorings erschwert

Die gewählte Lösung beseitigt die Ursache, nicht das Symptom.

---

# Aktualisierung ADR-001 → Version 3

Folgende Punkte wurden verbindlich festgeschrieben:

* Proxy-sichere Equality-Strategie
* Verbot von `@Data` in JPA-Entities
* Klare Lombok-Richtlinien
* Verbot redundanter Persistenzattribute bei vorhandenen Relationen
* Metadaten als rein technischer Shared-Kernel-Bestandteil

Damit ist die Architektur jetzt:

* deterministisch
* konsistent
* langfristig wartbar

---

# Build-Status

```
Tests run: 31
Failures: 0
Errors: 0
Skipped: 0

BUILD SUCCESS
```

---

# Bewertung Sprint 2.3

## Fachlich

* Tests korrekt restrukturiert
* Rechte- und Lifecycle-Trennung sauber abgebildet

## Technisch

* Shared Kernel stabilisiert
* Lombok-Risiken eliminiert
* Metadaten-Modell konsolidiert
* Equality deterministisch geregelt

## Architektonisch

* ADR-001 auf Produktionsniveau gehoben
* Keine verdeckten Persistenzrisiken mehr

---

# Fazit

Sprint 2.3 hat nicht nur die Testbasis stabilisiert, sondern eine strukturelle Qualitätsverbesserung im technischen Kern des personnel Services erreicht.

Die Scope-Erweiterung war:

* fachlich gerechtfertigt
* architektonisch notwendig
* nachhaltig korrekt

Der Code- und Architekturzustand ist nach Abschluss dieses Sprint-Abschnitts signifikant robuster als zu Beginn.
