# ADR PERS_ADR_010 – Record-Level Security

## Status

Accepted

---

## Kontext

Neben der Absicherung von Commands (siehe ADR 009) muss auch der Zugriff auf einzelne Datensätze (Records) geregelt werden.

Nicht jeder Benutzer darf alle Aggregate lesen oder verändern.

Beispiele:

* Zugriff nur auf bestimmte Organisationseinheiten
* Zugriff nur auf bestimmte Budgetjahre
* Zugriff nur auf eigene Objekte
* Zugriff abhängig von Mandant oder Geschäftsbereich

Diese Einschränkungen basieren auf Benutzerattributen aus dem JWT (Claims) oder aus einem technischen UserContext.

Dieses ADR definiert die verbindliche Strategie für Record-Level Security (RLS).

---

# 1. Grundprinzip

Record-Level Security wird ausschließlich auf Domain-Service-Ebene umgesetzt.

Nicht im:

* Controller
* Repository (kein magisches Query-Manipulieren)
* Datenbank (keine DB-spezifische Row-Level-Security)

Die Fachlogik entscheidet explizit über Sichtbarkeit.

---

# 2. User-Kontext

Ein technischer `UserContext` im `system` Package stellt folgende Informationen bereit:

* User-ID
* Permissions
* Rollen (optional, nicht primär für RLS)
* zusätzliche Claims (z. B. Organisationseinheit, Mandant, Budgetjahr, Scope-Flags)

Der `UserContext` enthält ausschließlich Benutzerattribute.

Er enthält **keine Fachlogik** und erzeugt keine Specifications.

Domain Services greifen ausschließlich über dieses Interface auf Benutzerinformationen zu.

---

# 3. Technisches Modell

## 3.1 Query-Erweiterung über Specification (ABAC-Ansatz)

Record-Level Security wird über eine explizite fachliche `Specification` implementiert.

Der `UserContext` liefert Benutzerattribute (Claims, Scopes, Organisationszugehörigkeit etc.).

Eine domänenspezifische RLS-Komponente (z. B. `PersonRlsSpecificationFactory`) erzeugt daraus eine `Specification`.

Beispiel (konzeptionell):

```
Specification<T> rlsSpec = rlsFactory.buildReadSpecification(userContext);
spec = spec.and(rlsSpec);
```

Damit gilt:

* UserContext ist technisch
* RLS-Regeln sind fachlich
* Query-Erweiterung ist explizit und nachvollziehbar
* Testbarkeit ist gewährleistet

Dieses Modell entspricht einem Attribute-Based Access Control (ABAC) Ansatz.

---

## 3.2 Kein automatisches Query-Rewriting

Es erfolgt:

* kein Repository-Proxy
* kein globaler Hibernate-Filter
* kein verstecktes Query-Injection-Modell

Begründung:

* Transparenz
* Nachvollziehbarkeit
* Debugbarkeit
* Testbarkeit

---

# 4. Lese-Operationen

Alle List- und Get-Operationen prüfen:

* ob das Objekt sichtbar ist
* ob es innerhalb des erlaubten Kontextes liegt

Für List-Operationen erfolgt die Filterung über `Specification`.

Für Einzelzugriffe gilt:

* Wenn das Objekt außerhalb des Zugriffskontextes liegt → 404

Nicht 403, um Informationslecks zu vermeiden.

---

# 5. Schreib-Operationen

Vor Update oder Delete wird geprüft:

* ob das Objekt im Zugriffskontext liegt

Wenn nicht:

* 404 (nicht sichtbar)

Die gleiche RLS-Logik, die für Read gilt, wird auch für Write angewendet.

Optional kann zwischen Read- und Write-Specifications unterschieden werden, wenn die Fachlogik dies erfordert.

---

# 6. Architektur-Regeln

| Regel                                 | Begründung                  |
| ------------------------------------- | --------------------------- |
| Sichtbarkeit wird explizit geprüft    | Keine implizite Magie       |
| Keine Rollenprüfung auf Record-Level  | Nur Permission + Kontext    |
| Keine Datenbank-RLS                   | Plattformunabhängigkeit     |
| Kein verstecktes Repository-Filtering | Debugbarkeit                |
| RLS über Specification                | Spring-konform und testbar  |
| UserContext ohne Fachlogik            | Saubere Trennung der Ebenen |

---

# 7. Erweiterbarkeit

Dieses Modell erlaubt:

* Mandantenfähigkeit
* Organisationsbasierte Einschränkungen
* Business-Area-basierte Einschränkungen
* Attribute-Based Access Control (ABAC)
* Unterschiedliche Read- und Write-Scope-Regeln

Ohne Änderung der Layer-Architektur.

Neue RLS-Regeln werden ausschließlich durch zusätzliche fachliche Specifications ergänzt.

---

# 8. Konsequenzen

## Vorteile

* Transparente Sicherheitslogik
* Testbare Sichtbarkeitsregeln
* Plattformunabhängigkeit
* Keine versteckten Seiteneffekte
* Vollständig Spring-konform

## Nachteile

* Domain Services müssen Filter explizit anwenden
* Mehr Code bei komplexen Filtern

Diese Nachteile sind akzeptabel.

---

# 9. Gültigkeit

Diese Strategie gilt für:

* alle Leseoperationen
* alle Update/Delete-Operationen
* alle zukünftigen Aggregate

Record-Level Securit
