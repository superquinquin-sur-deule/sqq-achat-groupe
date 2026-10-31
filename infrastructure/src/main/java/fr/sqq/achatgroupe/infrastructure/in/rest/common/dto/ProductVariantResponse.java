package fr.sqq.achatgroupe.infrastructure.in.rest.common.dto;

import java.math.BigDecimal;

public record ProductVariantResponse(
        Long id,
        String label,
        BigDecimal prixHt,
        BigDecimal prixTtc,
        int stock
) {
}
