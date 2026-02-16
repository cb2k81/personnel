package de.cocondo.app.system.core.security.permission;

public class PermissionDeniedException extends RuntimeException {

    public PermissionDeniedException() {
        super("Permission denied");
    }

    public PermissionDeniedException(String message) {
        super(message);
    }

    public PermissionDeniedException(String message, Throwable cause) {
        super(message, cause);
    }
}
