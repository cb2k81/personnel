package de.cocondo.app.system.core.security.account;

public class AccountDTO {
    private String id;
    private String loginName;
    private String principalName;
    private String email;

    public AccountDTO() {

    }

    public AccountDTO(Account account) {
        this.id = account.getId();
        this.loginName = account.getLoginName();
        this.principalName = account.getPrincipal().getName();
        this.email = account.getPrincipal().getEmail();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getPrincipalName() {
        return principalName;
    }

    public void setPrincipalName(String principalName) {
        this.principalName = principalName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
