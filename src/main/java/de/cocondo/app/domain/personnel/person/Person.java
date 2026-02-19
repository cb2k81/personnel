package de.cocondo.app.domain.personnel.person;

import de.cocondo.app.system.entity.DomainEntity;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

/**
 * Entity Klasse für die natürliche Person
 */
@Entity
@Table(name = "person")
@Data
public class Person extends DomainEntity {

    private String firstName;
    private String middleName;
    private String lastName;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private Gender gender;

    private String salutation;
    private String academicTitle;

    private LocalDate birthday;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private PersonStatus status;
}
