package de.cocondo.app.domain.personnel.budget;

import lombok.Getter;
import lombok.Setter;
import de.cocondo.app.system.entity.DomainEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.LocalDate;

/**
 * file: /opt/cocondo/personnel/src/main/java/de/cocondo/app/domain/personnel/budget/BudgetRevision.java
 *
 * Entity representing a revision or supplementary budget (Nachtragshaushalt). // Entität für einen Haushaltsänderungsstand (Nachtrag)
 */
@Entity
@Getter
@Setter
@Table(name = "budgetrevision")
public class BudgetRevision extends DomainEntity {

    @ManyToOne
    private Budget budget; // Zugehöriger Haushalt

    private Integer revisionNumber; // Revisionsnummer

    private String reason; // Begründung der Änderung

    private LocalDate validFrom; // Gültig ab

    private LocalDate validTo; // Gültig bis

    private String description; // Beschreibung
}
