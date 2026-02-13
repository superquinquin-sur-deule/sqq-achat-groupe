package fr.sqq.achatgroupe.application.service;

import fr.sqq.achatgroupe.application.port.in.GetOrderDetailsUseCase;
import fr.sqq.achatgroupe.application.port.out.OrderRepository;
import fr.sqq.achatgroupe.domain.model.order.Order;
import fr.sqq.achatgroupe.domain.exception.OrderNotFoundException;
import fr.sqq.achatgroupe.domain.model.order.OrderStatus;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class GetOrderDetails implements GetOrderDetailsUseCase {

    private final OrderRepository orderRepository;

    public GetOrderDetails(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    @Transactional
    public Order getOrderDetails(Long orderId) {
        Order order = orderRepository.findOrderById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));
        if (order.status() != OrderStatus.PAID && order.status() != OrderStatus.PICKED_UP) {
            throw new OrderNotFoundException(orderId);
        }
        return order;
    }
}
