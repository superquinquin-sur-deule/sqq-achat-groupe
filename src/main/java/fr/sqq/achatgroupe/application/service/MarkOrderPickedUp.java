package fr.sqq.achatgroupe.application.service;

import fr.sqq.achatgroupe.application.port.in.MarkOrderPickedUpUseCase;
import fr.sqq.achatgroupe.application.port.out.OrderRepository;
import fr.sqq.achatgroupe.domain.exception.OrderNotFoundException;
import fr.sqq.achatgroupe.domain.model.order.Order;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class MarkOrderPickedUp implements MarkOrderPickedUpUseCase {

    private final OrderRepository orderRepository;

    public MarkOrderPickedUp(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    @Transactional
    public void markAsPickedUp(Long orderId) {
        Order order = orderRepository.findOrderById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));
        order.markAsPickedUp();
        orderRepository.save(order);
    }
}
