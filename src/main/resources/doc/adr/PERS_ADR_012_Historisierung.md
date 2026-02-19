# ADR-012 – Historisierungsstrategie im Stellenplan-Modul

## Status

Accepted

---

## Kontext

Das Stellenplan-Modul trennt fachlich zwischen:

* **StaffingPlan (Soll)** – strukturelle Planung
* **StaffingAssignmentPlan (Ist)** – operative Besetzung

Das System muss eine vollständige, nachvollziehbare und revisionssichere Historie aller relevanten strukturellen und operativen Veränderungen gewährleisten.

Gleichzeitig soll das Modell für das MVP schlank bleiben und keine unnötige Überversionierung erzeugen.

Dieses ADR definiert verbindlich die Historisierungsstrategie für:

* Stammdaten
* Soll-Struktur
* Operative Besetzung (Ist)
* Revisionssichere Ereignisdokumentation (Ledger)

---

# 1. Grundprinzip

Historisierung erfolgt auf drei klar getrennten Ebenen:

1. **Stammdaten-Historisierung (Root + Version Pattern)**
2. **Snapshot-Historisierung für StaffingPlan (Soll)**
3. **Append-Only-Historisierung für StaffingAssignmentPlan (Ist) + verpflichtendes Ledger**

Nicht jede Entität wird versioniert.

---

# 2. Stammdaten-Historisierung

## 2.1 Anwendungsbereich

Root + Version Pattern wird ausschließlich verwendet für strukturelle Stammdaten mit zeitlicher Relevanz:

* `OrganisationUnit`
* `PositionPost`

Diese bestehen aus:

```
AggregateRoot
AggregateRootVersion
```

## 2.2 Eigenschaften

* Stabile technische Identität im Root
* Zeitliche Zustände in Version-Entity
* Änderungen erzeugen neue Version
* Versionen dürfen sich zeitlich nicht überlappen
* Physische Löschung im Fachbetrieb nicht vorgesehen

Diese Ebene deckt strukturelle Historie von Organisation und Stellenstamm vollständig ab.

---

# 3. Snapshot-Historisierung – StaffingPlan (Soll)

## 3.1 Grundsatz

Ein `StaffingPlan` repräsentiert einen vollständigen strukturellen Snapshot des Stellenplans für einen bestimmten Planungsstand.

Historie entsteht durch:

* Neue Planvarianten (`SCENARIO`)
* Übergang von `DRAFT` zu `APPROVED`
* Archivierung (`ARCHIVED`)

Es existiert **kein ACTIVE-Status**. Die Wirksamkeit ergibt sich ausschließlich aus `workflowStatus == APPROVED` und dem Gültigkeitszeitraum.

## 3.2 Konsequenzen

Folgende planstrukturellen Entitäten werden **nicht versioniert**:

* `PlannedPost`
* `PlannedShare`

Strukturelle Änderungen erfolgen:

* im Status `DRAFT` in-place
* nach `APPROVED` ausschließlich über eine neue Planvariante

Damit ist die Strukturhistorie revisionssicher über Planstände abgebildet.

---

# 4. Append-Only-Historisierung – StaffingAssignmentPlan (Ist)

## 4.1 Grundsatz

Der `StaffingAssignmentPlan` bildet die operative Nutzung des Stellenplans ab.

Ab `workflowStatus == APPROVED` gilt für operative Änderungen das **Append-Only-Prinzip**.

## 4.2 Geltungsbereich

Append-only betrifft insbesondere:

* `PositionFilling`
* Umbuchungen
* Beendigungen von Besetzungen
* befristete Aufstockungen

## 4.3 Umsetzung

* Keine Überschreibung historischer Datensätze
* Beendigungen erfolgen durch Setzen von `filledTo`
* Umbuchungen erfolgen als Beendigung + Neuanlage
* Neue operative Zustände erzeugen neue Datensätze

Im Status `DRAFT` sind in-place-Änderungen zulässig.

---

# 5. Verpflichtendes Ledger (Revisionssicherheit)

Ein **minimaler StaffingLedgerEntry** ist verpflichtender Bestandteil der Zielarchitektur.

## 5.1 Zweck

Das Ledger stellt sicher:

* Nachvollziehbarkeit von Herkunft und Veränderungen
* Dokumentation von Umbuchungen
* Transparenz über Nutzung und Beteiligte
* Revisionssichere Ereignisprotokollierung

## 5.2 Eigenschaften

* Append-only
* Keine Updates
* Keine Deletes
* Zeitstempel
* Benutzer-/Akteur-Referenz
* Referenz auf betroffenes Aggregat (StaffingPlan oder StaffingAssignmentPlan)
* Ereignistyp (z. B. CREATE, MODIFY, TRANSFER, END, APPROVE)

Das Ledger ergänzt Snapshot- und Append-Only-Historie und stellt vollständige Auditierbarkeit sicher.

---

# 6. Löschstrategie

## 6.1 Fachbetrieb

* Keine physische Löschung von Stammdaten
* Keine physische Löschung von StaffingPlan oder StaffingAssignmentPlan ab `APPROVED`
* In `DRAFT` vollständige Mutierbarkeit erlaubt

## 6.2 Administrative Löschung

Physische Löschung ist ausschließlich über administrative Services erlaubt und erfordert:

* Berechtigungsprüfung
* Referenzprüfung
* Audit-Protokollierung

Administrative Löschungen sind kein regulärer Fachprozess.

---

# 7. Zeitdimension

Zeitliche Gültigkeit wird modelliert über:

* `validFrom` / `validTo` bei Version-Entities
* `validFrom` / `validTo` bei Shares
* `filledFrom` / `filledTo` bei PositionFilling

Zeitdimension ersetzt keine Version-Entity, sondern ergänzt sie.

---

# 8. Keine Überversionierung

Folgende Entitäten werden bewusst nicht versioniert:

* `StaffingPlan`
* `PlannedPost`
* `PlannedShare`
* `StaffingAssignmentPlan`
* `PositionFilling`

Begründung:

* Strukturhistorie wird über Planvarianten abgebildet
* Operative Historie wird append-only modelliert
* Ledger gewährleistet vollständige Nachvollziehbarkeit
* Zusätzliche Version-Entities würden unnötige Komplexität erzeugen

---

# 9. Revisionssicherheit und Geltungsbereich

Revisionssicherheit gilt verbindlich ab `workflowStatus == APPROVED`.

* DRAFT-Phasen sind bewusst nicht revisionsgesichert.
* APPROVED- und ARCHIVED-Phasen sind revisionssicher.

---

# 10. Zielbild

Diese Historisierungsstrategie stellt sicher:

* Vollständige Nachvollziehbarkeit aller strukturellen und operativen Veränderungen
* Klare Trennung zwischen Soll-Snapshot und Ist-Append-Only
* Verpflichtende Ereignisdokumentation (Ledger)
* Keine Status-Inkonsistenzen
* Keine Überversionierung
* Konsistenz zu ADR-003 (Domänenmodell), ADR-011 (Lifecycle) und ADR-013 (Kapazitätsprojektion)

Die Historisierungsstrategie ist projektweit verbindlich.
