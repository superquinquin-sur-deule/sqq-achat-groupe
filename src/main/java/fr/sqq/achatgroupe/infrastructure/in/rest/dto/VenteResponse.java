package fr.sqq.achatgroupe.infrastructure.in.rest.dto;

import java.time.Instant;

public record VenteResponse(
        Long id,
        String name,
        String description,
        String status,
        Instant createdAt
) {
}
