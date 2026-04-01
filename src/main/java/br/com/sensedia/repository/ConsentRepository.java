package br.com.sensedia.repository;


import br.com.sensedia.domain.model.Consent;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ConsentRepository extends MongoRepository<Consent, UUID> {
}
