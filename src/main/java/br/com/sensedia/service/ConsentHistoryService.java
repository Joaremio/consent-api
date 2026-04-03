package br.com.sensedia.service;

import br.com.sensedia.domain.enums.ActionStatus;
import br.com.sensedia.domain.model.Consent;
import br.com.sensedia.domain.model.ConsentHistory;
import br.com.sensedia.repository.ConsentHistoryRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ConsentHistoryService {
    private final ConsentHistoryRepository consentHistoryRepository;

    public ConsentHistoryService(ConsentHistoryRepository consentHistoryRepository) {
        this.consentHistoryRepository = consentHistoryRepository;
    }

    public void saveHistory(Consent consent, ActionStatus action) {
        ConsentHistory history = new ConsentHistory();

        history.setConsentId(consent.getId());
        history.setCpf(consent.getCpf());
        history.setStatus(consent.getStatus());
        history.setAction(action);
        history.setTimestamp(LocalDateTime.now());

        consentHistoryRepository.save(history);
    }

    public List<ConsentHistory> getHistoryByConsentId(String consentId) {
        return consentHistoryRepository.findByConsentIdOrderByTimestampDesc(consentId);
    }
}