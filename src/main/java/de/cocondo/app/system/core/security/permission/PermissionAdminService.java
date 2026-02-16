package de.cocondo.app.system.core.security.permission;

import de.cocondo.app.system.core.config.permission.AccessSecurityPermissionSet;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PermissionAdminService {

    private final PermissionCrudService permissionCrudService;

    @Permit(scope = AccessSecurityPermissionSet.SCOPE, value = AccessSecurityPermissionSet.PERMISSION_ASSIGNMENT)
    public PermissionDTO createPermission(String scope, String name) {
        Permission permission = permissionCrudService.create(scope, name);
        return new PermissionDTO(permission.getId(), permission.getScope(), permission.getName());
    }

    public PermissionDTO getPermissionById(String id) {
        Permission permission = permissionCrudService.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Permission not found with ID: " + id));
        return new PermissionDTO(permission.getId(), permission.getScope(), permission.getName());
    }

    public List<PermissionDTO> getAllPermissions() {
        return permissionCrudService.findAll().stream()
                .map(permission -> new PermissionDTO(permission.getId(), permission.getScope(), permission.getName()))
                .collect(Collectors.toList());
    }

    @Permit(scope = AccessSecurityPermissionSet.SCOPE, value = AccessSecurityPermissionSet.PERMISSION_ASSIGNMENT)
    public void deletePermission(String id) {
        permissionCrudService.delete(id);
    }
}
