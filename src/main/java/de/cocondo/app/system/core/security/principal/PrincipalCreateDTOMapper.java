package de.cocondo.app.system.core.security.principal;

import de.cocondo.app.system.dto.DtoMapper;
import de.cocondo.app.system.dto.EntityDtoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PrincipalCreateDTOMapper implements EntityDtoMapper<Principal, PrincipalCreateDTO> {

    @Override
    public void toEntity(PrincipalCreateDTO dto, Principal entity) {
        entity.setName(dto.getName());
        entity.setEmail(dto.getEmail());
        entity.setDescription(dto.getDescription());
    }

    @Override
    public PrincipalCreateDTO fromEntity(Principal entity) {
        throw new UnsupportedOperationException("Mapping from Principal entity to PrincipalCreateDTO is not supported.");
    }
}
