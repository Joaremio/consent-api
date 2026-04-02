package br.com.sensedia.dto;

public record CreateConsentResultDTO(
    ConsentResponseDTO data,
    boolean isNew
) {}
