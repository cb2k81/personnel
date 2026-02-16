package de.cocondo.app.system.startup.sysdata.admin;

import de.cocondo.app.system.core.security.account.Account;
import de.cocondo.app.system.core.security.account.AccountCrudService;
import de.cocondo.app.system.core.security.principal.*;
import de.cocondo.app.system.core.config.permission.SystemRoles;
import de.cocondo.app.system.event.EventPublisher;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@DependsOn("rolesInitializer")
@RequiredArgsConstructor
public class AdminAccountInitializationService {

    private static final Logger logger = LoggerFactory.getLogger(AdminAccountInitializationService.class);

    private final PrincipalCrudService principalCrudService;
    private final AccountCrudService accountService;
    private final PrincipalRoleService principalRoleService;
    private final PrincipalFactory principalFactory;
    private final EventPublisher eventPublisher;

    @Value("${admin.init-on-startup:false}")
    private boolean initOnStartup;

    @Value("${admin.account.loginname:admin}")
    private String adminLoginName;

    @Value("${admin.account.password:cbi2023}")
    private String adminPassword;

    @Value("${admin.account.email:root@localhost}")
    private String adminEmail;


    @EventListener(ApplicationReadyEvent.class)
    public void initializeAdminAccount() {
        if (initOnStartup) {
            Account adminAccount;
            Optional<Account> existingAdminAccount = accountService.findByLoginName(adminLoginName);
            if (existingAdminAccount.isPresent()) {
                logger.info("Admin account exists: " + existingAdminAccount.get());
                adminAccount = existingAdminAccount.get();
            } else {
                Principal adminPrincipal = principalCrudService.findPrincipalByLoginName(adminLoginName).orElse(null);
                if (adminPrincipal == null) {
                    adminPrincipal = createAdminPrincipal();
                }
                principalRoleService.connectToRole(adminPrincipal, SystemRoles.ROLE_SYSADMIN);
                updateAdminPassword(adminPrincipal);
                adminAccount = adminPrincipal.getPrimaryAccount();
            }
            eventPublisher.publishEvent(new AdminAccountInitializedEvent(this, adminAccount));
        }
    }

    @Transactional
    private Principal createAdminPrincipal() {
        Principal adminPrincipal = principalFactory.createPrincipalWithPrimaryAccount(
                "Administrator", adminEmail, "System Administrator", adminLoginName, adminPassword
        );
        principalCrudService.create(adminPrincipal);
        logger.info("Admin Account created for Principal " + adminPrincipal.getName());
        return adminPrincipal;
    }

    private void updateAdminPassword(Principal adminPrincipal) {
        Account primaryAccount = adminPrincipal.getPrimaryAccount();
        if (!accountService.verifyPassword(primaryAccount, adminPassword)) {
            accountService.setPassword(primaryAccount, adminPassword);
            accountService.update(primaryAccount);
        }
    }
}
