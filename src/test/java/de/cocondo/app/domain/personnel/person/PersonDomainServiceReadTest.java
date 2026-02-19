package de.cocondo.app.domain.personnel.person;

import de.cocondo.app.domain.personnel.person.dto.PersonCreateDTO;
import de.cocondo.app.domain.personnel.person.dto.PersonDTO;
import jakarta.persistence.EntityNotFoundException;
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

    @Test
    @WithMockUser(authorities = PersonPermissions.READ)
    @DisplayName("READ permission: existing person is returned")
    void getPerson_existing_returnsDto() {

        PersonCreateDTO create = new PersonCreateDTO();
        create.setFirstName("Max");
        create.setLastName("Mustermann");
        create.setBirthday(LocalDate.of(1990, 1, 1));

        PersonDTO created = personDomainService.createPerson(create);

        Optional<PersonDTO> loaded = personDomainService.getPersonById(created.getId());

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
    @WithMockUser
    @DisplayName("Without READ permission -> AccessDeniedException")
    void getPerson_withoutPermission_throwsAccessDenied() {

        assertThrows(
                AccessDeniedException.class,
                () -> personDomainService.getPersonById("any-id")
        );
    }
}
