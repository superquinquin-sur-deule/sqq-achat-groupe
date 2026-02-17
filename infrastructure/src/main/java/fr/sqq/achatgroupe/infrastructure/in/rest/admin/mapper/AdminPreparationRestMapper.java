package fr.sqq.achatgroupe.infrastructure.in.rest.admin.mapper;

import fr.sqq.achatgroupe.application.query.GeneratePreparationListQuery.PreparationOrder;
import fr.sqq.achatgroupe.infrastructure.in.rest.common.dto.PreparationItemResponse;
import fr.sqq.achatgroupe.infrastructure.in.rest.common.dto.PreparationOrderResponse;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "cdi")
public interface AdminPreparationRestMapper {

    default List<PreparationOrderResponse> toResponse(List<PreparationOrder> orders) {
        return orders.stream()
                .map(this::toResponse)
                .toList();
    }

    default PreparationOrderResponse toResponse(PreparationOrder order) {
        List<PreparationItemResponse> items = order.items().stream()
                .map(item -> new PreparationItemResponse(item.productName(), item.supplier(), item.quantity()))
                .toList();
        return new PreparationOrderResponse(order.orderId(), order.orderNumber(), order.customerFirstName(),
                order.customerLastName(), order.customerEmail(), order.customerPhone(), order.timeSlotLabel(), items);
    }
}
