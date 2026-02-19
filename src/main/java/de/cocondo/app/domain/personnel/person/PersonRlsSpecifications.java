package de.cocondo.app.domain.personnel.person;

import org.springframework.data.jpa.domain.Specification;

/**
 * Record-Level-Security specifications for Person (ADR 010).
 *
 * This class contains ONLY filtering rules, not permission checks.
 * Permission checks are done via @PreAuthorize in the domain service.
 */
public final class PersonRlsSpecifications {

    private PersonRlsSpecifications() {
    }

    /**
     * Users without {@link PersonPermissions#READ_INACTIVE} may only see ACTIVE persons.
     *
     * Users with READ_INACTIVE may see both ACTIVE and INACTIVE persons.
     */
    public static Specification<Person> rlsCanReadPersons(boolean canReadInactive) {
        if (canReadInactive) {
            return Specification.where(null); // no restriction
        }
        return (root, query, cb) -> cb.equal(root.get("status"), PersonStatus.ACTIVE);
    }

    public static Specification<Person> byId(String id) {
        return (root, query, cb) -> cb.equal(root.get("id"), id);
    }
}
