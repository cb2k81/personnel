package de.cocondo.app.domain.personnel.assignment;

import lombok.Getter;
import lombok.Setter;
import de.cocondo.app.domain.personnel.staffing.StaffingPlan;
import de.cocondo.app.domain.personnel.staffing.WorkflowStatus;
import de.cocondo.app.system.entity.DomainEntity;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Aggregate Root: StaffingAssignmentPlan (Stellenbesetzungsplan)
 *
 * Ist-Plan (Besetzungen) – referenziert einen StaffingPlan (Soll) als Grundlage.
 */
@Entity
@Getter
@Setter
@Table(name = "staffing_assignment_plan")
public class StaffingAssignmentPlan extends DomainEntity {

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private StaffingPlan staffingPlan;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WorkflowStatus workflowStatus;

    @Column(nullable = false)
    private LocalDate validFrom;

    private LocalDate validTo;

    @OneToMany(
            mappedBy = "staffingAssignmentPlan",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.EAGER
    )

    private List<PositionFilling> positionFillings = new ArrayList<>();
}
