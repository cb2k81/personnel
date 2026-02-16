package de.cocondo.app.system.core.security.permission;

import de.cocondo.app.system.core.context.RequestContextDataContainer;
import de.cocondo.app.system.core.security.principal.Principal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PrincipalPermissionCheckService {

    private static final Logger logger = LoggerFactory.getLogger(PrincipalPermissionCheckService.class);

    private final RequestContextDataContainer requestContextDataContainer;
    private final RolePermissionCheckService rolePermissionCheckService;

    public PrincipalPermissionCheckService(RequestContextDataContainer requestContextDataContainer, RolePermissionCheckService rolePermissionCheckService) {
        this.requestContextDataContainer = requestContextDataContainer;
        this.rolePermissionCheckService = rolePermissionCheckService;
    }

    public Boolean checkPermission(String permissionScope, String permissionName) {
        Principal principal = this.requestContextDataContainer.getCurrentPrincipal();
        return checkPermission(principal, permissionScope, permissionName);
    }

    public Boolean checkPermission(Principal principal, String permissionScope, String permissionName) {
        List<String> roleNames = principal.getRoles().stream()
                .map(role -> role.getName())
                .collect(Collectors.toList());
        for (String roleName : roleNames) {
            if (rolePermissionCheckService.hasPermission(roleName, permissionScope, permissionName)) {
                logger.debug("Permission '{}' granted for principal '{}' by role '{}'", permissionName, principal.getName(), roleName);
                return true;
            }
        }
        logger.debug("Permission '{}' not granted for principal '{}'", permissionName, principal.getName());
        return false;
    }
}
