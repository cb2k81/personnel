package de.cocondo.app.system.core.security.crypto.service;

import de.cocondo.app.system.core.security.crypto.SecretContainer;

public interface PasswordEncryptor {

    SecretContainer encryptPassword(String rawPassword);

    boolean verifyPassword(String rawPassword, String hashedPassword, String salt);

}
