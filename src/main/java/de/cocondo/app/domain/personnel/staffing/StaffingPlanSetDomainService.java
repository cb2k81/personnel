package de.cocondo.app.domain.personnel.staffing;

import de.cocondo.app.domain.personnel.staffing.dto.StaffingPlanSetCreateDTO;
import de.cocondo.app.domain.personnel.staffing.dto.StaffingPlanSetDTO;
import de.cocondo.app.domain.personnel.staffing.dto.StaffingPlanSetUpdateDTO;
import de.cocondo.app.domain.personnel.staffing.mapper.StaffingPlanSetMapper;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * file: /opt/cocondo/personnel/src/main/java/de/cocondo/app/domain/personnel/staffing/StaffingPlanSetDomainService.java
 *
 * Domain service for StaffingPlanSet use cases. // Domain-Service für Use-Cases rund um StaffingPlanSet
 *
 * This service: // Dieser Service
 * - orchestrates entity services // orchestriert Entity-Services
 * - defines transactional boundaries // definiert Transaktionsgrenzen
 * - converts between DTOs and entities // mappt zwischen DTOs und Entities
 *
 * ID generation: handled by DomainEntityListener on persist. // ID-Generierung: via DomainEntityListener beim Persistieren
 */
@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class StaffingPlanSetDomainService {

    private final StaffingPlanSetEntityService entityService; // Entity-Service
    private final StaffingPlanSetMapper mapper; // Mapper

    public StaffingPlanSetDTO create(StaffingPlanSetCreateDTO createDTO) { // Anlegen
        StaffingPlanSet entity = mapper.fromPayload(createDTO);

        StaffingPlanSet persisted = entityService.save(entity);
        log.info("Created StaffingPlanSet id={}", persisted.getId());

        return mapper.toDto(persisted);
    }

    @Transactional(readOnly = true)
    public StaffingPlanSetDTO getById(String id) { // Laden
        StaffingPlanSet entity = entityService.loadById(id)
                .orElseThrow(() -> new EntityNotFoundException("StaffingPlanSet not found with ID: " + id));

        return mapper.toDto(entity);
    }

    @Transactional(readOnly = true)
    public Page<StaffingPlanSetDTO> list(Pageable pageable) { // Liste (Paging/Sort)
        return entityService.findAll(pageable).map(mapper::toDto);
    }

    public StaffingPlanSetDTO update(String id, StaffingPlanSetUpdateDTO updateDTO) { // Aktualisieren
        StaffingPlanSet entity = entityService.loadById(id)
                .orElseThrow(() -> new EntityNotFoundException("StaffingPlanSet not found with ID: " + id));

        // Apply changes (entity remains managed/persisted via save). // Änderungen anwenden
        StaffingPlanSet patch = mapper.fromPayload(updateDTO);

        entity.setBudgetRevision(patch.getBudgetRevision()); // Haushaltsänderungsstand
        entity.setName(patch.getName()); // Name
        entity.setDescription(patch.getDescription()); // Beschreibung

        StaffingPlanSet persisted = entityService.save(entity);
        log.info("Updated StaffingPlanSet id={}", persisted.getId());

        return mapper.toDto(persisted);
    }

    public void delete(String id) { // Löschen
        // Ensure proper 404 semantics by loading first. // Sicherstellen von 404-Semantik durch vorheriges Laden
        entityService.loadById(id)
                .orElseThrow(() -> new EntityNotFoundException("StaffingPlanSet not found with ID: " + id));

        entityService.deleteById(id);
        log.info("Deleted StaffingPlanSet id={}", id);
    }
}
