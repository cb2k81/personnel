package de.cocondo.app.system.batch;

import de.cocondo.app.batchjobs.doubletsearch.locationdoublets.LocationDoubletJobProgressRepository;
import de.cocondo.app.batchjobs.doubletsearch.locationdoublets.LocationDoubletJobProgressStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Component
@Slf4j
@RequiredArgsConstructor
public class BatchJobItemUpdateListener implements JobExecutionListener {

    private final BatchJobItemRepository repo;
    private final BatchJobRunRepository runRepository;
    private final LocationDoubletJobProgressRepository progressRepository;

    @Override
    @Transactional
    public void afterJob(JobExecution execution) {

        Long jobItemId = execution.getJobParameters()
                .getLong(BatchJobService.PARAM_BATCH_JOB_ITEM_ID);

        if (jobItemId == null) {
            return;
        }

        repo.findById(jobItemId).ifPresent(item -> {

            item.setFinishedAt(Instant.now());
            item.setSpringBatchStatus(execution.getStatus());

            switch (execution.getStatus()) {
                case COMPLETED -> item.setStatus(BatchItemStatus.COMPLETED);
                case STOPPED -> item.setStatus(BatchItemStatus.STOPPED);
                case FAILED -> item.setStatus(BatchItemStatus.FAILED);
                default -> item.setStatus(BatchItemStatus.UNKNOWN);
            }

            repo.save(item);

            long pending = progressRepository.countByBatchJobItemIdAndStatus(jobItemId, LocationDoubletJobProgressStatus.PENDING);
            long inProgress = progressRepository.countByBatchJobItemIdAndStatus(jobItemId, LocationDoubletJobProgressStatus.IN_PROGRESS);
            long done = progressRepository.countByBatchJobItemIdAndStatus(jobItemId, LocationDoubletJobProgressStatus.DONE);
            long failed = progressRepository.countByBatchJobItemIdAndStatus(jobItemId, LocationDoubletJobProgressStatus.FAILED);

            String terminationReason =
                    execution.getStatus() == BatchStatus.STOPPED
                            ? "STOP_REQUEST_OR_SHUTDOWN"
                            : "COMPLETED";

            // deterministisch: IMMER den Current-Run finalisieren (nicht "TopByStartedAtDesc")
            Long currentRunId = item.getCurrentRunId();
            if (currentRunId != null) {
                runRepository.findById(currentRunId).ifPresent(run -> {
                    run.setFinishedAt(Instant.now());
                    run.setFinalStatus(execution.getStatus().name());
                    run.setTerminationReason(terminationReason);
                    run.setSpringJobExecutionId(execution.getId());
                    run.setErrorMessage(
                            execution.getAllFailureExceptions() != null
                                    && !execution.getAllFailureExceptions().isEmpty()
                                    ? execution.getAllFailureExceptions().get(0).getMessage()
                                    : null
                    );
                    runRepository.save(run);

                    log.info(
                            "BatchJobRun finished (runId={}, jobItemId={}, finalStatus={}, terminationReason={})",
                            run.getId(), jobItemId, run.getFinalStatus(), run.getTerminationReason()
                    );
                });
            } else {
                log.warn("BatchJobRun finish skipped: item has no currentRunId (jobItemId={})", jobItemId);
            }

            log.info(
                    "[BatchJobSummary] jobName={}, itemId={}, springInstanceId={}, currentRunId={}, status={}, springStatus={}, terminationReason={} | PENDING={}, IN_PROGRESS={}, DONE={}, FAILED={}",
                    item.getJobDefinition().getName(),
                    jobItemId,
                    item.getSpringJobInstanceId(),
                    item.getCurrentRunId(),
                    item.getStatus(),
                    item.getSpringBatchStatus(),
                    terminationReason,
                    pending,
                    inProgress,
                    done,
                    failed
            );
        });
    }
}
