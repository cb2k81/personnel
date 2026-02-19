package de.cocondo.app.domain.personnel.person;

import de.cocondo.app.domain.personnel.assignment.PositionFillingRepository;
import de.cocondo.app.domain.personnel.person.dto.PersonDTO;
import de.cocondo.app.domain.personnel.person.dto.PersonPayloadDTO;
import de.cocondo.app.domain.personnel.person.mapper.PersonMapper;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Domain service for the Person aggregate.
 *
 * Responsibilities:
 * - Enforces business rules
 * - Applies Record-Level Security (ADR 010)
 * - Differentiates between hard delete and anonymization
 *
 * Architectural constraints:
 * - No repository access in controller layer
 * - No implicit RLS in repository layer
 * - Explicit visibility handling in service
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PersonDomainService {

    private final PersonEntityService personEntityService;
    private final PersonMapper personMapper;
    private final PositionFillingRepository positionFillingRepository;
    private final PersonAnonymizationProperties anonymizationProperties;

    /**
     * Creates a new Person.
     * Default status: ACTIVE
     */
    @PreAuthorize("hasAuthority(T(de.cocondo.app.domain.personnel.person.PersonPermissions).CREATE)")
    public PersonDTO createPerson(PersonPayloadDTO payloadDTO) {

        log.debug("Creating new Person entity");

        Person person = personMapper.fromPayload(payloadDTO);
        person.setStatus(PersonStatus.ACTIVE);

        Person persisted = personEntityService.save(person);

        log.info("Person created with id={} and status={}", persisted.getId(), persisted.getStatus());

        return personMapper.toDto(persisted);
    }

    /**
     * Returns a person by ID under RLS constraints.
     */
    @PreAuthorize("hasAuthority(T(de.cocondo.app.domain.personnel.person.PersonPermissions).READ)")
    @Transactional(readOnly = true)
    public Optional<PersonDTO> getPersonById(String id) {

        log.debug("Fetching Person id={} under RLS constraints", id);

        Specification<Person> rls = buildPersonReadRlsSpec();

        Optional<PersonDTO> result = personEntityService
                .loadOne(rls.and(PersonRlsSpecifications.byId(id)))
                .map(personMapper::toDto);

        if (result.isEmpty()) {
            log.debug("Person id={} not found or not visible under RLS", id);
        }

        return result;
    }

    /**
     * Lists persons under RLS constraints.
     */
    @PreAuthorize("hasAuthority(T(de.cocondo.app.domain.personnel.person.PersonPermissions).READ)")
    @Transactional(readOnly = true)
    public Page<PersonDTO> listPersons(Specification<Person> specification, Pageable pageable) {

        log.debug("Listing Persons with pageable={} under RLS constraints", pageable);

        Specification<Person> rls = buildPersonReadRlsSpec();

        Page<PersonDTO> page = personEntityService
                .findAll(specification.and(rls), pageable)
                .map(personMapper::toDto);

        log.debug("Person list result size={}", page.getTotalElements());

        return page;
    }

    /**
     * Updates a person if visible under RLS.
     */
    @PreAuthorize("hasAuthority(T(de.cocondo.app.domain.personnel.person.PersonPermissions).UPDATE)")
    public PersonDTO updatePerson(String id, PersonPayloadDTO payloadDTO) {

        log.debug("Updating Person id={}", id);

        Specification<Person> rls = buildPersonReadRlsSpec();

        Person person = personEntityService
                .loadOne(rls.and(PersonRlsSpecifications.byId(id)))
                .orElseThrow(() -> new EntityNotFoundException("Person not found: " + id));

        personMapper.updateFromPayload(payloadDTO, person);

        Person persisted = personEntityService.save(person);

        log.info("Person updated id={} status={}", persisted.getId(), persisted.getStatus());

        return personMapper.toDto(persisted);
    }

    /**
     * Deletes a person.
     *
     * If no references exist → hard delete.
     * If references exist → anonymize.
     */
    @PreAuthorize("hasAuthority(T(de.cocondo.app.domain.personnel.person.PersonPermissions).DELETE)")
    public void deletePerson(String id) {

        log.debug("Delete requested for Person id={}", id);

        Specification<Person> rls = buildPersonReadRlsSpec();

        Person person = personEntityService
                .loadOne(rls.and(PersonRlsSpecifications.byId(id)))
                .orElseThrow(() -> new EntityNotFoundException("Person not found: " + id));

        boolean hasReferences = positionFillingRepository.existsByPerson_Id(id);

        if (!hasReferences) {

            log.info("Hard-deleting Person id={}", id);

            personEntityService.delete(person);
            return;
        }

        log.info("Person id={} has references -> performing anonymization", id);

        anonymize(person);
        personEntityService.save(person);

        log.info("Person id={} anonymized successfully", id);
    }

    /**
     * Performs anonymization of PII fields.
     */
    private void anonymize(Person person) {

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

    /**
     * Builds RLS read specification.
     *
     * Without READ_INACTIVE permission:
     * - Only ACTIVE persons visible.
     *
     * With READ_INACTIVE:
     * - ACTIVE, INACTIVE and ANONYMIZED visible.
     */
    private Specification<Person> buildPersonReadRlsSpec() {

        boolean canReadInactive = SecurityContextHolder.getContext().getAuthentication() != null
                && SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                .anyMatch(a -> PersonPermissions.READ_INACTIVE.equals(a.getAuthority()));

        log.debug("RLS evaluation: canReadInactive={}", canReadInactive);

        return PersonRlsSpecifications.rlsCanReadPersons(canReadInactive);
    }
}
