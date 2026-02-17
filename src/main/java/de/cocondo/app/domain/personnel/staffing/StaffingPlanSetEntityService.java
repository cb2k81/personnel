package de.cocondo.app.domain.personnel.staffing;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * file: /opt/cocondo/personnel/src/main/java/de/cocondo/app/domain/personnel/staffing/StaffingPlanSetEntityService.java
 *
 * Entity service for the StaffingPlanSet aggregate. // Entity-Service für das Aggregat StaffingPlanSet
 *
 * Responsibilities: // Verantwortlichkeiten
 * - persistence access for StaffingPlanSet entities // Persistenzzugriff
 * - loading/saving/deleting aggregates // Laden/Speichern/Löschen
 *
 * This service: // Dieser Service
 * - knows no DTOs // kennt keine DTOs
 * - knows no permissions // kennt keine Berechtigungen
 * - contains no use-case orchestration // enthält keine Use-Case-Orchestrierung
 */
@Service
@RequiredArgsConstructor
public class StaffingPlanSetEntityService {

    private final StaffingPlanSetRepository repository; // Repository

    public Optional<StaffingPlanSet> loadById(String id) { // Laden per ID
        return repository.findById(id);
    }

    public StaffingPlanSet save(StaffingPlanSet entity) { // Speichern
        return repository.save(entity);
    }

    public void deleteById(String id) { // Löschen per ID
        repository.deleteById(id);
    }

    public Page<StaffingPlanSet> findAll(Pageable pageable) { // Liste mit Paging/Sort
        return repository.findAll(pageable);
    }
}
