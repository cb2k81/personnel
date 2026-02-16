package de.cocondo.app.system.core.security.jwt.component;

import de.cocondo.app.system.core.security.jwt.exception.InvalidJwtTokenException;
import de.cocondo.app.system.core.security.jwt.config.JwtConfig;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.crypto.SecretKey;
import java.util.Date;

/**
 * This class is responsible for JWT token validation.
 */
public class JwtTokenValidator {

    private static final Logger logger = LoggerFactory.getLogger(JwtTokenValidator.class);

    private final JwtConfig jwtConfig;
    private final SecretKey secretKey;

    @Autowired
    public JwtTokenValidator(JwtConfig jwtConfig) {
        this.jwtConfig = jwtConfig;
        this.secretKey = Keys.hmacShaKeyFor(jwtConfig.getSecret().getBytes());
    }

    public boolean isValid(String token) {
        try {
            validateToken(token);
            return true;
        } catch (ExpiredJwtException e) {
            logger.warn("Token has expired");
            return false;
        } catch (Exception e) {
            logger.error("An unexpected error occurred during token validation", e);
            throw new RuntimeException("Token validation failed", e);
        }
    }

    private void validateToken(String token) {
        Claims claims = parseToken(token);
        if (isTokenExpired(claims)) {
            throw new ExpiredJwtException(null, null, "Token has expired");
        }
    }

    private Claims parseToken(String token) throws InvalidJwtTokenException, ExpiredJwtException {
        if (token == null) {
            throw new InvalidJwtTokenException("Token is null");
        }
        try {
            Jws<Claims> jws = Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token);

            return jws.getBody();
        } catch (ExpiredJwtException e) {
            // bubble up expired exception
            throw e;
        } catch (JwtException e) {
            logger.error("Invalid JWT token", e);
            // Something went wrong parsing the token
            throw new InvalidJwtTokenException("Invalid JWT token", e);
        }
    }


    private boolean isTokenExpired(Claims claims) {
        Date expirationDate = claims.getExpiration();
        Date now = new Date();
        return expirationDate != null && expirationDate.before(now);
    }

    public Claims decodeToken(String token) {
        return parseToken(token);
    }

}
