package fr.sqq.achatgroupe.infrastructure.in.rest.dto;

public record TopProductResponse(
        String productName,
        long totalQuantity
) {
}
