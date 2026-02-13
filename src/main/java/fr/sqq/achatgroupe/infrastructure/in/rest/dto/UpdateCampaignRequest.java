package fr.sqq.achatgroupe.infrastructure.in.rest.dto;

import jakarta.validation.constraints.NotNull;

public record UpdateCampaignRequest(
        @NotNull Long venteId,
        @NotNull Boolean active
) {
}
