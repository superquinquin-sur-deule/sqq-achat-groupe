package fr.sqq.achatgroupe.infrastructure.in.rest.admin.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record AdminOrderResponse(
        UUID id,
        String orderNumber,
        String customerFirstName,
        String customerLastName,
        String customerEmail,
        String timeSlotLabel,
        BigDecimal totalAmount,
        String status,
        Instant createdAt
) {}
