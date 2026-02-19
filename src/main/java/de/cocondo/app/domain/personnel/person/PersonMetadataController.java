package de.cocondo.app.domain.personnel.person;

import de.cocondo.app.system.dto.DomainEntityMetadataDTO;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for Person metadata (tags + key-value pairs).
 *
 * Separate controller is required so metadata can have distinct permissions.
 */
@RestController
@RequestMapping("/api/persons/{id}/metadata")
public class PersonMetadataController {

    private final PersonMetadataDomainService personMetadataDomainService;

    public PersonMetadataController(PersonMetadataDomainService personMetadataDomainService) {
        this.personMetadataDomainService = personMetadataDomainService;
    }

    @GetMapping
    public DomainEntityMetadataDTO get(@PathVariable("id") String personId) {
        return personMetadataDomainService.getMetadata(personId);
    }

    @PutMapping
    public DomainEntityMetadataDTO replace(
            @PathVariable("id") String personId,
            @RequestBody DomainEntityMetadataDTO inbound
    ) {
        return personMetadataDomainService.replaceMetadata(personId, inbound);
    }

    @PatchMapping
    public DomainEntityMetadataDTO patch(
            @PathVariable("id") String personId,
            @RequestBody DomainEntityMetadataDTO inbound
    ) {
        return personMetadataDomainService.patchMetadata(personId, inbound);
    }
}
