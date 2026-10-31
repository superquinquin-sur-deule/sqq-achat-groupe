package fr.sqq.achatgroupe.infrastructure.in.rest.admin.dto;

import java.math.BigDecimal;

public record AdminProductVariantResponse(
        Long id,
        String label,
        String reference,
        BigDecimal prixHt,
        BigDecimal prixTtc,
        int stock,
        Integer colisage
) {
}
