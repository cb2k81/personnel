package de.cocondo.app.system.entity;

import de.cocondo.app.system.entity.metadata.KeyValuePair;
import jakarta.persistence.*;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@EntityListeners({DomainEntityListener.class, AuditingEntityListener.class})
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class DomainEntity implements Identifyable, Auditable, Taggable {

    private static final Logger logger = LoggerFactory.getLogger(DomainEntity.class);

    @Id
    String id;

    @Version
    private Long persistenceVersion;

    @ElementCollection(fetch = FetchType.EAGER)

    Set<String> tags = new HashSet<>();

    private String createdBy;
    private LocalDateTime createdAt;
    private String lastModifiedBy;
    private LocalDateTime lastModifiedAt;

    // Key-Value Store für zusätzliche Daten
    @OneToMany(mappedBy = "domainEntity", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    private Set<KeyValuePair> keyValuePairs = new HashSet<>();

    // Hinzufügen eines Key-Value-Paares
    public void addKeyValue(String key, String value) {
        logger.debug("Attempting to add key-value pair: key='{}', value='{}' to entity with ID '{}'", key, value, this.getId());

        // Überprüfen, ob die Entity eine gültige ID hat
        if (this.getId() == null || this.getId().isEmpty()) {
            logger.error("Entity with ID '{}' must have a valid ID before adding key-value pairs", this.getId());
            throw new IllegalStateException("Entity must have a valid ID before adding key-value pairs");
        }

        // Überprüfen, ob der Key bereits existiert und ggf. überschreiben
        KeyValuePair existingKvPair = keyValuePairs.stream()
                .filter(kvPair -> kvPair.getKey().equals(key) && kvPair.getEntityId().equals(this.getId()))
                .findFirst()
                .orElse(null);

        if (existingKvPair != null) {
            // Wenn der Key bereits existiert, überschreiben
            logger.warn("Key '{}' already exists for entity with ID '{}'. Overwriting with new value.", key, this.getId());
            existingKvPair.setValue(value); // Überschreibe den Wert
        } else {
            // Wenn der Key nicht existiert, neuen Key-Wert-Paar hinzufügen
            KeyValuePair kvPair = new KeyValuePair();
            kvPair.setEntityId(this.getId()); // Setze die ID der DomainEntity
            kvPair.setKey(key);
            kvPair.setValue(value);
            kvPair.setDomainEntity(this);
            keyValuePairs.add(kvPair);
        }
    }


    // Entfernen eines Key-Value-Paares
    public void removeKeyValue(String key) {
        logger.debug("Removing key-value pair with key='{}' from entity with ID '{}'", key, this.getId());
        keyValuePairs.removeIf(kvPair -> kvPair.getKey().equals(key) && kvPair.getEntityId().equals(this.getId()));
    }

    // Wert nach Schlüssel abrufen
    public String getValueByKey(String key) {
        logger.debug("Retrieving value for key='{}' from entity with ID '{}'", key, this.getId());
        return keyValuePairs.stream()
                .filter(kvPair -> kvPair.getKey().equals(key) && kvPair.getEntityId().equals(this.getId()))
                .map(KeyValuePair::getValue)
                .findFirst()
                .orElse(null);
    }

    // Alle Key-Value-Paare abrufen
    public Set<KeyValuePair> getAllKeyValues() {
        logger.debug("Retrieving all key-value pairs from entity with ID '{}'", this.getId());
        return keyValuePairs;
    }
}
