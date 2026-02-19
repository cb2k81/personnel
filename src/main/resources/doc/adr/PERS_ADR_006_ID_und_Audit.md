# ADR-006 – Identitäts- und Audit-Strategie (Version 4)

## Status

Accepted

---

## Kontext

Für den personnel Service müssen Identität, Referenzierung, Versionierung und Auditierung eindeutig geregelt sein.

Dabei ist strikt zwischen technischer Identität (Persistenzschlüssel) und fachlicher Identität (Business Key / Integrationsschlüssel) zu unterscheiden. Beide Konzepte haben unterschiedliche Verantwortlichkeiten und dürfen architektonisch nicht vermischt werden.

Dieses ADR definiert die verbindlichen Regeln.

---

# 1. Technische Identität (Persistenz-ID)

## 1.1 Definition

Jede persistierbare Aggregate Root besitzt eine technische Identität:

```
id : String (UUID)
```

Diese ID dient ausschließlich der Persistenzidentifikation.

## 1.2 Eigenschaften

* Wird automatisch beim Persistieren erzeugt
* Wird bei Create nicht vom Client gesetzt
* Ist für Clients fachlich nicht interpretierbar
* Ist unveränderlich
* Ist nicht Bestandteil von Create-DTOs

Die Generierung erfolgt durch die Infrastruktur (z. B. DomainEntityListener / IdGeneratorService).

Die Domain-Services setzen oder manipulieren diese ID nicht.

## 1.3 Referenzierungsregel

Die technische ID ist die **Referenzidentität für Update- und Delete-Operationen**.

* Sie wird typischerweise als Path-Parameter in REST-Endpunkten verwendet.
* Sie identifiziert eindeutig eine persistierte Entität.
* Sie ist unabhängig von fachlichen Schlüsseln.

Beispiel:

```
POST   /entities
→ keine id im Body

PUT    /entities/{id}
→ id im Path
→ version im Body
```

Die technische ID ist damit die einzige garantiert eindeutige Referenz für Änderungsoperationen.

## 1.4 Verantwortlichkeit

Die technische Identität gehört vollständig zur technischen Infrastruktur (`system`-Package).

---

# 2. Fachliche Identität (Business Keys / Integrationsschlüssel)

## 2.1 Definition

Fachliche Schlüssel repräsentieren eine domänenspezifische oder integrationsbezogene Identität, z. B.:

* Personalnummer
* centralPersonId
* externe Organisations-ID
* externe Planstellen-ID

Diese Schlüssel sind Bestandteil der fachlichen Bedeutung einer Entität.

Sie können gleichzeitig als Integrationsschlüssel gegenüber externen Systemen (z. B. OData-Services) dienen.

## 2.2 Eigenschaften

* Können von externen Systemen stammen
* Können Unique Constraints besitzen
* Können Bestandteil fachlicher Invarianten sein
* Können in Create-DTOs enthalten sein
* Können validiert werden
* Können je nach Fachregel änderbar oder unveränderlich sein
* Dürfen nicht mit der technischen UUID identisch sein

Externe Schlüssel können Bestandteil eines OData-Keys oder eines Integrationsvertrags sein. Sie ersetzen jedoch niemals die technische Persistenz-ID.

## 2.3 Optionalität

Nicht jede Entität benötigt einen fachlichen Schlüssel.

Es wird unterschieden zwischen:

1. **Extern integrierten Entitäten**
   → benötigen in der Regel einen fachlichen Schlüssel (z. B. externe ID)

2. **Rein internen Entitäten**
   → benötigen keinen fachlichen Schlüssel

3. **Historisierten Entitäten (Root + Version Pattern)**
   → besitzen einen stabilen fachlichen Schlüssel auf Root-Ebene
   → Version-Entities teilen sich denselben fachlichen Schlüssel
   → Zeitliche Eindeutigkeit wird über Gültigkeitszeiträume hergestellt

Fachliche Schlüssel sind daher optional und werden nur modelliert, wenn sie fachlich erforderlich sind.

## 2.4 Historisierte Aggregate

Bei historisierten Aggregaten gilt:

* Der Business Key gehört zur Aggregate Root.
* Version-Entities besitzen keinen eigenen fachlichen Schlüssel.
* Mehrere Versionen teilen sich dieselbe fachliche Identität.
* Zeitliche Disjunktheit wird über `validFrom` / `validTo` sichergestellt.

Version-Entities werden niemals direkt über REST referenziert oder aktualisiert. Änderungen erfolgen immer über die Aggregate Root.

## 2.5 Verantwortlichkeit

Fachliche Schlüssel gehören zur Domain-Schicht.

Sie werden:

* im Domain-Modell definiert
* im Domain-Service validiert
* ggf. mit fachlichen Regeln geschützt

Die Infrastruktur generiert keine fachlichen Schlüssel.

---

# 3. Versionierung (Optimistic Locking)

Jede Aggregate Root verwendet eine technische Versionsspalte:

```
@Version
private Long persistenceVersion;
```

Eigenschaften:

* Dient ausschließlich der technischen Konsistenzsicherung
* Wird von JPA/Hibernate verwaltet
* Ist nicht fachlich interpretierbar
* Muss in Response-DTOs enthalten sein
* Muss bei Updates vom Client zurückgegeben werden

## 3.1 Versionierung bei Append-Only-Modellen

Bei append-only modellierten Entitäten (z. B. operativen Besetzungen oder Ledger-Einträgen) gilt:

* Fachliche Änderungen erfolgen durch Anlegen neuer Datensätze
* Bestehende Datensätze werden nicht fachlich überschrieben
* Die technische Version dient ausschließlich der Konsistenzsicherung
* PATCH/PUT darf keine fachliche Historie überschreiben

Versionierung ist unabhängig von fachlichen Schlüsseln.

---

# 4. Auditierung

Technische Auditfelder (z. B. createdAt, createdBy, lastModifiedAt, lastModifiedBy):

* Werden ausschließlich durch Infrastrukturmechanismen gesetzt
* Basieren auf dem aktuellen UserContext
* Sind nicht durch Domain-Services direkt manipulierbar

Auditierung ist eine technische Querschnittsfunktion.

Ein fachliches Ledger (z. B. StaffingLedgerEntry) ist hiervon getrennt und Bestandteil der Domain (siehe ADR-012).

---

# 5. Beispiel (verallgemeinert)

Beispielhafte Struktur einer Aggregate Root:

```
class ExampleAggregate extends DomainEntity {

    // technische ID (geerbt)
    // private String id;

    // technische Version (geerbt)
    // private Long persistenceVersion;

    // optionaler fachlicher Schlüssel
    private String externalBusinessKey;

}
```

Die technische UUID ist rein infrastrukturell.
Der fachliche Schlüssel ist – sofern vorhanden – Teil der Domäne.

---

# 6. Klare Trennregel

Technische Identität ≠ Fachliche Identität

* Die technische ID darf niemals als fachlicher Schlüssel verwendet werden.
* Fachliche Schlüssel dürfen niemals als Primärschlüssel missbraucht werden.
* Updates referenzieren Entitäten über die technische ID.
* Externe Integrationsschlüssel ersetzen nicht die technische Identität.

---

# 7. Konsequenzen

* Aggregate Roots erben von `DomainEntity`
* ID-Generierung erfolgt ausschließlich technisch
* Business Keys sind explizit im Domain-Modell definiert (falls erforderlich)
* Externe System-IDs werden als fachliche Attribute modelliert
* Version-Entities sind nicht direkt adressierbar
* DTOs enthalten keine technische ID bei Create
* Update-DTOs enthalten die technische Version
* Append-Only-Modelle überschreiben keine Historie

---

# 8. Zielbild

Diese Regelung stellt sicher:

* saubere Trennung von Technik und Fachlichkeit
* Integrationsfähigkeit mit externen Systemen (z. B. OData)
* konsistente Persistenzstrategie
* stabile Erweiterbarkeit der Domäne
* eindeutige Referenzierung bei Änderungsoperationen
* Kompatibilität mit Snapshot- und Append-Only-Historisierung

Die Identitätsarchitektur ist damit eindeutig und projektweit verbindlich definiert.
