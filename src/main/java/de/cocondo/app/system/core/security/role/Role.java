package de.cocondo.app.system.core.security.role;

import de.cocondo.app.system.core.security.permission.Permission;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.checkerframework.common.aliasing.qual.Unique;

import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@EqualsAndHashCode(of = "id")
public class Role {
    @Id
    @Column(columnDefinition = "VARCHAR(36)")
    private String id;

    @Column(unique = true)
    private String name;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "role_permission",
            joinColumns = @JoinColumn(name = "role_id"),
            inverseJoinColumns = @JoinColumn(name = "permission_id")
    )
    private Set<Permission> permissions = new HashSet<>();
}
