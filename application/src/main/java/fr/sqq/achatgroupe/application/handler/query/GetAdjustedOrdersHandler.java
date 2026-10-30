package fr.sqq.achatgroupe.application.handler.query;

import fr.sqq.achatgroupe.application.port.out.OrderRepository;
import fr.sqq.achatgroupe.application.query.GetAdjustedOrdersQuery;
import fr.sqq.achatgroupe.application.query.GetAdjustedOrdersQuery.AdjustedOrderItem;
import fr.sqq.achatgroupe.application.query.GetAdjustedOrdersQuery.AdjustedOrdersResult;
import fr.sqq.achatgroupe.domain.model.order.Order;
import fr.sqq.mediator.QueryHandler;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.util.List;

@ApplicationScoped
public class GetAdjustedOrdersHandler implements QueryHandler<GetAdjustedOrdersQuery, AdjustedOrdersResult> {

    private final OrderRepository orderRepository;

    public GetAdjustedOrdersHandler(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    @Transactional
    public AdjustedOrdersResult handle(GetAdjustedOrdersQuery query) {
        List<AdjustedOrderItem> orders = orderRepository.findConfirmedByVenteId(query.venteId()).stream()
                .filter(order -> order.adjustmentAmount().isPositive())
                .map(this::toItem)
                .toList();

        return new AdjustedOrdersResult(orders);
    }

    private AdjustedOrderItem toItem(Order order) {
        String customerName = order.customer().firstName() + " " + order.customer().lastName();
        return new AdjustedOrderItem(
                order.id(),
                order.orderNumber().value(),
                customerName,
                order.totalAmount(),
                order.effectiveTotalAmount(),
                order.adjustmentAmount());
    }
}
