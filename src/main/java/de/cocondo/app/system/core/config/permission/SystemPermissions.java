package de.cocondo.app.system.core.config.permission;

import de.cocondo.app.system.core.security.permission.PermissionSet;

@PermissionSet(
        scope = SystemPermissions.SCOPE,
        permissions = {
                SystemPermissions.ROLE_EDIT,
                SystemPermissions.PRINCIPAL_ADMIN
        }
)
public class SystemPermissions {

    public static final String SCOPE = "system";

    public static final String ROLE_EDIT = "ROLE_ADMIN";
    public static final String PRINCIPAL_ADMIN = "PRINCIPAL_ADMIN";

}
