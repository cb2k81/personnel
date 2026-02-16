package de.cocondo.app.system.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;

@EqualsAndHashCode(callSuper = true)
@Data
public class DomainEntityUpdateDTO extends DomainEntityInboundDTO {
    @Serial
    private static final long serialVersionUID = 1L;
    private String id;

}
