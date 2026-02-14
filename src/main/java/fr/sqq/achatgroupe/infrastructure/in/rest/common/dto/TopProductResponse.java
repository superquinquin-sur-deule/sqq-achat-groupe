package fr.sqq.achatgroupe.infrastructure.in.rest.common.dto;

public record TopProductResponse(
        String productName,
        long totalQuantity
) {
}
