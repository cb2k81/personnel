package de.cocondo.app.system.mapper;

import de.cocondo.app.system.dto.DomainEntityDTO;

public interface EntityToDTOMapper<E, D extends DomainEntityDTO> extends Mapper {

    default D fromEntity(E entity) {
        D dto = createDtoInstance();
        fromEntity(entity, dto);
        return dto;
    }

    void fromEntity(E entity, D dto);

    D createDtoInstance();
}
