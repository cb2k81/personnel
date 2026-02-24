package de.cocondo.app.domain.personnel.person;

import de.cocondo.app.domain.personnel.person.dto.PersonDTO;
import de.cocondo.app.domain.personnel.person.mapper.PersonMapper;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Dedicated privacy service for Person.
 *
 * Anonymization is a separate operation from delete and state transitions.
 * It is irreversible and must be protected by a dedicated permission.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PersonPrivacyService {

    private final PersonEntityService personEntityService;
    private final PersonMapper personMapper;
    private final PersonAnonymizationProperties anonymizationProperties;

    @PreAuthorize("hasAuthority(T(de.cocondo.app.domain.personnel.person.PersonPermissions).ANONYMIZE)")
    public PersonDTO anonymize(String id) {

        log.debug("Anonymization requested for Person id={}", id);

        Person person = personEntityService
                .loadById(id)
                .orElseThrow(() -> new EntityNotFoundException("Person not found: " + id));

        applyAnonymization(person);

        Person persisted = personEntityService.save(person);

        log.info("Person anonymized id={} status={}", persisted.getId(), persisted.getStatus());

        return personMapper.toDto(persisted);
    }

    private void applyAnonymization(Person person) {

        String replacement = anonymizationProperties.getDefaultValue();

        log.debug("Applying anonymization replacement value='{}' for person id={}",
                replacement, person.getId());

        person.setStatus(PersonStatus.ANONYMIZED);

        person.setFirstName(replacement);
        person.setMiddleName(replacement);
        person.setLastName(replacement);
        person.setSalutation(replacement);
        person.setAcademicTitle(replacement);

        person.setGender(null);
        person.setBirthday(null);
    }
}