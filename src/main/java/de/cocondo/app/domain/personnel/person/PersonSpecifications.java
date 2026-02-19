package de.cocondo.app.domain.personnel.person;

import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

/**
 * Specification builder for Person filtering.
 *
 * Combines optional filter criteria into a single Specification.
 */
public final class PersonSpecifications {

    private PersonSpecifications() {
    }

    public static Specification<Person> build(
            String firstName,
            String middleName,
            String lastName,
            Gender gender,
            String salutation,
            String academicTitle,
            LocalDate birthdayFrom,
            LocalDate birthdayTo
    ) {
        Specification<Person> spec = Specification.where(null);

        spec = spec.and(containsIgnoreCase("firstName", firstName));
        spec = spec.and(containsIgnoreCase("middleName", middleName));
        spec = spec.and(containsIgnoreCase("lastName", lastName));
        spec = spec.and(equalsGender(gender));
        spec = spec.and(containsIgnoreCase("salutation", salutation));
        spec = spec.and(containsIgnoreCase("academicTitle", academicTitle));
        spec = spec.and(birthdayBetween(birthdayFrom, birthdayTo));

        return spec;
    }

    private static Specification<Person> containsIgnoreCase(String field, String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        String pattern = "%" + value.trim().toLowerCase() + "%";

        return (root, query, cb) ->
                cb.like(cb.lower(root.get(field)), pattern);
    }

    private static Specification<Person> equalsGender(Gender gender) {
        if (gender == null) {
            return null;
        }

        return (root, query, cb) ->
                cb.equal(root.get("gender"), gender);
    }

    private static Specification<Person> birthdayBetween(LocalDate from, LocalDate to) {
        if (from == null && to == null) {
            return null;
        }

        return (root, query, cb) -> {
            if (from != null && to != null) {
                return cb.between(root.get("birthday"), from, to);
            }
            if (from != null) {
                return cb.greaterThanOrEqualTo(root.get("birthday"), from);
            }
            return cb.lessThanOrEqualTo(root.get("birthday"), to);
        };
    }
}
