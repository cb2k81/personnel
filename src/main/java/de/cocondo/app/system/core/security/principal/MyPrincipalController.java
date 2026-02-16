package de.cocondo.app.system.core.security.principal;

import de.cocondo.app.system.core.context.RequestContextDataContainer;
import de.cocondo.app.system.core.security.account.AccountDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/me/principal")
@RequiredArgsConstructor
public class MyPrincipalController {

    private final RequestContextDataContainer dataContainer;
    private final PrincipalCrudService principalCrudService;


    @GetMapping
    public ResponseEntity<PrincipalDTO> getMyPrincipalInfo() {
        Principal currentPrincipal = dataContainer.getCurrentPrincipal();

        PrincipalDTO principalDTO = new PrincipalDTO();
        principalDTO.setId(currentPrincipal.getId());
        principalDTO.setName(currentPrincipal.getName());
        principalDTO.setEmail(currentPrincipal.getEmail());
        principalDTO.setDescription(currentPrincipal.getDescription());

        AccountDTO accountDTO = new AccountDTO(currentPrincipal.getPrimaryAccount());
        //principalDTO.setPrimaryAccount(accountDTO);

        return ResponseEntity.ok(principalDTO);
    }


    @PostMapping("/basedata")
    public ResponseEntity<Void> updateMyPrincipalBasedata(@RequestBody PrincipalUpdateBasedataDTO updateDTO) {
        Principal currentPrincipal = dataContainer.getCurrentPrincipal();

        // Update the attributes of the current principal based on the DTO
        currentPrincipal.setName(updateDTO.getName());
        currentPrincipal.setEmail(updateDTO.getEmail());
        currentPrincipal.setDescription(updateDTO.getDescription());

        // Update the principal in the database
        principalCrudService.update(currentPrincipal);

        return ResponseEntity.noContent().build();
    }
}
