package br.com.sensedia.dto;

import br.com.sensedia.domain.enums.ConsentStatus;
import java.time.LocalDateTime;
import java.util.UUID;

public record ConsentResponseDTO(
        UUID id,
        String cpf,
        ConsentStatus status,
        LocalDateTime creationDateTime,
        LocalDateTime expirationDateTime,
        String additionalInfo
) {
}