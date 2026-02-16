package de.cocondo.app.system.core.config.permission;

import de.cocondo.app.system.core.security.role.RoleDefinition;
import de.cocondo.app.system.core.security.role.Roles;

import java.util.HashMap;
import java.util.Map;

@Roles
public class SystemRoles implements RoleDefinition {

    private final Map<String, Map<String, String[]>> rolePermissions = new HashMap<>();

    private SystemRoles() {

        addPermission(ROLE_SYSADMIN, SystemPermissions.SCOPE,
                SystemPermissions.ROLE_EDIT,
                SystemPermissions.PRINCIPAL_ADMIN
        );

        addPermission(ROLE_SYSADMIN, AccessSecurityPermissionSet.SCOPE,
                AccessSecurityPermissionSet.PRINCIPAL_CREATE,
                AccessSecurityPermissionSet.PRINCIPAL_READ,
                AccessSecurityPermissionSet.PRINCIPAL_EDIT,
                AccessSecurityPermissionSet.PRINCIPAL_DELETE,
                AccessSecurityPermissionSet.ROLE_READ,
                AccessSecurityPermissionSet.ROLE_CREATE,
                AccessSecurityPermissionSet.ROLE_EDIT,
                AccessSecurityPermissionSet.ROLE_DELETE,
                AccessSecurityPermissionSet.PERMISSION_ASSIGNMENT,
                AccessSecurityPermissionSet.GENERATE_PERMANENT_TOKEN
        );
    }

    public static final String ROLE_SYSADMIN = "SYSADMIN";

    @Override
    public Map<String, Map<String, String[]>> getRoles() {
        return rolePermissions;
    }

}
