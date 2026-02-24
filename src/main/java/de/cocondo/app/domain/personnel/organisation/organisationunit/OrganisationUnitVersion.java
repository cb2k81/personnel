package de.cocondo.app.domain.personnel.organisation.organisationunit;

import lombok.Getter;
import lombok.Setter;
import de.cocondo.app.system.entity.DomainEntity;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

/**
 * Version Entity: OrganisationUnitVersion
 *
 * Nicht direkt adressierbar als Aggregate Root (kein Repository).
 * Zeitliche Gültigkeit wird über validFrom/validTo abgebildet.
 */
@Entity
@Getter
@Setter
@Table(name = "organisationunit_version")
public class OrganisationUnitVersion extends DomainEntity {

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private OrganisationUnit organisationUnit;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private LocalDate validFrom;

    private LocalDate validTo;
}
