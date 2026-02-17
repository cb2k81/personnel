package de.cocondo.app.domain.personnel.staffing;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * file: /opt/cocondo/personnel/src/main/java/de/cocondo/app/domain/personnel/staffing/StaffingPlanSetRepository.java
 *
 * Repository for accessing and persisting the StaffingPlanSet aggregate. // Repository für Zugriff und Persistenz von StaffingPlanSet
 *
 * Requirements covered by JpaRepository: // Durch JpaRepository abgedeckt
 * - CRUD operations // CRUD-Operationen
 * - pagination and sorting (via PagingAndSortingRepository) // Pagination und Sortierung
 */
@Repository
public interface StaffingPlanSetRepository extends JpaRepository<StaffingPlanSet, String> {
}
