package fr.sqq.achatgroupe.infrastructure.in.rest.dto;

import java.math.BigDecimal;

public record AdminProductResponse(
        Long id,
        Long venteId,
        String name,
        String description,
        BigDecimal price,
        String supplier,
        int stock,
        boolean active
) {}
