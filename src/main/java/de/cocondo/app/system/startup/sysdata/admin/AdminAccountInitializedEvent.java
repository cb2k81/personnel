package de.cocondo.app.system.startup.sysdata.admin;

import de.cocondo.app.system.core.security.account.Account;
import de.cocondo.app.system.event.DomainEvent;

import java.time.Clock;

public class AdminAccountInitializedEvent extends DomainEvent<Account> {

    private final String me = "admin-account-initialized";
    public AdminAccountInitializedEvent(Object source, Account account) {
        super(source);
        this.setPayload(account);
        this.setEventName(me);
    }

    public AdminAccountInitializedEvent(Object source, Account account, Clock clock) {
        super(source, clock);
        this.setPayload(account);
        this.setEventName(me);
    }

}
