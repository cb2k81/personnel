# ADR-013 – Budget- und Kapazitätsprojektion

## Status

Accepted

---

## Kontext

Das Stellenplan-Modul verfolgt folgende übergeordnete Ziele:

1. Vollständige Bindung der zur Verfügung stehenden Stellen in Arbeitsverträgen oder Dienstverhältnissen.
2. Vollständige Bindung der etatisierten Haushaltsmittel.
3. Haushaltsmittel sind der limitierende Faktor.

Insbesondere müssen folgende fachliche Anforderungen erfüllt werden:

* Unterscheidung zwischen Beamtenstellen und Tarifstellen
* Flexible Aufteilung von Tarifstellen (n:n-Beziehung)
* Nicht-Teilbarkeit von Beamtenstellen
* Sicherheitsabschlag bei Nutzung freier Tarifanteile
* Abbildung befristeter und unbefristeter Beschäftigungsoptionen
* Monatliche Projektion über mindestens drei Jahre
* Transparente Darstellung gebundener und freier Kapazitäten

Dieses ADR definiert die verbindliche Berechnungs- und Projektionslogik.

---

# 1. Grundprinzip

Kapazität und Budget werden nicht als statische Werte gespeichert, sondern aus strukturellen und operativen Daten berechnet.

Die Projektion basiert auf:

* PositionPost / PositionPostVersion
* PlannedPost
* PlannedShare
* PositionFilling
* Gültigkeitszeiträumen

---

# 2. Kapazitätsmodell

## 2.1 Grundkapazität

Jede PositionPost besitzt eine strukturelle Grundkapazität.

* CIVIL_SERVICE_POST → 100% (nicht teilbar)
* EMPLOYEE_POST → 100% (teilbar über Shares)

---

## 2.2 Vertragsbindung

Die arbeitsvertraglich gebundene Kapazität ergibt sich aus:

```
Sum(contractualPortionPercent aller aktiven PositionFilling)
```

Diese darf die verfügbare Kapazität nicht überschreiten.

---

## 2.3 Aktueller Beschäftigungsumfang

Der aktuelle Beschäftigungsumfang kann vom arbeitsvertraglich gebundenen Anteil abweichen.

Relevant für Budgetprojektion ist:

```
currentEmploymentPercent
```

---

## 2.4 Freie Kapazität

Freie Kapazität ergibt sich aus:

```
verfügbareKapazität - vertraglichGebundeneKapazität
```

Bei Tarifstellen:

* freie Anteile können gesammelt werden
* Sicherheitsabschlag wird berücksichtigt

Bei Beamtenstellen:

* Reduktion erzeugt keinen besetzbaren Stellenanteil
* Es entsteht ausschließlich finanzieller Rest

---

# 3. Sicherheitsabschlag

Für Tarifanteile kann ein Sicherheitsabschlag definiert werden:

```
verfügbareKapazitätNachSicherheitsabschlag =
freieKapazität * (1 - sicherheitsfaktor)
```

Der Sicherheitsfaktor wird fachlich festgelegt (z. B. durch BDH oder Personal).

---

# 4. Befristete und unbefristete Optionen

## 4.1 Unbefristete Optionen

* Basieren auf vertraglich gebundener Kapazität
* Müssen innerhalb der strukturellen Kapazität liegen

## 4.2 Befristete Optionen

* Basieren auf temporär freier Kapazität
* Werden zeitlich modelliert (filledFrom / filledTo)

Das System muss Kombinationen aus unbefristeten und befristeten Anteilen unterstützen.

---

# 5. Monatliche Projektion (3-Jahres-Zeitraum)

Die Budget- und Kapazitätsdarstellung erfolgt monatlich über mindestens drei Jahre.

Für jeden Monat werden berechnet:

* verfügbare strukturelle Kapazität
* vertraglich gebundene Kapazität
* aktueller Beschäftigungsumfang
* freie Kapazität
* freie Kapazität nach Sicherheitsabschlag

Die Berechnung erfolgt zeitbasiert anhand der jeweiligen Gültigkeitszeiträume.

---

# 6. Regelverletzungen

Das System muss folgende Verstöße erkennen:

* Unterwertige Besetzung (z. B. A15-Stelle mit A14)
* Falsche Beschäftigungsart (Beamter auf Tarifanteil)
* Überschreitung struktureller Kapazität

Regelverletzungen führen zu Kennzeichnungen, nicht zu automatischer Korrektur.

---

# 7. Keine doppelte Speicherung

Kapazitäts- und Budgetwerte werden nicht persistent gespeichert, sondern berechnet.

Persistiert werden ausschließlich:

* Strukturelle Daten
* Vertragsdaten
* Zeiträume

---

# 8. Zielbild

Diese Regelung stellt sicher:

* Transparente und dynamische Budgetdarstellung
* Konsistente Trennung von Struktur und Berechnung
* Unterstützung befristeter und unbefristeter Optionen
* Einhaltung der Stellen- und Haushaltslogik
* Skalierbarkeit für zukünftige Finanzintegration

Die Budget- und Kapazitätsprojektion ist projektweit verbindlich definiert.
