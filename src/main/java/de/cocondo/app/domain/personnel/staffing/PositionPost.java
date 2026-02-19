package de.cocondo.app.domain.personnel.staffing;

import de.cocondo.app.system.entity.DomainEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

/**
 * Aggregate Root: PositionPost (Stellen-Definition über Planperioden hinweg)
 *
 * Root trägt nur die fachliche Identität (BusinessKey).
 * Fachliche Änderungen erfolgen über PositionPostVersion.
 */
@Entity
@Data
@Table(
        name = "position_post",
        uniqueConstraints = @UniqueConstraint(columnNames = {"post_business_key"})
)
@EqualsAndHashCode(callSuper = true)
public class PositionPost extends DomainEntity {

    @Column(name = "post_business_key", nullable = false, updatable = false)
    private String postBusinessKey;

    @OneToMany(
            mappedBy = "positionPost",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.EAGER
    )
    @OrderBy("validFrom ASC")
    private List<PositionPostVersion> versions = new ArrayList<>();
}
