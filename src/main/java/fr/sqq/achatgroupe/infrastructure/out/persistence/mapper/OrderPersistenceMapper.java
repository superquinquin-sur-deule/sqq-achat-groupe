package fr.sqq.achatgroupe.infrastructure.out.persistence.mapper;

import fr.sqq.achatgroupe.domain.model.order.CustomerInfo;
import fr.sqq.achatgroupe.domain.model.order.Order;
import fr.sqq.achatgroupe.domain.model.order.OrderItem;
import fr.sqq.achatgroupe.domain.model.order.OrderNumber;
import fr.sqq.achatgroupe.domain.model.order.OrderStatus;
import fr.sqq.achatgroupe.infrastructure.out.persistence.entity.OrderEntity;
import fr.sqq.achatgroupe.infrastructure.out.persistence.entity.OrderItemEntity;

import java.util.List;

public class OrderPersistenceMapper {

    private OrderPersistenceMapper() {
    }

    public static Order toDomain(OrderEntity entity) {
        List<OrderItem> items = entity.getItems().stream()
                .map(OrderPersistenceMapper::toDomain)
                .toList();

        return new Order(
                entity.getId(),
                entity.getVenteId(),
                new OrderNumber(entity.getOrderNumber()),
                new CustomerInfo(entity.getCustomerName(), entity.getCustomerEmail(), entity.getCustomerPhone()),
                entity.getTimeSlotId(),
                items,
                OrderStatus.valueOf(entity.getStatus()),
                entity.getTotalAmount(),
                entity.getCreatedAt()
        );
    }

    public static OrderItem toDomain(OrderItemEntity entity) {
        return new OrderItem(
                entity.getId(),
                entity.getProductId(),
                entity.getQuantity(),
                entity.getUnitPrice()
        );
    }

    public static OrderEntity toEntity(Order domain) {
        var entity = new OrderEntity();
        entity.setId(domain.id());
        entity.setVenteId(domain.venteId());
        entity.setOrderNumber(domain.orderNumber().value());
        entity.setCustomerName(domain.customer().name());
        entity.setCustomerEmail(domain.customer().email());
        entity.setCustomerPhone(domain.customer().phone());
        entity.setTimeSlotId(domain.timeSlotId());
        entity.setStatus(domain.status().name());
        entity.setTotalAmount(domain.totalAmount());
        entity.setCreatedAt(domain.createdAt());

        List<OrderItemEntity> itemEntities = domain.items().stream()
                .map(item -> toEntity(item, entity))
                .toList();
        entity.setItems(itemEntities);

        return entity;
    }

    public static OrderItemEntity toEntity(OrderItem domain, OrderEntity orderEntity) {
        var entity = new OrderItemEntity();
        entity.setId(domain.id());
        entity.setOrder(orderEntity);
        entity.setProductId(domain.productId());
        entity.setQuantity(domain.quantity());
        entity.setUnitPrice(domain.unitPrice());
        return entity;
    }
}
