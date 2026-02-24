package de.cocondo.app.domain.personnel.person;

import de.cocondo.app.domain.personnel.person.dto.PersonDTO;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for privacy operations on Person.
 *
 * Anonymization is a dedicated operation (separate from delete).
 */
@RestController
@RequestMapping("/api/persons/{id}/privacy")
public class PersonPrivacyController {

    private final PersonPrivacyService personPrivacyService;

    public PersonPrivacyController(PersonPrivacyService personPrivacyService) {
        this.personPrivacyService = personPrivacyService;
    }

    @PutMapping("/anonymize")
    public PersonDTO anonymize(@PathVariable String id) {
        return personPrivacyService.anonymize(id);
    }
}