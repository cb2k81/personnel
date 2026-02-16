package de.cocondo.app.system.batch;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;

/**
 * Initialisiert alle BatchJobDefinition-Einträge auf Basis
 * von Beans, die das Interface BatchJobMetadata implementieren.
 * Wird automatisch beim Anwendungsstart ausgeführt.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class BatchJobDefinitionInitializer implements CommandLineRunner {

    private final BatchJobDefinitionRepository jobDefinitionRepository;
    private final ApplicationContext applicationContext;

    @Override
    @Transactional
    public void run(String... args) {
        log.info("Starting BatchJobDefinition initialization...");

        // Finde alle Spring Beans, die das BatchJobMetadata Interface implementieren
        Map<String, BatchJobMetadata> jobMetadataBeans = applicationContext.getBeansOfType(BatchJobMetadata.class);

        if (jobMetadataBeans.isEmpty()) {
            log.info("No BatchJobMetadata beans found. Skipping initialization.");
            return;
        }

        jobMetadataBeans.forEach((beanName, metadata) -> {
            String jobName = metadata.getJobName();
            Optional<BatchJobDefinition> existingDef = jobDefinitionRepository.findByName(jobName);

            if (existingDef.isPresent()) {
                // Definition existiert bereits → ggf. aktualisieren
                BatchJobDefinition def = existingDef.get();
                boolean changed = false;

                if (!def.getDescription().equals(metadata.getJobDescription())) {
                    def.setDescription(metadata.getJobDescription());
                    changed = true;
                }

                if (def.isSingleInstance() != metadata.isSingleInstance()) {
                    def.setSingleInstance(metadata.isSingleInstance());
                    changed = true;
                }

                if (!java.util.Objects.equals(def.getMaxConcurrentExecutions(), metadata.getMaxConcurrentExecutions())) {
                    def.setMaxConcurrentExecutions(metadata.getMaxConcurrentExecutions());
                    changed = true;
                }

                if (!java.util.Objects.equals(def.getRunUsername(), metadata.getRunAsUsername())) {
                    def.setRunUsername(metadata.getRunAsUsername());
                    changed = true;
                }

                if (changed) {
                    def.setLastUpdated(Instant.now());
                    jobDefinitionRepository.save(def);
                    log.info("Updated BatchJobDefinition for '{}'.", jobName);
                } else {
                    log.debug("No changes for BatchJobDefinition '{}'. Skipped.", jobName);
                }

            } else {
                // Definition existiert noch nicht → neu anlegen
                BatchJobDefinition newDef = BatchJobDefinition.builder()
                        .name(jobName)
                        .description(metadata.getJobDescription())
                        .singleInstance(metadata.isSingleInstance())
                        .maxConcurrentExecutions(metadata.getMaxConcurrentExecutions())
                        .runUsername(metadata.getRunAsUsername())
                        .createdAt(Instant.now())
                        .lastUpdated(Instant.now())
                        .build();

                jobDefinitionRepository.save(newDef);
                log.info("Created new BatchJobDefinition for '{}'.", jobName);
            }
        });

        log.info("BatchJobDefinition initialization complete.");
    }
}
