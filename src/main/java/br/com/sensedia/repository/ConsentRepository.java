package br.com.sensedia.repository;


import br.com.sensedia.domain.enums.ConsentStatus;
import br.com.sensedia.domain.model.Consent;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ConsentRepository extends MongoRepository<Consent, UUID> {
    Optional<Consent> findByIdempotencyKey(String idempotencyKey);
    List<Consent> findByStatusAndExpirationDateTimeBefore(ConsentStatus status, LocalDateTime dateTime);
}
