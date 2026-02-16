package de.cocondo.app.system.batch;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobExecutionNotRunningException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.batch.core.launch.NoSuchJobExecutionException;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Service
@RequiredArgsConstructor
public class BatchJobService {

    public static final String PARAM_BATCH_JOB_ITEM_ID = "batchJobItemId";
    public static final String PARAM_BATCH_JOB_RUN_ID  = "batchJobRunId";
    public static final String PARAM_RUN_USERNAME      = "runUsername";
    public static final String PARAM_RESTART_MODE      = "restartMode";

    /**
     * Technischer Uniquifier, um bei START immer eine neue Spring JobInstance zu erzwingen.
     */
    public static final String PARAM_SPRING_INSTANCE_UNIQUIFIER = "springInstanceUniquifier";

    /**
     * Optionaler Parameter für robustes Starten über JobName, wenn Definition-IDs zwischen Umgebungen driften.
     */
    public static final String PARAM_JOB_NAME = "jobName";

    private final BatchJobDefinitionRepository definitionRepository;
    private final BatchJobItemRepository itemRepository;
    private final BatchJobRunRepository runRepository;

    private final BatchJobRegistry batchJobRegistry;

    private final JobLauncher jobLauncher;
    private final JobExplorer jobExplorer;
    private final JobOperator jobOperator;

    private final Map<String, AtomicInteger> runningCounts = new ConcurrentHashMap<>();

    // =========================================================
    // START
    // =========================================================
    public BatchJobItem startJob(Long definitionId, Map<String, String> parameters) {

        BatchJobDefinition definition = resolveOrCreateDefinition(definitionId, parameters);

        Job job = batchJobRegistry.findJobByName(definition.getName())
                .orElseThrow(() -> new IllegalStateException("Spring Batch Job not found: " + definition.getName()));

        BatchJobItem item = BatchJobItem.builder()
                .jobDefinition(definition)
                .correlationId(UUID.randomUUID().toString())
                .startedAt(Instant.now())
                .status(BatchItemStatus.RUNNING)
                .totalCount(0)
                .processedCount(0)
                .lastUpdated(Instant.now())
                .runUsername(definition.getRunUsername())
                .build();

        itemRepository.save(item);

        String restartMode = parameters.getOrDefault(
                PARAM_RESTART_MODE,
                BatchJobRestartMode.RESUME.name()
        );

        JobParametersBuilder params = new JobParametersBuilder()
                // zwingend: START muss immer eine neue Spring JobInstance erzeugen
                .addString(PARAM_SPRING_INSTANCE_UNIQUIFIER, UUID.randomUUID().toString(), true)

                // fachliche/technische Zuordnung (nicht-identifizierend)
                .addLong(PARAM_BATCH_JOB_ITEM_ID, item.getId(), false)
                .addString(PARAM_RUN_USERNAME, item.getRunUsername(), false)
                .addString(PARAM_RESTART_MODE, restartMode, false);

        try {
            JobExecution execution = jobLauncher.run(job, params.toJobParameters());

            Long instanceId = execution.getJobInstance().getId();
            Long executionId = execution.getId();

            BatchJobRun run = BatchJobRun.builder()
                    .batchJobItem(item)
                    .action(BatchJobRunAction.START)
                    .restartMode(null)
                    .springJobInstanceId(instanceId)
                    .springJobExecutionId(executionId)
                    .startedAt(Instant.now())
                    .build();

            runRepository.save(run);

            // Current Run + "latest technical instance"
            item.setCurrentRunId(run.getId());
            item.setSpringJobInstanceId(instanceId);
            itemRepository.save(item);

            registerRunning(definition.getName());

            log.info(
                    "BatchJob '{}' started (jobItemId={}, runId={}, springJobInstanceId={}, springExecutionId={}, restartMode={})",
                    definition.getName(),
                    item.getId(),
                    run.getId(),
                    instanceId,
                    executionId,
                    restartMode
            );

            return item;

        } catch (JobExecutionAlreadyRunningException |
                 JobRestartException |
                 JobInstanceAlreadyCompleteException |
                 org.springframework.batch.core.JobParametersInvalidException ex) {

            item.setStatus(BatchItemStatus.FAILED);
            item.setFinishedAt(Instant.now());
            item.setErrorMessage(ex.getMessage());
            itemRepository.save(item);

            throw new IllegalStateException(
                    "Failed to start Spring Batch Job: " + definition.getName(),
                    ex
            );
        }
    }

    /**
     * Robust resolution of a BatchJobDefinition:
     * 1) findById(definitionId)
     * 2) if body contains jobName -> findByName(jobName) else create
     * 3) index fallback: if definitionId looks like "1..N" but IDs drifted -> map to Nth definition ordered by id
     */
    private BatchJobDefinition resolveOrCreateDefinition(Long definitionId, Map<String, String> parameters) {

        // 1) strict ID lookup
        Optional<BatchJobDefinition> byId = definitionRepository.findById(definitionId);
        if (byId.isPresent()) {
            return byId.get();
        }

        // 2) explicit jobName fallback (preferred over index fallback)
        String jobName = parameters != null ? parameters.get(PARAM_JOB_NAME) : null;
        if (jobName != null && !jobName.isBlank()) {
            Optional<BatchJobDefinition> byName = definitionRepository.findByName(jobName);
            if (byName.isPresent()) {
                log.warn("BatchJobDefinition id={} not found. Resolved by jobName='{}' → definitionId={}.",
                        definitionId, jobName, byName.get().getId());
                return byName.get();
            }

            // Create minimal definition on demand (DB not seeded / drift / manual cleanup)
            BatchJobDefinition created = BatchJobDefinition.builder()
                    .name(jobName)
                    .description("Auto-created (missing in DB). Please verify metadata seeding.")
                    .singleInstance(true)               // safest default
                    .maxConcurrentExecutions(null)
                    .runUsername("system")              // safe fallback
                    .createdAt(Instant.now())
                    .lastUpdated(Instant.now())
                    .build();

            definitionRepository.save(created);

            log.warn("BatchJobDefinition missing in DB. Auto-created definition for jobName='{}' with id={}.",
                    jobName, created.getId());

            return created;
        }

        // 3) index fallback: map "1" -> first definition (ORDER BY id ASC)
        // This makes manual/API starts robust across environments when IDs drift.
        if (definitionId != null && definitionId > 0) {
            List<BatchJobDefinition> all = definitionRepository.findAll()
                    .stream()
                    .sorted(Comparator.comparing(BatchJobDefinition::getId))
                    .toList();

            int idx = (int) (definitionId - 1);
            if (idx >= 0 && idx < all.size()) {
                BatchJobDefinition mapped = all.get(idx);
                log.warn("BatchJobDefinition id={} not found. Interpreting as index → mapped to definitionId={} (jobName='{}').",
                        definitionId, mapped.getId(), mapped.getName());
                return mapped;
            }
        }

        throw new IllegalArgumentException("Unknown BatchJobDefinition: " + definitionId);
    }

    // =========================================================
    // RESTART / RESUME
    // =========================================================
    public BatchJobItem restartJobItem(Long jobItemId, BatchJobRestartMode mode) {

        BatchJobItem item = itemRepository.findById(jobItemId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Unknown BatchJobItem: " + jobItemId));

        BatchJobDefinition definition = item.getJobDefinition();
        Job job = batchJobRegistry.findJobByName(definition.getName())
                .orElseThrow(() -> new IllegalStateException(
                        "Spring Batch Job not found: " + definition.getName()));

        // zusätzliche fachliche Guardrails:
        guardNoActiveExecution(item, mode);

        if (mode == BatchJobRestartMode.RESUME && item.getStatus() != BatchItemStatus.STOPPED) {
            throw new BatchJobConflictException(
                    "BatchJobItem " + item.getId()
                            + " is " + item.getStatus()
                            + ". Operation '" + mode.name() + "' is only allowed when status=STOPPED.");
        }
        if (mode == BatchJobRestartMode.RESET && item.getStatus() == BatchItemStatus.RUNNING) {
            throw new BatchJobConflictException(
                    "BatchJobItem " + item.getId()
                            + " is RUNNING. Operation '" + mode.name() + "' is not allowed.");
        }

        BatchJobRun run = BatchJobRun.builder()
                .batchJobItem(item)
                .action(mode == BatchJobRestartMode.RESUME
                        ? BatchJobRunAction.RESUME
                        : BatchJobRunAction.RESET)
                .restartMode(mode.name())
                .startedAt(Instant.now())
                .build();

        runRepository.save(run);

        JobParametersBuilder params = new JobParametersBuilder()
                // neue Spring JobInstance für RESUME/RESET, deterministisch über runId identifizierend
                .addLong(PARAM_BATCH_JOB_RUN_ID, run.getId(), true)

                // fachliche Zuordnung (nicht-identifizierend)
                .addLong(PARAM_BATCH_JOB_ITEM_ID, item.getId(), false)
                .addString(PARAM_RUN_USERNAME,
                        item.getRunUsername() != null
                                ? item.getRunUsername()
                                : definition.getRunUsername(),
                        false)
                .addString(PARAM_RESTART_MODE, mode.name(), false);

        item.setStatus(BatchItemStatus.RUNNING);
        item.setStartedAt(Instant.now());
        item.setFinishedAt(null);
        item.setErrorMessage(null);

        // Current run pointer sofort setzen (Prozess baut darauf auf)
        item.setCurrentRunId(run.getId());
        itemRepository.save(item);

        try {
            JobExecution execution = jobLauncher.run(job, params.toJobParameters());

            Long instanceId = execution.getJobInstance().getId();
            Long executionId = execution.getId();

            item.setSpringJobInstanceId(instanceId);
            itemRepository.save(item);

            run.setSpringJobInstanceId(instanceId);
            run.setSpringJobExecutionId(executionId);
            runRepository.save(run);

            registerRunning(definition.getName());

            log.info(
                    "BatchJob '{}' {} STARTED (jobItemId={}, runId={}, springJobInstanceId={}, springExecutionId={})",
                    definition.getName(),
                    mode.name(),
                    item.getId(),
                    run.getId(),
                    instanceId,
                    executionId
            );

            return item;

        } catch (JobExecutionAlreadyRunningException |
                 JobRestartException |
                 JobInstanceAlreadyCompleteException |
                 org.springframework.batch.core.JobParametersInvalidException ex) {

            failItem(item, ex.getMessage());

            throw new IllegalStateException(
                    "Failed to " + mode.name() + " Spring Batch Job: " + definition.getName(), ex);
        }
    }

    // =========================================================
    // STOP
    // =========================================================
    @Transactional
    public void stopJob(Long jobItemId) {

        BatchJobItem item = itemRepository.findById(jobItemId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Unknown BatchJobItem: " + jobItemId));

        // bevorzugt: STOP immer gegen Current-Run / konkrete Execution
        Long currentRunId = item.getCurrentRunId();
        if (currentRunId != null) {
            runRepository.findById(currentRunId).ifPresentOrElse(run -> {

                Long executionId = run.getSpringJobExecutionId();
                if (executionId == null) {
                    log.warn("STOP fallback: current run has no springJobExecutionId (jobItemId={}, runId={})",
                            jobItemId, currentRunId);
                    stopByInstanceFallback(item);
                    return;
                }

                JobExecution exec = jobExplorer.getJobExecution(executionId);
                if (exec == null) {
                    log.warn("STOP fallback: JobExecution not found (jobItemId={}, runId={}, springExecutionId={})",
                            jobItemId, currentRunId, executionId);
                    stopByInstanceFallback(item);
                    return;
                }

                switch (exec.getStatus()) {
                    case STARTING:
                    case STARTED:
                        try {
                            jobOperator.stop(executionId);
                            log.info("STOP requested (jobItemId={}, runId={}, springExecutionId={})",
                                    jobItemId, currentRunId, executionId);
                        } catch (NoSuchJobExecutionException | JobExecutionNotRunningException e) {
                            throw new IllegalStateException(
                                    "Stop failed: JobExecution not stoppable (executionId=" + executionId + ")", e);
                        }
                        break;

                    case STOPPING:
                        log.info("STOP already in progress (jobItemId={}, runId={}, springExecutionId={})",
                                jobItemId, currentRunId, executionId);
                        break;

                    case STOPPED:
                    case COMPLETED:
                    case FAILED:
                        log.info("STOP ignored: JobExecution already finished (status={}, jobItemId={}, runId={}, springExecutionId={})",
                                exec.getStatus(), jobItemId, currentRunId, executionId);
                        break;

                    default:
                        log.warn("STOP ignored: unexpected JobExecution status={} (jobItemId={}, runId={}, springExecutionId={})",
                                exec.getStatus(), jobItemId, currentRunId, executionId);
                }

            }, () -> {
                log.warn("STOP fallback: currentRunId points to missing run (jobItemId={}, currentRunId={})",
                        jobItemId, currentRunId);
                stopByInstanceFallback(item);
            });

            return;
        }

        // Fallback: historisches Verhalten (springJobInstanceId)
        stopByInstanceFallback(item);
    }

    private void stopByInstanceFallback(BatchJobItem item) {

        Long jobItemId = item.getId();

        Long springInstanceId = item.getSpringJobInstanceId();
        if (springInstanceId == null) {
            throw new IllegalStateException(
                    "BatchJobItem has no springJobInstanceId: " + jobItemId);
        }

        var jobInstance = jobExplorer.getJobInstance(springInstanceId);
        if (jobInstance == null) {
            log.warn("STOP ignored: no JobInstance found (jobItemId={}, springJobInstanceId={})",
                    jobItemId, springInstanceId);
            return;
        }

        var executions = jobExplorer.getJobExecutions(jobInstance);
        if (executions == null || executions.isEmpty()) {
            log.warn("STOP ignored: no JobExecutions found (jobItemId={}, springJobInstanceId={})",
                    jobItemId, springInstanceId);
            return;
        }

        JobExecution latest = executions.stream()
                .max((a, b) -> Long.compare(a.getId(), b.getId()))
                .orElseThrow();

        switch (latest.getStatus()) {

            case STARTING:
            case STARTED:
                try {
                    jobOperator.stop(latest.getId());
                    log.info("STOP requested (jobItemId={}, springExecutionId={})",
                            jobItemId, latest.getId());
                } catch (NoSuchJobExecutionException | JobExecutionNotRunningException e) {
                    throw new IllegalStateException(
                            "Stop failed: JobExecution not found (executionId=" + latest.getId() + ")", e);
                }
                break;

            case STOPPING:
                log.info("STOP already in progress (jobItemId={}, springExecutionId={})",
                        jobItemId, latest.getId());
                break;

            case STOPPED:
            case COMPLETED:
            case FAILED:
                log.info("STOP ignored: JobExecution already finished (status={}, jobItemId={}, springExecutionId={})",
                        latest.getStatus(), jobItemId, latest.getId());
                break;

            default:
                log.warn("STOP ignored: unexpected JobExecution status={} (jobItemId={}, springExecutionId={})",
                        latest.getStatus(), jobItemId, latest.getId());
        }
    }

    // =========================================================
    // GUARD
    // =========================================================
    private void guardNoActiveExecution(BatchJobItem item, BatchJobRestartMode requestedMode) {

        Long jobItemId = item.getId();

        // primär: Current-Run / konkrete Execution prüfen
        Long currentRunId = item.getCurrentRunId();
        if (currentRunId != null) {
            Optional<BatchJobRun> currentRunOpt = runRepository.findById(currentRunId);
            if (currentRunOpt.isPresent()) {
                BatchJobRun currentRun = currentRunOpt.get();
                Long executionId = currentRun.getSpringJobExecutionId();
                if (executionId != null) {
                    JobExecution exec = jobExplorer.getJobExecution(executionId);
                    if (exec != null) {
                        switch (exec.getStatus()) {
                            case STARTING:
                            case STARTED:
                            case STOPPING:
                                throw new BatchJobConflictException(
                                        "BatchJobItem " + jobItemId
                                                + " is currently " + exec.getStatus()
                                                + ". Operation '" + requestedMode.name() + "' is not allowed.");
                            default:
                                return;
                        }
                    }
                }
            }
        }

        // fallback: jobName-scan
        String jobName = item.getJobDefinition().getName();
        for (JobExecution execution : jobExplorer.findRunningJobExecutions(jobName)) {
            Long executionItemId =
                    execution.getJobParameters().getLong(PARAM_BATCH_JOB_ITEM_ID);

            if (executionItemId != null && jobItemId.equals(executionItemId)) {
                throw new BatchJobConflictException(
                        "BatchJobItem " + jobItemId
                                + " is currently " + execution.getStatus()
                                + ". Operation '" + requestedMode.name() + "' is not allowed.");
            }
        }
    }

    // =========================================================
    // LIST / COUNTERS
    // =========================================================
    @Transactional(readOnly = true)
    public List<BatchJobItem> listRunningJobs(Optional<String> jobName) {
        if (jobName.isPresent() && !jobName.get().isBlank()) {
            return itemRepository.findByJobDefinition_NameAndStatus(
                    jobName.get(), BatchItemStatus.RUNNING);
        }
        return itemRepository.findByStatus(BatchItemStatus.RUNNING);
    }

    public Map<String, Integer> getRunningCountsSnapshot() {
        Map<String, Integer> snapshot = new java.util.HashMap<>();
        runningCounts.forEach((k, v) -> snapshot.put(k, v.get()));
        return snapshot;
    }

    private void registerRunning(String jobName) {
        runningCounts
                .computeIfAbsent(jobName, k -> new AtomicInteger(0))
                .incrementAndGet();
    }

    public void decrementRunning(String jobName) {
        runningCounts
                .computeIfAbsent(jobName, k -> new AtomicInteger(0))
                .updateAndGet(v -> Math.max(0, v - 1));
    }

    // =========================================================
    // INTERNAL
    // =========================================================
    private void failItem(BatchJobItem item, String message) {
        item.setStatus(BatchItemStatus.FAILED);
        item.setFinishedAt(Instant.now());
        item.setErrorMessage(message);
        itemRepository.save(item);
    }
}
