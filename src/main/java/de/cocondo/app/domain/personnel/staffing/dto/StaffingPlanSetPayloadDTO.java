package de.cocondo.app.domain.personnel.staffing.dto;

import de.cocondo.app.system.dto.DataTransferObject;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * file: /opt/cocondo/personnel/src/main/java/de/cocondo/app/domain/personnel/staffing/dto/StaffingPlanSetPayloadDTO.java
 *
 * Payload DTO carrying staffing plan set data without use-case semantics. // Nutzdaten-DTO für einen Stellenplan-Planungsrahmen ohne Use-Case-Semantik
 */
@Data
public class StaffingPlanSetPayloadDTO implements DataTransferObject, Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String budgetRevisionId; // Budget-Änderungsstand-ID (Haushaltsänderungsstand)

    private String name; // Bezeichnung

    private String description; // Beschreibung
}
