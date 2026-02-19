# PERS Sprint 2 – Planung (PERS_Sprint2_1_Planung)

## Status
In Arbeit (Sprint 2)

## Ziel
Sprint 2 liefert einen **vollständigen, architekturkonformen Durchstich** für das Aggregat **Person** inkl. aller technischen Querschnittsanforderungen gemäß ADRs:

- **ADR 004** Layerarchitektur (Controller → Domain Service → Entity Service → Repository)
- **ADR 005** Metadaten (separate Metadata-Controller, keine Tags/KeyValues über fachliche DTOs)
- **ADR 006** ID & Audit (DomainEntity + Listener, keine ID-Generierung im Domain Service)
- **ADR 007** Error- und Paging-Standards (Pageable + Page<T>, zentrale Fehlerbehandlung)
- **ADR 009** Command Permissions (Annotation auf Domain-Service Methoden, Durchsetzung zentral)
- **ADR 010** Record-Level Security (RLS auf Domain-Service Ebene, über UserContext + Specifications)

Sprint 2 endet mit einem Stand, in dem das Projekt **baubar** ist und Person als Referenzaggregate für weitere Umsetzungen dient.

---

## Scope (verbindlich)

### 1) Person – Fachliche Use Cases (vollständig)
1. Person erstellen
2. Person per ID lesen
3. Personen suchen/listen inkl. **Pagination, Sortierung, Filter**
4. Person bearbeiten (Update)
5. Person löschen:
    - **Hard Delete**, wenn keine referenzierenden Datensätze existieren
    - **Anonymisierung**, wenn referenziert (aktueller Export: Referenz über `PositionFilling.person`)

### 2) Querschnitt – Architektur/Technik (für Person umgesetzt)
- DomainEntity-Ableitung inkl. Audit/Metadaten
- Metadatenzugriff getrennt (Metadata-Controller)
- Command Permissions:
    - technische Annotation (system package)
    - zentrale Durchsetzung (AOP/Method-Security)
    - Person-Permissions als stabile Strings
- Record-Level Security:
    - `UserContext` Interface (system package)
    - RLS-Hook im Person Domain-Service (Specification-Erweiterung)
    - Hinweis: Für Person sind im aktuellen Modell keine RLS-relevanten Felder vorhanden; Implementierung stellt daher zunächst einen technischen Mechanismus bereit (Default = keine Einschränkung), ohne fachliche Annahmen zu erfinden.
- Paging/Sorting nach ADR 007:
    - Query-Endpoint akzeptiert `page`, `size`, `sort`
    - Response ist `Page<PersonDTO>` (kein proprietäres Paging-Format)

---

## Deliverables (konkret)

### D1 – Person Aggregate: Layer-Implementierung
- Entity: `Person` wird zu `extends DomainEntity` migriert (ADR 005/006)
- Repository: `PersonRepository` erweitert um `JpaSpecificationExecutor<Person>`
- Entity Service: ergänzt um Query-/Delete-Operationen, rein domain-objektbasiert
- Domain Service: Use-Cases Create/Read/List/Update/Delete inkl. Anonymisierung
- Mapper: MapStruct bleibt, Update-Mapping wird ergänzt (Payload → bestehende Entity)
- REST Controller:
    - Fachcontroller für CRUD
    - Separater Metadata-Controller gemäß ADR 005

### D2 – Filter/Sort/Paging
- Suchendpoint mit optionalen Filterparametern (fachlich auf Person-Felder begrenzt)
- Umsetzung über Spring Data `Specification<Person>` + `Pageable`

### D3 – Delete + Anonymisierung
- Referenzprüfung über Repository auf referenzierende Entitäten
    - aktueller Export: `PositionFilling` referenziert `Person`
- Hard Delete nur ohne Referenzen
- Anonymisierung setzt personenbezogene Felder auf technische Werte/Null (Details in Implementierung festgelegt, ohne neue fachliche Attribute zu erfinden)
- Ergebnis ist deterministisch testbar (z. B. `existsByPersonId(...)`)

### D4 – Permissions (ADR 009)
- `@RequirePermission` Annotation im system package
- Enforcement über zentralen Interceptor/Aspect
- `UserContext` liefert Permissions (dev-Default: permissive)
- Domain-Service Methoden erhalten Permissions:
    - PERSONNEL_PERSON_CREATE
    - PERSONNEL_PERSON_READ
    - PERSONNEL_PERSON_UPDATE
    - PERSONNEL_PERSON_DELETE
    - PERSONNEL_PERSON_METADATA_READ/UPDATE (für Metadata-Endpoints)

### D5 – Record-Level Security (ADR 010)
- `UserContext` Interface (UserId, Roles, Permissions, Claims)
- Person-Query nutzt RLS-Hook via `Specification`-Kombination
- Hinweis: Ohne passende Felder in `Person` werden **keine** fachlichen Filter erfunden; Default-Spec bleibt neutral.

---

## Abnahmekriterien (Definition of Done)

### Funktional
- [ ] POST Person erstellt Person, ID wird durch Infrastruktur erzeugt (nicht im Domain Service)
- [ ] GET Person/{id} liefert DTO oder 400/404 gemäß zentraler Fehlerbehandlung
- [ ] GET Persons liefert `Page<PersonDTO>` mit `page/size/sort`
- [ ] Filter wirken deterministisch (nur vorhandene Person-Felder)
- [ ] PUT/PATCH Update aktualisiert Person
- [ ] DELETE:
    - [ ] ohne Referenzen: Person wird physisch gelöscht
    - [ ] mit Referenzen: Person wird anonymisiert (keine Löschung)
- [ ] Metadaten:
    - [ ] GET /{id}/metadata liefert `DomainEntityMetadataDTO`
    - [ ] PUT/PATCH /{id}/metadata aktualisiert ausschließlich Metadaten (Tags/KeyValues)

### Architektur/Qualität
- [ ] Layering strikt eingehalten (ADR 004)
- [ ] DTOs enthalten keine Metadatenfelder (ADR 005)
- [ ] Keine ID-Generierung im Domain Service (ADR 006)
- [ ] Paging Response ist Spring `Page<T>` (ADR 007)
- [ ] Permissions via Annotation auf Domain-Service Methoden (ADR 009)
- [ ] RLS Hook im Domain Service vorhanden (ADR 010)
- [ ] Projekt ist baubar

---

## Implementierungsreihenfolge (deterministisch)

1. **Entity-Migration Person → DomainEntity**
    - Person erweitert `DomainEntity`
    - Entfernen eigener `@Id`-Felddefinition in `Person`
    - Mapper/DTOs bleiben stabil

2. **Repositories**
    - PersonRepository: `JpaSpecificationExecutor`
    - Referenzprüfung: Repository für referenzierende Entität (`PositionFilling`) mit `existsByPerson_Id(...)` / `existsByPersonId(...)` (abhängig vom Modell)

3. **Services**
    - PersonEntityService: load/save/delete/findAll(spec,pageable)
    - PersonDomainService: Create/Read/List/Update/Delete + Anonymize
    - RLS Hook: `Specification`-Kombination (neutral, solange keine RLS-Felder existieren)

4. **Permissions (system package)**
    - Annotation + Aspect + UserContext (dev-Default)
    - Permissions-Konstanten für Person
    - Domain-Service Methoden annotieren

5. **Controller**
    - CRUD Controller (REST)
    - Metadata Controller (separat)

6. **Self-Check**
    - Buildfähigkeit sicherstellen
    - Keine Annahmen über nicht vorhandene Artefakte

---

## Offene Punkte / Risiken (aus Baseline abgeleitet)
- Derzeit existieren keine REST-Controller für Person im Export → müssen neu erstellt werden.
- RLS kann für Person fachlich nicht wirksam werden, solange Person keine RLS-relevanten Attribute besitzt (z. B. OrgUnit-Bezug). Sprint 2 liefert daher die technische Infrastruktur + Hook ohne erfundene Fachregeln.
- Delete+Anonymisierung hängt von Referenzen ab. Im Export existiert mindestens `PositionFilling.person` als Referenz.
