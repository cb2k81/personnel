package de.cocondo.app.system.core.security.auth;

import de.cocondo.app.system.core.security.account.Account;
import de.cocondo.app.system.core.security.crypto.Credential;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.HandlerInterceptor;

public class AuthenticationInterceptor implements HandlerInterceptor {

    @Value("${api.security.token.header-name}")
    private String headerName;

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationInterceptor.class);

    private final Authenticator authService;

    public AuthenticationInterceptor(Authenticator authService) {
        this.authService = authService;
    }


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws InvalidCredentialsException {
        Credential credential = readBasicAuthCredentialsFromHttpHeader(request);

        if (credential != null) {
            logger.debug("Authentication via Basic Auth: User {}", credential.getIdentifier());
            Account authenticatedAccount = authenticateByCredentials(credential.getIdentifier(), credential.getPlainPassword());
            // Set the authenticated account in the request for later use if needed
            request.setAttribute("authenticatedAccount", authenticatedAccount);
            return true;
        }

        String apiToken = readApiTokenFromHttpHeader(request);
        if (apiToken != null) {
            logger.debug("Authentication via API Token");
            Account authenticatedAccount = authenticateByToken(apiToken);
            // Set the authenticated account in the request for later use if needed
            request.setAttribute("authenticatedAccount", authenticatedAccount);
            return true;
        }

        logger.error("Authentication failed");
        throw new InvalidCredentialsException("Authentication failed");
    }


    private Credential readBasicAuthCredentialsFromHttpHeader(HttpServletRequest request) {
        String username = request.getHeader("username");
        String password = request.getHeader("password");

        if (username == null || password == null) {
            return null;
        }

        return new Credential(username, password);
    }


    private Account authenticateByCredentials(String username, String plainPassword) {
        return authService.authenticateByCredentials(username, plainPassword);
    }


    private String readApiTokenFromHttpHeader(HttpServletRequest request) {
        return request.getHeader(this.headerName);
    }


    private Account authenticateByToken(String token) {
        return authService.authenticateByToken(token);
    }
}
