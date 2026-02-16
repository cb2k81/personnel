package de.cocondo.app.system.batch;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * Repository für die BatchJobItem Entität.
 * Verwaltet die einzelnen Ausführungen (Instanzen) der Batch-Jobs.
 */
@Repository
public interface BatchJobItemRepository extends JpaRepository<BatchJobItem, Long> {

    /**
     * Findet ein BatchJobItem anhand seiner Spring Batch Job Instance ID.
     * Diese ID ist einzigartig für jede Spring Batch Ausführung.
     *
     * @param springJobInstanceId Die technische ID aus Spring Batch (BATCH_JOB_INSTANCE.JOB_INSTANCE_ID).
     * @return Ein Optional, das das gefundene BatchJobItem enthält, oder leer ist.
     */
    Optional<BatchJobItem> findBySpringJobInstanceId(Long springJobInstanceId);

    /**
     * Findet alle BatchJobItems für eine bestimmte BatchJobDefinition (Job-Typ).
     * Sortiert absteigend nach Startzeit.
     *
     * @param jobDefinition Die BatchJobDefinition, deren Ausführungen gesucht werden.
     * @return Eine Liste von BatchJobItems.
     */
    List<BatchJobItem> findByJobDefinitionOrderByStartedAtDesc(BatchJobDefinition jobDefinition);

    /**
     * Alle laufenden Jobs unabhängig von der Definition.
     */
    List<BatchJobItem> findByStatus(BatchItemStatus status);

    /**
     * Findet alle BatchJobItems für eine bestimmte Job-Definition (identifiziert über den Namen)
     * und einen bestimmten anwendungsspezifischen Status.
     *
     * @param jobDefinitionName Der Name der Job-Definition (entspricht dem Spring Bean Namen des Jobs).
     * @param status            Der anwendungsspezifische Status der Job-Ausführung (z.B. RUNNING, FAILED).
     * @return Eine Liste von BatchJobItems, die den Kriterien entsprechen.
     */
    List<BatchJobItem> findByJobDefinition_NameAndStatus(String jobDefinitionName, BatchItemStatus status);

    /**
     * Findet die n neuesten BatchJobItems für eine bestimmte Job-Definition (nach Name).
     * Nützlich für die Historie der letzten Ausführungen.
     *
     * @param jobDefinitionName Der Name der Job-Definition.
     * @param limit             Die maximale Anzahl der zurückzugebenden Items.
     * @return Eine Liste der neuesten BatchJobItems.
     */
    @Query(value = "SELECT bji FROM BatchJobItem bji JOIN bji.jobDefinition bjd WHERE bjd.name = :jobDefinitionName ORDER BY bji.startedAt DESC LIMIT :limit")
    List<BatchJobItem> findLatestByJobDefinitionName(@Param("jobDefinitionName") String jobDefinitionName, @Param("limit") int limit);


    /**
     * Zählt die Anzahl der laufenden JobItems pro Job-Definition.
     * Dies wird vom BatchJobService beim Start der Anwendung benötigt,
     * um die In-Memory-Zähler zu initialisieren.
     *
     * @return Eine Liste von Object-Arrays, wobei jedes Array den Job-Namen (String) und die Anzahl (Long) enthält.
     */
    @Query("SELECT bjd.name, COUNT(bji) FROM BatchJobItem bji JOIN bji.jobDefinition bjd WHERE bji.status = 'RUNNING' GROUP BY bjd.name")
    List<Object[]> countRunningJobsByDefinitionName();

    /**
     * Findet die IDs der laufenden JobItems pro Job-Definition.
     * Wird ebenfalls für die Initialisierung der In-Memory-Maps benötigt.
     *
     * @return Eine Liste von Object-Arrays, wobei jedes Array den Job-Namen (String) und die BatchJobItem-ID (Long) enthält.
     */
    @Query("SELECT bjd.name, bji.id FROM BatchJobItem bji JOIN bji.jobDefinition bjd WHERE bji.status = 'RUNNING'")
    List<Object[]> findRunningJobItemIdsByDefinitionName();

    /**
     * Findet alle JobItems, die in einem bestimmten Status sind und vor einem bestimmten Zeitpunkt gestartet wurden.
     * Nützlich für Aufräumarbeiten oder zur Identifizierung hängengebliebener Jobs.
     *
     * @param status Der Status (z.B. RUNNING, FAILED).
     * @param startedBefore Der Zeitpunkt, vor dem der Job gestartet wurde.
     * @return Eine Liste der entsprechenden BatchJobItems.
     */
    List<BatchJobItem> findByStatusAndStartedAtBefore(BatchItemStatus status, Instant startedBefore);

    /**
     * Zählt die Gesamtanzahl der JobItems für eine bestimmte Job-Definition.
     *
     * @param jobDefinition Die BatchJobDefinition.
     * @return Die Anzahl der JobItems.
     */
    long countByJobDefinition(BatchJobDefinition jobDefinition);

    // Beispiel für eine Aggregation: Zählt JobItems pro Status für eine bestimmte Definition
    // @Query("SELECT bji.status, COUNT(bji) FROM BatchJobItem bji WHERE bji.jobDefinition = :jobDefinition GROUP BY bji.status")
    // List<Object[]> countByStatusForJobDefinition(@Param("jobDefinition") BatchJobDefinition jobDefinition);
}