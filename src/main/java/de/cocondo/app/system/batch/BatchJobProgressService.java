package de.cocondo.app.system.batch;

import de.cocondo.app.batchjobs.doubletsearch.locationdoublets.LocationDoubletJobProgressRepository;
import de.cocondo.app.batchjobs.doubletsearch.locationdoublets.LocationDoubletJobProgressStatus;
import de.cocondo.app.system.batch.dto.BatchJobProgressDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BatchJobProgressService {

    private final BatchJobItemRepository jobItemRepository;
    private final BatchJobDefinitionRepository jobDefinitionRepository;

    private final LocationDoubletJobProgressRepository progressRepository;
    private final JobExplorer jobExplorer;

    /**
     * Wird vom Controller für Running-Count / Definition-basierte Auskünfte genutzt.
     */
    @Transactional(readOnly = true)
    public BatchJobDefinition getJobDefinition(Long jobDefinitionId) {
        return jobDefinitionRepository.findById(jobDefinitionId)
                .orElseThrow(() -> new IllegalArgumentException("Unknown BatchJobDefinition: " + jobDefinitionId));
    }

    @Transactional(readOnly = true)
    public BatchJobProgressDTO getProgress(Long jobItemId) {

        var jobItem = jobItemRepository.findById(jobItemId)
                .orElseThrow(() -> new IllegalArgumentException("Unknown BatchJobItem: " + jobItemId));

        long pending = progressRepository.countByBatchJobItemIdAndStatus(
                jobItemId, LocationDoubletJobProgressStatus.PENDING);

        long inProgress = progressRepository.countByBatchJobItemIdAndStatus(
                jobItemId, LocationDoubletJobProgressStatus.IN_PROGRESS);

        long done = progressRepository.countByBatchJobItemIdAndStatus(
                jobItemId, LocationDoubletJobProgressStatus.DONE);

        long failed = progressRepository.countByBatchJobItemIdAndStatus(
                jobItemId, LocationDoubletJobProgressStatus.FAILED);

        long total = pending + inProgress + done + failed;
        long processed = done + failed;

        Integer percent = (total > 0) ? (int) ((processed * 100) / total) : null;

        // RestartMode robust aus Spring Batch ermitteln:
        // bevorzugt über konkrete ExecutionId (current run), fallback über JobInstance.
        String restartMode = null;

        if (jobItem.getCurrentRunId() != null) {
            // currentRunId existiert -> falls möglich über executionId lesen
            // (ohne hard dependency auf RunRepository in diesem Service: best-effort via JobInstance-Fallback unten)
        }

        if (jobItem.getSpringJobInstanceId() != null) {
            var springJobInstance = jobExplorer.getJobInstance(jobItem.getSpringJobInstanceId());
            if (springJobInstance != null) {
                for (JobExecution exec : jobExplorer.getJobExecutions(springJobInstance)) {
                    restartMode = exec.getJobParameters().getString(BatchJobService.PARAM_RESTART_MODE);
                    break;
                }
            }
        }

        return BatchJobProgressDTO.builder()
                .jobItemId(jobItemId)
                .jobName(jobItem.getJobDefinition().getName())
                .springJobInstanceId(jobItem.getSpringJobInstanceId())
                .restartMode(restartMode)
                .status(jobItem.getStatus().name())
                .total(total)
                .pending(pending)
                .inProgress(inProgress)
                .done(done)
                .failed(failed)
                .processed(processed)
                .progressPercent(percent)
                .startedAt(jobItem.getStartedAt())
                .lastUpdatedAt(jobItem.getLastUpdated())
                .finishedAt(jobItem.getFinishedAt())
                .build();
    }
}
