package de.cocondo.app.domain.personnel.organisation.organisationunit;

import de.cocondo.app.system.entity.DomainEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

/**
 * Aggregate Root: OrganisationUnit
 *
 * Root trägt nur die fachliche Identität (BusinessKey).
 * Änderungen an fachlichen Daten erfolgen über Versionen (OrganisationUnitVersion).
 */
@Entity
@Data
@Table(
        name = "organisationunit",
        uniqueConstraints = @UniqueConstraint(columnNames = {"org_unit_business_key"})
)
@EqualsAndHashCode(callSuper = true)
public class OrganisationUnit extends DomainEntity {

    @Column(name = "org_unit_business_key", nullable = false, updatable = false)
    private String orgUnitBusinessKey;

    @OneToMany(
            mappedBy = "organisationUnit",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.EAGER
    )
    @OrderBy("validFrom ASC")
    private List<OrganisationUnitVersion> versions = new ArrayList<>();

}
