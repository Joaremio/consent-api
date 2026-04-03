package br.com.sensedia.config;

import br.com.sensedia.domain.enums.ActionStatus;
import br.com.sensedia.domain.enums.ConsentStatus;
import br.com.sensedia.domain.model.Consent;
import br.com.sensedia.repository.ConsentRepository;
import br.com.sensedia.service.ConsentHistoryService;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SchedulingConfigTest {

    @Mock
    private ConsentRepository consentRepository;

    @Mock
    private ConsentHistoryService consentHistoryService;

    @InjectMocks
    private SchedulingConfig schedulingConfig;

    @Test
    @DisplayName("Deve atualizar status para EXPIRED quando encontrar consentimentos vencidos")
    void shouldExpireConsentsWhenDateTimeIsBeforeNow() {

        Consent expiredConsent = new Consent();
        expiredConsent.setId(UUID.randomUUID());
        expiredConsent.setStatus(ConsentStatus.ACTIVE);

        List<Consent> expiredList = List.of(expiredConsent);

        when(consentRepository.findByStatusAndExpirationDateTimeBefore(
                eq(ConsentStatus.ACTIVE), any(LocalDateTime.class)))
                .thenReturn(expiredList);

        schedulingConfig.checkExpiredConsents();


        assertEquals(ConsentStatus.EXPIRED, expiredConsent.getStatus());


        verify(consentRepository, times(1)).save(expiredConsent);

        verify(consentHistoryService, times(1)).saveHistory(expiredConsent, ActionStatus.UPDATE);
    }

    @Test
    @DisplayName("Não deve fazer nada quando não houver consentimentos vencidos")
    void shouldDoNothingWhenNoExpiredConsentsFound() {
        when(consentRepository.findByStatusAndExpirationDateTimeBefore(any(), any()))
                .thenReturn(List.of());

        schedulingConfig.checkExpiredConsents();

        verify(consentRepository, never()).save(any());
        verify(consentHistoryService, never()).saveHistory(any(), any());
    }
}