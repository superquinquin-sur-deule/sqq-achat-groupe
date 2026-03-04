package fr.sqq.achatgroupe.application.query;

import fr.sqq.achatgroupe.domain.model.shared.Money;
import fr.sqq.mediator.Query;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record GetDashboardStatsQuery(Long venteId) implements Query<GetDashboardStatsQuery.DashboardStats> {

    public record DashboardStats(
            long totalOrders,
            Money totalAmount,
            double pickupRate,
            Money averageBasket,
            List<SlotOrderCount> slotDistribution,
            List<TopProductStat> topProducts,
            List<TopRevenueProductStat> topRevenueProducts,
            List<DailyOrderCount> dailyOrderCounts
    ) {
    }

    public record SlotOrderCount(Long slotId, long orderCount) {
    }

    public record TopProductStat(Long productId, String productName, long totalQuantity) {
    }

    public record TopRevenueProductStat(Long productId, String productName, BigDecimal totalRevenue) {
    }

    public record DailyOrderCount(LocalDate date, long orderCount) {
    }
}
