package fr.sqq.achatgroupe.infrastructure.in.rest.dto;

import java.math.BigDecimal;
import java.util.List;

public record DashboardStatsResponse(
        long totalOrders,
        BigDecimal totalAmount,
        double pickupRate,
        BigDecimal averageBasket,
        List<SlotDistribution> slotDistribution,
        List<TopProductResponse> topProducts
) {
}
