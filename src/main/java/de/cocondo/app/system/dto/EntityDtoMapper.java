package de.cocondo.app.system.dto;

import de.cocondo.app.system.mapper.Mapper;

/**
 * @deprecated
 */
public interface EntityDtoMapper<E, D> extends Mapper {

    void toEntity(D dto, E entity);

    D fromEntity(E entity);

}
