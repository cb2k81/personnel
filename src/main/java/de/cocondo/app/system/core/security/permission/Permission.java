package de.cocondo.app.system.core.security.permission;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"scope", "name"}))
@Data
@EqualsAndHashCode(of = "id")
public class Permission {

    @Id
    @Column(columnDefinition = "VARCHAR(36)")
    private String id;

    private String scope;
    private String name;

}
