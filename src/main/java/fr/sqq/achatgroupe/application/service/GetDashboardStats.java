package fr.sqq.achatgroupe.application.service;

import fr.sqq.achatgroupe.application.port.in.GetDashboardStatsUseCase;
import fr.sqq.achatgroupe.application.port.out.OrderRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@ApplicationScoped
public class GetDashboardStats implements GetDashboardStatsUseCase {

    private final OrderRepository orderRepository;

    public GetDashboardStats(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    @Transactional
    public DashboardStats getStats(Long venteId) {
        long totalOrders = orderRepository.countPaidByVenteId(venteId);
        BigDecimal totalAmount = orderRepository.sumTotalPaidByVenteId(venteId);
        long pickedUpCount = orderRepository.countPickedUpByVenteId(venteId);

        BigDecimal averageBasket = totalOrders > 0
                ? totalAmount.divide(BigDecimal.valueOf(totalOrders), 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        double pickupRate = totalOrders > 0
                ? (double) pickedUpCount / totalOrders * 100
                : 0.0;
        pickupRate = BigDecimal.valueOf(pickupRate).setScale(1, RoundingMode.HALF_UP).doubleValue();

        List<SlotOrderCount> slotDistribution = orderRepository.countByTimeSlotForVente(venteId).stream()
                .map(sc -> new SlotOrderCount(sc.slotId(), sc.orderCount()))
                .toList();

        return new DashboardStats(totalOrders, totalAmount, pickupRate, averageBasket, slotDistribution);
    }
}
