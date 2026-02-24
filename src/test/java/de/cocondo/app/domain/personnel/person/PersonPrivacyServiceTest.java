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

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class PersonPrivacyServiceTest {

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private PersonPrivacyService personPrivacyService;

    @Test
    @WithMockUser(authorities = PersonPermissions.ANONYMIZE)
    @DisplayName("Anonymize sets status and clears PII")
    void anonymize_setsStatusAndClearsPii() {

        Person p = new Person();
        p.setFirstName("Max");
        p.setMiddleName("M");
        p.setLastName("Mustermann");
        p.setSalutation("Herr");
        p.setAcademicTitle("Dr.");
        p.setGender(Gender.MALE);
        p.setBirthday(LocalDate.of(1990,1,1));
        p.setStatus(PersonStatus.ACTIVE);
        personRepository.save(p);

        PersonDTO dto = personPrivacyService.anonymize(p.getId());

        assertThat(dto.getStatus()).isEqualTo(PersonStatus.ANONYMIZED);
        assertThat(dto.getFirstName()).isEqualTo("anonym");
        assertThat(dto.getLastName()).isEqualTo("anonym");
        assertThat(dto.getGender()).isNull();
        assertThat(dto.getBirthday()).isNull();
    }

    @Test
    @WithMockUser
    @DisplayName("Anonymize without permission -> AccessDeniedException")
    void anonymize_withoutPermission_throws() {

        Person p = new Person();
        p.setFirstName("Max");
        p.setLastName("Mustermann");
        p.setStatus(PersonStatus.ACTIVE);
        personRepository.save(p);

        assertThatThrownBy(() ->
                personPrivacyService.anonymize(p.getId())
        ).isInstanceOf(AccessDeniedException.class);
    }
}