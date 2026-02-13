package fr.sqq.achatgroupe.application.port.in;

import java.math.BigDecimal;
import java.util.List;

public interface GetDashboardStatsUseCase {

    DashboardStats getStats(Long venteId);

    record DashboardStats(
            long totalOrders,
            BigDecimal totalAmount,
            double pickupRate,
            BigDecimal averageBasket,
            List<SlotOrderCount> slotDistribution
    ) {}

    record SlotOrderCount(Long slotId, long orderCount) {}
}
