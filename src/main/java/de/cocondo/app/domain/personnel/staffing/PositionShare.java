package de.cocondo.app.domain.personnel.staffing;

import de.cocondo.app.system.entity.DomainEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * file: /opt/cocondo/personnel/src/main/java/de/cocondo/app/domain/personnel/staffing/PositionShare.java
 *
 * Entity representing a splittable share of a planned position. // Entität für einen teilbaren Stellenanteil einer Planstelle
 */
@Entity
@Table(name = "positionshare")
@Data
@EqualsAndHashCode(callSuper = true)
public class PositionShare extends DomainEntity {

    @ManyToOne
    private PlannedPosition plannedPosition; // Planstelle

    private Integer sharePortionPercent; // Stellenanteil in Prozent

    private Integer safetyDiscountPercent; // Sicherheitsabschlag in Prozent

    private LocalDate validFrom; // Gültig ab
    private LocalDate validTo; // Gültig bis

    private String description; // Beschreibung
}
