package de.cocondo.app.system.core.security.principal;

import de.cocondo.app.system.core.security.account.Account;
import de.cocondo.app.system.core.security.account.AccountCrudService;
import org.springframework.stereotype.Component;

@Component
public class PrincipalFactory {

    private final AccountCrudService accountService;

    public PrincipalFactory(AccountCrudService accountService) {
        this.accountService = accountService;
    }

    public Principal createPrincipalWithPrimaryAccount(String name, String email, String description, String loginName, String rawPassword) {
        Principal principal = new Principal();
        principal.setName(name);
        principal.setEmail(email);
        principal.setDescription(description);

        // Create the primary account
        Account primaryAccount = accountService.createAccount(loginName, rawPassword);
        principal.setPrimaryAccount(primaryAccount);

        return principal;
    }
}
