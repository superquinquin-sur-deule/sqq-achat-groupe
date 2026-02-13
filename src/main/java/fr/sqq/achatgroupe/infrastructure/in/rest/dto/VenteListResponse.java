package fr.sqq.achatgroupe.infrastructure.in.rest.dto;

import java.util.List;

public record VenteListResponse(
        List<VenteResponse> data
) {
}
