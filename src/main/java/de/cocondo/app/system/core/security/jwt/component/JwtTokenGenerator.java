package de.cocondo.app.system.core.security.jwt.component;

import de.cocondo.app.system.core.security.jwt.config.JwtConfig;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.security.Key;
import java.util.Date;
import java.util.Map;

public class JwtTokenGenerator {

    private final JwtConfig jwtConfig;

    private static final Logger logger = LoggerFactory.getLogger(JwtTokenGenerator.class);

    public JwtTokenGenerator(JwtConfig jwtConfig) {
        this.jwtConfig = jwtConfig;
    }

    public String generateToken(String subject, String[] roles, Map<String, Serializable> properties) {
        return generateToken(subject, roles, properties, true);
    }

    public String generatePermanentToken(String subject, String[] roles, Map<String, Serializable> properties) {
        return generateToken(subject, roles, properties, false);
    }

    public String generateToken(String subject, String[] roles, Map<String, Serializable> properties, boolean expires) {
        logger.debug("Generating token for subject: " + subject);
        Key key = Keys.hmacShaKeyFor(jwtConfig.getSecret().getBytes());

        JwtBuilder jwtBuilder = Jwts.builder()
                .setSubject(subject)
                .signWith(key, SignatureAlgorithm.HS512);

        if (roles != null && roles.length > 0) {
            jwtBuilder.claim("roles", roles);
        }

        if (properties != null && !properties.isEmpty()) {
            for (Map.Entry<String, Serializable> entry : properties.entrySet()) {
                jwtBuilder.claim(entry.getKey(), entry.getValue());
            }
        }

        if (expires) {
            jwtBuilder.setExpiration(new Date(System.currentTimeMillis() + jwtConfig.getExpiration() * 1000));
        }

        String token = jwtBuilder.compact();
        logger.debug("Generated token:" + token);

        return token;
    }

}
