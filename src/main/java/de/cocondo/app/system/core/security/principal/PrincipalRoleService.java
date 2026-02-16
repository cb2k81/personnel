package de.cocondo.app.system.core.security.principal;

import de.cocondo.app.system.core.security.role.Role;
import de.cocondo.app.system.core.security.role.RoleRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PrincipalRoleService {

    private static final Logger logger = LoggerFactory.getLogger(PrincipalRoleService.class);

    private final PrincipalRepository principalRepository;
    private final RoleRepository roleRepository;

    @Transactional
    public void connectToRole(Principal principal, Role role) {
        principal.getRoles().add(role);
        principalRepository.save(principal);
        logger.info("Principal {} connected to role {}", principal.getName(), role.getName());
    }

    @Transactional
    public void connectToRole(Principal principal, String roleName) {
        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new IllegalArgumentException("Role not found with name: " + roleName));

        connectToRole(principal, role);
    }

    @Transactional
    public void connectToRole(String principalName, Role role) {
        Principal principal = principalRepository.findByName(principalName)
                .orElseThrow(() -> new IllegalArgumentException("Principal not found with name: " + principalName));

        connectToRole(principal, role);
    }

    @Transactional
    public void connectToRole(String principalName, String roleName) {
        Principal principal = principalRepository.findByName(principalName)
                .orElseThrow(() -> new IllegalArgumentException("Principal not found with name: " + principalName));

        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new IllegalArgumentException("Role not found with name: " + roleName));

        connectToRole(principal, role);
    }

    @Transactional
    public void disconnectFromRole(Principal principal, Role role) {
        principal.getRoles().remove(role);
        principalRepository.save(principal);
        logger.info("Principal {} disconnected from role {}", principal.getName(), role.getName());
    }

    @Transactional
    public void disconnectFromRole(Principal principal, String roleName) {
        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new IllegalArgumentException("Role not found with name: " + roleName));

        disconnectFromRole(principal, role);
    }

    @Transactional
    public void disconnectFromRole(String principalName, Role role) {
        Principal principal = principalRepository.findByName(principalName)
                .orElseThrow(() -> new IllegalArgumentException("Principal not found with name: " + principalName));

        disconnectFromRole(principal, role);
    }

    @Transactional
    public void disconnectFromRole(String principalName, String roleName) {
        Principal principal = principalRepository.findByName(principalName)
                .orElseThrow(() -> new IllegalArgumentException("Principal not found with name: " + principalName));

        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new IllegalArgumentException("Role not found with name: " + roleName));

        disconnectFromRole(principal, role);
    }
}
