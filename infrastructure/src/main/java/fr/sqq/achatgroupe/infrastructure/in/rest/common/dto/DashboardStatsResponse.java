package fr.sqq.achatgroupe.infrastructure.in.rest.common.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record DashboardStatsResponse(
        long totalOrders,
        BigDecimal totalAmount,
        double pickupRate,
        BigDecimal averageBasket,
        List<SlotDistribution> slotDistribution,
        List<TopProductResponse> topProducts,
        List<TopRevenueProductResponse> topRevenueProducts,
        List<DailyOrderCountResponse> dailyOrderCounts
) {
    public record DailyOrderCountResponse(LocalDate date, long orderCount) {}
}
