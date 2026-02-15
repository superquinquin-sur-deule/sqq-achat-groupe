package fr.sqq.achatgroupe.application.handler.command;

import fr.sqq.achatgroupe.application.command.MarkOrderPickedUpCommand;
import fr.sqq.achatgroupe.application.port.out.OrderRepository;
import fr.sqq.achatgroupe.domain.exception.OrderNotFoundException;
import fr.sqq.achatgroupe.domain.model.order.Order;
import fr.sqq.mediator.CommandHandler;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class MarkOrderPickedUpHandler implements CommandHandler<MarkOrderPickedUpCommand, Void> {

    private final OrderRepository orderRepository;

    public MarkOrderPickedUpHandler(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    @Transactional
    public Void handle(MarkOrderPickedUpCommand command) {
        Order order = orderRepository.findOrderByIdAndVenteId(command.orderId(), command.venteId())
                .orElseThrow(() -> new OrderNotFoundException(command.orderId()));
        order.markAsPickedUp();
        orderRepository.save(order);
        return null;
    }
}
