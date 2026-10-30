package fr.sqq.achatgroupe.application.query;

import fr.sqq.achatgroupe.domain.model.shared.Money;
import fr.sqq.mediator.Query;

import java.util.List;
import java.util.UUID;

public record GetAdjustedOrdersQuery(Long venteId) implements Query<GetAdjustedOrdersQuery.AdjustedOrdersResult> {

    public record AdjustedOrdersResult(List<AdjustedOrderItem> orders) {}

    public record AdjustedOrderItem(
            UUID orderId,
            String orderNumber,
            String customerName,
            Money originalAmount,
            Money adjustedAmount,
            Money adjustmentAmount) {}
}
