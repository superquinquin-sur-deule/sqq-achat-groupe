package fr.sqq.achatgroupe.infrastructure.in.rest.admin.mapper;

import fr.sqq.achatgroupe.domain.model.catalog.Product;
import fr.sqq.achatgroupe.domain.model.order.Order;
import fr.sqq.achatgroupe.domain.model.order.OrderItem;
import fr.sqq.achatgroupe.domain.model.planning.TimeSlot;
import fr.sqq.achatgroupe.infrastructure.in.rest.admin.dto.BackofficeOrderDetailResponse;
import fr.sqq.achatgroupe.infrastructure.in.rest.admin.dto.BackofficeOrderResponse;
import org.mapstruct.Mapper;

import java.util.List;
import java.util.Map;

@Mapper(componentModel = "cdi")
public interface BackofficeOrderRestMapper {

    default BackofficeOrderResponse toListResponse(Order order, Map<Long, TimeSlot> timeSlotsById) {
        TimeSlot slot = timeSlotsById.get(order.timeSlotId());
        String timeSlotLabel = slot != null
                ? slot.date() + " " + slot.startTime() + "-" + slot.endTime()
                : "Créneau inconnu";
        return new BackofficeOrderResponse(
                order.id(),
                order.orderNumber().value(),
                order.customer().name(),
                order.customer().email(),
                timeSlotLabel,
                order.totalAmount(),
                order.status().name(),
                order.createdAt()
        );
    }

    default List<BackofficeOrderResponse> toListResponse(List<Order> orders, Map<Long, TimeSlot> timeSlotsById) {
        return orders.stream()
                .map(order -> toListResponse(order, timeSlotsById))
                .toList();
    }

    default BackofficeOrderDetailResponse toDetailResponse(Order order, Map<Long, TimeSlot> timeSlotsById, Map<Long, Product> productsById) {
        TimeSlot slot = timeSlotsById.get(order.timeSlotId());
        BackofficeOrderDetailResponse.TimeSlotInfo timeSlotInfo = slot != null
                ? new BackofficeOrderDetailResponse.TimeSlotInfo(slot.date(), slot.startTime(), slot.endTime())
                : null;

        List<BackofficeOrderDetailResponse.OrderItemInfo> items = order.items().stream()
                .map(item -> toItemResponse(item, productsById))
                .toList();

        return new BackofficeOrderDetailResponse(
                order.id(),
                order.orderNumber().value(),
                order.status().name(),
                order.customer().name(),
                order.customer().email(),
                order.customer().phone(),
                timeSlotInfo,
                items,
                order.totalAmount(),
                order.createdAt()
        );
    }

    default BackofficeOrderDetailResponse.OrderItemInfo toItemResponse(OrderItem item, Map<Long, Product> productsById) {
        Product product = productsById.get(item.productId());
        String productName = product != null ? product.name() : "Produit #" + item.productId();
        return new BackofficeOrderDetailResponse.OrderItemInfo(
                productName,
                item.quantity(),
                item.unitPrice(),
                item.subtotal()
        );
    }
}
