package fr.sqq.achatgroupe.infrastructure.in.rest.common.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

public record OrderDetailResponse(
        UUID id,
        String orderNumber,
        String status,
        BigDecimal totalAmount,
        String customerName,
        String customerEmail,
        TimeSlotInfo timeSlot,
        List<OrderItemInfo> items,
        Instant createdAt
) {
    public record TimeSlotInfo(LocalDate date, LocalTime startTime, LocalTime endTime) {}
    public record OrderItemInfo(String productName, int quantity, BigDecimal unitPrice) {}
}
