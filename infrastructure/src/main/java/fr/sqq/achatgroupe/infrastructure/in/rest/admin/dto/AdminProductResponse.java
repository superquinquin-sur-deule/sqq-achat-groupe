package fr.sqq.achatgroupe.infrastructure.in.rest.admin.dto;

import java.math.BigDecimal;

public record AdminProductResponse(
        Long id,
        Long venteId,
        String name,
        String description,
        BigDecimal prixHt,
        BigDecimal tauxTva,
        BigDecimal prixTtc,
        String supplier,
        int stock,
        boolean active,
        String reference,
        String category,
        String brand,
        String imageUrl
) {}
