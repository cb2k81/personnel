# PERS_UC_001 – Use Cases Stellenplan

## Status

Draft

---

## Übersicht

Dieses Dokument beschreibt die fachlichen Use Cases der Applikation „Personnel – Stellenplan“ in kompakter, tabellarischer Form.

Die Use Cases sind nach Aggregaten strukturiert und berücksichtigen:

* Mutationsfälle (Command)
* Lesefälle (Query / DataGrid)
* Paging
* Sortierung
* Filterung
* Relevante Aggregate

---

# 1. Budget

| UC-ID      | Name                   | Typ     | Beschreibung                  | Aggregate | DataGrid (Filter/Sort/Page)                                             |
| ---------- | ---------------------- | ------- | ----------------------------- | --------- | ----------------------------------------------------------------------- |
| UC-BUD-001 | Budget anlegen         | Command | Neues Haushaltsjahr anlegen   | Budget    | –                                                                       |
| UC-BUD-002 | Budget ändern          | Command | Status oder Metadaten ändern  | Budget    | –                                                                       |
| UC-BUD-003 | BudgetRevision anlegen | Command | Nachtragshaushalt erzeugen    | Budget    | –                                                                       |
| UC-BUD-004 | Budget anzeigen        | Query   | Detailansicht eines Haushalts | Budget    | –                                                                       |
| UC-BUD-005 | Budget-Liste abrufen   | Query   | Liste aller Haushalte         | Budget    | Filter: fiscalYear, state / Sort: fiscalYear, approvedDate / Paging: ja |

---

# 2. PositionDefinition (Stellendefinition)

| UC-ID         | Name                       | Typ     | Beschreibung                                | Aggregate          | DataGrid                                                                       |
| ------------- | -------------------------- | ------- | ------------------------------------------- | ------------------ | ------------------------------------------------------------------------------ |
| UC-POSDEF-001 | Stellendefinition anlegen  | Command | Neue dauerhafte Stelle definieren           | PositionDefinition | –                                                                              |
| UC-POSDEF-002 | Stellendefinition ändern   | Command | Name, Laufbahngruppe oder Stellenart ändern | PositionDefinition | –                                                                              |
| UC-POSDEF-003 | Stellendefinition anzeigen | Query   | Detailansicht                               | PositionDefinition | –                                                                              |
| UC-POSDEF-004 | Stellendefinitionen suchen | Query   | Liste aller Definitionen                    | PositionDefinition | Filter: name, careerGroup, positionType / Sort: name, careerGroup / Paging: ja |

---

# 3. StaffingPlanSet (Planungsrahmen)

| UC-ID          | Name                    | Typ     | Beschreibung                                | Aggregate       | DataGrid                                               |
| -------------- | ----------------------- | ------- | ------------------------------------------- | --------------- | ------------------------------------------------------ |
| UC-PLANSET-001 | Planungsrahmen anlegen  | Command | Planungsrahmen für BudgetRevision erstellen | StaffingPlanSet | –                                                      |
| UC-PLANSET-002 | Planungsrahmen anzeigen | Query   | Detailansicht                               | StaffingPlanSet | –                                                      |
| UC-PLANSET-003 | Planungsrahmen-Liste    | Query   | Liste aller Planungsrahmen                  | StaffingPlanSet | Filter: budgetRevision, name / Sort: name / Paging: ja |

---

# 4. StaffingPlan (Planvariante)

| UC-ID       | Name                 | Typ     | Beschreibung                                    | Aggregate       | DataGrid                                                                                  |
| ----------- | -------------------- | ------- | ----------------------------------------------- | --------------- | ----------------------------------------------------------------------------------------- |
| UC-PLAN-001 | Planvariante anlegen | Command | Neue Planversion innerhalb eines Sets erstellen | StaffingPlanSet | –                                                                                         |
| UC-PLAN-002 | Plan kopieren        | Command | Plan als Simulation oder neue Version kopieren  | StaffingPlanSet | –                                                                                         |
| UC-PLAN-003 | Plan aktivieren      | Command | Plan als gültig deklarieren (von–bis)           | StaffingPlanSet | –                                                                                         |
| UC-PLAN-004 | Planliste anzeigen   | Query   | Liste aller Planvarianten                       | StaffingPlanSet | Filter: state, planType, validFrom, validTo / Sort: versionNumber, validFrom / Paging: ja |

---

# 5. PlannedPosition (Planstelle)

| UC-ID        | Name                       | Typ     | Beschreibung                            | Aggregate       | DataGrid                                                                                                                            |
| ------------ | -------------------------- | ------- | --------------------------------------- | --------------- | ----------------------------------------------------------------------------------------------------------------------------------- |
| UC-PLPOS-001 | Position in Plan aufnehmen | Command | Stellendefinition in Plan instanziieren | StaffingPlanSet | –                                                                                                                                   |
| UC-PLPOS-002 | Plananteil ändern          | Command | availablePortionPercent ändern          | StaffingPlanSet | –                                                                                                                                   |
| UC-PLPOS-003 | Planstelle anzeigen        | Query   | Detailansicht                           | StaffingPlanSet | –                                                                                                                                   |
| UC-PLPOS-004 | Planstellen-Liste          | Query   | Liste aller Planstellen eines Plans     | StaffingPlanSet | Filter: staffingPlan, organisationUnit, careerGroup, positionType, budgetAccount / Sort: organisationUnit, careerGroup / Paging: ja |

---

# 6. PositionFilling (Besetzung)

| UC-ID       | Name                  | Typ     | Beschreibung                                 | Aggregate       | DataGrid                                                                                                                                                                       |
| ----------- | --------------------- | ------- | -------------------------------------------- | --------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ |
| UC-FILL-001 | Stelle besetzen       | Command | Neue Besetzung anlegen                       | PositionFilling | –                                                                                                                                                                              |
| UC-FILL-002 | Vertragsanteil ändern | Command | contractualPortionPercent anpassen           | PositionFilling | –                                                                                                                                                                              |
| UC-FILL-003 | Aufstockung anlegen   | Command | Temporäre Erhöhung des Beschäftigungsumfangs | PositionFilling | –                                                                                                                                                                              |
| UC-FILL-004 | Besetzung beenden     | Command | filledTo setzen                              | PositionFilling | –                                                                                                                                                                              |
| UC-FILL-005 | Besetzungen-Liste     | Query   | Liste aller Besetzungen                      | PositionFilling | Filter: person, organisationUnit, careerGroup (Ist), employmentType (Ist), planId, dateRange, deviationFlag / Sort: filledFrom, person, contractualPortionPercent / Paging: ja |

---

# 7. Analyse-Use-Cases (Read-only)

| UC-ID     | Name                 | Typ   | Beschreibung                               | Aggregate                      | DataGrid                                                             |
| --------- | -------------------- | ----- | ------------------------------------------ | ------------------------------ | -------------------------------------------------------------------- |
| UC-AN-001 | Abweichungsanalyse   | Query | Soll- vs Ist-Laufbahngruppe und Stellenart | Staffing + Assignment          | Filter: deviationType / Sort: organisationUnit / Paging: ja          |
| UC-AN-002 | Unter-/Überbesetzung | Query | Plananteil vs Vertragsanteil               | Staffing + Assignment          | Filter: budgetAccount, planId / Sort: differencePercent / Paging: ja |
| UC-AN-003 | Budgetauslastung     | Query | Auslastung je Haushaltsstelle              | Budget + Staffing + Assignment | Filter: fiscalYear, revision / Sort: budgetAccount / Paging: ja      |

---

# 8. Nicht Bestandteil dieses Dokuments

* Technische API-Signaturen
* DTO-Definitionen
* Berechtigungsdefinitionen
* Validierungsregeln
* Workflow-Statusautomaten

Diese werden in separaten Dokumenten beschrieben.

---

## Fazit

Dieses Dokument definiert die fachlichen Use Cases als Grundlage für:

* Domain Services
* Entity Services
* Repository-Definitionen
* Berechtigungsmodell
* DataGrid-Implementierung

Es dient als verbindliche Referenz für die weitere Implementierung.
