package de.cocondo.app.system.core.security.permission;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/system/permissions")
public class PermissionAdminController {

    private final PermissionAdminService permissionAdminService;

    @PostMapping
    public PermissionDTO createPermission(@RequestParam String scope, @RequestParam String name) {
        return permissionAdminService.createPermission(scope, name);
    }

    @GetMapping("/{id}")
    public PermissionDTO getPermissionById(@PathVariable String id) {
        return permissionAdminService.getPermissionById(id);
    }

    @GetMapping
    public List<PermissionDTO> getAllPermissions() {
        return permissionAdminService.getAllPermissions();
    }

    @DeleteMapping("/{id}")
    public void deletePermission(@PathVariable String id) {
        permissionAdminService.deletePermission(id);
    }
}
