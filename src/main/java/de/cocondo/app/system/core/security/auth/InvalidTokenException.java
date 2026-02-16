package de.cocondo.app.system.core.security.auth;

public class InvalidTokenException extends RuntimeException {

    public InvalidTokenException(String message) {
        super(message);
    }

}
