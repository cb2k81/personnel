package de.cocondo.app.domain.personnel.person;

import de.cocondo.app.domain.personnel.person.dto.PersonDTO;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for Person workflow/state operations.
 *
 * Separated from standard update (Option 1).
 */
@RestController
@RequestMapping("/api/persons/{id}/state")
public class PersonStateController {

    private final PersonStateService personStateService;

    public PersonStateController(PersonStateService personStateService) {
        this.personStateService = personStateService;
    }

    @PutMapping("/activate")
    public PersonDTO activate(@PathVariable String id) {
        return personStateService.activate(id);
    }

    @PutMapping("/deactivate")
    public PersonDTO deactivate(@PathVariable String id) {
        return personStateService.deactivate(id);
    }

    @PutMapping
    public PersonDTO setStatus(@PathVariable String id, @RequestParam PersonStatus status) {
        return personStateService.setStatus(id, status);
    }
}