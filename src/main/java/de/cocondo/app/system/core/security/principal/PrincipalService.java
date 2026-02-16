package de.cocondo.app.system.core.security.principal;

import de.cocondo.app.system.core.id.IdGeneratorService;
import de.cocondo.app.system.core.security.account.Account;
import de.cocondo.app.system.core.security.account.AccountCrudService;
import de.cocondo.app.system.dto.DtoMapper;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Deprecated
public class PrincipalService {

    private static final Logger logger = LoggerFactory.getLogger(PrincipalService.class);

    private final PrincipalRepository principalRepository;
    private final AccountCrudService accountService;
    private final IdGeneratorService idGeneratorService;

    @Autowired
    public PrincipalService(PrincipalRepository principalRepository, AccountCrudService accountService, IdGeneratorService idGeneratorService) {
        this.principalRepository = principalRepository;
        this.accountService = accountService;
        this.idGeneratorService = idGeneratorService;
    }


    @Transactional
    public PrincipalDTO createPrincipal(PrincipalCreateDTO principalCreateDTO) {
        Principal principal = new Principal();
        String principalId = idGeneratorService.generateId();
        principal.setId(principalId);
        principal.setName(principalCreateDTO.getName());
        principal.setEmail(principalCreateDTO.getEmail());
        principal.setDescription(principalCreateDTO.getDescription());

        // Create the primary account and associate it with the principal
        Account primaryAccount = accountService.createAccount(principalCreateDTO.getLoginname(), principalCreateDTO.getPlainPassword());
        principal.setPrimaryAccount(primaryAccount);

        principalRepository.save(principal);
        return DtoMapper.fromEntity(principal, PrincipalDTO.class);
    }


    @Transactional
    public Principal createPrincipal(String name, String email, String description) {
        Principal principal = new Principal();
        String principalId = idGeneratorService.generateId();
        principal.setId(principalId);
        principal.setName(name);
        principal.setEmail(email);
        principal.setDescription(description);

        // Create the primary account and associate it with the principal
        Account account = accountService.createAccount(name, ""); // Passwort initial leer
        logger.info("Primary account of Principal created: {}", account.getId());
        principal.setPrimaryAccount(account);

        principalRepository.save(principal);
        logger.info("Principal created with ID: {}", principalId);
        logger.info("Account created and associated with Principal: {}", principalId);
        return principal;
    }


    public Principal updatePrincipal(Principal principal) {
        Principal updatedPrincipal = principalRepository.save(principal);
        logger.info("Principal updated with ID: {}", principal.getId());
        return updatedPrincipal;
    }

    public Principal updatePrincipal(String id, PrincipalDTO updatedPrincipal) {
        Optional<Principal> optionalPrincipal = findPrincipalById(id);
        if (optionalPrincipal.isPresent()) {
            Principal principalToUpdate = optionalPrincipal.get();
            updatedPrincipal.fromEntity(principalToUpdate);
            return updatePrincipal(principalToUpdate);
        } else {
            throw new EntityNotFoundException("Principal not found with ID: " + id);
        }
    }


    public void deletePrincipal(String principalId) {
        Optional<Principal> principalOptional = findPrincipalById(principalId);
        principalOptional.ifPresent(principal -> {
            principalRepository.delete(principal);
            logger.info("Principal deleted with ID: {}", principal.getId());
        });
    }


    public void deletePrincipal(Principal principal) {
        principalRepository.delete(principal);
        logger.info("Principal deleted with ID: {}", principal.getId());
    }

    public Optional<Principal> findPrincipalById(String principalId) {
        Optional<Principal> principalOptional = principalRepository.findById(principalId);
        if (principalOptional.isPresent()) {
            logger.info("Principal found with ID: {}", principalId);
        } else {
            logger.info("Principal not found with ID: {}", principalId);
        }
        return principalOptional;
    }

    public Iterable<Principal> getAllPrincipals() {
        return principalRepository.findAll();
    }

    public Optional<Principal> findPrincipalByName(String name) {
        Optional<Principal> principalOptional = principalRepository.findByName(name);
        if (principalOptional.isPresent()) {
            logger.info("Principal found with name: {}", name);
        } else {
            logger.info("Principal not found with name: {}", name);
        }
        return principalOptional;
    }


    public Optional<Principal> findPrincipalByLoginName(String loginName) {
        Optional<Account> accountOptional = accountService.findByLoginName(loginName);
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

    public Optional<Principal> findPrincipalByAccountId(String accountId) {
        Optional<Account> accountOptional = accountService.findById(accountId);
        if (accountOptional.isPresent()) {
            Account account = accountOptional.get();
            Principal principal = account.getPrincipal();
            if (principal != null) {
                logger.info("Principal found with account ID: {}", accountId);
                return Optional.of(principal);
            }
        }
        logger.info("Principal not found with account ID: {}", accountId);
        return Optional.empty();
    }

}
