package fr.sqq.achatgroupe.infrastructure.in.rest.admin.dto;

import java.time.Instant;
import java.time.LocalDate;

public record AdminVenteResponse(
        Long id,
        String name,
        String description,
        String status,
        Instant startDate,
        Instant endDate,
        String pickupMode,
        LocalDate pickupStartDate,
        LocalDate pickupEndDate,
        Instant createdAt,
        boolean hasOrders
) {
}
