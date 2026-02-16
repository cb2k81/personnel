package de.cocondo.app.domain.personnel.staffing;

import de.cocondo.app.domain.personnel.budget.BudgetRevision;
import de.cocondo.app.system.entity.DomainEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * file: /opt/cocondo/personnel/src/main/java/de/cocondo/app/domain/personnel/staffing/StaffingPlanSet.java
 *
 * Entity representing a planning context for a specific budget revision containing alternative staffing plans. // Entität für einen Planungsrahmen zu einem Haushaltsänderungsstand mit mehreren Planvarianten
 */
@Entity
@Table(name = "staffingplanset")
@Data
@EqualsAndHashCode(callSuper = true)
public class StaffingPlanSet extends DomainEntity {

    @ManyToOne
    private BudgetRevision budgetRevision; // Zugehöriger Haushaltsänderungsstand

    private String name; // Bezeichnung des Planungsrahmens

    private String description; // Beschreibung
}
