package de.cocondo.app.domain.personnel.person;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Repository for accessing and persisting the Person aggregate.
 *
 * Requirements covered by JpaRepository:
 * - CRUD operations
 * - pagination and sorting (via PagingAndSortingRepository)
 *
 * Additional capabilities:
 * - Specification support for filtering and Record-Level-Security (ADR 007, ADR 010)
 */
@Repository
public interface PersonRepository extends JpaRepository<Person, String>, JpaSpecificationExecutor<Person> {
}
