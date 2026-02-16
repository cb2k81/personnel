package de.cocondo.app.system.core.security.jwt.service;

import de.cocondo.app.system.core.config.permission.AccessSecurityPermissionSet;
import de.cocondo.app.system.core.security.jwt.component.JwtTokenGenerator;
import de.cocondo.app.system.core.security.jwt.component.JwtTokenValidator;
import de.cocondo.app.system.core.security.jwt.config.JwtConfig;
import de.cocondo.app.system.core.security.crypto.TokenManager;
import de.cocondo.app.system.core.security.permission.Permit;
import io.jsonwebtoken.Claims;

import java.io.Serializable;
import java.util.Map;

/**
 * This service provides all functionality for handling JWT.
 */
public class CommonJwtTokenService implements TokenManager {

    private final JwtTokenGenerator jwtTokenGenerator;
    private final JwtTokenValidator jwtTokenValidator;

    public CommonJwtTokenService(JwtConfig jwtConfig, JwtTokenGenerator jwtTokenGenerator, JwtTokenValidator jwtTokenValidator) {
        this.jwtTokenGenerator = jwtTokenGenerator;
        this.jwtTokenValidator = jwtTokenValidator;
    }

    // Method to generate a JWT token with roles and properties
    @Override
    public String generateToken(String subject, String[] roles, Map<String, Serializable> properties) {
        return jwtTokenGenerator.generateToken(subject, roles, properties);
    }

    @Override
    public String generateToken(String subject, String[] roles) {
        return generateToken(subject, roles, null);
    }

    @Override
    public String generateToken(String subject) {
        return generateToken(subject, null, null);
    }

    @Override
    @Permit(scope = AccessSecurityPermissionSet.SCOPE, value = AccessSecurityPermissionSet.GENERATE_PERMANENT_TOKEN)
    public String generatePermanentToken(String subject, String[] roles, Map<String, Serializable> properties) {
        return jwtTokenGenerator.generatePermanentToken(subject, roles, properties);
    }

    // Method to validate a JWT token and retrieve claims
    @Override
    public Boolean validateToken(String token) {
        return jwtTokenValidator.isValid(token);
    }

    // Method to extract Claims from token
    @Override
    public Claims decodeToken(String token) {
        return jwtTokenValidator.decodeToken(token);
    }

}
