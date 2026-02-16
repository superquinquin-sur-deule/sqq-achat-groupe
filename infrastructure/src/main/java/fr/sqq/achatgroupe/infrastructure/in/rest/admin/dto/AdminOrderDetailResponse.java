package fr.sqq.achatgroupe.infrastructure.in.rest.admin.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

public record AdminOrderDetailResponse(
        UUID id,
        String orderNumber,
        String status,
        String customerFirstName,
        String customerLastName,
        String customerEmail,
        String customerPhone,
        TimeSlotInfo timeSlot,
        List<OrderItemInfo> items,
        BigDecimal totalAmount,
        Instant createdAt
) {
    public record TimeSlotInfo(LocalDate date, LocalTime startTime, LocalTime endTime) {}
    public record OrderItemInfo(String productName, int quantity, BigDecimal unitPrice, BigDecimal subtotal) {}
}
