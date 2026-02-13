package fr.sqq.achatgroupe.application.port.in;

import fr.sqq.achatgroupe.domain.model.order.Order;

public interface GetOrderDetailsUseCase {

    Order getOrderDetails(Long orderId);
}
