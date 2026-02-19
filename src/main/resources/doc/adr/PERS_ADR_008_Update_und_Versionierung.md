# ADR-008 – Update- und Versionierungsstrategie (Version 2)

## Status

Accepted

---

## Kontext

Dieses ADR regelt die technische Update-Semantik im Zusammenspiel mit:

* ADR-003 (Domänenmodell – Soll/Ist-Trennung)
* ADR-006 (Identität & Audit)
* ADR-011 (Lifecycle & Mutability)
* ADR-012 (Historisierung & Append-Only)

Das System verwendet:

* technische UUID als Persistenz-ID
* Optimistic Locking (@Version)
* Lifecycle-abhängige Mutability
* Append-Only-Modell für operative Entitäten

Update-Regeln dürfen diese Architektur nicht unterlaufen.

---

# 1. Grundprinzip

Jede Update-Operation unterliegt drei Prüfungen:

1. Technische Identitätsprüfung (UUID)
2. Versionsprüfung (Optimistic Locking)
3. Lifecycle-/Mutability-Prüfung (WorkflowStatus)

Alle drei Prüfungen sind verpflichtend.

---

# 2. Optimistic Locking

Jede Aggregate Root besitzt:

```
@Version
private Long persistenceVersion;
```

Regeln:

* Version muss im Response-DTO enthalten sein.
* Version muss bei Update-Operationen vom Client zurückgegeben werden.
* Bei Versionskonflikt wird HTTP 409 (Conflict) zurückgegeben.

Die Version dient ausschließlich der technischen Konsistenzsicherung.

---

# 3. Lifecycle-abhängige Mutability

Neben der Versionsprüfung ist der `workflowStatus` zu prüfen.

## 3.1 StaffingPlan (Soll)

* DRAFT: vollständige Updates erlaubt
* IN_REVIEW: eingeschränkte Updates (fachlich definiert)
* APPROVED: keine strukturellen Updates
* ARCHIVED: keine Updates

Strukturelle Änderungen nach APPROVED sind ausschließlich über eine neue Planvariante zulässig.

## 3.2 StaffingAssignmentPlan (Ist)

* DRAFT: Updates erlaubt
* IN_REVIEW: eingeschränkt
* APPROVED: operative Änderungen append-only
* ARCHIVED: keine Updates

Lifecycle-Verstöße führen zu HTTP 409 oder 400 (fachlicher Fehler).

---

# 4. Append-Only-Regel

Für append-only modellierte Entitäten (z. B. PositionFilling, LedgerEntry) gilt:

* Keine fachliche Überschreibung bestehender Datensätze
* Keine PUT/PATCH-Operationen zur Zustandsänderung

Änderungen erfolgen durch:

* Beendigung (z. B. Setzen von filledTo)
* Neuanlage eines Datensatzes

PATCH oder PUT darf keine Historie überschreiben.

---

# 5. PUT vs PATCH

## PUT

* Ersetzt den vollständigen fachlichen Zustand einer mutierbaren Entität
* Nur zulässig, wenn Lifecycle-Status Änderungen erlaubt

## PATCH

* Teilupdate
* Nur zulässig bei mutierbaren Entitäten
* Darf keine verbotenen Felder verändern

Append-Only-Entitäten unterstützen kein klassisches PUT/PATCH-Update.

---

# 6. Snapshot-Modell

Für StaffingPlan gilt:

* APPROVED-Pläne sind strukturelle Snapshots
* Änderungen erfordern neue Planvariante
* Update-Operationen dürfen keine Snapshot-Struktur überschreiben

---

# 7. Fehlerbehandlung

Folgende Fälle führen zu Fehlern:

* Fehlende Version → 400
* Versionskonflikt → 409
* Lifecycle-Verstoß → 409
* Versuch, Append-Only-Daten zu überschreiben → 400

---

# 8. Konsequenzen für Architektur

* Repositories dürfen keine Lifecycle-Regeln umgehen.
* Update-Logik ist Bestandteil der Domain-Services.
* Append-Only wird fachlich, nicht technisch erzwungen.
* OData-Integrationen dürfen keine technische ID umgehen.

---

# 9. Zielbild

Diese Update-Strategie stellt sicher:

* Technische Konsistenz durch Optimistic Locking
* Fachliche Konsistenz durch Lifecycle-Regeln
* Revisionssichere Historie durch Append-Only
* Kompatibilität mit Snapshot- und Ledger-Modell

Die Update- und Versionierungsstrategie ist projektweit verbindlich.
