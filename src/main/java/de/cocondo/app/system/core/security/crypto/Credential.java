package de.cocondo.app.system.core.security.crypto;

public class Credential {

    private String identifier;

    private String plainPassword;

    public Credential(String identifier, String password) {
        this.identifier = identifier;
        this.plainPassword = password;
    }


    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getPlainPassword() {
        return plainPassword;
    }

    public void setPlainPassword(String plainPassword) {
        this.plainPassword = plainPassword;
    }
}
