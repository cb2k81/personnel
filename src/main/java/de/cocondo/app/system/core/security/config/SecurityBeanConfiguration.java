package de.cocondo.app.system.core.security.config;

import de.cocondo.app.system.core.context.RequestContextDataContainer;
import de.cocondo.app.system.core.security.account.AccountCrudService;
import de.cocondo.app.system.core.security.auth.CommonAuthenticationService;
import de.cocondo.app.system.core.security.auth.Authenticator;
import de.cocondo.app.system.core.security.crypto.service.CommonPasswordEncryptionService;
import de.cocondo.app.system.core.security.crypto.service.PasswordEncryptor;
import de.cocondo.app.system.core.security.jwt.component.JwtTokenGenerator;
import de.cocondo.app.system.core.security.jwt.component.JwtTokenValidator;
import de.cocondo.app.system.core.security.jwt.config.JwtConfig;
import de.cocondo.app.system.core.security.jwt.service.CommonJwtTokenService;
import de.cocondo.app.system.core.security.crypto.TokenManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class SecurityBeanConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(SecurityBeanConfiguration.class);

    @Bean
    public PasswordEncryptor passwordEncryptor() {
        logger.info("Registering PasswordEncryptor bean");
        return new CommonPasswordEncryptionService();
    }

    private final RequestContextDataContainer requestContextDataContainer;

    public SecurityBeanConfiguration(RequestContextDataContainer requestContextDataContainer) {
        this.requestContextDataContainer = requestContextDataContainer;
    }

    @Bean
    public Authenticator authenticator(AccountCrudService accountService, TokenManager tokenManager) {
        logger.info("Registering Authenticator bean");
        return new CommonAuthenticationService(accountService, tokenManager, requestContextDataContainer);
    }

    @Bean
    @Primary
    public TokenManager tokenService(JwtConfig jwtConfig, JwtTokenGenerator jwtTokenGenerator, JwtTokenValidator jwtTokenValidator) {
        logger.info("Registering Primary TokenManager bean");
        return new CommonJwtTokenService(jwtConfig, jwtTokenGenerator, jwtTokenValidator);
    }

    @Bean
    public JwtTokenGenerator jwtTokenGenerator(JwtConfig jwtConfig) {
        logger.info("Registering JwtTokenGenerator bean");
        // Return the JWT token generator component
        return new JwtTokenGenerator(jwtConfig);
    }

    @Bean
    public JwtTokenValidator jwtTokenValidator(JwtConfig jwtConfig) {
        logger.info("Registering JwtTokenValidator bean");
        // Return the JWT token validator component
        return new JwtTokenValidator(jwtConfig);
    }

}
