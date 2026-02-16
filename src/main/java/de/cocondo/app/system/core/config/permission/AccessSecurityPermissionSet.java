package de.cocondo.app.system.core.config.permission;

import de.cocondo.app.system.core.security.permission.PermissionSet;


@PermissionSet(
        scope = AccessSecurityPermissionSet.SCOPE,
        permissions = {
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
        }
)
public class AccessSecurityPermissionSet {

    public static final String SCOPE = "system.access";

    public static final String PRINCIPAL_CREATE = "PRINCIPAL_CREATE";
    public static final String PRINCIPAL_READ = "PRINCIPAL_READ";
    public static final String PRINCIPAL_EDIT = "PRINCIPAL_EDIT";
    public static final String PRINCIPAL_DELETE = "PRINCIPAL_DELETE";
    public static final String ROLE_READ = "ROLE_READ";
    public static final String ROLE_CREATE = "ROLE_CREATE";
    public static final String ROLE_EDIT = "ROLE_EDIT";
    public static final String ROLE_DELETE = "ROLE_DELETE";
    public static final String PERMISSION_ASSIGNMENT = "PERMISSION_ASSIGNMENT";
    public static final String GENERATE_PERMANENT_TOKEN = "GENERATE_PERMANENT_TOKEN";

}
