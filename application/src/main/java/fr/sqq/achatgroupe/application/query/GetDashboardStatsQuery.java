package fr.sqq.achatgroupe.application.query;

import fr.sqq.mediator.Query;

import java.math.BigDecimal;
import java.util.List;

public record GetDashboardStatsQuery(Long venteId) implements Query<GetDashboardStatsQuery.DashboardStats> {

    public record DashboardStats(
            long totalOrders,
            BigDecimal totalAmount,
            double pickupRate,
            BigDecimal averageBasket,
            List<SlotOrderCount> slotDistribution,
            List<TopProductStat> topProducts
    ) {
    }

    public record SlotOrderCount(Long slotId, long orderCount) {
    }

    public record TopProductStat(Long productId, String productName, long totalQuantity) {
    }
}
