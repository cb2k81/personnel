package de.cocondo.app.domain.personnel.person;

import de.cocondo.app.domain.personnel.person.dto.PersonDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class PersonDomainServiceReadTest {

    @Autowired
    private PersonDomainService personDomainService;

    @Autowired
    private PersonRepository personRepository;

    @Test
    @WithMockUser(authorities = PersonPermissions.READ)
    @DisplayName("READ permission: existing person is returned")
    void getPerson_existing_returnsDto() {

        // Setup direkt über Repository (kein CREATE-Recht notwendig)
        Person person = new Person();
        person.setFirstName("Max");
        person.setLastName("Mustermann");
        person.setBirthday(LocalDate.of(1990, 1, 1));
        person.setStatus(PersonStatus.ACTIVE);

        personRepository.save(person);

        Optional<PersonDTO> loaded =
                personDomainService.getPersonById(person.getId());

        assertTrue(loaded.isPresent());
        assertEquals("Max", loaded.get().getFirstName());
        assertEquals("Mustermann", loaded.get().getLastName());
    }

    @Test
    @WithMockUser(authorities = PersonPermissions.READ)
    @DisplayName("READ permission: unknown id -> empty result")
    void getPerson_unknown_returnsEmpty() {

        Optional<PersonDTO> result =
                personDomainService.getPersonById("does-not-exist");

        assertTrue(result.isEmpty());
    }

    @Test
    @WithMockUser(authorities = PersonPermissions.READ)
    @DisplayName("RLS: INACTIVE with READ but without READ_INACTIVE -> empty result")
    void getPerson_inactive_withoutReadInactive_returnsEmpty() {

        Person p = new Person();
        p.setFirstName("Ina");
        p.setLastName("Inactive");
        p.setStatus(PersonStatus.INACTIVE);
        personRepository.save(p);

        Optional<PersonDTO> loaded = personDomainService.getPersonById(p.getId());

        assertTrue(loaded.isEmpty());
    }

    @Test
    @WithMockUser(authorities = {PersonPermissions.READ, PersonPermissions.READ_INACTIVE})
    @DisplayName("RLS: INACTIVE with READ + READ_INACTIVE -> dto is returned")
    void getPerson_inactive_withReadInactive_returnsDto() {

        Person p = new Person();
        p.setFirstName("Ina");
        p.setLastName("Inactive");
        p.setStatus(PersonStatus.INACTIVE);
        personRepository.save(p);

        Optional<PersonDTO> loaded = personDomainService.getPersonById(p.getId());

        assertTrue(loaded.isPresent());
        assertEquals("Ina", loaded.get().getFirstName());
        assertEquals("Inactive", loaded.get().getLastName());
    }

    @Test
    @WithMockUser
    @DisplayName("Without READ permission -> AccessDeniedException")
    void getPerson_withoutPermission_throwsAccessDenied() {

        assertThrows(
                AccessDeniedException.class,
                () -> personDomainService.getPersonById("any-id")
        );
    }
}