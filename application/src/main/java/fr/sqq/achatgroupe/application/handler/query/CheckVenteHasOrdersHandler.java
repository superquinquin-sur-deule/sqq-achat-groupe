package fr.sqq.achatgroupe.application.handler.query;

import fr.sqq.achatgroupe.application.port.out.OrderRepository;
import fr.sqq.achatgroupe.application.query.CheckVenteHasOrdersQuery;
import fr.sqq.mediator.QueryHandler;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class CheckVenteHasOrdersHandler implements QueryHandler<CheckVenteHasOrdersQuery, Boolean> {

    private final OrderRepository orderRepository;

    public CheckVenteHasOrdersHandler(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    public Boolean handle(CheckVenteHasOrdersQuery query) {
        return orderRepository.existsNonCancelledByVenteId(query.venteId());
    }
}
