package de.cocondo.app.system.core.security.crypto.service;

import de.cocondo.app.system.core.security.crypto.SecretContainer;
import org.mindrot.jbcrypt.BCrypt;

public class CommonPasswordEncryptionService implements PasswordEncryptor {


    @Override
    public SecretContainer encryptPassword(String rawPassword) {
        String salt = generateSalt();
        return encryptPassword(rawPassword, salt);
    }

    public SecretContainer encryptPassword(String rawPassword, String salt) {
        SecretContainer sc = new SecretContainer();
        String hashedPassword = BCrypt.hashpw(rawPassword, salt);
        sc.setPassword(hashedPassword);
        sc.setSalt(salt);
        return sc;
    }


    public String generateSalt() {
        return BCrypt.gensalt();
    }


    @Override
    public boolean verifyPassword(String rawPassword, String hashedPassword, String salt) {
        if (salt == null) {
            return false;
        }
        SecretContainer sc = encryptPassword(rawPassword, salt);
        String passwordToCheck = sc.getPassword();
        return passwordToCheck.equals(hashedPassword);
    }


}
