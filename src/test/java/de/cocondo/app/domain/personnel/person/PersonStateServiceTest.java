package de.cocondo.app.domain.personnel.person;

import de.cocondo.app.domain.personnel.person.dto.PersonDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class PersonStateServiceTest {

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private PersonStateService personStateService;

    @Test
    @WithMockUser(authorities = PersonPermissions.STATE_UPDATE)
    @DisplayName("Activate sets status ACTIVE")
    void activate_setsActive() {

        Person p = new Person();
        p.setFirstName("Max");
        p.setLastName("Mustermann");
        p.setStatus(PersonStatus.INACTIVE);
        personRepository.save(p);

        PersonDTO dto = personStateService.activate(p.getId());

        assertThat(dto.getStatus()).isEqualTo(PersonStatus.ACTIVE);
    }

    @Test
    @WithMockUser
    @DisplayName("State update without permission -> AccessDeniedException")
    void stateUpdate_withoutPermission_throws() {

        Person p = new Person();
        p.setFirstName("Max");
        p.setLastName("Mustermann");
        p.setStatus(PersonStatus.ACTIVE);
        personRepository.save(p);

        assertThatThrownBy(() ->
                personStateService.deactivate(p.getId())
        ).isInstanceOf(AccessDeniedException.class);
    }
}