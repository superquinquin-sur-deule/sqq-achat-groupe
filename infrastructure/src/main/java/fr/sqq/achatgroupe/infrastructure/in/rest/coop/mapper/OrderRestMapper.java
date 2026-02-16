package fr.sqq.achatgroupe.infrastructure.in.rest.coop.mapper;

import fr.sqq.achatgroupe.application.command.CreateOrderCommand;
import fr.sqq.achatgroupe.application.command.CreateOrderCommand.OrderItemCommand;
import fr.sqq.achatgroupe.domain.model.order.Order;
import fr.sqq.achatgroupe.infrastructure.in.rest.common.dto.CreateOrderRequest;
import fr.sqq.achatgroupe.infrastructure.in.rest.common.dto.OrderResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "cdi")
public interface OrderRestMapper {

    default CreateOrderCommand toCommand(Long venteId, CreateOrderRequest request) {
        var items = request.items().stream()
                .map(i -> new OrderItemCommand(i.productId(), i.quantity()))
                .toList();
        return new CreateOrderCommand(
                venteId,
                request.customerFirstName(),
                request.customerLastName(),
                request.email(),
                request.phone(),
                request.timeSlotId(),
                items
        );
    }

    @Mapping(target = "id", expression = "java(order.id())")
    @Mapping(target = "orderNumber", expression = "java(order.orderNumber().value())")
    @Mapping(target = "status", expression = "java(order.status().name())")
    @Mapping(target = "totalAmount", expression = "java(order.totalAmount())")
    @Mapping(target = "createdAt", expression = "java(order.createdAt())")
    OrderResponse toResponse(Order order);
}
