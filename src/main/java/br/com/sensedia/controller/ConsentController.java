package br.com.sensedia.controller;

import br.com.sensedia.domain.model.ConsentHistory;
import br.com.sensedia.dto.ConsentRequestDTO;
import br.com.sensedia.dto.ConsentResponseDTO;
import br.com.sensedia.dto.CreateConsentResultDTO;
import br.com.sensedia.service.ConsentHistoryService;
import br.com.sensedia.service.ConsentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/consents")
@Tag(name = "Consentimento", description = "Endpoints para gestão do ciclo de vida de consentimentos no Open Insurance")
public class ConsentController {

    private final ConsentService consentService;
    private final ConsentHistoryService consentHistoryService;

    public ConsentController(ConsentService consentService,  ConsentHistoryService consentHistoryService) {
        this.consentService = consentService;
        this.consentHistoryService = consentHistoryService;
    }

    @Operation(
            summary = "Criar novo consentimento",
            description = "Cria um registro de consentimento. Suporta idempotência através do header X-Idempotency-Key."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Consentimento criado com sucesso"),
            @ApiResponse(responseCode = "200", description = "Retorno de consentimento já processado (Idempotência)"),
            @ApiResponse(responseCode = "400", description = "Dados de requisição inválidos")
    })
    @PostMapping
    public ResponseEntity<ConsentResponseDTO> create(
            @Parameter(description = "Chave de idempotência para evitar duplicidade", required = true)
            @RequestHeader("X-Idempotency-Key") String idempotencyKey,
            @Valid @RequestBody ConsentRequestDTO dto) {

        CreateConsentResultDTO result = consentService.createConsent(dto, idempotencyKey);

        if (result.isNew()) {
            return ResponseEntity.status(HttpStatus.CREATED).body(result.data());
        }

        return ResponseEntity.ok(result.data());
    }

    @Operation(
            summary = "Buscar consentimento por ID",
            description = "Retorna informações de um consentimento específico."
    )
    @GetMapping("/{consentId}")
    public ResponseEntity<ConsentResponseDTO> getConsentById(@PathVariable UUID consentId) {
        return ResponseEntity.ok(consentService.getConsentById(consentId));
    }

    @Operation(
            summary = "Listar consentimentos",
            description = "Retorna uma lista paginada de todos os consentimentos."
    )
    @GetMapping
    public ResponseEntity<Page<ConsentResponseDTO>> getAllConsents(Pageable pageable) {
        return ResponseEntity.ok(consentService.getAllConsents(pageable));
    }

    @Operation(
            summary = "Atualizar consentimento",
            description = "Permite atualizar informações de um consentimento existente."
    )
    @PutMapping("/{id}")
    public ResponseEntity<ConsentResponseDTO> update(@PathVariable UUID id, @Valid @RequestBody ConsentRequestDTO dto) {
        return ResponseEntity.ok(consentService.updateConsent(id, dto));
    }

    @Operation(
            summary = "Atualização parcial",
            description = "Atualiza apenas os campos enviados no corpo da requisição."
    )
    @PatchMapping("/{id}")
    public ResponseEntity<ConsentResponseDTO> patch(@PathVariable UUID id, @RequestBody ConsentRequestDTO dto) {
        return ResponseEntity.ok(consentService.patchConsent(id, dto));
    }

    @Operation(
            summary = "Revogar consentimento",
            description = "Realiza a revogação lógica (Soft Delete) do consentimento."
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> revoke(@PathVariable UUID id) {
        consentService.revokeConsent(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Consultar histórico de um consentimento",
            description = "Retorna a trilha de auditoria de um consentimento específico, listando todas as alterações de estado"
    )
    @GetMapping("/{consentId}/history")
    public ResponseEntity<List<ConsentHistory>> getHistory(
            @Parameter(description = "UUID do consentimento original", required = true)
            @PathVariable UUID consentId) {
        return ResponseEntity.ok(consentHistoryService.getHistoryByConsentId(consentId));
    }
}