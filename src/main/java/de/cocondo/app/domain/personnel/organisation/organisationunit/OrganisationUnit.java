package de.cocondo.app.domain.personnel.organisation.organisationunit;

import de.cocondo.app.system.entity.DomainEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * file: /opt/cocondo/personnel/src/main/java/de/cocondo/app/domain/personnel/organisation/organisationunit/OrganisationUnit.java
 *
 * Entity representing an organizational unit in which positions are planned and filled. // Entität für eine Organisationseinheit, der Planstellen zugeordnet sind
 */
@Entity
@Data
@Table(name = "organisationunit")
@EqualsAndHashCode(callSuper = true)
public class OrganisationUnit extends DomainEntity {

    private String name; // Name

    private String state; // Status (z. B. stillgelegt) – spätere Enum // Status

    @ManyToOne
    private OrganisationUnit parent; // Übergeordnete Organisationseinheit
}
