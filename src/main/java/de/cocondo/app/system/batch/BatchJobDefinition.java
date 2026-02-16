package de.cocondo.app.system.batch;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.Instant;

/**
 * Die Entität BatchJobDefinition beschreibt einen Batch-Job und seine Ausführungsregeln.
 * Jede konkrete Spring Batch Job Bean (z.B. "importCustomersJob") sollte hier eine
 * entsprechende Definitionseintragung haben, wobei der 'name' dem Bean-Namen entspricht.
 */
@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BatchJobDefinition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Eindeutiger Name der Job-Definition. Dieser Name MUSS dem Bean-Namen
     * des zugehörigen Spring Batch Jobs im ApplicationContext entsprechen
     * (z.B. "importCustomersJob").
     */
    @Column(nullable = false, unique = true, length = 100)
    private String name;

    /**
     * Eine lesbare Beschreibung des Jobs, z.B. für die Anzeige in einem UI.
     */
    @Column(columnDefinition = "TEXT")
    private String description;

    /**
     * Benutzername, unter dem dieser konkrete Job ausgeführt wird.
     */
    @Column(length = 100)
    private String runUsername;

    /**
     * Gibt an, ob nur eine Instanz dieses Jobs (basierend auf dieser Definition)
     * gleichzeitig laufen darf (true) oder mehrere (false).
     * Dies steuert die anwendungsspezifische Parallelität.
     */
    @Column(nullable = false)
    private boolean singleInstance;

    /**
     * Maximale Anzahl an gleichzeitig laufenden Ausführungen dieses Jobs,
     * wenn 'singleInstance' auf false gesetzt ist.
     * Kann null sein oder 0, wenn die Anzahl unbegrenzt ist oder
     * 'singleInstance' auf true gesetzt ist.
     * Ein positiver Wert (> 0) begrenzt die Anzahl.
     */
    private Integer maxConcurrentExecutions;

    /**
     * Zeitstempel der Erstellung dieser Job-Definition.
     */
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    /**
     * Zeitstempel der letzten Aktualisierung dieser Job-Definition.
     */
    @Column(nullable = false)
    private Instant lastUpdated;

    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
        lastUpdated = Instant.now();
    }

    @PreUpdate
    protected void onUpdate() {
        lastUpdated = Instant.now();
    }
}