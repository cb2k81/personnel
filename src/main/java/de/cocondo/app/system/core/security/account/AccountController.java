package de.cocondo.app.system.core.security.account;

import de.cocondo.app.system.core.config.permission.AccessSecurityPermissionSet;
import de.cocondo.app.system.core.security.permission.Permit;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private final AccountCrudService accountService;

    public AccountController(AccountCrudService accountService) {
        this.accountService = accountService;
    }

    @PostMapping("/{id}/change-password")
    @Permit(scope = AccessSecurityPermissionSet.SCOPE, value = AccessSecurityPermissionSet.PRINCIPAL_EDIT)
    public ResponseEntity<Void> changePassword(@PathVariable String id, @RequestParam String newPassword) {
        // Find the account by ID
        Account account = accountService.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Account not found with ID: " + id));

        // Change the password
        accountService.changePassword(account, newPassword);

        return ResponseEntity.noContent().build();
    }
}
