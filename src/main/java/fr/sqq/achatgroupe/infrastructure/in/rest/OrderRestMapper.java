package fr.sqq.achatgroupe.infrastructure.in.rest;

import fr.sqq.achatgroupe.domain.model.order.Order;
import fr.sqq.achatgroupe.application.port.in.CreateOrderUseCase.CreateOrderCommand;
import fr.sqq.achatgroupe.application.port.in.CreateOrderUseCase.CreateOrderCommand.OrderItemCommand;
import fr.sqq.achatgroupe.infrastructure.in.rest.dto.CreateOrderRequest;
import fr.sqq.achatgroupe.infrastructure.in.rest.dto.OrderResponse;

public class OrderRestMapper {

    private OrderRestMapper() {
    }

    public static CreateOrderCommand toCommand(Long venteId, CreateOrderRequest request) {
        var items = request.items().stream()
                .map(i -> new OrderItemCommand(i.productId(), i.quantity()))
                .toList();
        return new CreateOrderCommand(
                venteId,
                request.customerName(),
                request.email(),
                request.phone(),
                request.timeSlotId(),
                items
        );
    }

    public static OrderResponse toResponse(Order order) {
        return new OrderResponse(
                order.id(),
                order.orderNumber().value(),
                order.status().name(),
                order.totalAmount(),
                order.createdAt()
        );
    }
}
