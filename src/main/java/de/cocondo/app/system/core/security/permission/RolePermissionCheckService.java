package de.cocondo.app.system.core.security.permission;

import de.cocondo.app.system.core.security.role.Role;
import de.cocondo.app.system.core.security.role.RoleNotFoundException;
import de.cocondo.app.system.core.security.role.RoleRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.stereotype.Service;

@Service
public class RolePermissionCheckService {

    private static final Logger logger = LoggerFactory.getLogger(RolePermissionCheckService.class);

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    public RolePermissionCheckService(RoleRepository roleRepository, PermissionRepository permissionRepository) {
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
    }

    public boolean hasPermission(String roleName, String permissionScope, String permissionName) {
        try {
            Role role = roleRepository.findByName(roleName)
                    .orElseThrow(() -> new RoleNotFoundException("Role with name " + roleName + " not found"));

            return hasPermission(role, permissionScope, permissionName);
        } catch (RoleNotFoundException e) {
            logger.error("RoleNotFoundException: {}", e.getMessage(), e);
            throw e;
        } catch (IncorrectResultSizeDataAccessException e) {
            logger.error("IncorrectResultSizeDataAccessException: Expected single result but got multiple for role '{}', scope '{}', permission '{}'", roleName, permissionScope, permissionName, e);
            throw e;
        } catch (Exception e) {
            logger.error("Exception occurred while checking permission for role '{}', scope '{}', permission '{}': {}", roleName, permissionScope, permissionName, e.getMessage(), e);
            throw new RuntimeException("Exception occurred while checking permission", e);
        }
    }

    @Transactional
    public boolean hasPermission(Role role, String permissionScope, String permissionName) {
        try {
            boolean hasPermission = permissionRepository.hasPermission(role, permissionScope, permissionName);
            if (hasPermission) {
                logger.debug("Permission '{}' found for role '{}'", permissionName, role.getName());
            } else {
                logger.debug("Permission '{}' not found for role '{}'", permissionName, role.getName());
            }
            return hasPermission;
        } catch (IncorrectResultSizeDataAccessException e) {
            logger.error("IncorrectResultSizeDataAccessException: Expected single result but got multiple for role '{}', scope '{}', permission '{}'", role.getName(), permissionScope, permissionName, e);
            throw e;
        } catch (Exception e) {
            logger.error("Exception occurred while checking permission for role '{}', scope '{}', permission '{}': {}", role.getName(), permissionScope, permissionName, e.getMessage(), e);
            throw new RuntimeException("Exception occurred while checking permission", e);
        }
    }
}
