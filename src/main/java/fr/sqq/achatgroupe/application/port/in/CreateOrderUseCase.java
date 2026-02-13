package fr.sqq.achatgroupe.application.port.in;

import fr.sqq.achatgroupe.domain.model.order.Order;

import java.util.List;

public interface CreateOrderUseCase {

    Order execute(CreateOrderCommand command);

    record CreateOrderCommand(
            Long venteId,
            String customerName,
            String email,
            String phone,
            Long timeSlotId,
            List<OrderItemCommand> items
    ) {
        public record OrderItemCommand(Long productId, int quantity) {
        }
    }
}
