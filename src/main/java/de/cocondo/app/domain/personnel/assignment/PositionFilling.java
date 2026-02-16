package de.cocondo.app.domain.personnel.assignment;

import de.cocondo.app.domain.personnel.classification.CareerGroup;
import de.cocondo.app.domain.personnel.person.Person;
import de.cocondo.app.domain.personnel.staffing.PlannedPosition;
import de.cocondo.app.domain.personnel.staffing.PositionType;
import de.cocondo.app.system.entity.DomainEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * file: /opt/cocondo/personnel/src/main/java/de/cocondo/app/domain/personnel/assignment/PositionFilling.java
 *
 * Aggregate root representing the actual contractual filling of a planned position including deviations from plan. // Aggregat für die arbeitsvertragliche Besetzung einer Planstelle inkl. Abweichungen vom Plan
 */
@Entity
@Table(name = "positionfilling")
@Data
@EqualsAndHashCode(callSuper = true)
public class PositionFilling extends DomainEntity {

    @ManyToOne
    private PlannedPosition plannedPosition; // Referenz auf Planstelle

    @ManyToOne
    private Person person; // Person

    private LocalDate filledFrom; // Besetzt ab
    private LocalDate filledTo; // Besetzt bis

    private Integer contractualPortionPercent; // Arbeitsvertraglich gebundener Anteil

    private Integer currentEmploymentPortionPercent; // Aktueller Beschäftigungsumfang

    @Enumerated(EnumType.STRING)
    private PositionFillingType fillingType; // Typ der Besetzung

    @Enumerated(EnumType.STRING)
    private CareerGroup careerGroup; // Tatsächlich verwendete Laufbahngruppe (kann vom Plan abweichen)

    @Enumerated(EnumType.STRING)
    private PositionType employmentType; // Tatsächliche Besetzungsart (Beamter/Tarif) (kann vom Plan abweichen)

    private String description; // Beschreibung
}
