package de.cocondo.app.domain.personnel.person;

/**
 * Central definition of atomic permissions for the Person aggregate.
 *
 * Rules:
 * - Methods use permissions, never roles.
 * - Roles will later be mapped to these permissions.
 */
public final class PersonPermissions {

    public static final String CREATE = "PERSONNEL_PERSON_CREATE";
    public static final String READ = "PERSONNEL_PERSON_READ";
    public static final String UPDATE = "PERSONNEL_PERSON_UPDATE";

    /**
     * Hard delete permission (exclusive).
     * Deleting is only allowed if NO references exist.
     */
    public static final String DELETE = "PERSONNEL_PERSON_DELETE";

    /**
     * Workflow/state changes (e.g. ACTIVE <-> INACTIVE).
     */
    public static final String STATE_UPDATE = "PERSONNEL_PERSON_STATE_UPDATE";

    /**
     * Irreversible privacy operation: anonymize PII + set status ANONYMIZED.
     */
    public static final String ANONYMIZE = "PERSONNEL_PERSON_ANONYMIZE";

    public static final String METADATA_READ = "PERSONNEL_PERSON_METADATA_READ";
    public static final String METADATA_UPDATE = "PERSONNEL_PERSON_METADATA_UPDATE";

    /**
     * RLS-related permission:
     * Users without this permission must not see INACTIVE persons.
     */
    public static final String READ_INACTIVE = "PERSONNEL_PERSON_READ_INACTIVE";

    private PersonPermissions() {
    }
}