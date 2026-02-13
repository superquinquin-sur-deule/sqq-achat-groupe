package fr.sqq.achatgroupe.application.port.out;

import fr.sqq.achatgroupe.domain.model.order.Order;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface OrderRepository {

    Order save(Order order);

    Optional<Order> findOrderById(Long id);

    List<Order> findPendingOrdersBefore(Instant cutoff);

    long countPaidByVenteId(Long venteId);

    BigDecimal sumTotalPaidByVenteId(Long venteId);

    long countPickedUpByVenteId(Long venteId);

    List<SlotOrderCount> countByTimeSlotForVente(Long venteId);

    List<Order> findPaidByVenteId(Long venteId);

    record SlotOrderCount(Long slotId, long orderCount) {}
}
