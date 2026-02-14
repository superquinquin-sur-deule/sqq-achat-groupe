package fr.sqq.achatgroupe.infrastructure.in.rest.coop.dto;

import java.time.Instant;

public record VenteResponse(
        Long id,
        String name,
        String description,
        String status,
        Instant startDate,
        Instant endDate,
        Instant createdAt
) {
}
