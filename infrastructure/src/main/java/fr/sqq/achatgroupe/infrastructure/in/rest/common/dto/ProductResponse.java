package fr.sqq.achatgroupe.infrastructure.in.rest.common.dto;

import java.math.BigDecimal;
import java.util.List;

public record ProductResponse(
        Long id,
        String name,
        String description,
        BigDecimal tauxTva,
        String supplier,
        String category,
        String brand,
        String importantNote,
        String imageUrl,
        List<ProductVariantResponse> variants
) {
}
