package de.cocondo.app.system.core.security.config;

import de.cocondo.app.system.core.security.auth.Authenticator;
import de.cocondo.app.system.core.security.auth.AuthenticationInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class ApiSecurityConfig implements WebMvcConfigurer {

    private final Authenticator authService;

    private static final Logger logger = LoggerFactory.getLogger(ApiSecurityConfig.class);

    public ApiSecurityConfig(Authenticator authService) {
        this.authService = authService;
    }

    @Bean
    public AuthenticationInterceptor principalIdentificationInterceptor() {
        logger.info("Registering AuthenticationInterceptor bean");
        return new AuthenticationInterceptor(authService);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(principalIdentificationInterceptor())
                .addPathPatterns("/api/**");
    }
}
