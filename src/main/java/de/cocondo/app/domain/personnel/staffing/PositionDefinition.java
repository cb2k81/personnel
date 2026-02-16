package de.cocondo.app.domain.personnel.staffing;

import de.cocondo.app.domain.personnel.classification.CareerGroup;
import de.cocondo.app.system.entity.DomainEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * file: /opt/cocondo/personnel/src/main/java/de/cocondo/app/domain/personnel/staffing/PositionDefinition.java
 *
 * Entity representing the persistent definition of a position independent from a staffing plan. // Entität für die dauerhafte Definition einer Stelle unabhängig vom Stellenplan
 */
@Entity
@Table(name = "positiondefinition")
@Data
@EqualsAndHashCode(callSuper = true)
public class PositionDefinition extends DomainEntity {

    private String name; // Stellenbezeichnung (allgemein gültig)

    @Enumerated(EnumType.STRING)
    private PositionType positionType; // Stellenart (Beamtenstelle / Tarifstelle)

    @Enumerated(EnumType.STRING)
    private CareerGroup careerGroup; // Laufbahngruppe

    private String description; // Beschreibung
}
