package br.com.sensedia.service;

import br.com.sensedia.domain.enums.ActionStatus;
import br.com.sensedia.domain.model.Consent;
import br.com.sensedia.domain.model.ConsentHistory;
import br.com.sensedia.repository.ConsentHistoryRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class ConsentHistoryService {
    private final ConsentHistoryRepository repository;

    public ConsentHistoryService(ConsentHistoryRepository repository) {
        this.repository = repository;
    }

    public void saveHistory(Consent consent, ActionStatus action) {
        ConsentHistory history = new ConsentHistory();
        history.setConsentId(consent.getId());
        history.setCpf(consent.getCpf());
        history.setStatus(consent.getStatus());
        history.setAction(action);
        history.setTimestamp(LocalDateTime.now());
        repository.save(history);
    }

    public List<ConsentHistory> getHistoryByConsentId(UUID consentId) {
        return repository.findByConsentIdOrderByTimestampDesc(consentId);
    }
}