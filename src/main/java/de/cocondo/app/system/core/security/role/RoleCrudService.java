package de.cocondo.app.system.core.security.role;

import de.cocondo.app.system.core.id.IdGeneratorService;
import de.cocondo.app.system.service.CrudService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class RoleCrudService implements CrudService<Role, String> {

    private static final Logger logger = LoggerFactory.getLogger(RoleCrudService.class);
    private final RoleRepository roleRepository;

    private final IdGeneratorService idGeneratorService;

    @Override
    public Role create(Role role) {
        if (role.getId() == null || role.getId().isEmpty()) {
            role.setId(idGeneratorService.generateId());
        }
        Role savedRole = roleRepository.save(role);
        logger.info("Saved {} with ID: {}", Role.class.getSimpleName(), savedRole.getId());
        return savedRole;
    }


    public Optional<Role> findByName(String roleName) {
        Optional<Role> roleOptional = roleRepository.findByName(roleName);
        return roleOptional;
        /*
        if (roleOptional.isEmpty()) {
            throw new EntityNotFoundException("Role not found by name: " + roleName);
        }
        return roleOptional.get();
        */
    }

    @Override
    public List<Role> findAll() {
        Iterable<Role> roleIterable = roleRepository.findAll();
        logger.debug("Asking for all entities of {}", Role.class.getSimpleName());
        return StreamSupport.stream(roleIterable.spliterator(), false)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Role> findById(String id) {
        Optional<Role> roleOptional = roleRepository.findById(id);
        roleOptional.ifPresentOrElse(
                role -> logger.debug("Retrieved {} with ID: {}", Role.class.getSimpleName(), role.getId()),
                () -> logger.warn("{} with ID {} not found", Role.class.getSimpleName(), id)
        );
        return roleOptional;
    }

    @Override
    public Role update(Role role) {
        Role updatedRole = roleRepository.save(role);
        logger.info("Updated {} with ID: {}", Role.class.getSimpleName(), updatedRole.getId());
        return updatedRole;
    }

    @Override
    public void delete(String id) {
        roleRepository.deleteById(id);
        logger.info("Deleted {} with ID: {}", Role.class.getSimpleName(), id);
    }

    public void delete(Role role) {
        String id = role.getId();
        delete(id);
    }
}
