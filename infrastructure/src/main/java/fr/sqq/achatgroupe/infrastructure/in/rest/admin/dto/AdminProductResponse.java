package fr.sqq.achatgroupe.infrastructure.in.rest.admin.dto;

import java.math.BigDecimal;
import java.util.List;

public record AdminProductResponse(
        Long id,
        Long venteId,
        String name,
        String description,
        BigDecimal tauxTva,
        String supplier,
        boolean active,
        String category,
        String brand,
        String importantNote,
        String imageUrl,
        List<AdminProductVariantResponse> variants
) {}
