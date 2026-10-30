package fr.sqq.achatgroupe.infrastructure.in.rest.common.dto;

import jakarta.validation.constraints.NotBlank;

import java.time.Instant;
import java.time.LocalDate;

public record CreateAdminVenteRequest(
        @NotBlank String name,
        String description,
        Instant startDate,
        Instant endDate,
        String pickupMode,
        LocalDate pickupStartDate,
        LocalDate pickupEndDate
) {
}
