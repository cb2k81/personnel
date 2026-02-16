package de.cocondo.app.system.batch;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.batch.core.BatchStatus;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Die Entität BatchJobItem persistiert erweiterte Informationen zu
 * einzelnen Job-Ausführungen, die mit Spring Batch ausgeführt werden.
 * Jedes BatchJobItem bezieht sich auf eine BatchJobDefinition.
 */
@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BatchJobItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Referenz zur übergeordneten BatchJobDefinition, die die Beschreibung
     * und Ausführungsregeln für diesen Job-Typ enthält.
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "job_definition_id", nullable = false)
    private BatchJobDefinition jobDefinition;

    /**
     * Technische ID aus Spring Batch: BATCH_JOB_INSTANCE.JOB_INSTANCE_ID.
     * Hält die aktuelle/letzte technische Instanz für dieses Item (Current Run).
     *
     * Hinweis: Historie liegt in BatchJobRun.
     */
    @Column(name = "spring_job_instance_id", nullable = true, unique = true)
    private Long springJobInstanceId;

    /**
     * Pointer auf den aktuellen Run dieses Items.
     *
     * Motivation:
     * - BatchJobItem kann mehrere Runs haben (History).
     * - Genau ein Run ist "current" (der zuletzt gestartete, der zum aktuellen spring_job_instance_id passt).
     */
    @Column(name = "current_run_id")
    private Long currentRunId;

    /**
     * Run-Historie (append-only).
     */
    @JsonIgnore
    @OneToMany(mappedBy = "batchJobItem", cascade = CascadeType.ALL, orphanRemoval = false, fetch = FetchType.LAZY)
    @OrderBy("startedAt DESC")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @Builder.Default
    private List<BatchJobRun> runs = new ArrayList<>();

    /**
     * Thread-Name oder UUID für Korrelation von Log-Einträgen oder externen Systemen.
     */
    private String correlationId; // bzw. thread-id

    /** Zeitpunkt, zu dem die Job-Ausführung gestartet wurde. */
    private Instant startedAt;

    /** Zeitpunkt, zu dem die Job-Ausführung beendet wurde. */
    private Instant finishedAt;

    /**
     * Der anwendungsspezifische Status der Job-Ausführung.
     * Die Enum DuplicateJobStatus sollte umbenannt werden (z.B. BatchItemStatus).
     */
    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private BatchItemStatus status; // Name der Enum angepasst

    /** Gesamtanzahl der Elemente, die verarbeitet werden sollen. */
    @Column(nullable = false)
    private long totalCount;

    /** Anzahl der bereits verarbeiteten Elemente. */
    @Column(nullable = false)
    private long processedCount;

    /** Zeitpunkt der letzten Aktualisierung dieses Job-Items (z.B. Fortschritts-Update). */
    @Column(nullable = false)
    private Instant lastUpdated;

    /** Details zur Fehlerursache (Stacktrace oder Kurzmeldung). */
    @Column(columnDefinition = "TEXT")
    private String errorMessage;

    /**
     * Benutzername, unter dem dieser konkrete Job ausgeführt wird.
     */
    @Column(length = 100)
    private String runUsername;

    /**
     * Hält den aktuellen Fortschritt der Ausführung im Speicher.
     * Dieses Feld ist transient und wird nicht persistiert.
     */
    @Transient
    private double progress;

    /**
     * Letzter bekannter Spring-Batch-Status.
     * Dient ausschließlich der Transparenz für API/UI.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "spring_batch_status", length = 20)
    private BatchStatus springBatchStatus;

    /**
     * Berechnet den Fortschritt in Prozent basierend auf processedCount und totalCount.
     *
     * @return Der Fortschritt in Prozent (0.0 bis 100.0).
     */
    public double getProgress() {
        if (totalCount <= 0) return 0.0;
        return processedCount * 100.0 / totalCount;
    }

    @PrePersist
    protected void onCreate() {
        // startedAt wird vom Service gesetzt, sobald der Job wirklich startet
        // lastUpdated wird hier initial gesetzt
        lastUpdated = Instant.now();
    }

    @PreUpdate
    protected void onUpdate() {
        lastUpdated = Instant.now();
    }
}
