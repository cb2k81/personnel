package de.cocondo.app.system.mapper;

import de.cocondo.app.system.core.context.AppContextProvider;
import de.cocondo.app.system.core.id.IdGeneratorService;
import de.cocondo.app.system.dto.DomainEntityInboundDTO;
import de.cocondo.app.system.entity.DomainEntity;
import de.cocondo.app.system.entity.metadata.KeyValuePair;
import de.cocondo.app.system.entity.metadata.KeyValuePairDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;

public interface DTOToDomainEntityMapper<D extends DomainEntityInboundDTO, E extends DomainEntity> extends DTOToEntityMapper<D, E> {

    Logger logger = LoggerFactory.getLogger(DTOToDomainEntityMapper.class);

    @Override
    default void toEntity(D dto, E entity) {
        IdGeneratorService idGeneratorService = AppContextProvider.getBean(IdGeneratorService.class);

        String entityType = entity.getClass().getSimpleName();
        String dtoType = dto.getClass().getSimpleName();

        logger.info("Starting mapping of DTO type {} to entity type {}", dtoType, entityType);

        if (entity.getId() == null || entity.getId().isEmpty()) {
            if (idGeneratorService == null) {
                throw new RuntimeException("Bean not found: " + IdGeneratorService.class.getCanonicalName());
            }
            // Generiere eine neue ID
            String generatedId = idGeneratorService.generateId();
            entity.setId(generatedId);
            logger.info("Generated new ID for entity of type {} (DTO type: {}): {}", entityType, dtoType, generatedId);
        }

        // Metadaten mapping inklusive Logging der Änderungen
        mapMetadata(dto, entity, dtoType, entityType);
    }

    default void mapMetadata(D dto, E entity, String dtoType, String entityType) {
        logger.info("Mapping metadata for entity type {} (DTO type: {}, ID: {})", entityType, dtoType, entity.getId());
        mapTags(dto.getTags(), entity, dtoType, entityType);
        mapKeyValues(dto.getKeyValuePairs(), entity, dtoType, entityType);
    }

    default void mapTags(Set<String> tags, E entity, String dtoType, String entityType) {
        if (tags != null) {
            entity.setTags(new HashSet<>(tags));
            logger.info("Mapped tags for entity type {} (DTO type: {}, ID: {}): {}", entityType, dtoType, entity.getId(), tags);
        } else {
            logger.info("No tags to map for entity type {} (DTO type: {}, ID: {})", entityType, dtoType, entity.getId());
        }
    }

    default void mapKeyValues(Set<KeyValuePairDTO> keyValuePairs, E entity, String dtoType, String entityType) {
        if (keyValuePairs != null) {
            entity.getKeyValuePairs().clear();
            keyValuePairs.forEach(kvDto -> {
                KeyValuePair keyValue = new KeyValuePair();
                keyValue.setKey(kvDto.getKey());
                keyValue.setValue(kvDto.getValue());
                keyValue.setDomainEntity(entity);
                entity.getKeyValuePairs().add(keyValue);
            });
            logger.info("Mapped key-value pairs for entity type {} (DTO type: {}, ID: {}): {}", entityType, dtoType, entity.getId(), keyValuePairs);
        } else {
            logger.info("No key-value pairs to map for entity type {} (DTO type: {}, ID: {})", entityType, dtoType, entity.getId());
        }
    }
}
