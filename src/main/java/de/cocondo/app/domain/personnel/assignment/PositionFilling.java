package de.cocondo.app.domain.personnel.assignment;

import de.cocondo.app.domain.personnel.person.Person;
import de.cocondo.app.domain.personnel.staffing.PlannedPost;
import de.cocondo.app.domain.personnel.staffing.PlannedShare;
import de.cocondo.app.system.entity.DomainEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Entity innerhalb des Aggregats StaffingAssignmentPlan:
 * PositionFilling = konkrete Besetzung (Ist) bezogen auf PlannedPost und optional PlannedShare.
 *
 * - Tarif: plannedShare gesetzt
 * - Beamte: plannedShare null, nur plannedPost
 */
@Entity
@Table(name = "position_filling")
@Data
@EqualsAndHashCode(callSuper = true)
public class PositionFilling extends DomainEntity {

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private StaffingAssignmentPlan staffingAssignmentPlan;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private PlannedPost plannedPost;

    @ManyToOne(optional = true, fetch = FetchType.LAZY)
    private PlannedShare plannedShare;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Person person;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PositionFillingType fillingType;

    /**
     * Besetzt ab / bis (Ist).
     */
    @Column(nullable = false)
    private LocalDate filledFrom;

    private LocalDate filledTo;

    /**
     * Vertragsanteil in Prozent (0..100).
     */
    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal contractualPortionPercent;

    /**
     * Optional: aktueller Beschäftigungsumfang in Prozent (0..100).
     */
    @Column(precision = 5, scale = 2)
    private BigDecimal currentEmploymentPercent;
}
