package de.cocondo.app.domain.personnel.person.mapper;

import de.cocondo.app.domain.personnel.person.Person;
import de.cocondo.app.domain.personnel.person.dto.PersonDTO;
import de.cocondo.app.domain.personnel.person.dto.PersonPayloadDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface PersonMapper {

    // ----------------------------------------------------
    // CREATE
    // ----------------------------------------------------

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "persistenceVersion", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "lastModifiedBy", ignore = true)
    @Mapping(target = "lastModifiedAt", ignore = true)
    @Mapping(target = "tags", ignore = true)
    @Mapping(target = "keyValuePairs", ignore = true)
    @Mapping(target = "allKeyValues", ignore = true)
    @Mapping(target = "status", ignore = true)
    Person fromPayload(PersonPayloadDTO payload);

    // ----------------------------------------------------
    // READ
    // ----------------------------------------------------

    PersonDTO toDto(Person person);

    // ----------------------------------------------------
    // UPDATE
    // ----------------------------------------------------

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "persistenceVersion", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "lastModifiedBy", ignore = true)
    @Mapping(target = "lastModifiedAt", ignore = true)
    @Mapping(target = "tags", ignore = true)
    @Mapping(target = "keyValuePairs", ignore = true)
    @Mapping(target = "allKeyValues", ignore = true)
    @Mapping(target = "status", ignore = true)
    void updateFromPayload(PersonPayloadDTO payload, @MappingTarget Person person);
}