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
    public static final String DELETE = "PERSONNEL_PERSON_DELETE";

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
