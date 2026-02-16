package de.cocondo.app.system.core.security.role;

import de.cocondo.app.system.core.id.IdGeneratorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.PostConstruct;
import java.util.Map;
import java.util.Optional;

@Component
@DependsOn("permissionInitializer")
public class RolesInitializer implements ApplicationContextAware {

    private static final Logger logger = LoggerFactory.getLogger(RolesInitializer.class);

    private final RoleCrudService roleCrudService;
    private final RolePermissionService rolePermissionManager;
    private final IdGeneratorService idGeneratorService;
    private ApplicationContext applicationContext;

    public RolesInitializer(RoleCrudService roleCrudService, RolePermissionService rolePermissionManager, IdGeneratorService idGeneratorService) {
        this.roleCrudService = roleCrudService;
        this.rolePermissionManager = rolePermissionManager;
        this.idGeneratorService = idGeneratorService;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @PostConstruct
    @Transactional
    public void initializeRoles() {
        logger.info("Initializing domain roles...");

        Map<String, RoleDefinition> roleBeans = applicationContext.getBeansOfType(RoleDefinition.class);

        for (RoleDefinition roleDefinition : roleBeans.values()) {
            Map<String, Map<String, String[]>> rolesAndPermissions = roleDefinition.getRoles();
            for (Map.Entry<String, Map<String, String[]>> entry : rolesAndPermissions.entrySet()) {
                String roleName = entry.getKey();

                // Überprüfen, ob die Rolle bereits vorhanden ist
                Role role;
                Optional<Role> roleOptional = roleCrudService.findByName(roleName);
                if (roleOptional.isEmpty()) {
                    // Rolle erstellen, wenn nicht vorhanden
                    role = new Role();
                    role.setId(idGeneratorService.generateId());
                    role.setName(roleName);
                    roleCrudService.create(role);
                    logger.info("Successfully initialized role: {}", roleName);
                } else {
                    role = roleOptional.get();
                    logger.info("Role already exists: {}", roleName);
                }

                // Berechtigungen hinzufügen, wenn nicht bereits vorhanden
                Map<String, String[]> permissionsMap = entry.getValue();
                for (Map.Entry<String, String[]> permissionEntry : permissionsMap.entrySet()) {
                    String scope = permissionEntry.getKey();
                    String[] permissions = permissionEntry.getValue();
                    for (String permissionName : permissions) {
                        if (!rolePermissionManager.hasPermission(role.getId(), scope, permissionName)) {
                            rolePermissionManager.addPermissionToRole(role.getId(), scope, permissionName);
                            logger.info("Permission '{}' added to role '{}' with scope '{}'", permissionName, roleName, scope);
                        } else {
                            logger.info("Permission '{}' already assigned to role '{}' with scope '{}'", permissionName, roleName, scope);
                        }
                    }
                }
            }
        }

        logger.info("Domain roles initialization completed.");
    }

}
