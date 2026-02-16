package de.cocondo.app.system.mapper;

import de.cocondo.app.system.dto.DTO;

public interface DTOToEntityMapper<D extends DTO, E> extends Mapper {

    void toEntity(D dto, E entity);

    default E toEntity(D dto, Class<E> entityClass) {
        try {
            E entity = entityClass.getDeclaredConstructor().newInstance();
            toEntity(dto, entity);
            return entity;
        } catch (Exception e) {
            throw new RuntimeException("Failed to create entity instance", e);
        }
    }

}
