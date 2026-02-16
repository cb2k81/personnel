package de.cocondo.app.system.core.security.principal;

import de.cocondo.app.system.core.security.role.Role;
import de.cocondo.app.system.dto.EntityDtoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class PrincipalMapper implements EntityDtoMapper<Principal, PrincipalDTO> {

    @Override
    public void toEntity(PrincipalDTO dto, Principal entity) {
        entity.setId(dto.getId());
        entity.setName(dto.getName());
        entity.setStatus(dto.getStatus());
        entity.setEmail(dto.getEmail());
        entity.setDescription(dto.getDescription());

        // Map roles from roleNames
        if (dto.getRoleNames() != null) {
            entity.setRoles(dto.getRoleNames().stream()
                    .map(roleName -> {
                        Role role = new Role();
                        role.setName(roleName);
                        return role;
                    })
                    .collect(Collectors.toList()));
        } else {
            entity.setRoles(null);
        }

        // Note: primaryAccount mapping is omitted because it's not present in PrincipalDTO
    }

    @Override
    public PrincipalDTO fromEntity(Principal entity) {
        PrincipalDTO dto = new PrincipalDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setStatus(entity.getStatus());
        dto.setEmail(entity.getEmail());
        dto.setDescription(entity.getDescription());

        // Map role names from roles
        if (entity.getRoles() != null) {
            dto.setRoleNames(entity.getRoles().stream()
                    .map(Role::getName)
                    .collect(Collectors.toList()));
        } else {
            dto.setRoleNames(null);
        }

        // Note: primaryAccountId mapping is omitted because it's not present in Principal entity

        return dto;
    }
}
