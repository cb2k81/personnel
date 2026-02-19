package de.cocondo.app.domain.personnel.assignment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for PositionFilling entities.
 *
 * Used for reference checks (e.g. preventing hard delete of Person
 * when referenced by PositionFilling).
 */
@Repository
public interface PositionFillingRepository extends JpaRepository<PositionFilling, String> {

    /**
     * Checks whether at least one PositionFilling references the given Person.
     *
     * @param personId technical identifier of the Person
     * @return true if a reference exists
     */
    boolean existsByPerson_Id(String personId);
}
