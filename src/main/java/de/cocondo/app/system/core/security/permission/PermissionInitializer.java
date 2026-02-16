package de.cocondo.app.system.core.security.permission;

import de.cocondo.app.system.core.id.IdGeneratorService;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class PermissionInitializer implements ApplicationContextAware {

    private static final Logger logger = LoggerFactory.getLogger(PermissionInitializer.class);

    private static ApplicationContext context;
    private final PermissionRepository permissionRepository;
    private final IdGeneratorService idGeneratorService;

    public PermissionInitializer(PermissionRepository permissionRepository, IdGeneratorService idGeneratorService) {
        this.permissionRepository = permissionRepository;
        this.idGeneratorService = idGeneratorService;
    }

    @Override
    public void setApplicationContext(@NonNull ApplicationContext applicationContext) {
        context = applicationContext;
    }

    @PostConstruct
    @Transactional
    public void initializePermissions() {
        logger.debug("Initializing scoped Permissions...");

        // Get all PermissionSet beans in the application context
        Map<String, Object> permissionSetBeans = context.getBeansWithAnnotation(PermissionSet.class);
        List<Permission> newPermissions = new ArrayList<>();
        Set<String> processedPermissions = new HashSet<>();

        for (Object permissionSetBean : permissionSetBeans.values()) {
            if (permissionSetBean != null) {
                PermissionSet permissionSetAnnotation = permissionSetBean.getClass().getAnnotation(PermissionSet.class);
                if (permissionSetAnnotation != null) {
                    String scope = permissionSetAnnotation.scope();
                    String[] permissionNames = permissionSetAnnotation.permissions();
                    for (String permissionName : permissionNames) {
                        processPermission(scope, permissionName, newPermissions, processedPermissions);
                    }
                }
            }
        }

        saveNewPermissions(newPermissions);
    }

    private void processPermission(String scope, String permissionName, List<Permission> newPermissions, Set<String> processedPermissions) {
        String permissionKey = scope + ":" + permissionName;

        // Check if the permission is already processed
        if (!processedPermissions.contains(permissionKey)) {
            logger.debug("Processing Permission: {}.{}", scope, permissionName);

            // Check if the permission already exists in the database
            Optional<Permission> existingPermission = permissionRepository.findByScopeAndName(scope, permissionName);
            if (existingPermission.isEmpty()) {
                Permission newPermission = new Permission();
                newPermission.setId(idGeneratorService.generateId());
                newPermission.setScope(scope);
                newPermission.setName(permissionName);
                newPermissions.add(newPermission);
                logger.debug("Created new Permission: {}.{}", scope, permissionName);
            } else {
                logger.debug("Permission already exists: {}.{}", scope, permissionName);
            }

            // Mark the permission as processed
            processedPermissions.add(permissionKey);
        } else {
            logger.debug("Permission already processed: {}.{}", scope, permissionName);
        }
    }

    private void saveNewPermissions(List<Permission> newPermissions) {
        if (!newPermissions.isEmpty()) {
            permissionRepository.saveAll(newPermissions);
            logger.info("Successfully initialized scoped Permissions.");
        } else {
            logger.info("No new scoped Permissions to initialize.");
        }
    }
}
