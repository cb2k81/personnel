package de.cocondo.app.system.core.security.permission;

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
public class PermissionCrudService implements CrudService<Permission, String> {

    private static final Logger logger = LoggerFactory.getLogger(PermissionCrudService.class);
    private final PermissionRepository permissionRepository;
    private final IdGeneratorService idGeneratorService;

    @Override
    public Permission create(Permission permission) {
        if (permission.getId() == null || permission.getId().isEmpty()) {
            permission.setId(idGeneratorService.generateId());
        }
        Permission savedPermission = permissionRepository.save(permission);
        logger.info("Saved {} with ID: {}", Permission.class.getSimpleName(), savedPermission.getId());
        return savedPermission;
    }

    public Permission create(String scope, String name) {
        Permission permission = new Permission();
        permission.setId(idGeneratorService.generateId());
        permission.setScope(scope);
        permission.setName(name);
        return create(permission);
    }

    public Permission findByScopeAndName(String scope, String name) {
        Optional<Permission> permissionOptional = permissionRepository.findByScopeAndName(scope, name);
        if (permissionOptional.isEmpty()) {
            throw new EntityNotFoundException("Permission not found by scope and name: " + scope + ", " + name);
        }
        return permissionOptional.get();
    }

    @Override
    public Optional<Permission> findById(String id) {
        Optional<Permission> permissionOptional = permissionRepository.findById(id);
        permissionOptional.ifPresentOrElse(
                permission -> logger.info("Retrieved {} with ID: {}", Permission.class.getSimpleName(), permission.getId()),
                () -> logger.warn("{} with ID {} not found", Permission.class.getSimpleName(), id)
        );
        return permissionOptional;
    }

    @Override
    public Permission update(Permission permission) {
        Permission updatedPermission = permissionRepository.save(permission);
        logger.info("Updated {} with ID: {}", Permission.class.getSimpleName(), updatedPermission.getId());
        return updatedPermission;
    }

    @Override
    public void delete(String id) {
        permissionRepository.deleteById(id);
        logger.info("Deleted {} with ID: {}", Permission.class.getSimpleName(), id);
    }

    public void delete(Permission permission) {
        String id = permission.getId();
        delete(id);
    }

    @Override
    public List<Permission> findAll() {
        Iterable<Permission> permissionIterable = permissionRepository.findAll();
        return StreamSupport.stream(permissionIterable.spliterator(), false)
                .collect(Collectors.toList());
    }
}
