package fr.sqq.achatgroupe.application.port.out;

import fr.sqq.achatgroupe.application.query.CursorPage;
import fr.sqq.achatgroupe.application.query.CursorPageRequest;
import fr.sqq.achatgroupe.domain.model.order.Order;

import fr.sqq.achatgroupe.domain.model.shared.Money;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderRepository {

    Order save(Order order);

    Optional<Order> findOrderById(UUID id);

    Optional<Order> findByIdempotencyKey(String idempotencyKey);

    Optional<Order> findOrderByIdAndVenteId(UUID id, Long venteId);

    List<Order> findPendingOrdersBefore(Instant cutoff);

    OrderAggregates getOrderAggregates(Long venteId);

    List<SlotOrderCount> countByTimeSlotForVente(Long venteId);

    List<DailyOrderCount> countByDayForVente(Long venteId);

    List<Order> findPaidByVenteId(Long venteId);

    CursorPage<Order> findPaidByVenteId(Long venteId, CursorPageRequest pageRequest, String searchName, Long timeSlotId);

    List<ProductStats> findProductStats(Long venteId);

    record OrderAggregates(long totalOrders, Money totalAmount, long pickedUpCount) {}

    record ProductStats(Long productId, long totalQuantity, BigDecimal totalRevenue) {}

    record SlotOrderCount(Long slotId, long orderCount) {}

    boolean existsNonCancelledByVenteId(Long venteId);

    boolean existsNonCancelledByTimeSlotId(Long timeSlotId);

    void detachOrdersFromTimeSlot(Long timeSlotId);

    record DailyOrderCount(LocalDate date, long orderCount) {}
}
