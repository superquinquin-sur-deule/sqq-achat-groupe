package fr.sqq.achatgroupe.infrastructure.in.rest.admin.dto;

import java.time.Instant;

public record AdminVenteResponse(
        Long id,
        String name,
        String description,
        String status,
        Instant startDate,
        Instant endDate,
        Instant createdAt,
        boolean hasOrders
) {
}
