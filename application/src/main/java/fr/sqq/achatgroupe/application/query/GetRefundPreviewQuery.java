package fr.sqq.achatgroupe.application.query;

import fr.sqq.achatgroupe.domain.model.shared.Money;
import fr.sqq.mediator.Query;

import java.util.List;
import java.util.UUID;

public record GetRefundPreviewQuery(Long venteId) implements Query<GetRefundPreviewQuery.RefundPreviewResult> {

    public record RefundPreviewResult(List<RefundOrderItem> orders) {}

    public record RefundOrderItem(UUID orderId, String orderNumber, String customerName,
                                  Money originalAmount, Money adjustedAmount,
                                  Money refundAmount, String refundStatus) {}
}
