package fr.sqq.achatgroupe.application.port.in;

import fr.sqq.achatgroupe.domain.model.order.Order;

import java.util.List;

public interface ListOrdersUseCase {

    List<Order> listOrders(Long venteId);
}
