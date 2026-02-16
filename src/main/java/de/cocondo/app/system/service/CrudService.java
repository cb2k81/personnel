package de.cocondo.app.system.service;

import java.util.List;
import java.util.Optional;

public interface CrudService<E, I> {
    E create(E entity);

    Optional<E> findById(I id);

    E update(E entity);

    void delete(I id);

    List<E> findAll();
}
