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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConsentServiceTest {

    @Mock
    private ConsentRepository consentRepository;

    @Mock
    private ConsentHistoryService historyService;

    @Mock
    private ConsentMapper mapper;

    @InjectMocks
    private ConsentService service;

    @Test
    @DisplayName("Deve criar novo consentimento quando a chave de idempotência for inédita")
    void shouldCreateNewConsent() {
        String key = "123ABC";
        UUID idTest = UUID.randomUUID();
        ConsentRequestDTO dto = new ConsentRequestDTO("123.801.025-01", LocalDateTime.now(), "Alguns dados");
        Consent model = new Consent();
        Consent savedModel = new Consent();
        savedModel.setId(idTest);
        savedModel.setCreationDateTime(LocalDateTime.now());

        ConsentResponseDTO expectedResponseData = new ConsentResponseDTO(savedModel.getId(), "123.801.025-01", ConsentStatus.ACTIVE, savedModel.getCreationDateTime(), null, "Alguns dados");
        when(consentRepository.findByIdempotencyKey(key)).thenReturn(Optional.empty());
        when(mapper.toModel(dto)).thenReturn(model);
        when(consentRepository.save(any(Consent.class))).thenReturn(savedModel);
        when(mapper.toDto(savedModel)).thenReturn(expectedResponseData);


        CreateConsentResultDTO result = service.createConsent(dto,key);

        assertNotNull(result);
        assertEquals(expectedResponseData.id(), result.data().id());
        assertTrue(result.isNew());
        verify(consentRepository, times(1)).save(any(Consent.class));
    }

    @Test
    void shouldReturnExistingConsentWhenIdempotencyKeyExists() {
        String key = "123ABC";
        UUID idTest = UUID.randomUUID();
        ConsentRequestDTO dto = new ConsentRequestDTO(
                "123.456.389-02",
                LocalDateTime.now(),
                "Alguns dados"
        );

        Consent existing = new Consent();
        existing.setAdditionalInfo("Alguns dados");

        ConsentResponseDTO response = new ConsentResponseDTO(
                idTest,
                "123.456.389-02",
                ConsentStatus.ACTIVE,
                LocalDateTime.now(),
                null,
                "Alguns dados"
        );

        when(consentRepository.findByIdempotencyKey(key)).thenReturn(Optional.of(existing));
        when(mapper.toDto(existing)).thenReturn(response);

        CreateConsentResultDTO result = service.createConsent(dto, key);

        assertNotNull(result);
        assertFalse(result.isNew());

        assertEquals(response.id(), result.data().id());
        assertEquals(response.additionalInfo(), result.data().additionalInfo());

        verify(consentRepository, never()).save(any());
    }

    @Test
    void shouldReturnConsentById() {
        UUID idTest = UUID.randomUUID();

        Consent consent = new Consent();
        ConsentResponseDTO dto = mock(ConsentResponseDTO.class);

        when(consentRepository.findById(idTest)).thenReturn(Optional.of(consent));
        when(mapper.toDto(consent)).thenReturn(dto);

        ConsentResponseDTO result = service.getConsentById(idTest);

        assertNotNull(result);
    }

    @Test
    void shouldThrowExceptionWhenConsentNotFound() {
        UUID idTest = UUID.randomUUID();

        when(consentRepository.findById(idTest)).thenReturn(Optional.empty());

        assertThrows(ConsentNotFoundException.class, () -> {
            service.getConsentById(idTest);
        });
    }

    @Test
    @DisplayName("Deve retornar erro quando tiver diferentes payloads")
    void shouldThrowErrorWhenSameKeyWithDifferentPayload() {
        String key = "123ABC";

        ConsentRequestDTO dtoA = new ConsentRequestDTO(
                "123.456.789-00",
                LocalDateTime.now(),
                "details for A"
        );

        ConsentRequestDTO dtoB = new ConsentRequestDTO(
                "048.039.284-02",
                LocalDateTime.now(),
                "details for B"
        );

        Consent existing = new Consent();
        existing.setIdempotencyKey(key);
        existing.setAdditionalInfo("details for A");

        when(consentRepository.findByIdempotencyKey(key)).thenReturn(Optional.of(existing));

        assertThrows(IdempotencyConflictException.class, () -> {
            service.createConsent(dtoB, key);
        });
    }

    @Test
    @DisplayName("Deve retornar erro customizado quando o ID não existir")
    void shouldThrowExceptionWhenNotFound() {
        UUID idTest = UUID.randomUUID();
        when(consentRepository.findById(idTest)).thenReturn(Optional.empty());

        assertThrows(ConsentNotFoundException.class, () -> service.getConsentById(idTest));
    }

    @Test
    @DisplayName("Deve revogar um consentimento e salvar no histórico")
    void shouldRevokeConsentSuccessfully() {

        UUID idTest = UUID.randomUUID();
        Consent consent = new Consent();
        consent.setId(idTest);
        consent.setStatus(ConsentStatus.ACTIVE);

        when(consentRepository.findById(idTest)).thenReturn(Optional.of(consent));

        service.revokeConsent(idTest);

        assertEquals(ConsentStatus.REVOKED, consent.getStatus());
        verify(consentRepository, times(1)).save(consent);
        verify(historyService, times(1)).saveHistory(consent, ActionStatus.REVOKE);
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar revogar consentimento inexistente")
    void shouldThrowExceptionWhenRevokingInexistent() {
        UUID idTest = UUID.randomUUID();
        when(consentRepository.findById(idTest)).thenReturn(Optional.empty());

        assertThrows(ConsentNotFoundException.class, () -> service.revokeConsent(idTest));
        verify(consentRepository, never()).save(any());
    }
}