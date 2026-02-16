package de.cocondo.app.system.core.security.auth;

import de.cocondo.app.system.core.context.RequestContextDataContainer;
import de.cocondo.app.system.core.security.account.Account;
import de.cocondo.app.system.core.security.account.AccountCrudService;
import de.cocondo.app.system.core.security.crypto.TokenManager;
import de.cocondo.app.system.core.security.principal.Principal;
import io.jsonwebtoken.Claims;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class CommonAuthenticationService implements Authenticator {

    private static final Logger logger = LoggerFactory.getLogger(CommonAuthenticationService.class);
    private final AccountCrudService accountService;
    private final TokenManager tokenManager;
    private final RequestContextDataContainer dataContainer;

    public CommonAuthenticationService(AccountCrudService accountService, TokenManager tokenManager, RequestContextDataContainer dataContainer) {
        this.accountService = accountService;
        this.tokenManager = tokenManager;
        this.dataContainer = dataContainer;
    }

    @Override
    public Account authenticateByCredentials(String loginName, String password) throws AuthenticationException {
        try {
            Account account = findAccountByLoginName(loginName);
            if (accountService.verifyPassword(account, password)) {
                logger.info("Account " + loginName + " successfully authenticated by credentials");
                dataContainer.setCurrentAccount(account); // Set the current account in the data container
                Principal principal = account.getPrincipal();
                dataContainer.setCurrentPrincipal(principal);
                return account;
            } else {
                throw new InvalidCredentialsException("Authentication failed for Account " + account);
            }
        } catch (EntityNotFoundException | InvalidCredentialsException e) {
            throw new AuthenticationException("Authentication failed for account " + loginName, e);
        }
    }

    @Override
    public Account authenticateByToken(String token) throws AuthenticationException {
        String loginName = null;
        try {
            logger.debug("Token: " + token);
            boolean isValid = accountService.validateToken(token);
            if (!isValid) {
                logger.debug("The following token was verified and invalid: " + token);
                throw new InvalidTokenException("Token based authentication failed");
            }
            Claims claims = tokenManager.decodeToken(token);
            logger.debug("Decoded token claims:" + claims);
            loginName = claims.getSubject();
            Account account = findAccountByLoginName(loginName);
            Principal principal = account.getPrincipal();
            dataContainer.setCurrentPrincipal(principal);
            logger.info("Account " + loginName + " successfully authenticated by token");
            dataContainer.setCurrentAccount(account); // Set the current account in the data container
            return account;
        } catch (EntityNotFoundException | InvalidTokenException e) {
            throw new AuthenticationException("Authentication by token failed for account " + loginName, e);
        }
    }


    private Account findAccountByLoginName(String loginName) throws EntityNotFoundException {
        if (loginName == null) {
            throw new AuthenticationException("Authentication failed. Login name must not be empty");
        }
        Optional<Account> account = accountService.findByLoginName(loginName);
        if (account.isPresent()) {
            return account.get();
        }
        throw new EntityNotFoundException("Account not found by loginName " + loginName);
    }

}
