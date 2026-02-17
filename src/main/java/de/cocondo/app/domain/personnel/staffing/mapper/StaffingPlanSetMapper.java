package de.cocondo.app.domain.personnel.staffing.mapper;

import de.cocondo.app.domain.personnel.budget.BudgetRevision;
import de.cocondo.app.domain.personnel.staffing.StaffingPlanSet;
import de.cocondo.app.domain.personnel.staffing.dto.StaffingPlanSetDTO;
import de.cocondo.app.domain.personnel.staffing.dto.StaffingPlanSetPayloadDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * file: /opt/cocondo/personnel/src/main/java/de/cocondo/app/domain/personnel/staffing/mapper/StaffingPlanSetMapper.java
 *
 * MapStruct mapper for the StaffingPlanSet aggregate. // MapStruct-Mapper für das Aggregat StaffingPlanSet
 *
 * Notes: // Hinweise
 * - ID generation is NOT done here (handled by DomainEntityListener). // ID-Generierung erfolgt nicht hier (DomainEntityListener)
 * - BudgetRevision is mapped as an ID-only reference. // BudgetRevision wird als reine ID-Referenz gemappt
 */
@Mapper(componentModel = "spring")
public interface StaffingPlanSetMapper {

    @Mapping(
            target = "budgetRevisionId",
            expression = "java(entity.getBudgetRevision() != null ? entity.getBudgetRevision().getId() : null)" // Haushaltsänderungsstand-ID (null-safe)
    )
    StaffingPlanSetDTO toDto(StaffingPlanSet entity);

    @Mapping(target = "id", ignore = true) // ID wird technisch gesetzt (Listener) // ID wird ignoriert
    @Mapping(
            target = "budgetRevision",
            expression = "java(budgetRevisionFromId(payloadDTO.getBudgetRevisionId()))" // BudgetRevision aus ID-Referenz // BudgetRevision-Referenz
    )
    StaffingPlanSet fromPayload(StaffingPlanSetPayloadDTO payloadDTO);

    /**
     * Creates an ID-only reference for BudgetRevision. // Erzeugt eine reine ID-Referenz für BudgetRevision
     */
    default BudgetRevision budgetRevisionFromId(String id) {
        if (id == null || id.isBlank()) { // Leere ID -> keine Referenz // keine Referenz
            return null;
        }
        BudgetRevision ref = new BudgetRevision(); // ID-only Stub // ID-only-Stub
        ref.setId(id); // Referenz-ID setzen // Referenz-ID setzen
        return ref;
    }
}
