package fr.sqq.achatgroupe.infrastructure.in.rest.common.dto;

import java.math.BigDecimal;

public record ProductResponse(
        Long id,
        String name,
        String description,
        BigDecimal prixHt,
        BigDecimal tauxTva,
        BigDecimal prixTtc,
        String supplier,
        int stock,
        String category,
        String brand,
        String imageUrl
) {
}
