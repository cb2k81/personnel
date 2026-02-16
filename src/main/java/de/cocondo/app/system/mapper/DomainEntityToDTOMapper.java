package de.cocondo.app.system.mapper;

import de.cocondo.app.system.dto.DomainEntityDTO;
import de.cocondo.app.system.dto.DomainEntityMetadataDTO;
import de.cocondo.app.system.entity.DomainEntity;
import de.cocondo.app.system.entity.metadata.KeyValuePairDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;
import java.util.stream.Collectors;

public interface DomainEntityToDTOMapper<E extends DomainEntity, D extends DomainEntityDTO> extends EntityToDTOMapper<E, D> {

    Logger logger = LoggerFactory.getLogger(DomainEntityToDTOMapper.class);

    default void mapMetadata(E entity, D dto) {
        // Erstellen des MetadataDTO
        DomainEntityMetadataDTO metadataDTO = new DomainEntityMetadataDTO();

        // Null-Checks und Logging für die Metadaten
        if (entity.getId() == null) {
            logger.warn("Entity ID is null");
            metadataDTO.setId("N/A");
        } else {
            metadataDTO.setId(entity.getId());
        }

        if (entity.getCreatedBy() == null) {
            logger.warn("CreatedBy is null");
            metadataDTO.setCreatedBy("N/A");
        } else {
            metadataDTO.setCreatedBy(entity.getCreatedBy());
        }

        if (entity.getCreatedAt() == null) {
            logger.warn("CreatedAt is null");
            metadataDTO.setCreatedAt(null); // oder setze einen Standardwert
        } else {
            metadataDTO.setCreatedAt(entity.getCreatedAt());
        }

        if (entity.getLastModifiedBy() == null) {
            logger.warn("LastModifiedBy is null");
            metadataDTO.setLastModifiedBy("N/A");
        } else {
            metadataDTO.setLastModifiedBy(entity.getLastModifiedBy());
        }

        if (entity.getLastModifiedAt() == null) {
            logger.warn("LastModifiedAt is null");
            metadataDTO.setLastModifiedAt(null); // oder setze einen Standardwert
        } else {
            metadataDTO.setLastModifiedAt(entity.getLastModifiedAt());
        }

        // Füllen der Tags
        metadataDTO.setTags(entity.getTags());

        // Konvertieren der Key-Value-Pairs in DTOs
        if (entity.getKeyValuePairs() != null) {
            Set<KeyValuePairDTO> keyValuePairsDTO = entity.getKeyValuePairs()
                    .stream()
                    .map(pair -> {
                        KeyValuePairDTO dtoPair = new KeyValuePairDTO();
                        dtoPair.setKey(pair.getKey());
                        dtoPair.setValue(pair.getValue());
                        return dtoPair;
                    })
                    .collect(Collectors.toSet());
            metadataDTO.setKeyValuePairs(keyValuePairsDTO);
        } else {
            metadataDTO.setKeyValuePairs(Set.of()); // Leeres Set, wenn null
        }

        // Setzen der Metadaten im DTO
        dto.setMetadata(metadataDTO);
    }
}
