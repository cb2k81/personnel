package de.cocondo.app.domain.personnel.person.dto;

import de.cocondo.app.domain.personnel.person.PersonStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * DTO representing an existing Person aggregate.
 *
 * Extends the payload DTO by adding:
 * - technical identifier
 * - status (workflow / lifecycle)
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class PersonDTO extends PersonPayloadDTO {

    private String id;

    private PersonStatus status;
}