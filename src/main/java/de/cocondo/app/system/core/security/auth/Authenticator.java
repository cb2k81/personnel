package de.cocondo.app.system.core.security.auth;

import de.cocondo.app.system.core.security.account.Account;

public interface Authenticator {

    Account authenticateByCredentials(String loginName, String password);

    Account authenticateByToken(String token);

}
