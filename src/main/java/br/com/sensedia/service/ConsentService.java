package br.com.sensedia.service;

import br.com.sensedia.domain.enums.ActionStatus;
import br.com.sensedia.domain.enums.ConsentStatus;
import br.com.sensedia.domain.model.Consent;
import br.com.sensedia.dto.ConsentRequestDTO;
import br.com.sensedia.dto.ConsentResponseDTO;
import br.com.sensedia.dto.CreateConsentResultDTO;
import br.com.sensedia.exception.ConsentNotFoundException;
import br.com.sensedia.exception.IdempotencyConflictException;
import br.com.sensedia.mapper.ConsentMapper;
import br.com.sensedia.repository.ConsentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
public class ConsentService {

    private final ConsentRepository consentRepository;
    private final ConsentHistoryService historyService;
    private final ConsentMapper mapper;
    private final Logger log = LoggerFactory.getLogger(ConsentService.class);

    public ConsentService(ConsentRepository repository, ConsentHistoryService historyService, ConsentMapper mapper) {
        this.consentRepository = repository;
        this.historyService = historyService;
        this.mapper = mapper;
    }

    public CreateConsentResultDTO createConsent(ConsentRequestDTO dto, String idempotencyKey) {

        log.info("Recebida requisição para criar consentimento com idempotencyKey={}", idempotencyKey);

        Optional<Consent> existingConsentOpt = consentRepository.findByIdempotencyKey(idempotencyKey);

        if (existingConsentOpt.isPresent()) {
            Consent existingConsent = existingConsentOpt.get();

            boolean isSamePayload = Objects.equals(existingConsent.getAdditionalInfo(), dto.additionalInfo());

            if (!isSamePayload) {
                log.warn("Payload diferente para mesma idempotencyKey={}", idempotencyKey);
                throw new IdempotencyConflictException("Payload diferente para mesma chave");
            }

            log.info("Idempotência detectada para key={}", idempotencyKey);

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

        Consent savedConsent = consentRepository.save(newConsent);

        log.info("Novo consentimento criado com id={}", savedConsent.getId());

        historyService.saveHistory(savedConsent, ActionStatus.CREATE);

        CreateConsentResultDTO response = new CreateConsentResultDTO(
                mapper.toDto(savedConsent),
                true
        );

        return response;
    }

    public ConsentResponseDTO getConsentById(UUID consentId) {
        log.info("Buscando consentimento por id={}", consentId);

        return mapper.toDto(
                consentRepository.findById(consentId)
                        .orElseThrow(() -> {
                            log.warn("Consentimento não encontrado para id={}", consentId);
                            return new ConsentNotFoundException("Consentimento não encontrado");
                        })
        );
    }

    public Page<ConsentResponseDTO> getAllConsents(Pageable pageable) {
        log.info("Listando consentimentos - page={}, size={}", pageable.getPageNumber(), pageable.getPageSize());

        return consentRepository.findAll(pageable)
                .map(mapper::toDto);
    }

    public ConsentResponseDTO updateConsent(UUID consentId, ConsentRequestDTO data) {
        log.info("Atualizando consentimento id={}", consentId);

        Consent consent = consentRepository.findById(consentId)
                .orElseThrow(() -> {
                    log.warn("Consentimento não encontrado para atualização id={}", consentId);
                    return new ConsentNotFoundException("Consentimento não encontrado");
                });

        consent.setExpirationDateTime(data.expirationDateTime());
        consent.setAdditionalInfo(data.additionalInfo());

        Consent updateConsent = consentRepository.save(consent);

        log.info("Consentimento atualizado id={}", consentId);

        historyService.saveHistory(updateConsent, ActionStatus.UPDATE);

        return mapper.toDto(updateConsent);
    }

    public ConsentResponseDTO patchConsent(UUID consentId, ConsentRequestDTO dto) {
        log.info("Atualização parcial (PATCH) para consentimento id={}", consentId);

        Consent existingConsent = consentRepository.findById(consentId)
                .orElseThrow(() -> {
                    log.warn("Consentimento não encontrado para patch id={}", consentId);
                    return new ConsentNotFoundException("Consentimento não encontrado");
                });

        mapper.updateResultFromDto(dto, existingConsent);
        Consent saved = consentRepository.save(existingConsent);

        log.info("Consentimento atualizado parcialmente id={}", consentId);

        historyService.saveHistory(saved, ActionStatus.PATCH_UPDATE);

        return mapper.toDto(saved);
    }

    public void revokeConsent(UUID consentId) {
        log.info("Revogando consentimento id={}", consentId);

        Consent consent = consentRepository.findById(consentId)
                .orElseThrow(() -> {
                    log.warn("Consentimento não encontrado para revogação id={}", consentId);
                    return new ConsentNotFoundException("Consentimento não encontrado");
                });

        consent.setStatus(ConsentStatus.REVOKED);
        consentRepository.save(consent);

        log.info("Consentimento revogado id={}", consentId);

        historyService.saveHistory(consent, ActionStatus.REVOKE);
    }
}