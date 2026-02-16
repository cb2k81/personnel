package de.cocondo.app.system.core.security.crypto;

import io.jsonwebtoken.Claims;

import java.io.Serializable;
import java.util.Map;

public interface TokenManager {

    // Method to generate a JWT token with roles and properties
    String generateToken(String subject, String[] roles, Map<String, Serializable> properties);

    String generateToken(String subject, String[] roles);

    String generateToken(String subject);

    String generatePermanentToken(String subject, String[] roles, Map<String, Serializable> properties);

    // Method to validate a JWT token and retrieve claims
    Boolean validateToken(String token);

    // Method to extract Claims from token
    Claims decodeToken(String token);

}
