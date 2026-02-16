package de.cocondo.app.system.core.context;

public class UserContextHolder {
    private static final ThreadLocal<String> currentUser = ThreadLocal.withInitial(() -> "system");

    public static String getCurrentUser() {
        return currentUser.get();
    }

    public static void setCurrentUser(String user) {
        currentUser.set(user);
    }

    public static void clear() {
        currentUser.remove();
    }
}
