package br.com.sensedia.service;

import br.com.sensedia.domain.enums.ConsentStatus;
import br.com.sensedia.domain.model.Consent;
import br.com.sensedia.dto.ConsentRequestDTO;
import br.com.sensedia.dto.ConsentResponseDTO;
import br.com.sensedia.mapper.ConsentMapper;
import br.com.sensedia.repository.ConsentRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class ConsentService {

    private final ConsentRepository repository;
    private final ConsentMapper mapper;

    public ConsentService( ConsentRepository repository, ConsentMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public ConsentResponseDTO createConsent(ConsentRequestDTO dto, String idempotencyKey) {
        return repository.findByIdempotencyKey(idempotencyKey)
                .map(existingConsent -> {
                    return mapper.toDto(existingConsent);
                })
                .orElseGet(() -> {
                    Consent newConsent = mapper.toModel(dto);

                    newConsent.setId(UUID.randomUUID());
                    newConsent.setStatus(ConsentStatus.ACTIVE);
                    newConsent.setCreationDateTime(LocalDateTime.now());
                    newConsent.setIdempotencyKey(idempotencyKey);

                    Consent saved = repository.save(newConsent);
                    return mapper.toDto(saved);
                });
    }

    public ConsentResponseDTO getConsentById(UUID id) {
        return mapper.toDto(repository.findById(id).orElseThrow(()-> new RuntimeException("Consent not found")));
    }
}