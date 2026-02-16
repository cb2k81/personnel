package de.cocondo.app.system.core.context;

import de.cocondo.app.system.core.security.account.Account;
import de.cocondo.app.system.core.security.account.AccountCrudService;
import de.cocondo.app.system.core.security.principal.Principal;
import de.cocondo.app.system.core.security.principal.PrincipalCrudService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Slf4j
@Component
public class BatchUserContextListener implements JobExecutionListener {

    private final AccountCrudService accountService;
    private final PrincipalCrudService principalService;
    private final RequestContextDataContainer context;

    @Override
    public void beforeJob(JobExecution jobExecution) {
        String jobName = jobExecution.getJobInstance().getJobName();
        log.info("[{}] Starting beforeJob", jobName);

        // Parameter lesen (Default „system“)
        String runUser = jobExecution.getJobParameters()
                .getString("runUsername", "system");
        log.debug("[{}] Retrieved runUsername parameter: {}", jobName, runUser);

        // Account aus DB holen oder Exception, wenn nicht vorhanden
        Account account = accountService
                .findByLoginName(runUser)
                .orElseThrow(() -> {
                    log.error("[{}] No account found for runUser '{}'. Aborting job.", jobName, runUser);
                    return new IllegalArgumentException(
                            "Batch runUser '" + runUser + "' existiert nicht");
                });
        log.info("[{}] Account loaded for runUser '{}': id={}", jobName, runUser, account.getId());

        // Principal aus DB holen oder aus Account entnehmen
        Principal principal = principalService.findPrincipalByLoginName(runUser)
                .orElseGet(() -> {
                    log.warn("[{}] Principal not found via PrincipalCrudService for '{}', using Account.getPrincipal()", jobName, runUser);
                    return account.getPrincipal();
                });
        log.debug("[{}] Retrieved Principal for runUser: {}", jobName, principal.getName());

        // Im Thread‑Kontext setzen
        context.setCurrentAccount(account);
        context.setCurrentPrincipal(principal);
        log.info("[{}] Current Account and Principal set in RequestContextDataContainer", jobName);

        // Falls ihr zusätzlich UserContextHolder nutzt:
        UserContextHolder.setCurrentUser(account.getLoginName());
        log.debug("[{}] Current user set in UserContextHolder: {}", jobName, account.getLoginName());
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        String jobName = jobExecution.getJobInstance().getJobName();
        log.info("[{}] Starting afterJob cleanup", jobName);

        // Aufräumen
        context.clear();
        UserContextHolder.clear();
        log.info("[{}] RequestContextDataContainer and UserContextHolder cleared", jobName);
    }
}
