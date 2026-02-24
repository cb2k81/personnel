package de.cocondo.app.domain.personnel.staffing;

import lombok.Getter;
import lombok.Setter;
import de.cocondo.app.system.entity.DomainEntity;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Entity innerhalb des Aggregats StaffingPlanSet: StaffingPlan (Planvariante)
 */
@Entity
@Getter
@Setter
@Table(name = "staffing_plan")
public class StaffingPlan extends DomainEntity {

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private StaffingPlanSet staffingPlanSet;

    @Column(nullable = false)
    private Integer versionNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PlanVariantType planVariantType;

    /**
     * Einheitlicher Workflow-Status (Sprint 1 Zielbild).
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WorkflowStatus workflowStatus;

    @Column(nullable = false)
    private LocalDate validFrom;

    private LocalDate validTo;

    /**
     * Sicherheitsabschlag planweit (z.B. 0.10 für 10%).
     * Faktor statt Prozent vermeidet Missverständnisse (0..1).
     */
    @Column(nullable = false, precision = 6, scale = 4)
    private BigDecimal safetyDeductionFactor;

    @OneToMany(
            mappedBy = "staffingPlan",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.EAGER
    )
    @OrderBy("validFrom ASC")
    private List<PlannedPost> plannedPosts = new ArrayList<>();
}
