package fr.sqq.achatgroupe.infrastructure.in.rest.admin;

import fr.sqq.achatgroupe.application.port.in.GetDashboardStatsUseCase.DashboardStats;
import fr.sqq.achatgroupe.application.port.in.GetDashboardStatsUseCase.SlotOrderCount;
import fr.sqq.achatgroupe.domain.model.planning.TimeSlot;
import fr.sqq.achatgroupe.infrastructure.in.rest.dto.DashboardStatsResponse;
import fr.sqq.achatgroupe.infrastructure.in.rest.dto.SlotDistribution;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AdminDashboardRestMapper {

    private AdminDashboardRestMapper() {
    }

    public static DashboardStatsResponse toResponse(DashboardStats stats, List<TimeSlot> timeSlots) {
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

        return new DashboardStatsResponse(
                stats.totalOrders(),
                stats.totalAmount(),
                stats.pickupRate(),
                stats.averageBasket(),
                distribution
        );
    }
}
