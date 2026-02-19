package de.cocondo.app.domain.personnel.staffing;

import de.cocondo.app.system.entity.DomainEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

/**
 * Aggregate Root: StaffingPlanSet
 *
 * Enthält alternative Planvarianten (StaffingPlan).
 */
@Entity
@Table(name = "staffing_plan_set")
@Data
@EqualsAndHashCode(callSuper = true)
public class StaffingPlanSet extends DomainEntity {

    @OneToMany(
            mappedBy = "staffingPlanSet",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.EAGER
    )
    @OrderBy("versionNumber ASC, validFrom ASC")
    private List<StaffingPlan> staffingPlans = new ArrayList<>();
}
