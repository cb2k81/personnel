package de.cocondo.app.domain.personnel.budget;

import lombok.Getter;
import lombok.Setter;
import de.cocondo.app.system.entity.DomainEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

/**
 * file: /opt/cocondo/personnel/src/main/java/de/cocondo/app/domain/personnel/budget/BudgetAccount.java
 *
 * Entity representing a budget account (Haushaltsstelle) belonging to a budget revision. // Entität für eine Haushaltsstelle eines Haushaltsänderungsstands
 */
@Entity
@Getter
@Setter
@Table(name = "budgetaccount")
public class BudgetAccount extends DomainEntity {

    @ManyToOne
    private BudgetRevision budgetRevision; // Zugehöriger Haushaltsänderungsstand

    private String accountCode; // Haushaltsstellen-Code

    private String name; // Name der Haushaltsstelle
    private String description; // Beschreibung
}
