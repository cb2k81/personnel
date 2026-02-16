package de.cocondo.app.domain.personnel.staffing;

import de.cocondo.app.system.entity.DomainEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * file: /opt/cocondo/personnel/src/main/java/de/cocondo/app/domain/personnel/staffing/StaffingPlan.java
 *
 * Entity representing a concrete staffing plan version within a planning set. // Entität für eine konkrete Stellenplan-Variante innerhalb eines Planungsrahmens
 */
@Entity
@Table(name = "staffingplan")
@Data
@EqualsAndHashCode(callSuper = true)
public class StaffingPlan extends DomainEntity {

    @ManyToOne
    private StaffingPlanSet staffingPlanSet; // Planungsrahmen

    private String name; // Bezeichnung des Stellenplans

    private Integer versionNumber; // Versionsnummer

    private String state; // Status (DRAFT / APPROVED / ACTIVE / ARCHIVED)

    private String planType; // Typ (REAL / SIMULATION)

    private LocalDate validFrom; // Gültig ab
    private LocalDate validTo; // Gültig bis

    private String description; // Beschreibung
}
