package br.com.sensedia.service;


import br.com.sensedia.domain.enums.ActionStatus;
import br.com.sensedia.domain.enums.ConsentStatus;
import br.com.sensedia.domain.model.Consent;
import br.com.sensedia.domain.model.ConsentHistory;
import br.com.sensedia.repository.ConsentHistoryRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConsentHistoryServiceTest {

    @Mock
    private ConsentHistoryRepository repository;

    @InjectMocks
    private ConsentHistoryService service;

    @Test
    @DisplayName("Deve salvar o histórico com todos os campos preenchidos corretamente")
    void shouldSaveHistoryWithAllFields() {

        UUID id = UUID.randomUUID();

        Consent consent = new Consent();
        consent.setId(id);
        consent.setCpf("123.456.789-00");
        consent.setStatus(ConsentStatus.ACTIVE);

        ActionStatus action = ActionStatus.CREATE;

        service.saveHistory(consent, action);

        verify(repository, times(1)).save(argThat(history ->
                history.getConsentId().equals(id) &&
                        history.getCpf().equals("123.456.789-00") &&
                        history.getAction().equals(ActionStatus.CREATE) &&
                        history.getTimestamp() != null
        ));
    }

    @Test
    @DisplayName("Deve retornar a lista de histórico ordenada por data")
    void shouldReturnHistoryList() {
        UUID id = UUID.randomUUID();
        UUID consentId = UUID.randomUUID();

        List<ConsentHistory> mockList = List.of(
                new ConsentHistory(id, consentId, "123.456.789-00", ConsentStatus.ACTIVE, ActionStatus.UPDATE, LocalDateTime.now()),
                new ConsentHistory(id, consentId, "123.456.789-00", ConsentStatus.ACTIVE, ActionStatus.CREATE, LocalDateTime.now().minusHours(1))
        );

        when(repository.findByConsentIdOrderByTimestampDesc(consentId)).thenReturn(mockList);


        List<ConsentHistory> result = service.getHistoryByConsentId(consentId);


        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(ActionStatus.UPDATE, result.get(0).getAction());
        verify(repository, times(1)).findByConsentIdOrderByTimestampDesc(consentId);
    }
}