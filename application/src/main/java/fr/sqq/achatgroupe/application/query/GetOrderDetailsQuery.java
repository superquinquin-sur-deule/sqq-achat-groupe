package fr.sqq.achatgroupe.application.query;

import fr.sqq.achatgroupe.domain.model.order.Order;
import fr.sqq.mediator.Query;

import java.util.UUID;

public record GetOrderDetailsQuery(UUID orderId) implements Query<Order> {
}
