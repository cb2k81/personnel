package de.cocondo.app.system.core.security.role;

import java.util.HashMap;
import java.util.Map;

/**
 * Interface for defining roles and their associated permissions.
 */
public interface RoleDefinition {

    /**
     * Defines the roles and their associated permissions.
     *
     * @return A map containing roles as keys and their permissions as inner maps.
     */
    Map<String, Map<String, String[]>> getRoles();

    default void addPermission(String roleName, String scope, String... permissionNames) {
        Map<String, Map<String, String[]>> roles = getRoles();

        roles.putIfAbsent(roleName, new HashMap<>());

        Map<String, String[]> rolePermissions = roles.get(roleName);
        rolePermissions.putIfAbsent(scope, permissionNames);

    }

}
