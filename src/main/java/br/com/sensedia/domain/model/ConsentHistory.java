package br.com.sensedia.domain.model;

import br.com.sensedia.domain.enums.ActionStatus;
import br.com.sensedia.domain.enums.ConsentStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.UUID;

@Document(collection = "consents_history")
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class ConsentHistory {
    @Id
    private UUID id;
    private UUID consentId;
    private String cpf;
    private ConsentStatus status;
    private ActionStatus action;
    private LocalDateTime timestamp;
    private String modifiedBy;
}