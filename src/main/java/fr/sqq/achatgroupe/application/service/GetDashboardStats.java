package fr.sqq.achatgroupe.application.service;

import fr.sqq.achatgroupe.application.port.in.GetDashboardStatsUseCase;
import fr.sqq.achatgroupe.application.port.out.OrderRepository;
import fr.sqq.achatgroupe.application.port.out.ProductRepository;
import fr.sqq.achatgroupe.domain.model.catalog.Product;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ApplicationScoped
public class GetDashboardStats implements GetDashboardStatsUseCase {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    public GetDashboardStats(OrderRepository orderRepository, ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
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

        Map<Long, String> productNames = productRepository.findAllByVenteId(venteId).stream()
                .collect(Collectors.toMap(Product::id, Product::name));

        List<TopProductStat> topProducts = orderRepository.findTopSellingProducts(venteId, 3).stream()
                .map(tp -> new TopProductStat(
                        tp.productId(),
                        productNames.getOrDefault(tp.productId(), "Produit #" + tp.productId()),
                        tp.totalQuantity()))
                .toList();

        return new DashboardStats(totalOrders, totalAmount, pickupRate, averageBasket, slotDistribution, topProducts);
    }
}
