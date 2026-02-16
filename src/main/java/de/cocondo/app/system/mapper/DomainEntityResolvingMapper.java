package de.cocondo.app.system.mapper;

import de.cocondo.app.system.dto.DomainEntityInboundDTO;

public interface DomainEntityResolvingMapper<I extends DomainEntityInboundDTO, O extends DomainEntityInboundDTO> extends Mapper {

        void mapAndResolve(I inboundDTO, O outboundDTO);

        default void mapMetadata(I inboundDTO, O outboundDTO) {
            outboundDTO.setTags(inboundDTO.getTags());
            outboundDTO.setKeyValuePairs(inboundDTO.getKeyValuePairs());
        }
}
