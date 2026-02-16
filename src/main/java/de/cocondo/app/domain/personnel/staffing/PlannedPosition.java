package de.cocondo.app.domain.personnel.staffing;

import de.cocondo.app.domain.personnel.budget.BudgetAccount;
import de.cocondo.app.domain.personnel.organisation.organisationunit.OrganisationUnit;
import de.cocondo.app.system.entity.DomainEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * file: /opt/cocondo/personnel/src/main/java/de/cocondo/app/domain/personnel/staffing/PlannedPosition.java
 *
 * Entity representing the occurrence of a position within a specific staffing plan. // Entität für das Auftreten einer Stelle in einem konkreten Stellenplan
 */
@Entity
@Table(name = "plannedposition")
@Data
@EqualsAndHashCode(callSuper = true)
public class PlannedPosition extends DomainEntity {

    @ManyToOne
    private StaffingPlan staffingPlan; // Zugehöriger Stellenplan

    @ManyToOne
    private PositionDefinition positionDefinition; // Referenz auf dauerhafte Stellendefinition

    @ManyToOne
    private BudgetAccount budgetAccount; // Haushaltsstelle

    @ManyToOne
    private OrganisationUnit organisationUnit; // Organisationseinheit

    private Integer availablePortionPercent; // Im Plan verfügbarer Stellenanteil in Prozent

    private LocalDate validFrom; // Gültig ab
    private LocalDate validTo; // Gültig bis

    private String description; // Beschreibung
}
