package de.cocondo.app.system.dto;

import de.cocondo.app.system.mapper.Mapper;

import javax.naming.OperationNotSupportedException;

/**
 * @deprecated
 */
public interface DtoMapper<E, D> extends Mapper {

    E toEntity(D dto) throws OperationNotSupportedException;

    D fromEntity(E entity);

    static <E, D extends DtoMapper<E, D>> D fromEntity(E entity, Class<D> dtoClass) {
        try {
            D dto = dtoClass.getDeclaredConstructor().newInstance();
            dto.fromEntity(entity);
            return dto;
        } catch (Exception e) {
            throw new RuntimeException("Failed to create DTO from entity", e);
        }
    }
}
