package de.cocondo.app.domain.personnel.staffing;

import de.cocondo.app.domain.personnel.staffing.dto.StaffingPlanSetCreateDTO;
import de.cocondo.app.domain.personnel.staffing.dto.StaffingPlanSetDTO;
import de.cocondo.app.domain.personnel.staffing.dto.StaffingPlanSetUpdateDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * file: /opt/cocondo/personnel/src/main/java/de/cocondo/app/domain/personnel/staffing/StaffingPlanSetController.java
 *
 * REST controller exposing StaffingPlanSet use cases. // REST-Controller zur Exponierung der StaffingPlanSet-Use-Cases
 *
 * Responsibilities: // Verantwortlichkeiten
 * - HTTP request handling // HTTP-Request-Handling
 * - Delegation to DomainService // Delegation an Domain-Service
 * - No business logic // Keine Business-Logik
 *
 * Errors are mapped by GlobalExceptionHandler. // Fehler werden vom GlobalExceptionHandler verarbeitet
 */
@RestController
@RequestMapping("/api/personnel/staffing/plan-sets")
@RequiredArgsConstructor
@Slf4j
public class StaffingPlanSetController {

    private final StaffingPlanSetDomainService domainService; // Domain-Service

    @PostMapping
    public ResponseEntity<StaffingPlanSetDTO> create(@RequestBody StaffingPlanSetCreateDTO body) {
        StaffingPlanSetDTO created = domainService.create(body);
        log.info("REST: Created StaffingPlanSet id={}", created.getId());
        return ResponseEntity.ok(created);
    }

    @GetMapping("/{id}")
    public ResponseEntity<StaffingPlanSetDTO> getById(@PathVariable String id) {
        return ResponseEntity.ok(domainService.getById(id));
    }

    @GetMapping
    public ResponseEntity<Page<StaffingPlanSetDTO>> list(Pageable pageable) {
        return ResponseEntity.ok(domainService.list(pageable));
    }

    @PutMapping("/{id}")
    public ResponseEntity<StaffingPlanSetDTO> update(
            @PathVariable String id,
            @RequestBody StaffingPlanSetUpdateDTO body
    ) {
        return ResponseEntity.ok(domainService.update(id, body));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        domainService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
