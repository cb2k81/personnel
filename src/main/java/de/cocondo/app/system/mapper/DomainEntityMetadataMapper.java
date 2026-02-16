package de.cocondo.app.system.mapper;

import de.cocondo.app.system.entity.DomainEntity;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class DomainEntityMetadataMapper {

    public Map<String, Object> mapMetadata(DomainEntity entity) {
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("id", entity.getId());
        metadata.put("createdBy", entity.getCreatedBy());
        metadata.put("createdAt", entity.getCreatedAt());
        metadata.put("lastModifiedBy", entity.getLastModifiedBy());
        metadata.put("lastModifiedAt", entity.getLastModifiedAt());
        metadata.put("tags", entity.getTags());
        metadata.put("keyValuePairs", entity.getKeyValuePairs());
        return metadata;
    }
}
