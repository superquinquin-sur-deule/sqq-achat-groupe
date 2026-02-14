package fr.sqq.achatgroupe.infrastructure.in.rest.admin.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record AdminOrderResponse(
        Long id,
        String orderNumber,
        String customerName,
        String customerEmail,
        String timeSlotLabel,
        BigDecimal totalAmount,
        String status,
        Instant createdAt
) {}
