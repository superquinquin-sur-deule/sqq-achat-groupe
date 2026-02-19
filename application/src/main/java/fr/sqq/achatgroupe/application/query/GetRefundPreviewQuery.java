package fr.sqq.achatgroupe.application.query;

import fr.sqq.mediator.Query;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record GetRefundPreviewQuery(Long venteId) implements Query<GetRefundPreviewQuery.RefundPreviewResult> {

    public record RefundPreviewResult(List<RefundOrderItem> orders) {}

    public record RefundOrderItem(UUID orderId, String orderNumber, String customerName,
                                  BigDecimal originalAmount, BigDecimal adjustedAmount,
                                  BigDecimal refundAmount, String refundStatus) {}
}
