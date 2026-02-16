package de.cocondo.app.system.entity;

import de.cocondo.app.system.core.context.AppContextProvider;
import de.cocondo.app.system.core.id.IdGeneratorService;
import jakarta.persistence.PrePersist;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class DomainEntityListener {

    private static final Logger logger = LoggerFactory.getLogger(DomainEntityListener.class);

    @PrePersist
    public void setIdBeforePersist(DomainEntity entity) {
        logger.debug("DomainEntityListener triggered for Entity: {} with current ID: {}", entity.getClass().getSimpleName(), entity.getId());

        // Lookup des IdGeneratorService über AppContextProvider
        IdGeneratorService idGeneratorService = AppContextProvider.getBean(IdGeneratorService.class);

        if (entity.getId() == null) {
            entity.setId(idGeneratorService.generateId());
            logger.debug("Generated new ID: {} for Entity: {}", entity.getId(), entity.getClass().getSimpleName());
        }
    }
}
