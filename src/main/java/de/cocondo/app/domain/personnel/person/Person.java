package de.cocondo.app.domain.personnel.person;

import de.cocondo.app.system.entity.DomainEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "person")
@Getter
@Setter
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