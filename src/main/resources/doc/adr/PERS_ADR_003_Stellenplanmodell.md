# ADR PERS_ADR_003 – Stellenplanmodell

## Status

Accepted

## Kontext

Im Projekt "Personnel" wird ein revisionsfähiges, simulationsfähiges Stellenplanmodell für eine Behörde implementiert.

Fachliche Rahmenbedingungen:

* Haushalte sind jahresbezogen.
* Unterjährige Nachtragshaushalte sind möglich.
* Stellenpläne werden auf Basis eines genehmigten Haushalts erstellt.
* Es existieren parallele Planvarianten.
* Ein Plan kann als gültig (von–bis) deklariert werden.
* Stellen besitzen Kontinuität über mehrere Pläne hinweg.
* Stellenanteile existieren auf zwei Ebenen:

  * Planebene
  * arbeitsvertragliche Besetzungsebene
* Tarifstellen sind splittbar.
* Beamtenstellen sind nicht splittbar.
* Leerstellen sind eigenständige genehmigte Zustände.
* PositionFilling besitzt eigenständige fachliche Operationen.

Das Modell soll DDD-Prinzipien folgen.

---

## Entscheidung

Das Domänenmodell wird in drei klar getrennte Aggregate/Bereiche strukturiert:

1. Budget
2. Staffing (Stellenplanung)
3. Assignment (Besetzung)

Die Dependency-Richtung ist strikt hierarchisch.

---

## 1. Bounded Contexts / Packages

```
de.cocondo.app.domain.personnel
│
├── budget
│   ├── Budget
│   ├── BudgetRevision
│   └── BudgetAccount
│
├── staffing
│   ├── StaffingPlanSet
│   ├── StaffingPlan
│   ├── PositionDefinition
│   ├── PlannedPosition
│   ├── PositionShare
│   ├── Vacancy
│   └── PositionType
│
├── assignment
│   ├── PositionFilling
│   └── PositionFillingType
│
├── organisation
│   └── OrganisationUnit
│
└── person
    └── Person
```

---

## 2. Aggregates

### 2.1 Budget Aggregate

Aggregate Root: `Budget`

Enthält:

* BudgetRevision
* BudgetAccount

Verantwortung:

* Haushaltsjahr
* Nachtragshaushalte
* Haushaltsstellen
* Zweckbindung

Budget kennt keine Stellen und keine Besetzungen.

---

### 2.2 Staffing Aggregate

Aggregate Roots:

* `StaffingPlanSet`
* `PositionDefinition`

#### StaffingPlanSet

Verantwortung:

* Planungsrahmen zu einem BudgetRevision-Stand
* Enthält mehrere StaffingPlan-Varianten

#### StaffingPlan

* konkrete Planvariante
* versionierbar
* validFrom/validTo
* state
* planType (REAL / SIMULATION)

#### PositionDefinition

* dauerhafte Stellendefinition
* stabile Identität über mehrere Jahre/Pläne

#### PlannedPosition

* Auftreten einer PositionDefinition innerhalb eines StaffingPlans
* enthält planabhängige Attribute:

  * BudgetAccount
  * OrganisationUnit
  * availablePortionPercent

#### PositionShare

* splittbare Tarifanteile
* Sicherheitsabschlag bei Teilzeit

#### Vacancy

* genehmigte Leerstelle
* kein bloßes „nicht besetzt“

---

### 2.3 Assignment Aggregate

Aggregate Root: `PositionFilling`

Verantwortung:

* arbeitsvertraglich gebundener Anteil
* aktueller Beschäftigungsumfang
* Aufstockungen
* befristete Änderungen

PositionFilling besitzt eigene fachliche Operationen
und ist kein Bestandteil des Staffing-Aggregats.

---

## 3. Stellenkontinuität

Problem:
Positionen existieren über mehrere Pläne hinweg.

Lösung:
Trennung in:

* `PositionDefinition` (dauerhafte Identität)
* `PlannedPosition` (planabhängige Instanz)

Dadurch möglich:

* Vergleich von Planjahren
* Simulationen
* Extrapolation
* Nachtragshaushalt ohne Verlust der Identität

---

## 4. Dependency-Matrix

Erlaubte Abhängigkeiten:

| From \ To  | Budget | Staffing | Assignment |
| ---------- | ------ | -------- | ---------- |
| Budget     | ✔      | ❌        | ❌          |
| Staffing   | ✔      | ✔        | ❌          |
| Assignment | ❌      | ✔        | ✔          |

Bedeutung:

* Staffing darf Budget referenzieren.
* Assignment darf Staffing referenzieren.
* Budget darf niemals Staffing referenzieren.
* Staffing darf niemals Assignment referenzieren.
* Assignment darf niemals Budget referenzieren.

Keine bidirektionalen Beziehungen über Aggregatgrenzen.

---

## 5. Stellenanteil- und Abweichungsmodell

Stellenanteile existieren auf zwei Ebenen:

### Planebene

* PlannedPosition.availablePortionPercent
* PositionShare.sharePortionPercent
* PositionDefinition.careerGroup (Soll-Laufbahngruppe)
* PositionDefinition.positionType (Soll-Stellenart)

### Besetzungsebene

* PositionFilling.contractualPortionPercent
* PositionFilling.currentEmploymentPortionPercent
* PositionFilling.careerGroup (Ist-Laufbahngruppe)
* PositionFilling.employmentType (Ist-Stellenart)

Regeln (fachlich, nicht technisch erzwungen):

* Vertragsanteil ≤ Plananteil
* Beamtenstellen sind nicht splittbar
* Tarifanteile dürfen nur durch Tarifbeschäftigte genutzt werden
* Abweichungen zwischen Soll- und Ist-Zustand sind zulässig, müssen jedoch explizit gespeichert werden

Dadurch können folgende Szenarien ausgewertet werden:

* Unterwertige oder höherwertige Besetzung
* Tarifbeschäftigter auf Beamtenstelle
* Beamter auf Tarifstelle
* Abweichungen mit finanziellen Auswirkungen

Die Ist-Werte werden niemals implizit aus der Planebene abgeleitet, sondern eigenständig gespeichert, um historische Stabilität und revisionssichere Auswertungen zu gewährleisten.

---

## 6. Simulation

Simulation ist kein eigener Aggregate-Typ.

Simulation wird realisiert durch:

* StaffingPlan.planType = SIMULATION

Simulationen gehören zu einem StaffingPlanSet
und referenzieren denselben BudgetRevision-Stand.

---

## 7. Gründe für diese Architektur

* Klare Aggregatgrenzen
* Keine zyklischen Abhängigkeiten
* Revisionsfähigkeit
* Simulationsfähigkeit
* Historisierung möglich
* Transaktionale Konsistenz pro Aggregate
* Vorbereitung für Domain Services

---

## 8. Konsequenzen

* Domain Services arbeiten ausschließlich innerhalb eines Aggregats.
* Aggregatübergreifende Logik erfolgt koordiniert, nicht atomar.
* Keine Cascade-Operationen über Aggregatgrenzen.
* PositionFilling bleibt eigenständig.

---

## 9. Nicht Bestandteil dieser ADR

* Validierungslogik
* Kostenmodell
* Status-Workflow
* technische Persistenzoptimierungen
* XML-Templates

Diese werden in separaten ADRs dokumentiert.

---

## Fazit

Das Stellenplanmodell ist DDD-konform strukturiert,
unterstützt den vollständigen Planungsprozess
und erlaubt Historisierung sowie Simulation,
ohne Aggregatgrenzen zu verletzen.
