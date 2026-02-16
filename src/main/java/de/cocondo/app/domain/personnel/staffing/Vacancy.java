package de.cocondo.app.domain.personnel.staffing;

import de.cocondo.app.system.entity.DomainEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * file: /opt/cocondo/personnel/src/main/java/de/cocondo/app/domain/personnel/staffing/Vacancy.java
 *
 * Entity representing an approved but currently unfilled planned position (vacancy). // Entität für eine genehmigte, aktuell unbesetzte Planstelle (Leerstelle)
 */
@Entity
@Table(name = "vacancy")
@Data
@EqualsAndHashCode(callSuper = true)
public class Vacancy extends DomainEntity {

    @ManyToOne
    private PlannedPosition plannedPosition; // Planstelle

    private LocalDate approvedFrom; // Genehmigt ab
    private LocalDate approvedTo; // Genehmigt bis

    private String reason; // Grund (z. B. Elternzeit)

    private String description; // Beschreibung
}
