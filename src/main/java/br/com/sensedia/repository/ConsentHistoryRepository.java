package br.com.sensedia.repository;

import br.com.sensedia.domain.model.ConsentHistory;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.UUID;

public interface ConsentHistoryRepository extends MongoRepository<ConsentHistory, UUID> {
    List<ConsentHistory> findByConsentIdOrderByTimestampDesc(UUID consentId);
}
