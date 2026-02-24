package de.cocondo.app.domain.personnel.person;

import de.cocondo.app.domain.personnel.person.dto.PersonDTO;
import de.cocondo.app.domain.personnel.person.dto.PersonPayloadDTO;
import de.cocondo.app.support.AbstractSpringIntegrationTest;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.context.support.WithMockUser;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PersonDomainServiceUpdateTest extends AbstractSpringIntegrationTest {

    @Autowired
    private PersonDomainService personDomainService;

    @Autowired
    private PersonEntityService personEntityService;

    @Test
    @WithMockUser(authorities = {
            PersonPermissions.CREATE,
            PersonPermissions.UPDATE
    })
    void updatePerson_success() {

        // create initial entity
        PersonPayloadDTO create = new PersonPayloadDTO();
        create.setFirstName("Max");
        create.setLastName("Mustermann");

        PersonDTO created = personDomainService.createPerson(create);

        // update
        PersonPayloadDTO update = new PersonPayloadDTO();
        update.setFirstName("John");
        update.setLastName("Doe");
        update.setBirthday(LocalDate.of(1985, 5, 5));

        PersonDTO updated = personDomainService.updatePerson(created.getId(), update);

        assertThat(updated.getFirstName()).isEqualTo("John");
        assertThat(updated.getLastName()).isEqualTo("Doe");
        assertThat(updated.getBirthday()).isEqualTo(LocalDate.of(1985, 5, 5));
    }

    @Test
    @WithMockUser
    void updatePerson_withoutPermission_throwsAccessDenied() {

        PersonPayloadDTO update = new PersonPayloadDTO();

        assertThrows(
                AccessDeniedException.class,
                () -> personDomainService.updatePerson("any-id", update)
        );
    }

    @Test
    @WithMockUser(authorities = {
            PersonPermissions.CREATE,
            PersonPermissions.UPDATE
    })
    void updatePerson_inactive_withoutReadInactive_notVisible() {

        // create ACTIVE
        PersonPayloadDTO create = new PersonPayloadDTO();
        create.setFirstName("Inactive");
        create.setLastName("User");

        PersonDTO created = personDomainService.createPerson(create);

        // set INACTIVE directly
        Person entity = personEntityService.loadById(created.getId()).orElseThrow();
        entity.setStatus(PersonStatus.INACTIVE);
        personEntityService.save(entity);

        PersonPayloadDTO update = new PersonPayloadDTO();
        update.setFirstName("Changed");

        assertThrows(
                EntityNotFoundException.class,
                () -> personDomainService.updatePerson(created.getId(), update)
        );
    }

    @Test
    @WithMockUser(authorities = {
            PersonPermissions.CREATE,
            PersonPermissions.UPDATE,
            PersonPermissions.READ_INACTIVE
    })
    void updatePerson_inactive_withReadInactive_success() {

        PersonPayloadDTO create = new PersonPayloadDTO();
        create.setFirstName("Inactive");
        create.setLastName("User");

        PersonDTO created = personDomainService.createPerson(create);

        Person entity = personEntityService.loadById(created.getId()).orElseThrow();
        entity.setStatus(PersonStatus.INACTIVE);
        personEntityService.save(entity);

        PersonPayloadDTO update = new PersonPayloadDTO();
        update.setLastName("Changed");

        PersonDTO updated = personDomainService.updatePerson(created.getId(), update);

        assertThat(updated.getLastName()).isEqualTo("Changed");
    }
}