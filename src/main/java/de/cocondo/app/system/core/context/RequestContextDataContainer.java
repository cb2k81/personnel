package de.cocondo.app.system.core.context;

import de.cocondo.app.system.core.security.account.Account;
import de.cocondo.app.system.core.security.principal.Principal;
import org.springframework.stereotype.Component;

/**
 * Verwaltet den Benutzerkontext (Account, Principal, Mapping-Infos) unabhängig vom HTTP-Request-Scope.
 */
@Component
public class RequestContextDataContainer {

    private static final ThreadLocal<Account> currentAccount = new ThreadLocal<>();
    private static final ThreadLocal<Principal> currentPrincipal = new ThreadLocal<>();
    private static final ThreadLocal<Object> mappedObject = new ThreadLocal<>();
    private static final ThreadLocal<Class<?>> mappedObjectClass = new ThreadLocal<>();
    private static final ThreadLocal<String> requestBody = new ThreadLocal<>();

    public Principal getCurrentPrincipal() {
        return currentPrincipal.get();
    }

    public void setCurrentPrincipal(Principal principal) {
        currentPrincipal.set(principal);
    }

    public Account getCurrentAccount() {
        return currentAccount.get();
    }

    public void setCurrentAccount(Account account) {
        currentAccount.set(account);
    }

    public void setRequestBody(String body) {
        requestBody.set(body);
    }

    public String getRequestBody() {
        return requestBody.get();
    }

    public void setMappedObject(Object obj, Class<?> clazz) {
        mappedObject.set(obj);
        mappedObjectClass.set(clazz);
    }

    public Object getMappedObject() {
        return mappedObject.get();
    }

    public Class<?> getMappedObjectClass() {
        return mappedObjectClass.get();
    }

    public void clear() {
        currentAccount.remove();
        currentPrincipal.remove();
        mappedObject.remove();
        mappedObjectClass.remove();
        requestBody.remove();
    }
}
