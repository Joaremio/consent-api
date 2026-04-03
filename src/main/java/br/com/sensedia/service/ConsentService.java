package br.com.sensedia.service;

import br.com.sensedia.domain.enums.ActionStatus;
import br.com.sensedia.domain.enums.ConsentStatus;
import br.com.sensedia.domain.model.Consent;
import br.com.sensedia.dto.ConsentRequestDTO;
import br.com.sensedia.dto.ConsentResponseDTO;
import br.com.sensedia.dto.CreateConsentResultDTO;
import br.com.sensedia.exception.ConsentNotFoundException;
import br.com.sensedia.mapper.ConsentMapper;
import br.com.sensedia.repository.ConsentRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class ConsentService {

    private final ConsentRepository repository;
    private final ConsentHistoryService historyService;
    private final ConsentMapper mapper;


    public ConsentService( ConsentRepository repository, ConsentHistoryService historyService ,ConsentMapper mapper) {
        this.repository = repository;
        this.historyService = historyService;
        this.mapper = mapper;

    }

    public CreateConsentResultDTO createConsent(ConsentRequestDTO dto, String idempotencyKey) {

        Optional<Consent> existingConsentOpt = repository.findByIdempotencyKey(idempotencyKey);

        if (existingConsentOpt.isPresent()) {
            Consent existingConsent = existingConsentOpt.get();

            boolean isSamePayload = existingConsent.getAdditionalInfo()
                    .equals(dto.additionalInfo());

            if (!isSamePayload) {
                throw new IllegalArgumentException("Payload diferente para mesma chave");
            }

            CreateConsentResultDTO response = new CreateConsentResultDTO(
                    mapper.toDto(existingConsent),
                    false
            );

            return response;
        }

        Consent newConsent = mapper.toModel(dto);

        newConsent.setId(UUID.randomUUID());
        newConsent.setStatus(ConsentStatus.ACTIVE);
        newConsent.setCreationDateTime(LocalDateTime.now());
        newConsent.setIdempotencyKey(idempotencyKey);

        Consent savedConsent = repository.save(newConsent);

        historyService.saveHistory(savedConsent, ActionStatus.CREATE);

        CreateConsentResultDTO response = new CreateConsentResultDTO(
                mapper.toDto(savedConsent),
                true
        );

        return response;
    }

    public ConsentResponseDTO getConsentById(UUID id) {
        return mapper.toDto(repository.findById(id).orElseThrow(()-> new ConsentNotFoundException("Consentimento não encontrado")));
    }

    public Page<ConsentResponseDTO> getAllConsents(Pageable pageable) {
        return repository.findAll(pageable)
                .map(mapper::toDto);
    }

    public ConsentResponseDTO updateConsent(UUID consentId, ConsentRequestDTO data){
        Consent consent = repository.findById(consentId).orElseThrow(()-> new ConsentNotFoundException("Consentimento não encontrado"));

        consent.setExpirationDateTime(data.expirationDateTime());
        consent.setAdditionalInfo(data.additionalInfo());


        Consent updateConsent = repository.save(consent);

        historyService.saveHistory(updateConsent, ActionStatus.UPDATE);

        return mapper.toDto(updateConsent);
    }

    public ConsentResponseDTO patchConsent(UUID id, ConsentRequestDTO dto) {
        Consent existingConsent = repository.findById(id)
                .orElseThrow(() -> new ConsentNotFoundException("Consentimento não encontrado"));

        mapper.updateResultFromDto(dto, existingConsent);
        Consent saved = repository.save(existingConsent);

        historyService.saveHistory(saved, ActionStatus.PATCH_UPDATE);

        return mapper.toDto(saved);
    }

    public void revokeConsent(UUID id) {
        Consent consent = repository.findById(id)
                .orElseThrow(() -> new ConsentNotFoundException("Consentimento não encontrado"));

        consent.setStatus(ConsentStatus.REVOKED);
        repository.save(consent);

        historyService.saveHistory(consent, ActionStatus.REVOKE);
    }
}