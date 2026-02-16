package de.cocondo.app.system.core.security.principal;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/system/principals")
@RequiredArgsConstructor
public class PrincipalAdminController {

    private final PrincipalAdminApiService principalAdminService;


    @GetMapping
    public ResponseEntity<List<PrincipalDTO>> getAllPrincipals() {
        List<PrincipalDTO> principals = principalAdminService.getAllPrincipals();
        return ResponseEntity.ok(principals);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PrincipalDTO> getPrincipalById(@PathVariable String id) {
        PrincipalDTO principal = principalAdminService.getPrincipalById(id);
        return ResponseEntity.ok(principal);
    }

    @GetMapping("/find")
    public ResponseEntity<PrincipalDTO> findPrincipalByName(@RequestParam String name) {
        PrincipalDTO principal = principalAdminService.findPrincipalByName(name);
        return ResponseEntity.ok(principal);
    }

    @PostMapping
    public ResponseEntity<PrincipalDTO> createPrincipal(@RequestBody PrincipalCreateDTO principalCreateDTO) {
        PrincipalDTO createdPrincipal = principalAdminService.createPrincipalWithPrimaryAccount(principalCreateDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdPrincipal);
    }


    @PutMapping("/{id}")
    public ResponseEntity<PrincipalDTO> updatePrincipalBasedata(@PathVariable String id, @RequestBody PrincipalUpdateBasedataDTO principalUpdateBasedataDTO) {
        PrincipalDTO updatedPrincipal = principalAdminService.updatePrincipalBasedata(id, principalUpdateBasedataDTO);
        return ResponseEntity.ok(updatedPrincipal);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePrincipal(@PathVariable String id) {
        principalAdminService.deletePrincipal(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/connect-role/{roleName}")
    public ResponseEntity<Void> connectPrincipalToRole(@PathVariable String id, @PathVariable String roleName) {
        principalAdminService.connectPrincipalToRole(id, roleName);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/disconnect-role/{roleName}")
    public ResponseEntity<Void> disconnectPrincipalFromRole(@PathVariable String id, @PathVariable String roleName) {
        principalAdminService.disconnectPrincipalFromRole(id, roleName);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/update-password")
    public ResponseEntity<Void> updatePrimaryAccountPassword(@PathVariable String id, @RequestParam String newRawPassword) {
        principalAdminService.updatePrimaryAccountPassword(id, newRawPassword);
        return ResponseEntity.noContent().build();
    }

}
