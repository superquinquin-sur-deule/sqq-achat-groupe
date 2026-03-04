package fr.sqq.achatgroupe.infrastructure.in.rest.common.dto;

import java.math.BigDecimal;

public record TopRevenueProductResponse(
        String productName,
        BigDecimal totalRevenue
) {
}
