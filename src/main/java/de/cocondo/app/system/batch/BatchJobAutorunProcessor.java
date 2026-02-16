package de.cocondo.app.system.batch;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Startet alle BatchJobs automatisch, die in ihrer Metadaten-Konfiguration
 * das Flag isAutoStart() = true gesetzt haben.
 * Wird nach dem Initialisieren des Spring-Kontexts ausgeführt.
 */
@Component
@DependsOn("batchJobDefinitionInitializer")
@RequiredArgsConstructor
@Slf4j
public class BatchJobAutorunProcessor {

    private final ApplicationContext context;
    private final BatchJobDefinitionRepository definitionRepository;
    private final BatchJobService batchJobService;

    @PostConstruct
    @EventListener(ApplicationReadyEvent.class)
    public void triggerAutoStartJobs() {
        log.info("Checking for auto-start BatchJobs...");

        Map<String, BatchJobMetadata> jobs = context.getBeansOfType(BatchJobMetadata.class);

        jobs.values().forEach(meta -> {
            if (!meta.isAutoStart()) return;

            // BatchJobDefinition aus der DB finden (muss durch Initializer bereits angelegt sein)
            definitionRepository.findByName(meta.getJobName()).ifPresentOrElse(def -> {
                try {
                    batchJobService.startJob(def.getId(), Map.of());
                    log.info("Auto-started BatchJob '{}' (id={})", def.getName(), def.getId());
                } catch (Exception e) {
                    log.warn("Auto-start failed for Job '{}': {}", def.getName(), e.getMessage(), e);
                }
            }, () -> {
                log.warn("Skipping auto-start for '{}': definition not found in DB.", meta.getJobName());
            });
        });

        log.info("Auto-start BatchJob processing complete.");
    }
}
