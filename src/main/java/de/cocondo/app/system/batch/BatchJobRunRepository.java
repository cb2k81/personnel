package de.cocondo.app.system.batch;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * Zugriff auf Run-History (BatchJobRun).
 */
public interface BatchJobRunRepository extends JpaRepository<BatchJobRun, Long> {

    List<BatchJobRun> findByBatchJobItem_IdOrderByStartedAtDesc(Long batchJobItemId);

    /**
     * Liefert den zuletzt gestarteten Run (für afterJob-Abschluss).
     */
    Optional<BatchJobRun> findTopByBatchJobItem_IdOrderByStartedAtDesc(Long batchJobItemId);
}
