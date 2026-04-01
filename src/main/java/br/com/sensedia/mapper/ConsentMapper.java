package br.com.sensedia.mapper;

import br.com.sensedia.domain.model.Consent;
import br.com.sensedia.dto.ConsentRequestDTO;
import br.com.sensedia.dto.ConsentResponseDTO;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface ConsentMapper {
    Consent toModel(ConsentRequestDTO dto);
    ConsentResponseDTO toDto(Consent model);
}
