package fr.sqq.achatgroupe.infrastructure.in.rest.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record BackofficeOrderResponse(
        Long id,
        String orderNumber,
        String customerName,
        String customerEmail,
        String timeSlotLabel,
        BigDecimal totalAmount,
        String status,
        Instant createdAt
) {}
