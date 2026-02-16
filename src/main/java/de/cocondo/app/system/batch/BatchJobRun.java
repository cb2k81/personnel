package de.cocondo.app.system.batch;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

/**
 * Append-only Run-History für BatchJobItem.
 *
 * Ein BatchJobItem repräsentiert die fachliche "Job-Ausführung" (Kontrollobjekt im System).
 * Ein BatchJobRun repräsentiert eine technische Spring-Batch-Ausführung (Run) innerhalb dieses Items.
 *
 * Beziehung:
 * - BatchJobItem (1) -> (n) BatchJobRun
 *
 * Persistiert technische IDs, Status und Beendigungsgrund für Debugging und Nachvollziehbarkeit.
 */
@Entity
@Table(name = "batch_job_run")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BatchJobRun {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Zugehöriges BatchJobItem (fachliche Klammer).
     */
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "batch_job_item_id", nullable = false)
    private BatchJobItem batchJobItem;

    /**
     * Aktion dieses Runs (START/RESUME/RESET).
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "action", nullable = false, length = 20)
    private BatchJobRunAction action;

    /**
     * Optionaler Restart-Mode aus API/Service (z.B. RESUME/RESET).
     * Für START wird hier i.d.R. null gespeichert.
     *
     * Bewusst String, um keine Kopplung an Enum-Refactorings zu erzwingen.
     */
    @Column(name = "restart_mode", length = 20)
    private String restartMode;

    /**
     * Spring JobInstance ID (technische Instanz).
     *
     * Wichtig: Bei RESUME/RESET wird der Run aus Gründen der deterministischen Parameter-Erzeugung
     * (runId als identifizierender Parameter) VOR jobLauncher.run(...) persistiert.
     * Daher muss dieses Feld initial NULL sein dürfen und wird nach dem Start nachgezogen.
     */
    @Column(name = "spring_job_instance_id", nullable = true)
    private Long springJobInstanceId;

    /**
     * Spring JobExecution ID (konkrete Execution).
     */
    @Column(name = "spring_job_execution_id")
    private Long springJobExecutionId;

    @Column(name = "started_at", nullable = false)
    private Instant startedAt;

    @Column(name = "finished_at")
    private Instant finishedAt;

    /**
     * Finaler Spring Batch Status (COMPLETED/STOPPED/FAILED/...) als String.
     */
    @Column(name = "final_status", length = 20)
    private String finalStatus;

    /**
     * Technischer Beendigungsgrund (z.B. STOP_REQUEST_OR_SHUTDOWN).
     */
    @Column(name = "termination_reason", length = 100)
    private String terminationReason;

    /**
     * Optional: Fehlermeldung bei Failure.
     */
    @Column(name = "error_message", length = 1024)
    private String errorMessage;
}
