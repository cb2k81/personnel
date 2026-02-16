package de.cocondo.app.system.core.security.account;

import de.cocondo.app.system.core.context.RequestContextDataContainer;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/me/account")
public class MyAccountController {

    private final RequestContextDataContainer dataContainer;
    private final AccountCrudService accountService;

    public MyAccountController(RequestContextDataContainer dataContainer, AccountCrudService accountService) {
        this.dataContainer = dataContainer;
        this.accountService = accountService;
    }

    @PostMapping("/change-password")
    public ResponseEntity<Void> changePassword(@RequestParam String newPassword) {
        Account currentAccount = dataContainer.getCurrentAccount();

        // Change the password for the current account
        accountService.changePassword(currentAccount, newPassword);

        return ResponseEntity.noContent().build();
    }
}
