package de.cocondo.app.domain.personnel.person;

import de.cocondo.app.domain.personnel.person.dto.PersonDTO;
import de.cocondo.app.domain.personnel.person.mapper.PersonMapper;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Dedicated workflow/state service for Person.
 *
 * Separates state management from attribute updates (Option 1).
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PersonStateService {

    private final PersonEntityService personEntityService;
    private final PersonMapper personMapper;

    @PreAuthorize("hasAuthority(T(de.cocondo.app.domain.personnel.person.PersonPermissions).STATE_UPDATE)")
    public PersonDTO setStatus(String id, PersonStatus status) {

        log.debug("Setting Person status id={} to {}", id, status);

        // State transitions are NOT protected by READ RLS; they require explicit permission.
        // We still need to ensure the entity exists.
        Person person = personEntityService
                .loadById(id)
                .orElseThrow(() -> new EntityNotFoundException("Person not found: " + id));

        person.setStatus(status);

        Person persisted = personEntityService.save(person);

        log.info("Person status updated id={} status={}", persisted.getId(), persisted.getStatus());

        return personMapper.toDto(persisted);
    }

    @PreAuthorize("hasAuthority(T(de.cocondo.app.domain.personnel.person.PersonPermissions).STATE_UPDATE)")
    public PersonDTO activate(String id) {
        return setStatus(id, PersonStatus.ACTIVE);
    }

    @PreAuthorize("hasAuthority(T(de.cocondo.app.domain.personnel.person.PersonPermissions).STATE_UPDATE)")
    public PersonDTO deactivate(String id) {
        return setStatus(id, PersonStatus.INACTIVE);
    }
}