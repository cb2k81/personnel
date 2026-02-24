package de.cocondo.app.domain.personnel.person;

import de.cocondo.app.domain.personnel.person.dto.PersonCreateDTO;
import de.cocondo.app.domain.personnel.person.dto.PersonDTO;
import de.cocondo.app.domain.personnel.person.dto.PersonUpdateDTO;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

/**
 * REST Controller for Person aggregate.
 *
 * Responsibilities:
 * - HTTP boundary only (no business logic, no repository access)
 * - Paging via Pageable
 * - Filtering via Specification
 *
 * API base path is under /api.
 */
@RestController
@RequestMapping("/api/persons")
public class PersonController {

    private final PersonDomainService personDomainService;

    public PersonController(PersonDomainService personDomainService) {
        this.personDomainService = personDomainService;
    }

    @PostMapping
    public PersonDTO create(@RequestBody PersonCreateDTO dto) {
        return personDomainService.createPerson(dto);
    }

    @GetMapping("/{id}")
    public PersonDTO getById(@PathVariable String id) {
        return personDomainService
                .getPersonById(id)
                .orElseThrow(() -> new EntityNotFoundException("Person not found: " + id));
    }

    @GetMapping
    public Page<PersonDTO> list(
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String middleName,
            @RequestParam(required = false) String lastName,
            @RequestParam(required = false) Gender gender,
            @RequestParam(required = false) String salutation,
            @RequestParam(required = false) String academicTitle,
            @RequestParam(required = false) LocalDate birthdayFrom,
            @RequestParam(required = false) LocalDate birthdayTo,
            Pageable pageable
    ) {
        Specification<Person> spec = PersonSpecifications.build(
                firstName,
                middleName,
                lastName,
                gender,
                salutation,
                academicTitle,
                birthdayFrom,
                birthdayTo
        );

        return personDomainService.listPersons(spec, pageable);
    }

    @PutMapping("/{id}")
    public PersonDTO update(@PathVariable String id, @RequestBody PersonUpdateDTO dto) {
        return personDomainService.updatePerson(id, dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id) {
        personDomainService.deletePerson(id);
    }
}