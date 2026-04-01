package br.com.sensedia.domain.model;
import br.com.sensedia.domain.enums.ConsentStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.UUID;

@Document(collection = "consents")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Consent {

    @Id
    private UUID id;

    @NotNull
    @Pattern(regexp = "\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}", message = "Formato de CPF inválido. Use ###.###.###-##")
    private String cpf;

    @NotNull
    private ConsentStatus status;

    private LocalDateTime creationDateTime;

    private LocalDateTime expirationDateTime;

    @Size(min = 1, max = 50)
    private String additionalInfo;

    private String idempotencyKey;

}