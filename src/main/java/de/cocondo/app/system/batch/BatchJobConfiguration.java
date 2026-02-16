package de.cocondo.app.system.batch;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.support.JobRegistryBeanPostProcessor;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.batch.core.launch.support.SimpleJobOperator;
import org.springframework.batch.core.launch.support.TaskExecutorJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Zentrale Konfiguration für das Spring Batch Framework in der Anwendung.
 * Aktiviert Spring Batch, konfiguriert den asynchronen JobLauncher mit
 * einem ThreadPoolTaskExecutor und stellt JobRegistry-/JobOperator-Beans bereit.
 */
@Configuration
@EnableBatchProcessing  // Aktiviert Kernfunktionalitäten von Spring Batch
@EnableAsync            // Ermöglicht @Async (optional für andere Async-Aufgaben)
@RequiredArgsConstructor
@Slf4j
public class BatchJobConfiguration {

    private final JobRegistry jobRegistry;

    /**
     * TaskExecutor für asynchrone Job-Starts.
     * ThreadPoolTaskExecutor verwaltet einen Pool und wartet auf Tasks beim Shutdown.
     */
    @Bean
    public ThreadPoolTaskExecutor jobLauncherTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(4);
        executor.setMaxPoolSize(8);
        executor.setThreadNamePrefix("batch-job-");
        executor.setWaitForTasksToCompleteOnShutdown(false);
        executor.setAwaitTerminationSeconds(60); // ← wartet max. 60 Sek. auf Terminierung (ShutdownNow bei wait=false)
        executor.initialize();
        log.info("Initialized ThreadPoolTaskExecutor for batch jobs: core={}, max={}",
                executor.getCorePoolSize(), executor.getMaxPoolSize());
        return executor;
    }


    /**
     * Überschreibt den Standard-JobLauncher durch einen asynchronen Launcher.
     * Der Bean-Name "jobLauncher" ist entscheidend, damit Spring Batch diesen verwendet.
     */
    @Bean(name = "jobLauncher")
    @Primary
    public JobLauncher jobLauncher(JobRepository jobRepository,
                                   TaskExecutor jobLauncherTaskExecutor) throws Exception {
        TaskExecutorJobLauncher launcher = new TaskExecutorJobLauncher();
        launcher.setJobRepository(jobRepository);
        launcher.setTaskExecutor(jobLauncherTaskExecutor);
        launcher.afterPropertiesSet();
        log.info("Configured TaskExecutorJobLauncher with ThreadPoolTaskExecutor");
        return launcher;
    }

    /**
     * Stellt sicher, dass alle @Bean-Jobs in die Spring Batch Registry eingetragen werden.
     * Die Methode ist static, um Lifecycle-Probleme zu vermeiden.
     */
    @Bean
    public static JobRegistryBeanPostProcessor jobRegistryBeanPostProcessor(JobRegistry jobRegistry) {
        JobRegistryBeanPostProcessor pp = new JobRegistryBeanPostProcessor();
        pp.setJobRegistry(jobRegistry);
        return pp;
    }

    /**
     * JobOperator für Job-Steuerung zur Laufzeit (z.B. stop, restart).
     */
    @Bean
    public JobOperator jobOperator(JobExplorer jobExplorer,
                                   @Qualifier("jobLauncher") JobLauncher jobLauncher,
                                   JobRegistry jobRegistry,
                                   JobRepository jobRepository) throws Exception {
        SimpleJobOperator operator = new SimpleJobOperator();
        operator.setJobExplorer(jobExplorer);
        operator.setJobLauncher(jobLauncher);
        operator.setJobRegistry(jobRegistry);
        operator.setJobRepository(jobRepository);
        operator.afterPropertiesSet();
        log.info("Initialized JobOperator for batch job control");
        return operator;
    }
}

