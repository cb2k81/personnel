package de.cocondo.app.system.core.security.role;

import de.cocondo.app.system.core.security.permission.PermissionDTO;

import java.util.HashSet;
import java.util.Set;

public class RoleAggregate {

    private final String id;
    private String name;
    private final Set<PermissionDTO> permissions;

    // Constructor
    public RoleAggregate(String id, String name) {
        this.id = id;
        this.name = name;
        this.permissions = new HashSet<>();
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Set<PermissionDTO> getPermissions() {
        return permissions;
    }

    // Methods to manage permissions
    public void addPermission(PermissionDTO permission) {
        permissions.add(permission);
    }

    public void removePermission(PermissionDTO permission) {
        permissions.remove(permission);
    }

    // Business logic
    public boolean hasPermission(PermissionDTO permission) {
        return permissions.contains(permission);
    }

    public void renameRole(String newName) {
        this.name = newName;
    }

}
