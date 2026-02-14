package fr.sqq.achatgroupe.infrastructure.in.rest.common.dto;

import jakarta.validation.constraints.NotBlank;

import java.time.Instant;

public record CreateAdminVenteRequest(
        @NotBlank String name,
        String description,
        Instant startDate,
        Instant endDate
) {
}
