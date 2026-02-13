package fr.sqq.achatgroupe.infrastructure.in.rest.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record OrderResponse(
        Long id,
        String orderNumber,
        String status,
        BigDecimal totalAmount,
        Instant createdAt
) {
}
