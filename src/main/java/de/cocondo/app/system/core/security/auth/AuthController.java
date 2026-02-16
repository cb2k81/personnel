package de.cocondo.app.system.core.security.auth;

import de.cocondo.app.system.core.security.account.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final Authenticator authenticator;
    private final AccountCrudService accountService;

    public AuthController(Authenticator authenticator, AccountCrudService accountService) {
        this.authenticator = authenticator;
        this.accountService = accountService;
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody AuthCredentialsDTO authCredentials) {
        Account account = authenticateAndGetAccount(authCredentials.getUsername(), authCredentials.getPassword());
        String token = accountService.generateToken(account);

        return ResponseEntity.ok(token);
    }

    private Account authenticateAndGetAccount(String loginName, String password) {
        return authenticator.authenticateByCredentials(loginName, password);
    }
}
