package br.com.sensedia.dto;

import java.time.LocalDateTime;

public record ErrorResponseDTO(
        String message,
        int status,
        LocalDateTime timestamp
) {}