package fr.sqq.achatgroupe.infrastructure.in.rest.common.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record OrderResponse(
        UUID id,
        String orderNumber,
        String status,
        BigDecimal totalAmount,
        Instant createdAt
) {
}
