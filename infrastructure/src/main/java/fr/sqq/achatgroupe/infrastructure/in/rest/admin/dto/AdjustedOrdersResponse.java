package fr.sqq.achatgroupe.infrastructure.in.rest.admin.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record AdjustedOrdersResponse(List<AdjustedOrderItem> orders) {

    public record AdjustedOrderItem(
            UUID orderId,
            String orderNumber,
            String customerName,
            BigDecimal originalAmount,
            BigDecimal adjustedAmount,
            BigDecimal adjustmentAmount) {
    }
}
