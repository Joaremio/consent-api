package br.com.sensedia.mapper;

import br.com.sensedia.domain.model.Consent;
import br.com.sensedia.dto.ConsentRequestDTO;
import br.com.sensedia.dto.ConsentResponseDTO;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;


@Mapper(componentModel = "spring")
public interface ConsentMapper {
    Consent toModel(ConsentRequestDTO dto);
    ConsentResponseDTO toDto(Consent model);
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateResultFromDto(ConsentRequestDTO dto, @MappingTarget Consent consent);
}

