package de.cocondo.app.domain.personnel.person;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.LocalDate;

/**
 * Entity Klasse für die natürliche Person
 */
@Entity
@Table(name = "person")
@Data
public class Person {

    /**
     * Technical identifier (UUID stored as String).
     * Generation is handled outside of the entity.
     */
    @Id
    private String id;

    private String firstName;
    private String middleName;
    private String lastName;

    private String gender;
    private String salutation;
    private String academicTitle;

    private LocalDate birthday;
}
