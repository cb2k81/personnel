package de.cocondo.app.system.core.security.role;

import de.cocondo.app.system.core.config.permission.AccessSecurityPermissionSet;
import de.cocondo.app.system.core.security.permission.Permission;
import de.cocondo.app.system.core.security.permission.PermissionCrudService;
import de.cocondo.app.system.core.security.permission.PermissionDTO;
import de.cocondo.app.system.core.security.permission.Permit;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoleAdminApiService {

    private static final Logger logger = LoggerFactory.getLogger(RoleAdminApiService.class);

    private final RoleCrudService roleCrudService;
    private final PermissionCrudService permissionCrudService;

    @Permit(scope = AccessSecurityPermissionSet.SCOPE, value = AccessSecurityPermissionSet.ROLE_CREATE)
    public RoleAggregate createRole(String name) {
        logger.info("Creating role with name: {}", name);
        Role role = new Role();
        role.setName(name);
        roleCrudService.create(role);
        logger.info("Role created with ID: {}", role.getId());
        return new RoleAggregate(role.getId(), role.getName());
    }

    @Permit(scope = AccessSecurityPermissionSet.SCOPE, value = AccessSecurityPermissionSet.ROLE_READ)
    public Optional<RoleAggregate> getRoleById(String id) {
        logger.info("Fetching role with ID: {}", id);
        Optional<RoleAggregate> role = roleCrudService.findById(id).map(this::mapToAggregate);
        if (role.isPresent()) {
            logger.info("Role found with ID: {}", id);
        } else {
            logger.warn("Role not found with ID: {}", id);
        }
        return role;
    }

    @Permit(scope = AccessSecurityPermissionSet.SCOPE, value = AccessSecurityPermissionSet.ROLE_READ)
    public List<RoleAggregate> getAllRoles() {
        logger.info("Fetching all roles");
        List<RoleAggregate> roles = roleCrudService.findAll().stream()
                .map(role -> new RoleAggregate(role.getId(), role.getName()))
                .collect(Collectors.toList());
        logger.info("Fetched {} roles", roles.size());
        return roles;
    }

    @Permit(scope = AccessSecurityPermissionSet.SCOPE, value = AccessSecurityPermissionSet.ROLE_READ)
    public List<RoleAggregate> getAllRolesWithPermissions() {
        logger.info("Fetching all roles with permissions");
        List<RoleAggregate> roles = roleCrudService.findAll().stream()
                .map(this::mapToAggregate)
                .collect(Collectors.toList());
        logger.info("Fetched {} roles with permissions", roles.size());
        return roles;
    }

    @Permit(scope = AccessSecurityPermissionSet.SCOPE, value = AccessSecurityPermissionSet.ROLE_DELETE)
    public void deleteRole(String id) {
        logger.info("Deleting role with ID: {}", id);
        roleCrudService.delete(id);
        logger.info("Role with ID: {} deleted", id);
    }

    @Permit(scope = AccessSecurityPermissionSet.SCOPE, value = AccessSecurityPermissionSet.ROLE_EDIT)
    public void addPermissionToRole(String roleId, PermissionDTO permissionDTO) {
        logger.info("Adding permission with ID: {} to role with ID: {}", permissionDTO.getId(), roleId);
        Role role = roleCrudService.findById(roleId)
                .orElseThrow(() -> {
                    logger.error("Role not found with ID: {}", roleId);
                    return new EntityNotFoundException("Role not found with ID: " + roleId);
                });
        Permission permission = permissionCrudService.findById(permissionDTO.getId())
                .orElseThrow(() -> {
                    logger.error("Permission not found with ID: {}", permissionDTO.getId());
                    return new EntityNotFoundException("Permission not found with ID: " + permissionDTO.getId());
                });

        role.getPermissions().add(permission);
        roleCrudService.update(role);
        logger.info("Added permission with ID: {} to role with ID: {}", permissionDTO.getId(), roleId);
    }

    @Permit(scope = AccessSecurityPermissionSet.SCOPE, value = AccessSecurityPermissionSet.ROLE_EDIT)
    public void removePermissionFromRole(String roleId, PermissionDTO permissionDTO) {
        logger.info("Removing permission with ID: {} from role with ID: {}", permissionDTO.getId(), roleId);
        Role role = roleCrudService.findById(roleId)
                .orElseThrow(() -> {
                    logger.error("Role not found with ID: {}", roleId);
                    return new EntityNotFoundException("Role not found with ID: " + roleId);
                });
        Permission permission = permissionCrudService.findById(permissionDTO.getId())
                .orElseThrow(() -> {
                    logger.error("Permission not found with ID: {}", permissionDTO.getId());
                    return new EntityNotFoundException("Permission not found with ID: " + permissionDTO.getId());
                });

        role.getPermissions().remove(permission);
        roleCrudService.update(role);
        logger.info("Removed permission with ID: {} from role with ID: {}", permissionDTO.getId(), roleId);
    }

    @Permit(scope = AccessSecurityPermissionSet.SCOPE, value = AccessSecurityPermissionSet.ROLE_EDIT)
    public void addPermissionsToRole(String roleId, List<PermissionDTO> permissionDTOs) {
        logger.info("Adding multiple permissions to role with ID: {}", roleId);
        Role role = roleCrudService.findById(roleId)
                .orElseThrow(() -> {
                    logger.error("Role not found with ID: {}", roleId);
                    return new EntityNotFoundException("Role not found with ID: " + roleId);
                });

        List<Permission> permissions = new ArrayList<>();
        for (PermissionDTO permissionDTO : permissionDTOs) {
            Permission permission = permissionCrudService.findById(permissionDTO.getId())
                    .orElseThrow(() -> {
                        logger.error("Permission not found with ID: {}", permissionDTO.getId());
                        return new EntityNotFoundException("Permission not found with ID: " + permissionDTO.getId());
                    });
            permissions.add(permission);
        }

        role.getPermissions().addAll(permissions);
        roleCrudService.update(role);
        logger.info("Added {} permissions to role with ID: {}", permissions.size(), roleId);
    }

    @Permit(scope = AccessSecurityPermissionSet.SCOPE, value = AccessSecurityPermissionSet.ROLE_EDIT)
    public void removePermissionsFromRole(String roleId, List<PermissionDTO> permissionDTOs) {
        logger.info("Removing multiple permissions from role with ID: {}", roleId);
        Role role = roleCrudService.findById(roleId)
                .orElseThrow(() -> {
                    logger.error("Role not found with ID: {}", roleId);
                    return new EntityNotFoundException("Role not found with ID: " + roleId);
                });

        List<Permission> permissions = new ArrayList<>();
        for (PermissionDTO permissionDTO : permissionDTOs) {
            Permission permission = permissionCrudService.findById(permissionDTO.getId())
                    .orElseThrow(() -> {
                        logger.error("Permission not found with ID: {}", permissionDTO.getId());
                        return new EntityNotFoundException("Permission not found with ID: " + permissionDTO.getId());
                    });
            permissions.add(permission);
        }

        permissions.forEach(role.getPermissions()::remove);
        roleCrudService.update(role);
        logger.info("Removed {} permissions from role with ID: {}", permissions.size(), roleId);
    }

    private RoleAggregate mapToAggregate(Role role) {
        logger.info("Mapping role with ID: {} to RoleAggregate", role.getId());
        RoleAggregate aggregate = new RoleAggregate(role.getId(), role.getName());
        role.getPermissions().forEach(permission ->
                aggregate.addPermission(new PermissionDTO(permission.getId(), permission.getScope(), permission.getName()))
        );
        return aggregate;
    }
}
