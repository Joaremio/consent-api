package br.com.sensedia.controller;

import br.com.sensedia.dto.ConsentRequestDTO;
import br.com.sensedia.dto.ConsentResponseDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class ConsentControllerIT {

    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:latest");

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    @DisplayName("Deve criar um consentimento e persistir no MongoDB real do container")
    void shouldCreateAndPersistConsent() {

        ConsentRequestDTO request = new ConsentRequestDTO("123.456.789-01", LocalDateTime.now(), "Integração");
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Idempotency-Key", "it-key-123");
        HttpEntity<ConsentRequestDTO> entity = new HttpEntity<>(request, headers);


        ResponseEntity<ConsentResponseDTO> response = restTemplate.exchange(
                "/consents", HttpMethod.POST, entity, ConsentResponseDTO.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().cpf()).isEqualTo("123.456.789-01");
    }

    @Test
    @DisplayName("Deve retornar 200 ao reenviar mesma idempotency key")
    void shouldReturn200WhenSameIdempotencyKeyIsSentAgain() {

        String key = "it-key-456";

        ConsentRequestDTO request = new ConsentRequestDTO(
                "123.456.789-01",
                LocalDateTime.now(),
                "Teste Idempotente"
        );

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Idempotency-Key", key);

        HttpEntity<ConsentRequestDTO> entity = new HttpEntity<>(request, headers);


        ResponseEntity<ConsentResponseDTO> firstResponse = restTemplate.exchange(
                "/consents", HttpMethod.POST, entity, ConsentResponseDTO.class);


        ResponseEntity<ConsentResponseDTO> secondResponse = restTemplate.exchange(
                "/consents", HttpMethod.POST, entity, ConsentResponseDTO.class);


        assertThat(firstResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(secondResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        assertThat(secondResponse.getBody()).isNotNull();
        assertThat(firstResponse.getBody().id())
                .isEqualTo(secondResponse.getBody().id());
    }
}