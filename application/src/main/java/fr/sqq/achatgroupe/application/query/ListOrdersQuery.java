package fr.sqq.achatgroupe.application.query;

import fr.sqq.achatgroupe.domain.model.order.Order;
import fr.sqq.mediator.Query;

public record ListOrdersQuery(Long venteId, CursorPageRequest pageRequest, String searchName, Long timeSlotId) implements Query<CursorPage<Order>> {
}
