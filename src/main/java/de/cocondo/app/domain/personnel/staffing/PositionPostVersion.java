package de.cocondo.app.domain.personnel.staffing;

import de.cocondo.app.domain.personnel.organisation.organisationunit.OrganisationUnit;
import de.cocondo.app.system.entity.DomainEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * Version Entity: PositionPostVersion
 *
 * Budget-Referenz MVP: nur als String (oberflächlich, keine tiefe Budget-Domäne im Sprint 1).
 */
@Entity
@Data
@Table(name = "position_post_version")
@EqualsAndHashCode(callSuper = true)
public class PositionPostVersion extends DomainEntity {

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private PositionPost positionPost;

    /**
     * Root-Level Link (Variante A):
     * In der Planung wird auf OrganisationUnit (Root) verknüpft.
     * Die gültige Version ergibt sich aus Zeitüberlappung.
     */
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private OrganisationUnit organisationUnit;

    @Column(nullable = false)
    private String name;

    private String careerGroup;
    private String grade;

    /**
     * MVP Budget-Bezug: "oberflächlich" als Referenzstring.
     * (keine inhaltliche Budget-Modellierung im Sprint 1)
     */
    private String budgetReference;

    @Column(nullable = false)
    private LocalDate validFrom;

    private LocalDate validTo;
}
