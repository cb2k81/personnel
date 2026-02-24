package de.cocondo.app.domain.personnel.budget;

import lombok.Getter;
import lombok.Setter;
import de.cocondo.app.system.entity.DomainEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.LocalDate;

/**
 * file: /opt/cocondo/personnel/src/main/java/de/cocondo/app/domain/personnel/budget/Budget.java
 *
 * Entity representing an approved annual public budget as the legal basis for staffing plans. // Entität für einen genehmigten jährlichen Haushalt als Grundlage für Stellenpläne
 */
@Entity
@Getter
@Setter
@Table(name = "budget")
public class Budget extends DomainEntity {

    private Integer fiscalYear; // Haushaltsjahr

    private String state; // Status (z. B. DRAFT / APPROVED / CLOSED) // Status

    private LocalDate approvedDate; // Genehmigungsdatum

    private String description; // Beschreibung
}
