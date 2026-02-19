package de.cocondo.app.domain.personnel.person;

import de.cocondo.app.domain.personnel.assignment.PositionFillingRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
class PersonDomainServiceDeleteTest {

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private PersonDomainService personDomainService;

    /**
     * We mock this repository because PositionFilling has multiple non-nullable fields,
     * and persisting a minimal PositionFilling instance would be invalid in the baseline model.
     *
     * The domain service uses exactly this method to decide between hard delete and anonymize.
     */
    @MockBean
    private PositionFillingRepository positionFillingRepository;

    @Test
    @WithMockUser(authorities = { PersonPermissions.DELETE })
    @DisplayName("Delete person without references -> entity is hard-deleted")
    void deletePerson_withoutReferences_deletesRow() {

        Person person = new Person();
        person.setFirstName("Max");
        person.setLastName("Mustermann");
        personRepository.save(person);

        String id = person.getId();

        when(positionFillingRepository.existsByPerson_Id(id)).thenReturn(false);

        personDomainService.deletePerson(id);

        Optional<Person> deleted = personRepository.findById(id);
        assertThat(deleted).isEmpty();
    }

    @Test
    @WithMockUser(authorities = { PersonPermissions.DELETE })
    @DisplayName("Delete person with references -> person is anonymized instead of hard-deleted")
    void deletePerson_withReferences_anonymizes() {

        Person person = new Person();
        person.setFirstName("Max");
        person.setMiddleName("X.");
        person.setLastName("Mustermann");
        person.setSalutation("Herr");
        person.setAcademicTitle("Dr.");
        personRepository.save(person);

        String id = person.getId();

        when(positionFillingRepository.existsByPerson_Id(id)).thenReturn(true);

        personDomainService.deletePerson(id);

        Person anonymized = personRepository.findById(id).orElseThrow();

        // PII cleared
        assertThat(anonymized.getFirstName()).isNull();
        assertThat(anonymized.getMiddleName()).isNull();
        assertThat(anonymized.getGender()).isNull();
        assertThat(anonymized.getSalutation()).isNull();
        assertThat(anonymized.getAcademicTitle()).isNull();
        assertThat(anonymized.getBirthday()).isNull();

        // Marker stays as String (as per service logic)
        assertThat(anonymized.getLastName()).isEqualTo("ANONYMIZED");

        // If you extended the enum, we should also assert the status
        assertThat(anonymized.getStatus()).isEqualTo(PersonStatus.ANONYMIZED);
    }
}
