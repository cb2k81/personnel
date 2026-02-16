package de.cocondo.app.system.batch;

import de.cocondo.app.system.batch.dto.BatchJobProgressDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/batch/jobs")
@RequiredArgsConstructor
@Slf4j
public class BatchJobController {

    private final BatchJobService batchJobService;
    private final BatchJobProgressService progressService;

    /**
     * Start a new job run for a given definition.
     * Optional body parameters are forwarded as JobParameters.
     *
     * Robustness:
     * - Allows fallback resolution when definitionId differs between environments.
     * - Allows resolving/creating a definition by jobName (optional body param "jobName").
     */
    @PostMapping("/{definitionId}/start")
    public ResponseEntity<BatchJobItem> startJob(
            @PathVariable Long definitionId,
            @RequestBody(required = false) Map<String, String> parameters
    ) {
        Map<String, String> params = (parameters != null) ? parameters : new HashMap<>();
        BatchJobItem item = batchJobService.startJob(definitionId, params);
        log.info("REST: Started job definitionId={} → itemId={}", definitionId, item.getId());
        return ResponseEntity.ok(item);
    }

    /**
     * Stop a running job by its BatchJobItem id.
     */
    @PostMapping("/runs/{jobItemId}/stop")
    public ResponseEntity<Void> stopJob(@PathVariable Long jobItemId) {
        batchJobService.stopJob(jobItemId);
        log.info("REST: Stopped jobItemId={}", jobItemId);
        return ResponseEntity.ok().build();
    }

    /**
     * Resume a stopped or failed job run.
     * Semantically equivalent to restart in Phase 5.x.
     */
    @PostMapping("/runs/{jobItemId}/resume")
    public ResponseEntity<BatchJobItem> resumeJob(@PathVariable Long jobItemId) {
        BatchJobItem item = batchJobService.restartJobItem(jobItemId, BatchJobRestartMode.RESUME);
        return ResponseEntity.ok(item);
    }

    /**
     * Explicit restart endpoint (alias of resume for now).
     */
    @PostMapping("/runs/{jobItemId}/restart")
    public ResponseEntity<BatchJobItem> restartJob(@PathVariable Long jobItemId) {
        BatchJobItem item = batchJobService.restartJobItem(jobItemId, BatchJobRestartMode.RESET);
        return ResponseEntity.ok(item);
    }

    /**
     * Get progress information for a job run.
     */
    @GetMapping("/runs/{jobItemId}/progress")
    public ResponseEntity<BatchJobProgressDTO> getProgress(@PathVariable Long jobItemId) {
        return ResponseEntity.ok(progressService.getProgress(jobItemId));
    }

    /**
     * Snapshot of currently running jobs (in-memory counters).
     */
    @GetMapping("/running")
    public ResponseEntity<Map<String, Integer>> getRunningCounts() {
        return ResponseEntity.ok(batchJobService.getRunningCountsSnapshot());
    }
}
