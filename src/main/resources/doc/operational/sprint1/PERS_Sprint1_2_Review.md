# Personnel Service – Sprint 1 Review

Stand: 2026-02-19
Sprint: 1 – Datenmodell (Stellenplan + Stellenbesetzung)

---

## 1. Sprint-Ziel

Ziel von Sprint 1 war:

* Eine lauffähige Spring-Boot-Anwendung
* Erfolgreicher Application-Start ohne Fehler
* Hibernate/JPA projiziert das vollständige Sprint-1-Datenmodell
* Konsistenz zwischen UML, Planungsdokument und JPA-Entities

Der Fokus lag ausschließlich auf dem Datenmodell (Domain Layer), nicht auf Services, Controllern oder vollständiger Geschäftslogik.

---

## 2. Umgesetztes Zielmodell

### 2.1 Organisation

* `OrganisationUnit` (Aggregate Root)
* `OrganisationUnitVersion` (Root+Version Pattern)
* Zeitliche Historisierung über `validFrom` / `validTo`
* Keine Überlappungslogik technisch erzwungen (fachliche Invariante)

### 2.2 Position Master Data (Stellenstamm)

* `PositionPost` (Aggregate Root)
* `PositionPostVersion`
* Fachlicher Schlüssel: `postBusinessKey`
* `postType` (CIVIL_SERVICE_POST / EMPLOYEE_POST)
* Optional vorbereitete Attribute: `kwFlag`, `kwEffectiveDate`
* Budgetreferenz als MVP-Feld (`budgetReference`)

### 2.3 StaffingPlan (Soll)

Aggregate-Struktur:

* `StaffingPlanSet` (Container für Varianten)
* `StaffingPlan` (Snapshot-Plan)
* `PlannedPost`
* `PlannedShare`

Wesentliche Eigenschaften:

* `planVariantType` (APPROVED / SCENARIO)
* `workflowStatus` (DRAFT / IN_REVIEW / APPROVED / ARCHIVED)
* `versionNumber`
* `validFrom` / `validTo`
* `safetyDeductionFactor` (planweit)
* Planbezogene Organisationszuordnung über Root-Link (`PlannedPost → OrganisationUnit`)

### 2.4 StaffingAssignmentPlan (Ist)

Aggregate-Struktur:

* `StaffingAssignmentPlan`
* `PositionFilling`

Besonderheiten:

* Referenz auf `StaffingPlan`
* `workflowStatus`
* `filledFrom` / `filledTo`
* `contractualPortionPercent`
* Optional `currentEmploymentPercent`
* Referenz auf `PlannedPost`
* Optional Referenz auf `PlannedShare`
* Direkte Referenz auf `Person` (Employee-Entscheidung Backlog)

### 2.5 Tarif vs. Beamte

* Tarifstellen sind über `PlannedShare` teilbar
* Beamtenstellen sind nicht teilbar
* Modellierung erfolgt über optionales `plannedShare` in `PositionFilling`

---

## 3. Abgleich mit UML

Die JPA-Entities entsprechen der Struktur aus `PERS_DOMAIN_ENTITIES.puml`:

* Soll/Ist-Trennung korrekt umgesetzt
* Root+Version Pattern korrekt umgesetzt
* Planvarianten korrekt modelliert
* Enumerationen konsistent

Keine Relation widerspricht dem Zielmodell.

---

## 4. Technischer Status

* Anwendung startet erfolgreich
* Spring Context wird vollständig initialisiert
* Hibernate erzeugt Schema ohne Mapping-Fehler
* Keine zyklischen Cascade-Probleme
* Keine fehlerhaften `@OrderBy`-Annotationen

Mapper wurden temporär deaktiviert, um den Fokus vollständig auf das Domain-Modell zu legen.

---

## 5. Leitplanken / Invarianten (noch nicht technisch erzwungen)

Folgende fachliche Regeln sind modelliert, aber noch nicht technisch abgesichert:

* Keine überlappenden Versionen innerhalb eines Root-Aggregats
* Maximal ein APPROVED-Plan mit überlappender Gültigkeit pro `StaffingPlanSet`
* APPROVED-Pläne sind strukturelle Snapshots
* Keine physische Löschung zentraler Identitäten

Diese Regeln werden in späteren Sprints durch Services, Constraints oder Validierungen abgesichert.

---

## 6. Offene Punkte (Backlog für Sprint 2)

* Reaktivierung und Anpassung der MapStruct-Mapper
* Einführung von DTO-Schichten
* Repository-Validierungen für Invarianten
* Entscheidung und Migration: `Person` → `Employee`
* Optional: Entfernung des shareweiten `safetyDeductionFactor`, falls ausschließlich planweit geführt werden soll

---

## 7. Definition of Done – Bewertung

| Kriterium                          | Status  |
| ---------------------------------- | ------- |
| App startet ohne Fehler            | Erfüllt |
| Hibernate generiert Schema         | Erfüllt |
| UML vollständig umgesetzt          | Erfüllt |
| Soll/Ist sauber getrennt           | Erfüllt |
| Historisierung korrekt modelliert  | Erfüllt |
| Keine widersprüchlichen Relationen | Erfüllt |

---

## 8. Fazit

Sprint 1 hat das fachliche Fundament des Personnel-Service erfolgreich etabliert.

Das Datenmodell ist:

* strukturell konsistent
* UML-konform
* historisierungsfähig
* erweiterbar für zukünftige Anforderungen

Damit ist die Grundlage für Services, Queries, DataGrids und Validierungslogik in Sprint 2 gelegt.
