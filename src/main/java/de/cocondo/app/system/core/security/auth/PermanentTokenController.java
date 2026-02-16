package de.cocondo.app.system.core.security.auth;


import de.cocondo.app.system.core.context.RequestContextDataContainer;
import de.cocondo.app.system.core.security.crypto.TokenManager;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/permanent-token")
public class PermanentTokenController {

    private final TokenManager tokenManager;

    private final RequestContextDataContainer requestContextDataContainer;

    public PermanentTokenController(TokenManager tokenManager, RequestContextDataContainer requestContextDataContainer) {
        this.tokenManager = tokenManager;
        this.requestContextDataContainer = requestContextDataContainer;
    }

    @PostMapping
    public ResponseEntity<String> generatePermanentToken() {
        String username = requestContextDataContainer.getCurrentAccount().getLoginName();
        String token = tokenManager.generatePermanentToken(username, null, null);

        return ResponseEntity.ok(token);
    }

}
