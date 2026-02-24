package de.cocondo.app.domain.personnel.staffing;

import lombok.Getter;
import lombok.Setter;
import de.cocondo.app.domain.personnel.organisation.organisationunit.OrganisationUnit;
import de.cocondo.app.system.entity.DomainEntity;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Entity innerhalb des Aggregats StaffingPlanSet:
 * PlannedPost = geplante Stelle (Instanz einer PositionPost in einem konkreten StaffingPlan).
 */
@Entity
@Getter
@Setter
@Table(name = "planned_post")
public class PlannedPost extends DomainEntity {

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private StaffingPlan staffingPlan;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private PositionPost positionPost;

    /**
     * Planbezogene Organisationszuordnung (Variante A: Root-Link).
     * Die gültige Organisationsversion ergibt sich aus Zeitüberlappung.
     */
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private OrganisationUnit plannedOrganisationUnit;

    @Column(nullable = false)
    private LocalDate validFrom;

    private LocalDate validTo;

    @OneToMany(
            mappedBy = "plannedPost",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.EAGER
    )
    @OrderBy("validFrom ASC")
    private List<PlannedShare> plannedShares = new ArrayList<>();
}
