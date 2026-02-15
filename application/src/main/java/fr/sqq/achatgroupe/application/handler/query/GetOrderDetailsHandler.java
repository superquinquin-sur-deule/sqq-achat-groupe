package fr.sqq.achatgroupe.application.handler.query;

import fr.sqq.achatgroupe.application.port.out.OrderRepository;
import fr.sqq.achatgroupe.application.query.GetOrderDetailsQuery;
import fr.sqq.achatgroupe.domain.exception.OrderNotFoundException;
import fr.sqq.achatgroupe.domain.model.order.Order;
import fr.sqq.achatgroupe.domain.model.order.OrderStatus;
import fr.sqq.mediator.QueryHandler;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class GetOrderDetailsHandler implements QueryHandler<GetOrderDetailsQuery, Order> {

    private final OrderRepository orderRepository;

    public GetOrderDetailsHandler(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    @Transactional
    public Order handle(GetOrderDetailsQuery query) {
        Order order = orderRepository.findOrderByIdAndVenteId(query.orderId(), query.venteId())
                .orElseThrow(() -> new OrderNotFoundException(query.orderId()));
        if (order.status() != OrderStatus.PAID && order.status() != OrderStatus.PICKED_UP) {
            throw new OrderNotFoundException(query.orderId());
        }
        return order;
    }
}
