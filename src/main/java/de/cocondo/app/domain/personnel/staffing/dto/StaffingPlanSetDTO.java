package de.cocondo.app.domain.personnel.staffing.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;

/**
 * file: /opt/cocondo/personnel/src/main/java/de/cocondo/app/domain/personnel/staffing/dto/StaffingPlanSetDTO.java
 *
 * DTO representing an existing StaffingPlanSet aggregate. // DTO für einen bestehenden Planungsrahmen
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class StaffingPlanSetDTO extends StaffingPlanSetPayloadDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String id; // Technische ID
}
