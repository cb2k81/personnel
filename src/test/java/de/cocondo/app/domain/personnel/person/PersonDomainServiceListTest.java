package de.cocondo.app.domain.personnel.person;

import de.cocondo.app.domain.personnel.person.dto.PersonDTO;
import de.cocondo.app.support.AbstractSpringIntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.context.support.WithMockUser;

import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PersonDomainServiceListTest extends AbstractSpringIntegrationTest {

    @Autowired
    private PersonDomainService personDomainService;

    @Autowired
    private PersonRepository personRepository;

    @Test
    @WithMockUser(authorities = PersonPermissions.READ)
    @DisplayName("LIST (RLS): without READ_INACTIVE -> only ACTIVE persons are visible")
    void listPersons_withoutReadInactive_onlyActiveVisible() {

        Person a1 = new Person();
        a1.setFirstName("A");
        a1.setLastName("One");
        a1.setStatus(PersonStatus.ACTIVE);
        personRepository.save(a1);

        Person a2 = new Person();
        a2.setFirstName("A");
        a2.setLastName("Two");
        a2.setStatus(PersonStatus.ACTIVE);
        personRepository.save(a2);

        Person i1 = new Person();
        i1.setFirstName("I");
        i1.setLastName("One");
        i1.setStatus(PersonStatus.INACTIVE);
        personRepository.save(i1);

        Page<PersonDTO> page = personDomainService.listPersons(
                Specification.where(null),
                PageRequest.of(0, 10)
        );

        assertThat(page.getTotalElements()).isEqualTo(2);

        Set<String> ids = page.getContent().stream().map(PersonDTO::getId).collect(Collectors.toSet());
        assertThat(ids).contains(a1.getId(), a2.getId());
        assertThat(ids).doesNotContain(i1.getId());
    }

    @Test
    @WithMockUser(authorities = {PersonPermissions.READ, PersonPermissions.READ_INACTIVE})
    @DisplayName("LIST (RLS): with READ_INACTIVE -> ACTIVE and INACTIVE are visible")
    void listPersons_withReadInactive_allVisible() {

        Person a1 = new Person();
        a1.setFirstName("A");
        a1.setLastName("One");
        a1.setStatus(PersonStatus.ACTIVE);
        personRepository.save(a1);

        Person i1 = new Person();
        i1.setFirstName("I");
        i1.setLastName("One");
        i1.setStatus(PersonStatus.INACTIVE);
        personRepository.save(i1);

        Page<PersonDTO> page = personDomainService.listPersons(
                Specification.where(null),
                PageRequest.of(0, 10)
        );

        assertThat(page.getTotalElements()).isEqualTo(2);

        Set<String> ids = page.getContent().stream().map(PersonDTO::getId).collect(Collectors.toSet());
        assertThat(ids).contains(a1.getId(), i1.getId());
    }

    @Test
    @WithMockUser(authorities = PersonPermissions.READ)
    @DisplayName("LIST (Filter): firstName containsIgnoreCase -> hit + no-hit")
    void listPersons_filter_firstName_equivalenceClasses() {

        Person p1 = new Person();
        p1.setFirstName("Max");
        p1.setLastName("One");
        p1.setStatus(PersonStatus.ACTIVE);
        personRepository.save(p1);

        Person p2 = new Person();
        p2.setFirstName("Moritz");
        p2.setLastName("Two");
        p2.setStatus(PersonStatus.ACTIVE);
        personRepository.save(p2);

        Specification<Person> hitSpec = PersonSpecifications.build(
                "ma", null, null, null, null, null, null, null
        );

        Page<PersonDTO> hit = personDomainService.listPersons(hitSpec, PageRequest.of(0, 10));
        assertThat(hit.getTotalElements()).isEqualTo(1);
        assertThat(hit.getContent().getFirst().getId()).isEqualTo(p1.getId());

        Specification<Person> noHitSpec = PersonSpecifications.build(
                "zzz", null, null, null, null, null, null, null
        );

        Page<PersonDTO> noHit = personDomainService.listPersons(noHitSpec, PageRequest.of(0, 10));
        assertThat(noHit.getTotalElements()).isEqualTo(0);
    }

    @Test
    @WithMockUser(authorities = PersonPermissions.READ)
    @DisplayName("LIST (Filter): gender equals -> hit + no-hit")
    void listPersons_filter_gender_equivalenceClasses() {

        Person p1 = new Person();
        p1.setFirstName("Alex");
        p1.setLastName("Male");
        p1.setGender(Gender.MALE);
        p1.setStatus(PersonStatus.ACTIVE);
        personRepository.save(p1);

        Person p2 = new Person();
        p2.setFirstName("Alex");
        p2.setLastName("Female");
        p2.setGender(Gender.FEMALE);
        p2.setStatus(PersonStatus.ACTIVE);
        personRepository.save(p2);

        Specification<Person> maleSpec = PersonSpecifications.build(
                null, null, null, Gender.MALE, null, null, null, null
        );

        Page<PersonDTO> male = personDomainService.listPersons(maleSpec, PageRequest.of(0, 10));
        assertThat(male.getTotalElements()).isEqualTo(1);
        assertThat(male.getContent().getFirst().getId()).isEqualTo(p1.getId());

        Specification<Person> diverseSpec = PersonSpecifications.build(
                null, null, null, Gender.DIVERSE, null, null, null, null
        );

        Page<PersonDTO> diverse = personDomainService.listPersons(diverseSpec, PageRequest.of(0, 10));
        assertThat(diverse.getTotalElements()).isEqualTo(0);
    }

    @Test
    @WithMockUser(authorities = PersonPermissions.READ)
    @DisplayName("LIST (Filter): birthdayBetween -> hit + no-hit")
    void listPersons_filter_birthdayBetween_equivalenceClasses() {

        Person p1 = new Person();
        p1.setFirstName("Older");
        p1.setLastName("Person");
        p1.setBirthday(LocalDate.of(1980, 1, 1));
        p1.setStatus(PersonStatus.ACTIVE);
        personRepository.save(p1);

        Person p2 = new Person();
        p2.setFirstName("Younger");
        p2.setLastName("Person");
        p2.setBirthday(LocalDate.of(2000, 1, 1));
        p2.setStatus(PersonStatus.ACTIVE);
        personRepository.save(p2);

        Specification<Person> hitSpec = PersonSpecifications.build(
                null, null, null, null, null, null,
                LocalDate.of(1979, 1, 1),
                LocalDate.of(1990, 12, 31)
        );

        Page<PersonDTO> hit = personDomainService.listPersons(hitSpec, PageRequest.of(0, 10));
        assertThat(hit.getTotalElements()).isEqualTo(1);
        assertThat(hit.getContent().getFirst().getId()).isEqualTo(p1.getId());

        Specification<Person> noHitSpec = PersonSpecifications.build(
                null, null, null, null, null, null,
                LocalDate.of(2010, 1, 1),
                LocalDate.of(2020, 12, 31)
        );

        Page<PersonDTO> noHit = personDomainService.listPersons(noHitSpec, PageRequest.of(0, 10));
        assertThat(noHit.getTotalElements()).isEqualTo(0);
    }

    @Test
    @WithMockUser // no authorities
    @DisplayName("LIST without READ -> AccessDeniedException")
    void listPersons_withoutReadPermission_denied() {

        assertThrows(
                AccessDeniedException.class,
                () -> personDomainService.listPersons(Specification.where(null), PageRequest.of(0, 10))
        );
    }
}