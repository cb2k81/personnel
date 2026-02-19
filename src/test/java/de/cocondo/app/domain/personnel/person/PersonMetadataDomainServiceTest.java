package de.cocondo.app.domain.personnel.person;

import de.cocondo.app.system.dto.DomainEntityMetadataDTO;
import de.cocondo.app.system.entity.metadata.KeyValuePairDTO;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;

import java.util.Set;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class PersonMetadataDomainServiceTest {

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private PersonMetadataDomainService personMetadataDomainService;

    @Test
    @WithMockUser(authorities = {PersonPermissions.METADATA_READ})
    @DisplayName("getMetadata returns tags + keyValuePairs")
    void getMetadata_returnsMetadata() {

        Person p = new Person();
        p.setFirstName("Max");
        p.setLastName("Mustermann");

        p.getTags().add("t1");
        p.addKeyValue("k1", "v1");

        personRepository.save(p);

        DomainEntityMetadataDTO meta = personMetadataDomainService.getMetadata(p.getId());

        assertThat(meta.getTags()).containsExactlyInAnyOrder("t1");
        assertThat(meta.getKeyValuePairs())
                .extracting(KeyValuePairDTO::getKey, KeyValuePairDTO::getValue)
                .contains(tuple("k1", "v1"));
    }

    @Test
    @WithMockUser(authorities = {}) // no metadata rights
    @DisplayName("getMetadata without permission is denied")
    void getMetadata_withoutPermission_denied() {
        Person p = new Person();
        p.setFirstName("Max");
        p.setLastName("Mustermann");
        personRepository.save(p);

        assertThatThrownBy(() -> personMetadataDomainService.getMetadata(p.getId()))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    @WithMockUser(authorities = {PersonPermissions.METADATA_UPDATE, PersonPermissions.METADATA_READ})
    @DisplayName("replaceMetadata clears existing metadata and sets inbound")
    void replaceMetadata_replacesAll() {

        Person p = new Person();
        p.setFirstName("Max");
        p.setLastName("Mustermann");
        p.getTags().add("oldTag");
        p.addKeyValue("oldKey", "oldVal");
        personRepository.save(p);

        KeyValuePairDTO kv = new KeyValuePairDTO();
        kv.setKey("newKey");
        kv.setValue("newVal");

        DomainEntityMetadataDTO inbound = new DomainEntityMetadataDTO();
        inbound.setTags(Set.of("newTag"));
        inbound.setKeyValuePairs(Set.of(kv));

        DomainEntityMetadataDTO updated = personMetadataDomainService.replaceMetadata(p.getId(), inbound);

        assertThat(updated.getTags()).containsExactlyInAnyOrder("newTag");
        assertThat(updated.getKeyValuePairs())
                .extracting(KeyValuePairDTO::getKey, KeyValuePairDTO::getValue)
                .contains(tuple("newKey", "newVal"))
                .doesNotContain(tuple("oldKey", "oldVal"));
    }

    @Test
    @WithMockUser(authorities = {PersonPermissions.METADATA_UPDATE, PersonPermissions.METADATA_READ})
    @DisplayName("patchMetadata: null inbound does not change anything")
    void patchMetadata_nullInbound_noChange() {

        Person p = new Person();
        p.setFirstName("Max");
        p.setLastName("Mustermann");
        p.getTags().add("t1");
        p.addKeyValue("k1", "v1");
        personRepository.save(p);

        DomainEntityMetadataDTO metaBefore = personMetadataDomainService.getMetadata(p.getId());
        DomainEntityMetadataDTO metaAfter = personMetadataDomainService.patchMetadata(p.getId(), null);

        assertThat(metaAfter.getTags()).isEqualTo(metaBefore.getTags());
        assertThat(metaAfter.getKeyValuePairs()).isEqualTo(metaBefore.getKeyValuePairs());
    }

    @Test
    @WithMockUser(authorities = {PersonPermissions.METADATA_UPDATE, PersonPermissions.METADATA_READ})
    @DisplayName("patchMetadata: if tags != null -> tags are replaced, if keyValuePairs == null -> kv stays unchanged")
    void patchMetadata_partialUpdate() {

        Person p = new Person();
        p.setFirstName("Max");
        p.setLastName("Mustermann");
        p.getTags().add("oldTag");
        p.addKeyValue("k1", "v1");
        personRepository.save(p);

        DomainEntityMetadataDTO inbound = new DomainEntityMetadataDTO();
        inbound.setTags(Set.of("newTag"));
        inbound.setKeyValuePairs(null); // keep kv as-is per service logic :contentReference[oaicite:4]{index=4}

        DomainEntityMetadataDTO updated = personMetadataDomainService.patchMetadata(p.getId(), inbound);

        assertThat(updated.getTags()).containsExactlyInAnyOrder("newTag");
        assertThat(updated.getKeyValuePairs())
                .extracting(KeyValuePairDTO::getKey, KeyValuePairDTO::getValue)
                .contains(tuple("k1", "v1"));
    }

    @Test
    @WithMockUser(authorities = {PersonPermissions.METADATA_READ})
    @DisplayName("metadata calls for unknown person -> EntityNotFoundException")
    void metadata_unknownPerson_throws() {
        assertThatThrownBy(() -> personMetadataDomainService.getMetadata("does-not-exist"))
                .isInstanceOf(EntityNotFoundException.class);
    }
}
