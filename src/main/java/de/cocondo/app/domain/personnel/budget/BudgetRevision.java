package de.cocondo.app.domain.personnel.budget;

import de.cocondo.app.system.entity.DomainEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * file: /opt/cocondo/personnel/src/main/java/de/cocondo/app/domain/personnel/budget/BudgetRevision.java
 *
 * Entity representing a revision or supplementary budget (Nachtragshaushalt). // Entität für einen Haushaltsänderungsstand (Nachtrag)
 */
@Entity
@Table(name = "budgetrevision")
@Data
@EqualsAndHashCode(callSuper = true)
public class BudgetRevision extends DomainEntity {

    @ManyToOne
    private Budget budget; // Zugehöriger Haushalt

    private Integer revisionNumber; // Revisionsnummer

    private String reason; // Begründung der Änderung

    private LocalDate validFrom; // Gültig ab

    private LocalDate validTo; // Gültig bis

    private String description; // Beschreibung
}
