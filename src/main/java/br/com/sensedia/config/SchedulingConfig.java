package br.com.sensedia.config;


import br.com.sensedia.domain.enums.ActionStatus;
import br.com.sensedia.domain.enums.ConsentStatus;
import br.com.sensedia.domain.model.Consent;
import br.com.sensedia.repository.ConsentRepository;
import br.com.sensedia.service.ConsentHistoryService;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalDateTime;
import java.util.List;

@Configuration
@EnableScheduling
public class SchedulingConfig {

    private final ConsentRepository consentRepository;
    private final ConsentHistoryService consentHistoryService;

    SchedulingConfig (ConsentRepository consentRepository,  ConsentHistoryService consentHistoryService) {
        this.consentRepository = consentRepository;
        this.consentHistoryService = consentHistoryService;
    }

    @Scheduled(fixedRate = 60000)
    public void checkExpiredConsents() {
        LocalDateTime now = LocalDateTime.now();

        List<Consent> expiredList = consentRepository.findByStatusAndExpirationDateTimeBefore(
                ConsentStatus.ACTIVE, now);

        if (!expiredList.isEmpty()) {
            expiredList.forEach(consent -> {
                consent.setStatus(ConsentStatus.EXPIRED);
                consentRepository.save(consent);

                consentHistoryService.saveHistory(consent, ActionStatus.UPDATE);
            });
            System.out.println("Foram expirados " + expiredList.size() + " consentimentos.");
        }
    }
}
