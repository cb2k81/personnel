package de.cocondo.app.system.core.security.account;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import de.cocondo.app.system.core.security.principal.Principal;
import jakarta.persistence.*;

import java.io.Serializable;

@Entity
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Account implements Serializable {

    @Id
    @Column(columnDefinition = "VARCHAR(36)")
    private String id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "principal_id")
    private Principal principal;

    @Column(unique = true)
    private String loginName;

    private String password;

    private String salt;

    private String token;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Principal getPrincipal() {
        return principal;
    }

    public void setPrincipal(Principal principal) {
        this.principal = principal;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public String toString() {
        return "Account{" +
                "id='" + id + '\'' +
                ", loginName='" + loginName + '\'' +
                '}';
    }
}