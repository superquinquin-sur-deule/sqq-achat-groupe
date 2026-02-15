package fr.sqq.achatgroupe.application.handler.query;

import fr.sqq.achatgroupe.application.port.out.OrderRepository;
import fr.sqq.achatgroupe.application.query.CursorPage;
import fr.sqq.achatgroupe.application.query.ListOrdersQuery;
import fr.sqq.achatgroupe.domain.model.order.Order;
import fr.sqq.mediator.QueryHandler;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class ListOrdersHandler implements QueryHandler<ListOrdersQuery, CursorPage<Order>> {

    private final OrderRepository orderRepository;

    public ListOrdersHandler(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    @Transactional
    public CursorPage<Order> handle(ListOrdersQuery query) {
        return orderRepository.findPaidByVenteId(query.venteId(), query.pageRequest(), query.searchName(), query.timeSlotId());
    }
}
