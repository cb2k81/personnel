package de.cocondo.app.system.core.config;

import de.cocondo.app.system.core.id.IdGeneratorService;
import de.cocondo.app.system.core.id.UUIDGenerationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CoreServiceBeanConfig {

    private static final Logger logger = LoggerFactory.getLogger(CoreServiceBeanConfig.class);

    @Bean
    public IdGeneratorService idGeneratorService() {
        logger.info("Creating IdGeneratorService bean");
        return new UUIDGenerationService();
    }

}
