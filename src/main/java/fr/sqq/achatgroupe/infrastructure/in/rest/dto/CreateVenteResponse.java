package fr.sqq.achatgroupe.infrastructure.in.rest.dto;

import java.time.Instant;
import java.util.List;

public record CreateVenteResponse(
        Long id,
        String name,
        String description,
        String status,
        Instant createdAt,
        List<Long> productIds,
        List<Long> timeSlotIds
) {
}
