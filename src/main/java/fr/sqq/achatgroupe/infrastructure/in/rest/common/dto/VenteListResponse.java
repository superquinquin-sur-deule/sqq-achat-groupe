package fr.sqq.achatgroupe.infrastructure.in.rest.common.dto;

import fr.sqq.achatgroupe.infrastructure.in.rest.coop.dto.VenteResponse;

import java.util.List;

public record VenteListResponse(
        List<VenteResponse> data
) {
}
