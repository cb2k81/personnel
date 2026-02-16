package de.cocondo.app.system.core.security.permission;

import de.cocondo.app.system.core.security.role.Role;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PermissionRepository extends CrudRepository<Permission, String> {

    @Query("SELECT p FROM Permission p WHERE p.scope = :scope AND p.name = :name")
    Optional<Permission> findByScopeAndName(String scope, String name);

    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN TRUE ELSE FALSE END FROM Role r JOIN r.permissions p WHERE r = :role AND p.scope = :permissionScope AND p.name = :permissionName")
    boolean hasPermission(Role role, String permissionScope, String permissionName);


}
