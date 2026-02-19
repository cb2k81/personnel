# ADR-001 – Basisstrukturen und Abhängigkeitsregeln (Version 2)

## Status

Accepted

---

## Kontext

Der personnel Service folgt einer klar strukturierten, schichtenbasierten Architektur mit fachlicher Trennung nach Domänen sowie technischen Basisdiensten.

In der ersten Fassung dieses ADRs war definiert, dass das `domain`-Package keine Abhängigkeiten zum `system`-Package besitzen darf. Diese Regel ist nicht mit der tatsächlichen Architektur vereinbar, da zentrale technische Basiskomponenten (z. B. `DomainEntity`, ID-Generierung, Auditierung, Metadaten) im `system`-Package verortet sind und von fachlichen Entitäten verwendet werden.

Zur Sicherstellung einer konsistenten und stabilen Architektur wird die Abhängigkeitsregel präzisiert.

---

## Entscheidung

### 1. Paketstruktur

Der personnel Service ist in folgende Hauptpakete gegliedert:

* `web` – REST-Controller, API-Endpunkte
* `domain` – Fachliche Aggregate, Value Objects, Domain Services
* `persistence` – Repositories und Persistenzadapter
* `system` – Technische Basiskomponenten (Shared Kernel)
* `config` – Konfiguration
* `security` – Sicherheitsintegration

---

### 2. Rolle des `system`-Packages

Das `system`-Package stellt einen **Shared Kernel für technische Infrastruktur** dar.

Es enthält ausschließlich technische Basiskomponenten, z. B.:

* `DomainEntity`
* ID-Generierungsmechanismen
* Auditierungsinfrastruktur
* Metadaten-Mechanismen (Tags, Key-Value-Paare)
* technische Hilfsklassen

Das `system`-Package enthält **keine fachlichen Konzepte oder Domänenlogik**.

---

### 3. Abhängigkeitsregeln

Die zulässigen Abhängigkeiten sind wie folgt definiert:

```
web        → domain
web        → system (nur technische Aspekte wie Error-Handling)

domain     → system

persistence → domain
persistence → system

system     → (keine Abhängigkeit zu domain)
```

Zentrale Regel:

> Das `domain`-Package darf auf `system` zugreifen.
> Das `system`-Package darf niemals auf `domain` zugreifen.

Damit ist die Abhängigkeitsrichtung eindeutig von fachlich zu technisch gerichtet.

---

### 4. Architekturprinzipien

1. **Fachliche Isolation**
   Sämtliche Geschäftslogik (Workflow-Regeln, Mutability-Prüfungen, Invarianten, Budget- und Kapazitätslogik, Ledger-Erzeugung etc.) befindet sich ausschließlich im `domain`-Package.

2. **Technische Kapselung**
   Technische Infrastruktur (ID, Audit, Version-Feld, Basisklassen, generische Utilities) liegt ausschließlich im `system`-Package.

3. **Keine Zyklen**
   Zyklische Paketabhängigkeiten sind unzulässig.

4. **DomainEntity ist rein technisch**
   Alle fachlichen Aggregate Roots erben von `system.entity.DomainEntity`.
   `DomainEntity` enthält ausschließlich technische Basisfunktionalität (z. B. technische ID, Audit-Felder, Versionsfeld).
   Sie enthält keine fachliche Logik, keine Workflow-Regeln und keine planbezogenen Konzepte.

5. **Workflow- und Mutability-Logik verbleiben in der Domäne**
   Statusübergänge, Mutability-Prüfungen (z. B. DRAFT/APPROVED-Regeln) sowie Append-Only-Regeln werden ausschließlich im `domain`-Layer implementiert.
   Das `system`-Package darf keine generischen Mechanismen enthalten, die fachliche Statuslogik erzwingen oder umgehen.

6. **Ledger ist fachliches Konzept**
   Ein fachliches Ledger (z. B. StaffingLedgerEntry) ist Bestandteil der Domäne.
   Das `system`-Package stellt lediglich technische Audit-Infrastruktur bereit und darf kein fachliches Ereignisprotokoll enthalten.

7. **Repositories umgehen keine Fachregeln**
   Persistenzkomponenten dürfen fachliche Mutability- oder Workflow-Regeln nicht umgehen.
   Die Einhaltung von Lifecycle- und Append-Only-Regeln wird im Domain-Layer geprüft und ist vor Persistenzoperationen sicherzustellen.

8. **Technik kennt keine Fachlichkeit**
   Das `system`-Package darf keinerlei Referenzen auf fachliche Klassen oder planbezogene Aggregate enthalten.

---

## Konsequenzen

* Die Verwendung von `DomainEntity` innerhalb fachlicher Entitäten ist explizit zulässig.
* Gemeinsame technische Mechanismen (ID, Audit, Versionierung, Metadaten) werden zentral im `system`-Package gehalten.
* Änderungen an Basiskomponenten erfolgen ausschließlich im `system`-Package.
* ArchUnit-Tests oder vergleichbare Prüfmechanismen sollten die Abhängigkeitsrichtung absichern.

---

## Begründung

Diese Regelung ermöglicht:

* klare Trennung von Fachlichkeit und Technik
* Wiederverwendbarkeit technischer Basiskomponenten
* konsistente ID-, Audit- und Versionierungsstrategien
* stabile Erweiterbarkeit der Domäne ohne technische Streuung

Die Architektur entspricht damit einer klar definierten Schichtenarchitektur mit einem technischen Shared Kernel und einer strikt eingehaltenen Abhängigkeitsrichtung.
