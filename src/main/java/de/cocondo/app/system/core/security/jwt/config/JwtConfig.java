package de.cocondo.app.system.core.security.jwt.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JwtConfig {

    @Value("${jwt.secret:asdf43no43fnnuoasnff46n3u4qntq43f3quinfc2f}")
    private String secret;

    @Value("${jwt.expiration:3600}")
    private long expiration;

    public String getSecret() {
        return secret;
    }

    public long getExpiration() {
        return expiration;
    }
}
