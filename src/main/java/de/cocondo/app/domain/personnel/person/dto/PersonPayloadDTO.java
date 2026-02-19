package de.cocondo.app.domain.personnel.person.dto;

import de.cocondo.app.domain.personnel.person.Gender;
import de.cocondo.app.system.dto.DataTransferObject;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;

/**
 * Payload DTO carrying person data without use-case semantics.
 */
@Data
public class PersonPayloadDTO implements DataTransferObject, Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String firstName;
    private String middleName;
    private String lastName;

    private Gender gender;

    private String salutation;
    private String academicTitle;

    private LocalDate birthday;
}
