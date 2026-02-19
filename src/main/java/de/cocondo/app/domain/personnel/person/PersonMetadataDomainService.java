package de.cocondo.app.domain.personnel.person;

import de.cocondo.app.system.dto.DomainEntityMetadataDTO;
import de.cocondo.app.system.entity.metadata.AbstractMetadataDomainService;
import de.cocondo.app.system.entity.metadata.KeyValueService;
import de.cocondo.app.system.entity.metadata.TagService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Domain service for Person metadata (tags + key-value pairs).
 *
 * Only responsible for:
 * - permissions
 * - delegating to abstract base logic
 */
@Service
@Transactional
public class PersonMetadataDomainService extends AbstractMetadataDomainService<Person> {

    private final PersonEntityService personEntityService;

    public PersonMetadataDomainService(
            PersonEntityService personEntityService,
            TagService tagService,
            KeyValueService keyValueService
    ) {
        super(tagService, keyValueService);
        this.personEntityService = personEntityService;
    }

    @Override
    protected Person loadEntity(String id) {
        return personEntityService
                .loadById(id)
                .orElse(null);
    }

    @Override
    protected Person saveEntity(Person entity) {
        return personEntityService.save(entity);
    }

    @PreAuthorize("hasAuthority(T(de.cocondo.app.domain.personnel.person.PersonPermissions).METADATA_READ)")
    @Transactional(readOnly = true)
    public DomainEntityMetadataDTO getMetadata(String personId) {
        return getMetadataInternal(personId);
    }

    @PreAuthorize("hasAuthority(T(de.cocondo.app.domain.personnel.person.PersonPermissions).METADATA_UPDATE)")
    public DomainEntityMetadataDTO replaceMetadata(String personId, DomainEntityMetadataDTO inbound) {
        return replaceMetadataInternal(personId, inbound);
    }

    @PreAuthorize("hasAuthority(T(de.cocondo.app.domain.personnel.person.PersonPermissions).METADATA_UPDATE)")
    public DomainEntityMetadataDTO patchMetadata(String personId, DomainEntityMetadataDTO inbound) {
        return patchMetadataInternal(personId, inbound);
    }
}
