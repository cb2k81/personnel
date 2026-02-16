package de.cocondo.app.system.core.security.role;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface RoleRepository extends CrudRepository<Role, String> {

    Optional<Role> findByName(String name);

}
