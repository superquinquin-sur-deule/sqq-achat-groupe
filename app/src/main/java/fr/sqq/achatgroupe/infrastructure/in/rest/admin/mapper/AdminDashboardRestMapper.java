package fr.sqq.achatgroupe.infrastructure.in.rest.admin.mapper;

import fr.sqq.achatgroupe.application.query.GetDashboardStatsQuery.DashboardStats;
import fr.sqq.achatgroupe.domain.model.planning.TimeSlot;
import fr.sqq.achatgroupe.infrastructure.in.rest.common.dto.DashboardStatsResponse;
import fr.sqq.achatgroupe.infrastructure.in.rest.common.dto.SlotDistribution;
import fr.sqq.achatgroupe.infrastructure.in.rest.common.dto.TopProductResponse;
import org.mapstruct.Mapper;

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
                    return new SlotDistribution(sc.slotId(), label, sc.orderCount());
                })
                .toList();

        List<TopProductResponse> topProducts = stats.topProducts().stream()
                .map(tp -> new TopProductResponse(tp.productName(), tp.totalQuantity()))
                .toList();

        return new DashboardStatsResponse(
                stats.totalOrders(),
                stats.totalAmount(),
                stats.pickupRate(),
                stats.averageBasket(),
                distribution,
                topProducts
        );
    }
}
