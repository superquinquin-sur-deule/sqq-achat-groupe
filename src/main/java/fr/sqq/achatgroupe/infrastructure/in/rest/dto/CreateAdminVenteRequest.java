package fr.sqq.achatgroupe.infrastructure.in.rest.dto;

import jakarta.validation.constraints.NotBlank;

import java.time.Instant;

public record CreateAdminVenteRequest(
        @NotBlank String name,
        String description,
        Instant startDate,
        Instant endDate
) {
}
