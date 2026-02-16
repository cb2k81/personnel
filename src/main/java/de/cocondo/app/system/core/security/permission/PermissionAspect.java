package de.cocondo.app.system.core.security.permission;

import de.cocondo.app.system.core.context.RequestContextDataContainer;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class PermissionAspect {

    private static final Logger logger = LoggerFactory.getLogger(PermissionAspect.class);

    private final PrincipalPermissionCheckService principalPermissionCheckService;
    private final RequestContextDataContainer requestContextDataContainer;

    @Before("@annotation(permission)")
    public void checkPermission(Permit permission) {
        String permissionScope = permission.scope();
        String permissionName = permission.value();
        String principalName = requestContextDataContainer.getCurrentPrincipal().getName();

        boolean hasPermission = principalPermissionCheckService.checkPermission(permissionScope, permissionName);
        if (!hasPermission) {
            throw new PermissionDeniedException("Permission '" + permissionScope + "." + permissionName  + "' for principal '" + principalName + "'");
        } else {
            logger.info("Permission '" + permissionScope + "." + permissionName + "' granted for principal '" + principalName + "'");
        }
    }
}
