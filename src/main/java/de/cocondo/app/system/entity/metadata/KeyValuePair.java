package de.cocondo.app.system.entity.metadata;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.cocondo.app.system.entity.DomainEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = {"entity_id", "key_name"})
})
@EqualsAndHashCode
public class KeyValuePair {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "entity_id")
    private String entityId;

    @Column(name = "key_name")
    private String key;

    private String value;

    @ManyToOne(fetch = FetchType.LAZY)
    @EqualsAndHashCode.Exclude
    @JsonIgnore
    private DomainEntity domainEntity;

}
