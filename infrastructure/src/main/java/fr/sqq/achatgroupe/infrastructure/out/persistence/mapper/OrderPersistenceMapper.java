package fr.sqq.achatgroupe.infrastructure.out.persistence.mapper;

import fr.sqq.achatgroupe.domain.model.order.CustomerInfo;
import fr.sqq.achatgroupe.domain.model.order.Order;
import fr.sqq.achatgroupe.domain.model.order.OrderItem;
import fr.sqq.achatgroupe.domain.model.order.OrderNumber;
import fr.sqq.achatgroupe.domain.model.order.OrderStatus;
import fr.sqq.achatgroupe.domain.model.shared.Money;
import fr.sqq.achatgroupe.infrastructure.out.persistence.entity.OrderEntity;
import fr.sqq.achatgroupe.infrastructure.out.persistence.entity.OrderItemEntity;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "cdi")
public interface OrderPersistenceMapper {

    default Order toDomain(OrderEntity entity) {
        List<OrderItem> items = entity.getItems().stream()
                .map(this::toDomain)
                .toList();

        return new Order(
                entity.getId(),
                entity.getVenteId(),
                new OrderNumber(entity.getOrderNumber()),
                new CustomerInfo(entity.getCustomerFirstName(), entity.getCustomerLastName(), entity.getCustomerEmail(), entity.getCustomerPhone()),
                entity.getTimeSlotId(),
                items,
                OrderStatus.valueOf(entity.getStatus()),
                Money.eur(entity.getTotalAmount()),
                entity.getCreatedAt()
        );
    }

    default OrderItem toDomain(OrderItemEntity entity) {
        return new OrderItem(entity.getId(), entity.getProductId(), entity.getQuantity(),
                Money.eur(entity.getUnitPrice()), entity.getCancelledQuantity());
    }

    default OrderEntity toEntity(Order domain) {
        var entity = new OrderEntity();
        entity.setId(domain.id());
        entity.setVenteId(domain.venteId());
        entity.setOrderNumber(domain.orderNumber().value());
        entity.setCustomerFirstName(domain.customer().firstName());
        entity.setCustomerLastName(domain.customer().lastName());
        entity.setCustomerEmail(domain.customer().email());
        entity.setCustomerPhone(domain.customer().phone());
        entity.setTimeSlotId(domain.timeSlotId());
        entity.setStatus(domain.status().name());
        entity.setTotalAmount(domain.totalAmount().amount());
        entity.setCreatedAt(domain.createdAt());

        List<OrderItemEntity> itemEntities = domain.items().stream()
                .map(item -> toEntity(item, entity))
                .toList();
        entity.setItems(itemEntities);

        return entity;
    }

    default OrderItemEntity toEntity(OrderItem domain, OrderEntity orderEntity) {
        var entity = new OrderItemEntity();
        entity.setId(domain.id());
        entity.setOrder(orderEntity);
        entity.setProductId(domain.productId());
        entity.setQuantity(domain.quantity());
        entity.setUnitPrice(domain.unitPrice().amount());
        entity.setCancelledQuantity(domain.cancelledQuantity());
        return entity;
    }
}
