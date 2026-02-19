package de.cocondo.app.domain.personnel.staffing;

import de.cocondo.app.system.entity.DomainEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Entity innerhalb des Aggregats StaffingPlanSet:
 * PlannedShare = geplanter Anteil einer PlannedPost.
 */
@Entity
@Table(name = "planned_share")
@Data
@EqualsAndHashCode(callSuper = true)
public class PlannedShare extends DomainEntity {

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private PlannedPost plannedPost;

    /**
     * Anteil in Prozent (0..100).
     */
    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal portionPercent;

    /**
     * Kennzeichen, ob der Share als "permanent" geführt wird (unbefristet).
     */
    @Column(nullable = false)
    private Boolean isPermanent = Boolean.FALSE;

    /**
     * Sicherheitsabschlag als Faktor (z.B. 0.10 für 10%).
     * Faktor statt Prozent vermeidet Missverständnisse (0..1).
     */
    @Column(nullable = false, precision = 6, scale = 4)
    private BigDecimal safetyDeductionFactor;

    @Column(nullable = false)
    private LocalDate validFrom;

    private LocalDate validTo;
}
