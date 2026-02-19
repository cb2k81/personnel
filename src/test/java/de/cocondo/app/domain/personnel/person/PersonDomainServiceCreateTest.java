package de.cocondo.app.domain.personnel.person;

import de.cocondo.app.domain.personnel.person.dto.PersonCreateDTO;
import de.cocondo.app.domain.personnel.person.dto.PersonDTO;
import de.cocondo.app.domain.personnel.person.PersonPermissions;
import de.cocondo.app.support.AbstractSpringIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.context.support.WithMockUser;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Tests for PersonDomainService create operation.
 *
 * Focus:
 * - Permission enforcement
 * - Default status handling
 * - Entity persistence
 */
class PersonDomainServiceCreateTest extends AbstractSpringIntegrationTest {

    @Autowired
    private PersonDomainService personDomainService;

    @Test
    @WithMockUser(authorities = PersonPermissions.CREATE)
    void createPerson_success() {

        PersonCreateDTO dto = new PersonCreateDTO();
        dto.setFirstName("Max");
        dto.setLastName("Mustermann");
        dto.setBirthday(LocalDate.of(1990, 1, 1));

        PersonDTO created = personDomainService.createPerson(dto);

        assertThat(created).isNotNull();
        assertThat(created.getId()).isNotBlank();
        assertThat(created.getFirstName()).isEqualTo("Max");
        assertThat(created.getLastName()).isEqualTo("Mustermann");
    }

    @Test
    @WithMockUser // no authorities
    void createPerson_withoutPermission_throwsAccessDenied() {

        PersonCreateDTO dto = new PersonCreateDTO();
        dto.setFirstName("Test");
        dto.setLastName("User");

        assertThrows(
                AccessDeniedException.class,
                () -> personDomainService.createPerson(dto)
        );
    }

    @Test
    @WithMockUser(authorities = PersonPermissions.CREATE)
    void createPerson_nullGender_allowed() {

        PersonCreateDTO dto = new PersonCreateDTO();
        dto.setFirstName("Anna");
        dto.setLastName("Example");
        dto.setGender(null);

        PersonDTO created = personDomainService.createPerson(dto);

        assertThat(created).isNotNull();
        assertThat(created.getGender()).isNull();
    }
}
