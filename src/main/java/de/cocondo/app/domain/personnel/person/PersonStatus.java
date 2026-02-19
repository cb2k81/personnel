package de.cocondo.app.domain.personnel.person;

/**
 * Status of a Person record.
 *
 * Used for Record-Level Security demonstrations (ADR 010):
 * - Default users may only see ACTIVE records.
 * - Users with READ_INACTIVE may also see INACTIVE records.
 */
public enum PersonStatus {

    ACTIVE,
    ANONYMIZED,
    INACTIVE
}
