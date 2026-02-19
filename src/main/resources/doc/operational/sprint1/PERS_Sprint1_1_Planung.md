# Personnel Service – Sprint 1 Planungsdokument (Stellenplan + Stellenbesetzung)

Stand: 2026-02-18 (Baseline: personnel_code-export_2026-02-18_15-01-39)

## 1. Zielsetzung des Sprints

Ziel von Sprint 1 ist eine lauffähige Spring-Boot-Anwendung, die ohne Fehler startet und das Sprint-1-Datenmodell via Hibernate/JPA in die Datenbank projiziert.

Sprint-1-Fokus:
1. Stellenplan (Soll) mit Historisierung und Planungsvarianten (APPROVED/SCENARIO)
2. Stellenbesetzungsplan (Ist) als operativer Besetzungsstand auf Basis eines Soll-Plans
3. Budget-Informationen im MVP nur als oberflächliche Referenz (Mittelherkunftsbezug), kein Haushaltsplanungsprozess

## 2. Scope (In Scope)

### 2.1 Datenmodell (JPA Entities)

Gemäß Domain-Dokumentation und UML (PERS_DOMAIN_ENTITIES.puml) werden in Sprint 1 die folgenden Aggregate / Entitäten umgesetzt:

**Organisation**
- OrganisationUnit (Aggregate Root) mit fachlichem Schlüssel `orgUnitBusinessKey`
- OrganisationUnitVersion (Version-Entity) mit Gültigkeitszeitraum

**Position Master Data (Stellenstamm)**
- PositionPost (Aggregate Root) mit fachlichem Schlüssel `postBusinessKey`, `postType`, optional `kwFlag`, `kwEffectiveDate`
- PositionPostVersion (Version-Entity) inkl. `budgetReference` sowie Organisationszuordnung (Referenz auf OrganisationUnit)

**StaffingPlan (Soll)**
- StaffingPlanSet (Aggregate Root) als Container für Planvarianten
- StaffingPlan (Aggregate Root) als Snapshot-Planvariante:
  - `planVariantType` (APPROVED/SCENARIO)
  - `workflowStatus` (DRAFT/IN_REVIEW/APPROVED/ARCHIVED)
  - `validFrom`, `validTo`, `versionNumber`
  - `safetyDeductionFactor` (planweit)
- PlannedPost (Teil des StaffingPlan): Plan-Vorkommen einer PositionPost inkl. planbezogener Organisationszuordnung
- PlannedShare (Teil des PlannedPost): Splittbare Tarif-Anteile inkl. Zeitraum und Kennzeichen permanent

**StaffingAssignmentPlan (Ist)**
- StaffingAssignmentPlan (Aggregate Root) referenziert StaffingPlan und trägt `workflowStatus`, `validFrom`, `validTo`
- PositionFilling (Teil des StaffingAssignmentPlan) referenziert:
  - PlannedPost und optional PlannedShare
  - Person (vorläufig direkt; Employee-Entscheidung ist Backlog)
  - `filledFrom`, `filledTo`, `contractualPortionPercent`, optional `currentEmploymentPercent`

### 2.2 Enums
- PostType: CIVIL_SERVICE_POST / EMPLOYEE_POST
- PlanVariantType: APPROVED / SCENARIO
- WorkflowStatus: DRAFT / IN_REVIEW / APPROVED / ARCHIVED

## 3. Out of Scope (Explizite Nicht-Ziele in Sprint 1)

- Vollständige Haushaltsplanung als Prozess (Kapitel/Titel, Budget-Workflows, u. ä.)
- Vollständiges Personaldatenmodell inkl. Employment/Employee (Entscheidung vorbereitet, aber nicht umgesetzt)
- Zeitwirtschaft
- Persistente Speicherung von Kapazitäts-/Budgetprojektionen (Berechnung später gemäß ADR-013)

## 4. Leitplanken / Invarianten (modellrelevant)

- Keine physische Löschung zentraler Identitäten im Fachbetrieb (Stammdaten)
- Root+Version Pattern: Versionen dürfen sich zeitlich nicht überlappen
- Planvarianten: Pro StaffingPlanSet darf maximal ein APPROVED-Plan mit überlappender Gültigkeit existieren
- APPROVED-Pläne sind strukturelle Snapshots; Strukturänderungen erfordern neue Planvariante
- Tarifstellen sind teilbar über Shares; Beamtenstellen sind nicht teilbar
- Sicherheitsabschlag wird planweit über `safetyDeductionFactor` geführt

## 5. Offene Entscheidung (Backlog / Risiko)

**Person vs. Employee**
- Sprint 1 nutzt `Person` direkt in `PositionFilling` (gemäß UML).
- Einführung von `Employee` (Trennung Mitarbeiterdaten vs. natürliche Person) wird als Folge-Change vorgesehen und muss Migrations- und Referenzierungsstrategie definieren.

## 6. Definition of Done (Sprint 1)

- Anwendung startet ohne Fehler
- Hibernate erzeugt das Sprint-1-Schema ohne Mapping-Fehler (Entities/Relations/Enums)
- Alle Sprint-1-Entities sind im Projekt vorhanden, korrekt annotiert und konsistent zu den Dokumentationsvorgaben
- Repositories existieren ausschließlich für Aggregate Roots (gemäß Doku)
- Keine Entität/Relation im Sprint-1-Paket widerspricht dem Zielmodell (Soll/Ist-Trennung, Historisierung, Planvarianten)

