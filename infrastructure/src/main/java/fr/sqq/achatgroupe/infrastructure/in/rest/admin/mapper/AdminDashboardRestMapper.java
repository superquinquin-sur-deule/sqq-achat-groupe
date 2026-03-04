package fr.sqq.achatgroupe.infrastructure.in.rest.admin.mapper;

import fr.sqq.achatgroupe.application.query.GetDashboardStatsQuery.DashboardStats;
import fr.sqq.achatgroupe.domain.model.planning.TimeSlot;
import fr.sqq.achatgroupe.infrastructure.in.rest.common.dto.DashboardStatsResponse;
import fr.sqq.achatgroupe.infrastructure.in.rest.common.dto.DashboardStatsResponse.DailyOrderCountResponse;
import fr.sqq.achatgroupe.infrastructure.in.rest.common.dto.SlotDistribution;
import fr.sqq.achatgroupe.infrastructure.in.rest.common.dto.TopProductResponse;
import fr.sqq.achatgroupe.infrastructure.in.rest.common.dto.TopRevenueProductResponse;
import org.mapstruct.Mapper;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Mapper(componentModel = "cdi")
public interface AdminDashboardRestMapper {

    default DashboardStatsResponse toResponse(DashboardStats stats, List<TimeSlot> timeSlots) {
        Map<Long, TimeSlot> slotMap = timeSlots.stream()
                .collect(Collectors.toMap(TimeSlot::id, ts -> ts));

        List<SlotDistribution> distribution = stats.slotDistribution().stream()
                .map(sc -> {
                    TimeSlot slot = slotMap.get(sc.slotId());
                    String label = slot != null
                            ? slot.date() + " " + slot.startTime() + "-" + slot.endTime()
                            : "Créneau #" + sc.slotId();
                    LocalDate date = slot != null ? slot.date() : null;
                    return new SlotDistribution(sc.slotId(), label, sc.orderCount(), date);
                })
                .toList();

        List<TopProductResponse> topProducts = stats.topProducts().stream()
                .map(tp -> new TopProductResponse(tp.productName(), tp.totalQuantity()))
                .toList();

        List<TopRevenueProductResponse> topRevenueProducts = stats.topRevenueProducts().stream()
                .map(tp -> new TopRevenueProductResponse(tp.productName(), tp.totalRevenue()))
                .toList();

        List<DailyOrderCountResponse> dailyOrderCounts = stats.dailyOrderCounts().stream()
                .map(dc -> new DailyOrderCountResponse(dc.date(), dc.orderCount()))
                .toList();

        return new DashboardStatsResponse(
                stats.totalOrders(),
                stats.totalAmount().amount(),
                stats.pickupRate(),
                stats.averageBasket().amount(),
                distribution,
                topProducts,
                topRevenueProducts,
                dailyOrderCounts
        );
    }
}
