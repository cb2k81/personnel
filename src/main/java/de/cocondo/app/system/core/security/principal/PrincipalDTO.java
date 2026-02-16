package de.cocondo.app.system.core.security.principal;

import de.cocondo.app.system.core.security.role.Role;
import de.cocondo.app.system.dto.DtoMapper;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class PrincipalDTO implements DtoMapper<Principal, PrincipalDTO> {

    private String id;
    private String name;
    private PrincipalStatus status;
    private String primaryAccountId;
    private String email;
    private String description;
    private List<String> roleNames = new ArrayList<>();

    @Override
    public Principal toEntity(PrincipalDTO dto) {
        Principal principal = new Principal();
        principal.setId(dto.getId());
        principal.setName(dto.getName());
        principal.setStatus(dto.getStatus());
        principal.setEmail(dto.getEmail());
        principal.setDescription(dto.getDescription());
        // Roles are not set here as they require special handling
        return principal;
    }

    @Override
    public PrincipalDTO fromEntity(Principal principal) {
        this.setId(principal.getId());
        this.setName(principal.getName());
        this.setStatus(principal.getStatus());
        this.setEmail(principal.getEmail());
        this.setDescription(principal.getDescription());
        this.roleNames.clear();
        for (Role role : principal.getRoles()) {
            this.roleNames.add(role.getName());
        }
        return this;
    }
}
