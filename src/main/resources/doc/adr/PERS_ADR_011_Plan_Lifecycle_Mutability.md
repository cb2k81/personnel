# ADR-011 – Plan Lifecycle & Mutability Rules

## Status

Accepted

---

## Kontext

Das Stellenplan-Modul trennt fachlich zwischen:

* **StaffingPlan (Soll)** – strukturelle Planung
* **StaffingAssignmentPlan (Ist)** – operative Besetzung

Beide Aggregate unterliegen einer einheitlichen Workflow-Logik. Gleichzeitig unterscheiden sich ihre Mutability-Regeln fachlich deutlich:

* Der StaffingPlan wird nach Freigabe strukturell gesperrt.
* Der StaffingAssignmentPlan erlaubt operative Buchungen, jedoch append-only.

Die zeitliche Wirksamkeit eines Plans darf nicht mit seinem fachlichen Freigabestatus vermischt werden. Insbesondere darf ein Jahreswechsel keine automatische Statusänderung eines Plans auslösen.

Dieses ADR definiert daher verbindlich:

* den einheitlichen Workflow-Status
* die zeitliche Gültigkeit
* die differenzierten Mutability-Regeln für Soll und Ist

---

# 1. Geltungsbereich

Dieses ADR gilt für:

* `StaffingPlan`
* `PlannedPost`
* `PlannedShare`
* `StaffingAssignmentPlan`
* `PositionFilling`

Nicht betroffen sind:

* historisierte Stammdaten (`OrganisationUnit`, `PositionPost`)
* rein administrative Funktionen außerhalb des Fachbetriebs

---

# 2. Plan Variant Types (StaffingPlan)

Jeder `StaffingPlan` besitzt einen Variantentyp:

```
planVariantType:
- APPROVED   (verabschiedeter Referenzplan)
- SCENARIO   (Planungsalternative)
```

Regeln:

* Pro `StaffingPlanSet` darf es maximal einen APPROVED-Plan mit überlappender Gültigkeit geben.
* SCENARIO-Pläne dürfen denselben Zeitraum wie APPROVED-Pläne besitzen.
* SCENARIO-Pläne dienen Vergleichs- und Simulationszwecken.

Die Wirksamkeit eines Plans ergibt sich nicht aus dem Variantentyp, sondern aus Workflow-Status und Gültigkeitszeitraum.

---

# 3. Einheitlicher Workflow-Status

Sowohl `StaffingPlan` als auch `StaffingAssignmentPlan` verwenden dieselbe Workflow-Logik:

```
workflowStatus:
- DRAFT
- IN_REVIEW
- APPROVED
- ARCHIVED
```

Der Workflow-Status steuert:

* Mutierbarkeit
* Freigabelogik
* Übergangsregeln

Er ist unabhängig vom aktuellen Datum.

---

# 4. Zeitliche Gültigkeit

Sowohl `StaffingPlan` als auch `StaffingAssignmentPlan` besitzen:

```
validFrom
validTo
```

Ein Plan ist fachlich wirksam, wenn:

* workflowStatus == APPROVED
* aktuelles Datum liegt zwischen validFrom und validTo

Die Wirksamkeit ist eine abgeleitete Eigenschaft und kein persistierter Status.

Ein Jahreswechsel oder Ablauf von `validTo` führt nicht zu einer automatischen Statusänderung.

---

# 5. Mutability Rules – Differenzierung Soll / Ist

Die Mutierbarkeit wird über `workflowStatus` gesteuert, unterscheidet sich jedoch je Aggregat.

---

## 5.1 Status: DRAFT

### StaffingPlan (Soll)

Erlaubt:

* Erstellen
* Ändern
* Löschen
* Umbuchen
* Anlegen/Entfernen von PlannedPost
* Anlegen/Entfernen von PlannedShare

Änderungen erfolgen in-place.

### StaffingAssignmentPlan (Ist)

Erlaubt:

* Erstellen, Ändern und Löschen von PositionFilling
* Korrekturen operativer Daten

Änderungen erfolgen in-place.

Ziel:

* Flexibles Arbeiten ohne Datenmüll.

---

## 5.2 Status: IN_REVIEW (optional)

Für beide Aggregate gilt:

Erlaubt:

* Fachliche Korrekturen (optional konfigurierbar)

Nicht erlaubt:

* Löschen von Kernentitäten
* Strukturelle Umbuchungen im StaffingPlan

Append-Only-Regel:

* Noch nicht aktiv.

---

## 5.3 Status: APPROVED

### StaffingPlan (Soll)

Nicht erlaubt:

* Strukturelle Änderungen an PlannedPost oder PlannedShare
* Löschen von planstrukturellen Entitäten

Strukturelle Änderungen erfordern:

* Neue Planvariante

### StaffingAssignmentPlan (Ist)

Erlaubt:

* Operative Buchungen (neue PositionFilling)
* Beendigung von Besetzungen (filledTo setzen)

Append-Only-Regel:

* Operative Änderungen erfolgen append-only.
* Keine Überschreibung bestehender historischer Einträge.

Ziel:

* Struktur stabil (Soll)
* Operative Bewirtschaftung nachvollziehbar (Ist)

---

## 5.4 Status: ARCHIVED

Für beide Aggregate gilt:

* Keine Mutationen
* Vollständig schreibgeschützt

Ziel:

* Revisionssichere Historie.

---

# 6. Statusübergänge

Zulässige Übergänge:

```
DRAFT -> IN_REVIEW
IN_REVIEW -> APPROVED
DRAFT -> APPROVED (optional)
APPROVED -> ARCHIVED
```

Nicht zulässig:

* APPROVED -> DRAFT
* ARCHIVED -> irgendein anderer Status

Es existiert kein ACTIVE-Status.

---

# 7. Delete Policy

## Fachbetrieb

* Hard Deletes sind nur im Status DRAFT erlaubt.
* Ab APPROVED keine physische Löschung.

## Administrative Override

Physische Löschung ist ausschließlich über einen administrativen Service erlaubt und erfordert:

* Berechtigungsprüfung
* Referenzprüfung
* Audit-Protokollierung

---

# 8. Konsequenzen für Architektur

* Mutability wird zentral im DomainService geprüft.
* Repositories dürfen Statusregeln nicht umgehen.
* Snapshot-Historisierung gilt für StaffingPlan.
* Append-Only-Historisierung gilt für StaffingAssignmentPlan.
* Zeitliche Wirksamkeit wird berechnet, nicht persistiert.

---

# 9. Zielbild

Dieses ADR stellt sicher:

* Einheitliche Workflow-Logik für Soll und Ist
* Klare Trennung zwischen Struktur (Soll) und operativer Nutzung (Ist)
* Keine redundanten oder automatisch wechselnden Status
* Revisionssichere Historie
* Deterministische Mutability-Regeln

Die Lifecycle-Regeln sind projektweit verbindlich.
