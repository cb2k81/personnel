package de.cocondo.app.system.core.security.role;

import de.cocondo.app.system.core.security.permission.PermissionDTO;
import de.cocondo.app.system.core.security.permission.PermissionNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/system/roles")
public class RoleAdminController {

    private final RoleAdminApiService roleAdminService;

    @PostMapping
    public ResponseEntity<RoleAggregate> createRole(@RequestParam String name) {
        RoleAggregate role = roleAdminService.createRole(name);
        return ResponseEntity.ok(role);
    }

    @GetMapping
    public ResponseEntity<List<RoleAggregate>> getAllRoles() {
        List<RoleAggregate> roles = roleAdminService.getAllRoles();
        return ResponseEntity.ok(roles);
    }

    @GetMapping("/with-permissions")
    public ResponseEntity<List<RoleAggregate>> getAllRolesWithPermissions() {
        List<RoleAggregate> roles = roleAdminService.getAllRolesWithPermissions();
        return ResponseEntity.ok(roles);
    }


    @GetMapping("/{id}")
    public ResponseEntity<RoleAggregate> getRoleById(@PathVariable String id) {
        Optional<RoleAggregate> role = roleAdminService.getRoleById(id);
        return role.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRole(@PathVariable String id) {
        roleAdminService.deleteRole(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/permissions")
    public ResponseEntity<Void> addPermissionsToRole(@PathVariable String id, @RequestBody List<PermissionDTO> permissionDTOs) {
        try {
            roleAdminService.addPermissionsToRole(id, permissionDTOs);
            return ResponseEntity.noContent().build();
        } catch (PermissionNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}/permissions")
    public ResponseEntity<Void> removePermissionsFromRole(@PathVariable String id, @RequestBody List<PermissionDTO> permissionDTOs) {
        try {
            roleAdminService.removePermissionsFromRole(id, permissionDTOs);
            return ResponseEntity.noContent().build();
        } catch (PermissionNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
