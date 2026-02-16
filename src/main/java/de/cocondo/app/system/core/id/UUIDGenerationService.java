package de.cocondo.app.system.core.id;

import java.util.UUID;

public class UUIDGenerationService implements IdGeneratorService {

    public String generateId() {
        return generateUUIDasString();
    }

    public String generateUUIDasString() {
        return UUID.randomUUID().toString();
    }

}
