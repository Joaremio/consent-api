package br.com.sensedia.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

public record ConsentRequestDTO(
        @NotBlank(message = "O CPF é obrigatório")
        @Pattern(regexp = "\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}", message = "Formato de CPF inválido. Use ###.###.###-##")
        String cpf,

        LocalDateTime expirationDateTime,

        @Size(min = 1, max = 50, message = "Informações adicionais devem ter entre 1 e 50 caracteres")
        String additionalInfo
) {
}