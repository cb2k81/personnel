package de.cocondo.app.system.entity.metadata;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.cocondo.app.system.entity.DomainEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * KeyValuePair entity used for dynamic metadata on DomainEntity.
 *
 * Design decisions:
 * - Unique constraint on (entity_id, key_name)
 * - Explicit column names to avoid SQL reserved keywords
 * - 'value' column renamed to 'kv_value' to avoid H2 and SQL conflicts
 */
@Data
@Entity
@Table(
        name = "key_value_pair",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"entity_id", "key_name"})
        }
)
@EqualsAndHashCode
public class KeyValuePair {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "entity_id", nullable = false)
    private String entityId;

    @Column(name = "key_name", nullable = false)
    private String key;

    /**
     * Renamed column to avoid SQL reserved keyword conflicts.
     */
    @Column(name = "kv_value")
    private String value;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "domain_entity_id")
    @EqualsAndHashCode.Exclude
    @JsonIgnore
    private DomainEntity domainEntity;
}
