package de.cocondo.app.system.core.security.auth;

import de.cocondo.app.system.core.security.account.AccountDTO;

public class AuthResponseDTO {

    private AccountDTO account;

    private String token;


    public AccountDTO getAccount() {
        return account;
    }

    public void setAccount(AccountDTO account) {
        this.account = account;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
