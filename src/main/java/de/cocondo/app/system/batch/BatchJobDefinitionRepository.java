package de.cocondo.app.system.batch;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository für die BatchJobDefinition Entität.
 * Verwaltet die Definitionen der Batch-Jobs.
 */
@Repository
public interface BatchJobDefinitionRepository extends JpaRepository<BatchJobDefinition, Long> {

    /**
     * Findet eine BatchJobDefinition anhand ihres eindeutigen Namens.
     * Dieser Name entspricht dem Spring Bean Namen des Jobs.
     *
     * @param name Der Name der Job-Definition (z.B. "importCustomersJob").
     * @return Ein Optional, das die gefundene BatchJobDefinition enthält, oder leer ist.
     */
    Optional<BatchJobDefinition> findByName(String name);

    /**
     * Prüft, ob eine BatchJobDefinition mit einem bestimmten Namen existiert.
     *
     * @param name Der Name der Job-Definition.
     * @return true, wenn eine Definition existiert, sonst false.
     */
    boolean existsByName(String name);

    // Weitere Abfragen könnten hier hinzugefügt werden, je nach Bedarf:
    // List<BatchJobDefinition> findBySingleInstanceTrue();
    // List<BatchJobDefinition> findByMaxConcurrentExecutionsGreaterThan(int count);
}