package de.cocondo.app.system.core.security.principal;

import de.cocondo.app.system.core.id.IdGeneratorService;
import de.cocondo.app.system.core.security.account.Account;
import de.cocondo.app.system.core.security.account.AccountCrudService;
import de.cocondo.app.system.service.CrudService;
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
public class PrincipalCrudService implements CrudService<Principal, String> {

    private static final Logger logger = LoggerFactory.getLogger(PrincipalCrudService.class);
    private final PrincipalRepository principalRepository;
    private final IdGeneratorService idGeneratorService;
    private final AccountCrudService accountCrudService;

    @Override
    public Principal create(Principal principal) {
        if (principal.getId() == null || principal.getId().isEmpty()) {
            principal.setId(idGeneratorService.generateId());
        }
        Principal savedPrincipal = principalRepository.save(principal);
        logger.info("Saved {} with ID: {}", Principal.class.getSimpleName(), savedPrincipal.getId());
        return savedPrincipal;
    }

    @Override
    public Optional<Principal> findById(String id) {
        Optional<Principal> principalOptional = principalRepository.findById(id);
        principalOptional.ifPresentOrElse(
                principal -> logger.info("Retrieved {} with ID: {}", Principal.class.getSimpleName(), principal.getId()),
                () -> logger.warn("{} with ID {} not found", Principal.class.getSimpleName(), id)
        );
        return principalOptional;
    }

    public Optional<Principal> findPrincipalByLoginName(String loginName) {
        Optional<Account> accountOptional = accountCrudService.findByLoginName(loginName);
        if (accountOptional.isPresent()) {
            Account account = accountOptional.get();
            Principal principal = account.getPrincipal();
            if (principal != null) {
                logger.info("Principal found with login name: {}", loginName);
                return Optional.of(principal);
            }
        }
        logger.info("Principal not found with login name: {}", loginName);
        return Optional.empty();
    }

    @Override
    public Principal update(Principal principal) {
        Principal updatedPrincipal = principalRepository.save(principal);
        logger.info("Updated {} with ID: {}", Principal.class.getSimpleName(), updatedPrincipal.getId());
        return updatedPrincipal;
    }

    @Override
    public void delete(String id) {
        principalRepository.deleteById(id);
        logger.info("Deleted {} with ID: {}", Principal.class.getSimpleName(), id);
    }

    @Override
    public List<Principal> findAll() {
        Iterable<Principal> principalIterable = principalRepository.findAll();
        logger.info("Asking for all entities of {}", Principal.class.getSimpleName());
        return StreamSupport.stream(principalIterable.spliterator(), false)
                .collect(Collectors.toList());
    }

    public Optional<Principal> findByName(String name) {
        return principalRepository.findByName(name);
    }

}
