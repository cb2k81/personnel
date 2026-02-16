package de.cocondo.app.system.core.security.role;

import java.util.HashMap;
import java.util.Map;

/**
 * Abstract class for defining roles and their associated permissions.
 */


public abstract class RolePermissionConfig implements RoleDefinition {

    private final Map<String, Map<String, String[]>> rolePermissions = new HashMap<>();

    /**
     * Initializes the role permissions map with default values if needed.
     */
    protected RolePermissionConfig() {
        initializeRoles();
    }

    /**
     * Subclasses should override this method to initialize roles and permissions.
     */
    protected abstract void initializeRoles();

    @Override
    public Map<String, Map<String, String[]>> getRoles() {
        return rolePermissions;
    }

    @Override
    public void addPermission(String roleName, String scope, String... permissionNames) {
        rolePermissions.putIfAbsent(roleName, new HashMap<>());
        Map<String, String[]> rolePermissionsMap = rolePermissions.get(roleName);
        rolePermissionsMap.putIfAbsent(scope, permissionNames);
    }
}
