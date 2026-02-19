# ADR PERS_ADR_009 – Command Permissions

## Status

Proposed

---

## Kontext

Die Applikation implementiert fachliche Use Cases über Domain Services.

Diese Methoden stellen fachliche Commands dar (Create, Update, Delete, spezielle Operationen).

Zukünftig werden Rollen über JWT von einem externen Identity Provider geliefert.

Es muss definiert werden:

* Wo Berechtigungen geprüft werden
* Wie Rechte definiert werden
* Wie Rollen und Rechte verknüpft werden
* Wie die Implementierung IoC-fähig bleibt

Ohne klare Strategie entsteht eine unkontrollierte Verteilung von Security-Logik.

Dieses ADR definiert das verbindliche Modell für Command Permissions.

---

# 1. Grundprinzip

Berechtigungen werden auf Domain-Service-Ebene geprüft.

Nicht im:

* Controller
* Repository
* Entity Service

Der Domain Service ist die fachliche Autoritätsgrenze.

---

# 2. Rechte-Modell

## 2.1 Permission als technische Konstante

Rechte werden als stabile, technische Strings definiert.

Beispiel:

* PERSONNEL_STAFFING_PLANSET_CREATE
* PERSONNEL_STAFFING_PLANSET_UPDATE
* PERSONNEL_STAFFING_PLANSET_DELETE
* PERSONNEL_STAFFING_PLANSET_READ

Diese sind unabhängig von Rollen.

---

## 2.2 Rollen kommen von außen

JWT enthält Rollen oder Claims.

Die Applikation kennt keine festen Rollen.

Mapping Rolle → Permission erfolgt:

* entweder extern (Preferred)
* oder konfigurierbar über Properties

Die Fachdomäne kennt ausschließlich Permissions.

---

# 3. Technische Umsetzung

## 3.1 Annotation-basierte Sicherung

Domain-Service-Methoden werden mit einer Annotation versehen:

```
@RequirePermission("PERSONNEL_STAFFING_PLANSET_CREATE")
```

Diese Annotation ist technisch und gehört ins system Package.

---

## 3.2 Durchsetzung

Die Durchsetzung erfolgt über:

* Spring AOP
* oder Method Security (PreAuthorize Wrapper)

Die Domain Services bleiben frei von Security-Logik.

---

# 4. Architektur-Regeln

| Regel                                  | Begründung                        |
| -------------------------------------- | --------------------------------- |
| Keine Permission-Prüfung im Controller | Trennung HTTP/Fachlichkeit        |
| Keine Permission-Prüfung im Repository | Repository ist rein technisch     |
| Keine Rollen im Code                   | Entkopplung vom Identity Provider |
| Nur Permissions im Code                | Stabil und versionierbar          |

---

# 5. Erweiterbarkeit

Dieses Modell erlaubt:

* Austausch des Identity Providers
* Zentrale Anpassung von Berechtigungslogik
* Testbarkeit von Domain Services ohne Security

---

# 6. Konsequenzen

## Vorteile

* Klare fachliche Sicherheitsgrenze
* Stabiler Permission-Katalog
* IoC-fähige Security
* Kein Rollenwissen in der Domäne

## Nachteile

* Zusätzlicher Infrastruktur-Code im system Package
* AOP-Komplexität

Diese Nachteile sind akzeptabel.

---

# 7. Gültigkeit

Diese Strategie gilt für:

* alle Domain-Service-Methoden mit fachlicher Relevanz
* alle zukünftigen Aggregate

Command Permissions sind verbindlich standardisiert.
