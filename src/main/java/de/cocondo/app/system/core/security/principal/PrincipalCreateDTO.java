package de.cocondo.app.system.core.security.principal;

import lombok.Data;

@Data
public class PrincipalCreateDTO {

    private String name;
    private String email;
    private String description;
    private String loginname;
    private String plainPassword;

}
