package de.cocondo.app.system.core.security.permission;

import java.util.Objects;

public class PermissionDTO {
    private String id;
    private String scope;
    private String name;

    public PermissionDTO(String id, String scope, String name) {
        this.id = id;
        this.scope = scope;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getScope() {
        return scope;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PermissionDTO that = (PermissionDTO) o;
        return Objects.equals(id, that.id) && Objects.equals(scope, that.scope) && Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, scope, name);
    }
}
