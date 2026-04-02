package br.com.sensedia.controller;

import br.com.sensedia.dto.ConsentRequestDTO;
import br.com.sensedia.dto.ConsentResponseDTO;
import br.com.sensedia.dto.CreateConsentResultDTO;
import br.com.sensedia.service.ConsentService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/consents")
public class ConsentController {

    private final ConsentService consentService;

    public ConsentController(ConsentService consentService) {
        this.consentService = consentService;
    }

    @PostMapping
    public ResponseEntity<ConsentResponseDTO> create(
            @RequestHeader("X-Idempotency-Key") String idempotencyKey,
            @Valid @RequestBody ConsentRequestDTO dto) {

        CreateConsentResultDTO result = consentService.createConsent(dto, idempotencyKey);

        if (result.isNew()) {
            return ResponseEntity.status(HttpStatus.CREATED).body(result.data());
        }

        return ResponseEntity.ok(result.data());
    }

    @GetMapping("/{consentId}")
    public ResponseEntity<ConsentResponseDTO> getConsentById(@PathVariable  UUID consentId) {
        return ResponseEntity.status(HttpStatus.OK).body(consentService.getConsentById(consentId));
    }

    @GetMapping
    public ResponseEntity<Page<ConsentResponseDTO>> getAllConsents(Pageable pageable) {
        return ResponseEntity.status(HttpStatus.OK).body(consentService.getAllConsents(pageable));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ConsentResponseDTO> update(
            @PathVariable UUID id,
            @Valid @RequestBody ConsentRequestDTO dto) {
        return ResponseEntity.ok(consentService.updateConsent(id, dto));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void revoke(@PathVariable UUID id) {
        consentService.revokeConsent(id);
    }


}
