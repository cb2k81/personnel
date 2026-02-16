package de.cocondo.app.system.core.security.principal;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PrincipalRepository extends CrudRepository<Principal, String> {

    Optional<Principal> findByName(String name);

}
