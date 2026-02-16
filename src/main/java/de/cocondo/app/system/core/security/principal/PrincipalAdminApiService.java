package de.cocondo.app.system.core.security.principal;

import de.cocondo.app.system.core.config.permission.AccessSecurityPermissionSet;
import de.cocondo.app.system.core.security.account.Account;
import de.cocondo.app.system.core.security.account.AccountCrudService;
import de.cocondo.app.system.core.security.permission.Permit;
import de.cocondo.app.system.core.security.role.Role;
import de.cocondo.app.system.core.security.role.RoleCrudService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PrincipalAdminApiService {

    private static final Logger logger = LoggerFactory.getLogger(PrincipalAdminApiService.class);

    private final PrincipalCrudService principalCrudService;
    private final RoleCrudService roleCrudService;
    private final AccountCrudService accountCrudService;

    @Permit(scope = AccessSecurityPermissionSet.SCOPE, value = AccessSecurityPermissionSet.PRINCIPAL_CREATE)
    @Transactional
    public PrincipalDTO createPrincipalWithPrimaryAccount(PrincipalCreateDTO principalCreateDTO) {
        logger.info("Creating principal with name: {}", principalCreateDTO.getName());
        Principal principal = new Principal();
        principal.setName(principalCreateDTO.getName());
        principal.setEmail(principalCreateDTO.getEmail());
        principal.setDescription(principalCreateDTO.getDescription());
        // Create the primary account and associate it with the principal
        Account primaryAccount = accountCrudService.createAccount(principalCreateDTO.getLoginname(), principalCreateDTO.getPlainPassword());
        principal.setPrimaryAccount(primaryAccount);

        // Add the primary account to the list of accounts in principal
        Principal createdPrincipal = principalCrudService.create(principal);

        logger.info("Principal created with ID: {}", createdPrincipal.getId());
        return mapToDTO(createdPrincipal);
    }

    @Permit(scope = AccessSecurityPermissionSet.SCOPE, value = AccessSecurityPermissionSet.PRINCIPAL_READ)
    public PrincipalDTO getPrincipalById(String id) {
        logger.info("Fetching principal with ID: {}", id);
        Optional<Principal> principalOptional = principalCrudService.findById(id);
        if (principalOptional.isPresent()) {
            logger.info("Principal found with ID: {}", id);
            return mapToDTO(principalOptional.get());
        } else {
            logger.warn("Principal not found with ID: {}", id);
            throw new EntityNotFoundException("Principal not found with ID: " + id);
        }
    }

    @Permit(scope = AccessSecurityPermissionSet.SCOPE, value = AccessSecurityPermissionSet.PRINCIPAL_READ)
    public List<PrincipalDTO> getAllPrincipals() {
        logger.info("Fetching all principals");
        List<Principal> principals = principalCrudService.findAll();
        logger.info("Fetched {} principals", principals.size());
        return principals.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Permit(scope = AccessSecurityPermissionSet.SCOPE, value = AccessSecurityPermissionSet.PRINCIPAL_EDIT)
    public PrincipalDTO updatePrincipalBasedata(String id, PrincipalUpdateBasedataDTO principalUpdateBasedataDTO) {
        logger.info("Updating principal basedata with ID: {}", id);
        Principal principal = principalCrudService.findById(id)
                .orElseThrow(() -> {
                    logger.error("Principal not found with ID: {}", id);
                    return new EntityNotFoundException("Principal not found with ID: " + id);
                });
        principal.setName(principalUpdateBasedataDTO.getName());
        principal.setEmail(principalUpdateBasedataDTO.getEmail());
        principal.setDescription(principalUpdateBasedataDTO.getDescription());

        Principal updatedPrincipal = principalCrudService.update(principal);
        logger.info("Principal basedata updated for ID: {}", id);
        return mapToDTO(updatedPrincipal);
    }

    @Permit(scope = AccessSecurityPermissionSet.SCOPE, value = AccessSecurityPermissionSet.PRINCIPAL_DELETE)
    public void deletePrincipal(String id) {
        logger.info("Deleting principal with ID: {}", id);
        principalCrudService.delete(id);
        logger.info("Principal with ID: {} deleted", id);
    }

    @Permit(scope = AccessSecurityPermissionSet.SCOPE, value = AccessSecurityPermissionSet.PRINCIPAL_READ)
    public PrincipalDTO findPrincipalByName(String name) {
        logger.info("Finding principal by name: {}", name);
        Optional<Principal> principalOptional = principalCrudService.findByName(name);
        if (principalOptional.isEmpty()) {
            logger.warn("Principal not found by name: {}", name);
            throw new EntityNotFoundException("Principal not found by name: " + name);
        }
        logger.info("Principal found by name: {}", name);
        return new PrincipalDTO().fromEntity(principalOptional.get());
    }

    @Permit(scope = AccessSecurityPermissionSet.SCOPE, value = AccessSecurityPermissionSet.PRINCIPAL_EDIT)
    public void connectPrincipalToRole(String principalId, String roleName) {
        logger.info("Connecting principal with ID: {} to role with name: {}", principalId, roleName);
        Optional<Role> roleOptional = roleCrudService.findByName(roleName);
        if (roleOptional.isEmpty()) {
            throw new EntityNotFoundException("Role not found with name: " + roleName);
        }
        Role role = roleOptional.get();
        Optional<Principal> principalOptional = principalCrudService.findById(principalId);
        if (principalOptional.isPresent()) {
            Principal principal = principalOptional.get();
            principal.getRoles().add(role);
            principalCrudService.update(principal);
            logger.info("Principal with ID: {} connected to role with name: {}", principalId, roleName);
        } else {
            logger.warn("Principal not found with ID: {}", principalId);
            throw new EntityNotFoundException("Principal not found with ID: " + principalId);
        }
    }

    @Permit(scope = AccessSecurityPermissionSet.SCOPE, value = AccessSecurityPermissionSet.PRINCIPAL_EDIT)
    public void disconnectPrincipalFromRole(String principalId, String roleName) {
        logger.info("Disconnecting principal with ID: {} from role with name: {}", principalId, roleName);
        Optional<Role> roleOptional = roleCrudService.findByName(roleName);
        if (roleOptional.isEmpty()) {
            throw new EntityNotFoundException("Role not found with name: " + roleName);
        }
        Role role = roleOptional.get();
        Optional<Principal> principalOptional = principalCrudService.findById(principalId);
        if (principalOptional.isPresent()) {
            Principal principal = principalOptional.get();
            principal.getRoles().remove(role);
            principalCrudService.update(principal);
            logger.info("Principal with ID: {} disconnected from role with name: {}", principalId, roleName);
        } else {
            logger.warn("Principal not found with ID: {}", principalId);
            throw new EntityNotFoundException("Principal not found with ID: " + principalId);
        }
    }

    @Permit(scope = AccessSecurityPermissionSet.SCOPE, value = AccessSecurityPermissionSet.PRINCIPAL_EDIT)
    public void updatePrimaryAccountPassword(String principalId, String newRawPassword) {
        logger.info("Updating password for primary account of principal with ID: {}", principalId);
        Principal principal = principalCrudService.findById(principalId)
                .orElseThrow(() -> {
                    logger.error("Principal not found with ID: {}", principalId);
                    return new EntityNotFoundException("Principal not found with ID: " + principalId);
                });

        Account primaryAccount = principal.getPrimaryAccount();
        if (primaryAccount == null) {
            logger.error("No primary account associated with principal ID: {}", principalId);
            throw new RuntimeException("No primary account associated with principal ID: " + principalId);
        }

        accountCrudService.changePassword(primaryAccount, newRawPassword);
        logger.info("Password updated for primary account of principal with ID: {}", principalId);
    }

    private PrincipalDTO mapToDTO(Principal principal) {
        logger.info("Mapping principal with ID: {} to PrincipalDTO", principal.getId());
        PrincipalDTO dto = new PrincipalDTO();
        dto.setId(principal.getId());
        dto.setName(principal.getName());
        dto.setEmail(principal.getEmail());
        dto.setDescription(principal.getDescription());
        List<String> roleNames = principal.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toList());
        dto.setRoleNames(roleNames);
        return dto;
    }
}
