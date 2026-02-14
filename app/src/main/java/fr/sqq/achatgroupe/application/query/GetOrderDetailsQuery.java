package fr.sqq.achatgroupe.application.query;

import fr.sqq.achatgroupe.domain.model.order.Order;
import fr.sqq.mediator.Query;

public record GetOrderDetailsQuery(Long orderId) implements Query<Order> {
}
