package fr.sqq.achatgroupe.application.command;

import fr.sqq.mediator.Command;
import fr.sqq.achatgroupe.domain.model.order.Order;

import java.util.List;

public record CreateOrderCommand(
        Long venteId,
        String customerFirstName,
        String customerLastName,
        String email,
        String phone,
        Long timeSlotId,
        List<OrderItemCommand> items
) implements Command<Order> {

    public record OrderItemCommand(Long productId, int quantity) {
    }
}
