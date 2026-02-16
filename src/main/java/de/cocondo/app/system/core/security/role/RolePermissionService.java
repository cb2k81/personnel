package de.cocondo.app.system.core.security.role;

import de.cocondo.app.system.core.security.permission.Permission;
import de.cocondo.app.system.core.security.permission.PermissionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class RolePermissionService {

    private final RoleCrudService roleCrudService;
    private final PermissionRepository permissionRepository;

    public RolePermissionService(RoleCrudService roleCrudService, PermissionRepository permissionRepository) {
        this.roleCrudService = roleCrudService;
        this.permissionRepository = permissionRepository;
    }

    @Transactional
    public void addPermissionToRole(String roleId, String permissionScope, String permissionName) {
        Optional<Role> roleOptional = roleCrudService.findById(roleId);
        if (roleOptional.isPresent()) {
            Role role = roleOptional.get();
            Permission permission = permissionRepository.findByScopeAndName(permissionScope, permissionName)
                    .orElseThrow(() -> new RuntimeException("Permission not found: " + permissionName + " (" + permissionScope + ")"));
            role.getPermissions().add(permission);
            roleCrudService.update(role);
        } else {
            throw new RuntimeException("Role not found with ID: " + roleId);
        }
    }

    @Transactional
    public void removePermissionFromRole(String roleId, String permissionScope, String permissionName) {
        Optional<Role> roleOptional = roleCrudService.findById(roleId);
        if (roleOptional.isPresent()) {
            Role role = roleOptional.get();
            Permission permission = permissionRepository.findByScopeAndName(permissionScope, permissionName)
                    .orElseThrow(() -> new RuntimeException("Permission not found: " + permissionName + " (" + permissionScope + ")"));
            role.getPermissions().remove(permission);
            roleCrudService.update(role);
        } else {
            throw new RuntimeException("Role not found with ID: " + roleId);
        }
    }

    @Transactional(readOnly = true)
    public boolean hasPermission(String roleId, String permissionScope, String permissionName) {
        Optional<Role> roleOptional = roleCrudService.findById(roleId);
        if (roleOptional.isPresent()) {
            Role role = roleOptional.get();
            Permission permission = permissionRepository.findByScopeAndName(permissionScope, permissionName)
                    .orElseThrow(() -> new RuntimeException("Permission not found: " + permissionName + " (" + permissionScope + ")"));
            return role.getPermissions().contains(permission);
        } else {
            throw new RuntimeException("Role not found with ID: " + roleId);
        }
    }

}
