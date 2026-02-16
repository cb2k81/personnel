package de.cocondo.app.system.core.security.account;

import de.cocondo.app.system.core.id.IdGeneratorService;
import de.cocondo.app.system.core.security.principal.Principal;
import de.cocondo.app.system.core.security.crypto.TokenManager;
import de.cocondo.app.system.core.security.crypto.SecretContainer;
import de.cocondo.app.system.core.security.crypto.service.PasswordEncryptor;
import de.cocondo.app.system.core.security.role.Role;
import de.cocondo.app.system.service.CrudService;
import jakarta.transaction.Transactional;
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
public class AccountCrudService implements CrudService<Account, String> {

    private static final Logger logger = LoggerFactory.getLogger(AccountCrudService.class);

    private final AccountRepository accountRepository;
    private final IdGeneratorService idGeneratorService;
    private final TokenManager tokenService;
    private final PasswordEncryptor passwordEncryptor;

    @Override
    public Account create(Account account) {
        if (account.getId() == null || account.getId().isEmpty()) {
            account.setId(idGeneratorService.generateId());
        }
        SecretContainer sc = passwordEncryptor.encryptPassword(account.getPassword());
        account.setPassword(sc.getPassword());
        account.setSalt(sc.getSalt());
        Account savedAccount = accountRepository.save(account);
        logger.info("Account saved {} with ID: {}", Account.class.getSimpleName(), savedAccount.getId());
        return savedAccount;
    }


    public Account createAccount(String loginName, String rawPassword) {
        Account account = new Account();
        account.setLoginName(loginName);
        account.setPassword(rawPassword);
        return create(account);
    }


    @Override
    public Optional<Account> findById(String id) {
        Optional<Account> accountOptional = accountRepository.findById(id);
        accountOptional.ifPresentOrElse(
                account -> logger.debug("Account retrieved {} with ID: {}", Account.class.getSimpleName(), account.getId()),
                () -> logger.warn("{} with ID {} not found", Account.class.getSimpleName(), id)
        );
        return accountOptional;
    }

    @Override
    public Account update(Account account) {
        SecretContainer sc = passwordEncryptor.encryptPassword(account.getPassword());
        account.setPassword(sc.getPassword());
        account.setSalt(sc.getSalt());
        Account updatedAccount = accountRepository.save(account);
        logger.debug("Account updated {} with ID: {}", Account.class.getSimpleName(), updatedAccount.getId());
        return updatedAccount;
    }

    @Override
    public void delete(String id) {
        accountRepository.deleteById(id);
        logger.info("Account deleted {} with ID: {}", Account.class.getSimpleName(), id);
    }

    @Override
    public List<Account> findAll() {
        Iterable<Account> accountIterable = accountRepository.findAll();
        logger.debug("Asking for all account entities of {}", Account.class.getSimpleName());
        return StreamSupport.stream(accountIterable.spliterator(), false)
                .collect(Collectors.toList());
    }

    public Optional<Account> findByLoginName(String loginName) {
        Optional<Account> accountOptional = accountRepository.findByLoginName(loginName);
        accountOptional.ifPresentOrElse(
                account -> logger.debug("Account retrieved {} with login name: {}", Account.class.getSimpleName(), loginName),
                () -> logger.warn("{} with login name {} not found", Account.class.getSimpleName(), loginName)
        );
        return accountOptional;
    }

    public boolean verifyPassword(Account account, String rawComparePassword) {
        boolean result = passwordEncryptor.verifyPassword(rawComparePassword, account.getPassword(), account.getSalt());
        logger.debug("Password verification for account ID: {} resulted in: {}", account.getId(), result);
        return result;
    }

    public void setPassword(Account account, String rawPassword) {
        SecretContainer sc = passwordEncryptor.encryptPassword(rawPassword);
        account.setPassword(sc.getPassword());
        account.setSalt(sc.getSalt());
        accountRepository.save(account);
        logger.info("Password set for account with ID: {}", account.getId());
    }

    public void changePassword(Account account, String newRawPassword) {
        SecretContainer sc = passwordEncryptor.encryptPassword(newRawPassword);
        account.setPassword(sc.getPassword());
        account.setSalt(sc.getSalt());
        update(account); // Save the updated account
        logger.info("Password changed for account with ID: {}", account.getId());
    }

    @Transactional
    public String generateToken(Account account) {
        String subject = account.getLoginName();
        Principal principal = account.getPrincipal();
        if (principal == null) {
            logger.error("No principal connected to account ID: {}", account.getId());
            throw new RuntimeException("No principal connected to account: " + account);
        }
        List<String> roleNames = principal.getRoles().stream()
                .map(Role::getName)
                .toList();
        String[] roles = roleNames.toArray(new String[0]);
        String token = tokenService.generateToken(subject, roles);
        logger.debug("Generated token for account ID: {}", account.getId());
        return token;
    }

    public boolean validateToken(String token) {
        boolean result = tokenService.validateToken(token);
        logger.debug("Token validation resulted in: {}", result);
        return result;
    }
}
