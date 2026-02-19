# ADR-003 – Stellenplan-Domänenmodell (Version 5)

## Status

Accepted

---

## Kontext

Der personnel Service implementiert im MVP das Modul **Stellenplan** mit klarer und expliziter Trennung zwischen:

* **Stellenplan (Soll)** – strukturelle Planung, Kapazität, Anteile
* **Stellenbesetzungsplan (Ist)** – operative Besetzung durch Personen

Das Modell muss folgende fachliche Anforderungen vollständig abbilden:

* Planvarianten (Szenarien) neben einem verabschiedeten Referenzplan
* Vollständige Historisierung (keine Löschung zentraler Identitäten im Fachbetrieb)
* Unterscheidung zwischen Beamtenstellen (nicht teilbar) und Tarifstellen (teilbar, n:n-Beziehung)
* Sammeln freier Tarifanteile zur Bildung neuer Optionen
* Sicherheitsabschlag bei Nutzung freier Tarifanteile (planweit)
* Befristete und unbefristete Besetzungen
* Abbildung von Leerstellen
* Umbuchungen innerhalb des Stellenplans
* Transparente Soll-/Ist-Differenz (Kapazität vs. Nutzung)
* 3‑Jahres‑Betrachtung (monatliche Projektion gemäß ADR-013)
* Organisationsstruktur mit zeitlicher Dimension

Haushaltsplanung als Prozess ist **nicht** Bestandteil des MVP. Budget wird ausschließlich als **Mittelherkunftsreferenz** geführt.

Dieses ADR definiert verbindlich:

* Aggregate und Entitäten
* Aggregatgrenzen
* Invarianten
* Soll-/Ist-Trennung
* Fachlich relevante Sonderfälle (Leerstelle, Umbuchung, KW)

Referenzierte ADRs:

* ADR-006 Identität & Audit
* ADR-011 Plan Lifecycle & Mutability
* ADR-012 Historisierung
* ADR-013 Budget- und Kapazitätsprojektion

---

# 1. Fachliche Kernbereiche (MVP)

1. **Organisation** (Struktur, historisiert)
2. **Position Master Data** (Stellenstamm, historisiert)
3. **StaffingPlan (Soll)** – strukturelle Planung
4. **StaffingAssignmentPlan (Ist)** – operative Besetzung

---

# 2. Aggregate und Entitäten

## 2.1 Organisation

### Aggregate Root: `OrganisationUnit`

Repräsentiert eine organisatorische Einheit mit stabiler fachlicher Identität.

Attribute (konzeptionell):

* technische ID: `id` (UUID)
* fachliche ID: `orgUnitBusinessKey` (z. B. Organisationscode) – verpflichtend, eindeutig und dauerhaft stabil

**Invarianten:**

* `orgUnitBusinessKey` ist fachlich eindeutig (Unique Constraint)
* `orgUnitBusinessKey` darf nach initialer Vergabe nicht geändert werden
* Keine physische Löschung im Fachbetrieb

### Version Entity: `OrganisationUnitVersion`

Historisierte Eigenschaften der Organisationseinheit.

* `validFrom`, `validTo`
* Hierarchie ist versionsbezogen (z. B. parentOrganisationUnit als versionsbezogene Referenz)
* Keine zeitliche Überlappung von Versionen

Stammdaten werden im Fachbetrieb nicht physisch gelöscht.

* `validFrom`, `validTo`
* Hierarchie ist versionsbezogen
* Keine zeitliche Überlappung von Versionen

Stammdaten werden im Fachbetrieb nicht physisch gelöscht.

---

## 2.2 Position Master Data (Stellenstamm / Stellendefinition)

### Aggregate Root: `PositionPost`

Repräsentiert die **Stellendefinition** als dauerhaftes, planübergreifendes Referenzobjekt.

Wichtig: Eine Stelle existiert in vielen Plänen (Soll-Snapshots) mehrfach als *PlannedPost*, muss aber über alle Pläne hinweg eindeutig als **dieselbe Stelle** erkennbar bleiben.

Daher besitzt `PositionPost` neben der technischen UUID (ADR-006) einen **fachlichen Schlüssel** (Business Key), der die „Kette“ der Stelle über den Lebenszyklus erhält.

Attribute (konzeptionell):

* technische ID: `id` (UUID) – rein technisch, nicht fachlich
* fachliche ID: `postBusinessKey` (z. B. Stellen-ID aus Stellenstamm / Planstellen-ID) – **verpflichtend für jede Stelle** und dauerhaft stabil über den gesamten Lebenszyklus
* `postType`: `CIVIL_SERVICE_POST` | `EMPLOYEE_POST`
* optional `kwFlag`
* optional `kwEffectiveDate`
* optional externer Integrationsschlüssel (falls abweichend vom Business Key)

**Invarianten:**

* Keine physische Löschung im Fachbetrieb
* `postBusinessKey` ist fachlich eindeutig (Unique Constraint gemäß Fachregeln) und darf nach initialer Vergabe nicht geändert werden, da er die Identitätskette der Stelle über alle Pläne hinweg sichert
* `kwFlag` beendet nicht die Identität, sondern markiert perspektivischen Wegfall

### Version Entity: `PositionPostVersion`

Historisierte Eigenschaften der Stellendefinition, u. a.:

* Budget-/Mittelherkunftsreferenz
* Laufbahngruppe / Wertigkeit
* administrative Klassifizierungen
* Fachbezug
* **Zuordnung zur Organisationseinheit** (organisatorischer „Owner“ der Stelle)
* `validFrom`, `validTo`

**Invarianten:**

* Versionen dürfen sich zeitlich nicht überlappen
* Organisationszuordnungen ändern sich über neue Versionen (keine In-Place-Änderung historischer Zustände)

---

# 3. StaffingPlan (Soll)

## 3.1 Aggregate Root: `StaffingPlanSet`

Container für Planvarianten.

## 3.2 Aggregate Root: `StaffingPlan`

Repräsentiert eine strukturelle Planvariante (Soll-Snapshot).

Attribute:

* `planVariantType`: `APPROVED` | `SCENARIO`
* `workflowStatus`: `DRAFT` | `IN_REVIEW` (optional) | `APPROVED` | `ARCHIVED`
* `validFrom`, `validTo`
* `versionNumber`
* `safetyDeductionFactor` (planweit)

Regeln:

* SCENARIO darf denselben Zeitraum wie APPROVED haben.
* Pro StaffingPlanSet darf es maximal einen APPROVED-Plan mit überlappender Gültigkeit geben.
* Wirksamkeit = `workflowStatus == APPROVED` + Datum im Gültigkeitszeitraum.
* Ein StaffingPlan muss Zeiträume unterstützen, die eine mindestens dreijährige Betrachtung ermöglichen.

**Kontinuitätsanforderung (approved Pläne):**

Wenn eine fachliche Änderung einen neuen APPROVED-Plan erforderlich macht (z. B. Nachtragshaushalt, strukturelle Umbuchung), dann gilt:

* Der bisher wirksame APPROVED-Plan endet fachlich früher (`validTo` wird auf den Tag vor dem neuen Start gesetzt).
* Der neue APPROVED-Plan startet mit `validFrom` zum fachlich wirksamen Änderungszeitpunkt.
* Historie bleibt erhalten, da beide Pläne als Snapshots bestehen.

Damit wird die zeitliche Kontinuität der Planhistorie abgebildet.

**Organisationsreorganisation (verpflichtende Plananpassung):**

Wenn sich durch eine organisatorische Reorganisation die strukturelle Zuordnung einer Stelle ändert (z. B. neue oder veränderte Organisationseinheiten mit Wirkung auf die Planverantwortung), ist zwingend ein neuer APPROVED StaffingPlan zu erzeugen.

Dabei gilt:

* Der bisher gültige APPROVED-Plan endet am Tag vor Wirksamkeit der Reorganisation.
* Der neue APPROVED-Plan beginnt am Wirksamkeitstag der Reorganisation.
* Eine strukturelle Anpassung eines bereits APPROVED Plans ist unzulässig.

Damit wird sichergestellt, dass Organisationsreorganisationen stets über Snapshot-Wechsel abgebildet werden und keine impliziten Strukturänderungen innerhalb eines wirksamen Plans stattfinden.

Mutability gemäß ADR-011:

* DRAFT: vollständig mutierbar inkl. Löschen
* APPROVED: strukturell gesperrt

---

## 3.3 Planstruktur-Entitäten

### `PlannedPost`

Planbezogene Instanz einer `PositionPost` innerhalb eines konkreten `StaffingPlan` (Soll-Snapshot).

* Referenziert `PositionPost`
* Besitzt keine eigene fachliche Identität über Pläne hinweg; die Kette wird über `PositionPost.postBusinessKey` gehalten.

**Organisatorische Zuordnung im Plan:**

Für die Planung wird eine Organisationseinheit als *Plan-Zuordnung* geführt (z. B. Zuständigkeit/Verantwortung im Plan). Diese kann von der historisierten Organisationszuordnung der Stellendefinition (`PositionPostVersion`) abweichen.

* `plannedOrganisationUnit` (Referenz auf `OrganisationUnit`)

Damit sind zwei fachlich getrennte Sichtweisen möglich:

* **Stellendefinition (Stellenstamm):** historisierte Organisationszuordnung über `PositionPostVersion` (strukturelle/organisatorische Zugehörigkeit der Stelle)
* **Stellenplan (Soll):** geplante Zuordnung/Verantwortung im jeweiligen Plan über `PlannedPost.plannedOrganisationUnit`

**Führungsregel:**

* Für strukturelle und historische Auswertungen der Stelle ist die Organisationszuordnung aus `PositionPostVersion` maßgeblich.
* Für planbezogene Auswertungen (z. B. Kapazitätsplanung im konkreten StaffingPlan) ist `PlannedPost.plannedOrganisationUnit` maßgeblich.
* Ein Widerspruch zwischen beiden Sichten ist fachlich zulässig und bildet geplante organisatorische Veränderungen ab.

Eine Leerstelle ist fachlich ein `PlannedPost` ohne aktives `PositionFilling`.

Umbuchungen (z. B. Organisationswechsel) erfolgen:

* im DRAFT durch strukturelle Änderung
* nach APPROVED durch neue Planvariante

### `PlannedShare`

Repräsentiert einen nutzbaren Anteil einer Tarifstelle.

* Nur für `EMPLOYEE_POST`
* Attribute: `percent`, `validFrom`, `validTo`, optional `isPermanent`
* Mehrere Shares pro PlannedPost möglich
* Shares können befristet oder unbefristet sein

Invarianten:

* Beamtenstellen erzeugen keine Shares
* Summe der Shares darf strukturelle Kapazität nicht überschreiten
* Freie Shares können rechnerisch aggregiert werden (Sammeln freier Anteile)

---

# 4. StaffingAssignmentPlan (Ist)

## 4.1 Aggregate Root: `StaffingAssignmentPlan`

Repräsentiert den operativen Besetzungsstand auf Basis eines `StaffingPlan`.

Attribute:

* Referenz auf `StaffingPlan`
* `workflowStatus`: identisch zu ADR-011 (`DRAFT` | `IN_REVIEW` | `APPROVED` | `ARCHIVED`)
* `validFrom`, `validTo`

Es wird projektweit **nur eine einheitliche Workflow-Logik** verwendet.

Invarianten:

* Im operativen Betrieb existiert genau ein aktiver AssignmentPlan pro APPROVED StaffingPlan.
* Optional kann für SCENARIO-Pläne ein eigener AssignmentPlan zu Simulationszwecken existieren.

---

## 4.2 Entity: `PositionFilling`

Teil des StaffingAssignmentPlan (kein eigenes Aggregate Root).

Attribute:

* Referenz auf `PlannedShare` (bei Tarif)
* Referenz auf `PlannedPost` (bei Beamten)
* Person-Referenz
* `filledFrom`, `filledTo`
* `contractualPortionPercent`
* optional `currentEmploymentPercent`

Invarianten:

* Tarifanteile dürfen nur von Tarifbeschäftigten genutzt werden
* Beamtenstellen sind nicht teilbar
* Vertragsbindung darf verfügbare Kapazität nicht überschreiten
* Mehrere Fillings pro Share möglich (n:n-Beziehung)
* Befristete Aufstockungen werden über zeitlich begrenzte Fillings modelliert

Append-Only-Regel gemäß ADR-012:

* Ab `workflowStatus == APPROVED` erfolgen operative Änderungen append-only

Umbuchungen im Ist (z. B. Wechsel der Organisationseinheit) erfolgen als Beendigung + Neuanlage eines Filling.

---

# 5. Historisierung und Revisionssicherheit

Gemäß ADR-012:

* Stammdaten: Root + Version Pattern
* StaffingPlan: Snapshot-Historisierung über Planvarianten
* StaffingAssignmentPlan: operative Historisierung (append-only)

Ein minimaler Ledger für revisionssichere Ereignisdokumentation ist Bestandteil der Zielarchitektur.

---

# 6. Soll-/Ist-Trennung

* Soll = StaffingPlan (Kapazitäten, Shares, Sicherheitsabschlag)
* Ist = StaffingAssignmentPlan (PositionFilling)
* Differenz (Rest/Überbuchung) wird berechnet (ADR-013)

Finanzielle Reste werden nicht persistent gespeichert, sondern rechnerisch aus Soll/Ist ermittelt.

---

# 7. Aggregate Roots (MVP)

* `OrganisationUnit`
* `PositionPost`
* `StaffingPlanSet`
* `StaffingPlan`
* `StaffingAssignmentPlan`

`PositionFilling` ist keine Aggregatwurzel.

Repositories existieren ausschließlich für Aggregate Roots.

---

# 8. Modellregeln und zusätzliche Invarianten

Dieser Abschnitt expliziert projektweit verbindliche Modellregeln, die sich aus den vorherigen Kapiteln ergeben.

## 8.1 Organisationskonsistenz

* Eine `OrganisationUnitVersion` darf nur beendet werden, wenn alle referenzierenden `PositionPostVersion` zeitlich konsistent angepasst wurden.
* Historische Referenzen auf beendete Organisationseinheiten bleiben zulässig und auswertbar.

## 8.2 KW-Regel

* Ab `kwEffectiveDate` darf eine Stelle in neu erzeugten APPROVED-Plänen nicht mehr als aktive Kapazität geplant werden.
* Die Identität der Stelle bleibt erhalten; sie wird nicht gelöscht.

## 8.3 Planvarianten im DRAFT

* Pro `StaffingPlanSet` dürfen mehrere DRAFT-Pläne existieren.
* Es darf jedoch maximal ein APPROVED-Plan mit überlappender Gültigkeit existieren.

## 8.4 Strukturelle Kapazität von Tarifstellen

* Für `EMPLOYEE_POST` beträgt die strukturelle Kapazität 100 %.
* Die Summe aller `PlannedShare.percent` innerhalb eines `PlannedPost` darf 100 % nicht überschreiten.

## 8.5 Beamtenstellen

* Für `CIVIL_SERVICE_POST` darf zu keinem Zeitpunkt mehr als ein aktives `PositionFilling` existieren.
* Beamtenstellen erzeugen keine Shares.

## 8.6 Aktiver AssignmentPlan

* Ein AssignmentPlan gilt als aktiv, wenn `workflowStatus == APPROVED` und das aktuelle Datum innerhalb des Gültigkeitszeitraums liegt.
* Pro aktivem APPROVED `StaffingPlan` darf genau ein aktiver `StaffingAssignmentPlan` existieren.

## 8.7 Sicherheitsabschlag

* Der `safetyDeductionFactor` wirkt ausschließlich bei der strukturellen Neubildung freier Kapazitäten.
* Temporäre Aufstockungen verändern den Sicherheitsabschlag nicht.

---

# 9. Nicht-Ziele (MVP)

* Haushaltsplanungsprozesse
* Kapitel/Titel als Entitäten
* Zeitwirtschaft
* Vollständiges Personaldatenmodell

---

## Ergebnis

Das Modell bildet fachlich vollständig ab:

* Struktur (Soll)
* Operative Nutzung (Ist)
* Leerstellen
* Umbuchungen
* Tarif‑n:n‑Logik
* Einheitliche Workflow-Logik
* Historisierung ohne Überversionierung
* Dreijährige Planungs- und Projektionsfähigkeit

Damit ist das Stellenplan-Domänenmodell für das MVP fachlich und architektonisch konsistent definiert.
