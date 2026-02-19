package fr.sqq.achatgroupe.infrastructure.in.rest.admin.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record RefundPreviewResponse(List<RefundOrderItem> orders) {

    public record RefundOrderItem(UUID orderId, String orderNumber, String customerName,
                                  BigDecimal originalAmount, BigDecimal adjustedAmount,
                                  BigDecimal refundAmount, String refundStatus) {}
}
