package fr.sqq.achatgroupe.infrastructure.in.rest.dto;

import java.util.List;

public record ProductListResponse(
        List<ProductResponse> data,
        Meta meta
) {

    public record Meta(int total, int page, int pageSize) {
    }
}
