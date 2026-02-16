package de.cocondo.app.system.core.security.principal;

import de.cocondo.app.system.core.security.account.Account;
import de.cocondo.app.system.core.security.role.Role;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@EqualsAndHashCode(of = "id")
public class Principal {

    @Id
    @Column(columnDefinition = "VARCHAR(36)")
    private String id;

    @Column(unique = true)
    private String name;

    @Enumerated(EnumType.STRING)
    private PrincipalStatus status;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "primary_account_id")
    private Account primaryAccount;

    @Column(unique = true)
    private String email;

    private String description;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "principal_role",
            joinColumns = @JoinColumn(name = "principal_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private List<Role> roles = new ArrayList<>();


    public void setPrimaryAccount(Account primaryAccount) {
        if (this.primaryAccount != null) {
            this.primaryAccount.setPrincipal(null);
        }
        this.primaryAccount = primaryAccount;
        if (primaryAccount != null) {
            primaryAccount.setPrincipal(this);
        }
    }

}