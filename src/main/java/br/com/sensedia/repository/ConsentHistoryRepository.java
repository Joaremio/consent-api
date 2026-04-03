package br.com.sensedia.repository;

import br.com.sensedia.domain.model.ConsentHistory;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ConsentHistoryRepository extends MongoRepository<ConsentHistory, String> {
    List<ConsentHistory> findByConsentIdOrderByTimestampDesc(String consentId);
}
