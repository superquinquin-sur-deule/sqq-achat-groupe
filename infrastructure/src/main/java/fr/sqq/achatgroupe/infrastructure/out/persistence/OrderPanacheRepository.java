package fr.sqq.achatgroupe.infrastructure.out.persistence;

import fr.sqq.achatgroupe.domain.model.order.Order;
import fr.sqq.achatgroupe.application.port.out.OrderRepository;
import fr.sqq.achatgroupe.application.port.out.OrderRepository.SlotOrderCount;
import fr.sqq.achatgroupe.application.port.out.OrderRepository.TopProduct;
import fr.sqq.achatgroupe.application.query.CursorPage;
import fr.sqq.achatgroupe.application.query.CursorPageRequest;
import fr.sqq.achatgroupe.infrastructure.out.persistence.cursor.CursorCodec;
import fr.sqq.achatgroupe.infrastructure.out.persistence.entity.OrderEntity;
import fr.sqq.achatgroupe.infrastructure.out.persistence.mapper.OrderPersistenceMapper;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class OrderPanacheRepository implements OrderRepository, PanacheRepositoryBase<OrderEntity, UUID> {

    @Inject
    OrderPersistenceMapper mapper;

    @Override
    public Order save(Order order) {
        OrderEntity entity = mapper.toEntity(order);
        entity = getEntityManager().merge(entity);
        return mapper.toDomain(entity);
    }

    @Override
    public Optional<Order> findOrderById(UUID id) {
        return find("id", id).firstResultOptional()
                .map(mapper::toDomain);
    }

    @Override
    public Optional<Order> findOrderByIdAndVenteId(UUID id, Long venteId) {
        return find("id = ?1 AND venteId = ?2", id, venteId).firstResultOptional()
                .map(mapper::toDomain);
    }

    @Override
    public List<Order> findPendingOrdersBefore(Instant cutoff) {
        return list("status = ?1 and createdAt < ?2", "PENDING", cutoff).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public long countPaidByVenteId(Long venteId) {
        return count("venteId = ?1 and status in ('PAID', 'PICKED_UP')", venteId);
    }

    @Override
    public BigDecimal sumTotalPaidByVenteId(Long venteId) {
        BigDecimal result = getEntityManager()
                .createQuery("SELECT COALESCE(SUM(o.totalAmount), 0) FROM OrderEntity o WHERE o.venteId = ?1 AND o.status IN ('PAID', 'PICKED_UP')", BigDecimal.class)
                .setParameter(1, venteId)
                .getSingleResult();
        return result != null ? result : BigDecimal.ZERO;
    }

    @Override
    public long countPickedUpByVenteId(Long venteId) {
        return count("venteId = ?1 and status = 'PICKED_UP'", venteId);
    }

    @Override
    public List<Order> findPaidByVenteId(Long venteId) {
        return list("venteId = ?1 AND status IN ?2 ORDER BY createdAt DESC",
                venteId, List.of("PAID", "PICKED_UP")).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public CursorPage<Order> findPaidByVenteId(Long venteId, CursorPageRequest pageRequest, String searchName, Long timeSlotId) {
        var conditions = new ArrayList<String>();
        var params = new HashMap<String, Object>();

        conditions.add("venteId = :venteId");
        params.put("venteId", venteId);

        conditions.add("status IN :statuses");
        params.put("statuses", List.of("PAID", "PICKED_UP"));

        if (searchName != null && !searchName.isBlank()) {
            conditions.add("(LOWER(customerFirstName) LIKE :searchName OR LOWER(customerLastName) LIKE :searchName)");
            params.put("searchName", "%" + searchName.toLowerCase() + "%");
        }

        if (timeSlotId != null) {
            conditions.add("timeSlotId = :timeSlotId");
            params.put("timeSlotId", timeSlotId);
        }

        if (pageRequest.cursor() != null) {
            Instant cursorCreatedAt = CursorCodec.decodeOrderCursorCreatedAt(pageRequest.cursor());
            UUID cursorId = CursorCodec.decodeOrderCursorId(pageRequest.cursor());
            conditions.add("(createdAt < :cursorCreatedAt OR (createdAt = :cursorCreatedAt AND id < :cursorId))");
            params.put("cursorCreatedAt", cursorCreatedAt);
            params.put("cursorId", cursorId);
        }

        String query = String.join(" AND ", conditions) + " ORDER BY createdAt DESC, id DESC";
        int fetchSize = pageRequest.size() + 1;

        List<OrderEntity> entities = find(query, params).range(0, fetchSize - 1).list();

        boolean hasNext = entities.size() > pageRequest.size();
        List<OrderEntity> page = hasNext ? entities.subList(0, pageRequest.size()) : entities;
        List<Order> items = page.stream().map(mapper::toDomain).toList();

        String endCursor = null;
        if (!page.isEmpty()) {
            OrderEntity last = page.get(page.size() - 1);
            endCursor = CursorCodec.encodeOrderCursor(last.getCreatedAt(), last.getId());
        }

        return new CursorPage<>(items, endCursor, hasNext);
    }

    @Override
    public List<SlotOrderCount> countByTimeSlotForVente(Long venteId) {
        List<Object[]> rows = getEntityManager()
                .createQuery("SELECT o.timeSlotId, COUNT(o) FROM OrderEntity o WHERE o.venteId = ?1 AND o.status IN ('PAID', 'PICKED_UP') GROUP BY o.timeSlotId", Object[].class)
                .setParameter(1, venteId)
                .getResultList();
        return rows.stream()
                .map(row -> new SlotOrderCount((Long) row[0], (Long) row[1]))
                .toList();
    }

    @Override
    public boolean existsNonCancelledByVenteId(Long venteId) {
        return count("venteId = ?1 and status != 'CANCELLED'", venteId) > 0;
    }

    @Override
    public List<TopProduct> findTopSellingProducts(Long venteId, int limit) {
        List<Object[]> rows = getEntityManager()
                .createQuery("SELECT oi.productId, SUM(oi.quantity) FROM OrderItemEntity oi JOIN oi.order o WHERE o.venteId = ?1 AND o.status IN ('PAID', 'PICKED_UP') GROUP BY oi.productId ORDER BY SUM(oi.quantity) DESC", Object[].class)
                .setParameter(1, venteId)
                .setMaxResults(limit)
                .getResultList();
        return rows.stream()
                .map(row -> new TopProduct((Long) row[0], ((Number) row[1]).longValue()))
                .toList();
    }
}
