package fr.sqq.achatgroupe.application.port.out;

import fr.sqq.achatgroupe.application.query.CursorPage;
import fr.sqq.achatgroupe.application.query.CursorPageRequest;
import fr.sqq.achatgroupe.domain.model.order.Order;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderRepository {

    Order save(Order order);

    Optional<Order> findOrderById(UUID id);

    Optional<Order> findOrderByIdAndVenteId(UUID id, Long venteId);

    List<Order> findPendingOrdersBefore(Instant cutoff);

    long countPaidByVenteId(Long venteId);

    BigDecimal sumTotalPaidByVenteId(Long venteId);

    long countPickedUpByVenteId(Long venteId);

    List<SlotOrderCount> countByTimeSlotForVente(Long venteId);

    List<Order> findPaidByVenteId(Long venteId);

    CursorPage<Order> findPaidByVenteId(Long venteId, CursorPageRequest pageRequest, String searchName, Long timeSlotId);

    List<TopProduct> findTopSellingProducts(Long venteId, int limit);

    record SlotOrderCount(Long slotId, long orderCount) {}

    record TopProduct(Long productId, long totalQuantity) {}
}
