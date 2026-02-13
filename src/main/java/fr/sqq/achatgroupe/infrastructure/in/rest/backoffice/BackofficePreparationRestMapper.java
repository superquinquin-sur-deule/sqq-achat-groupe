package fr.sqq.achatgroupe.infrastructure.in.rest.backoffice;

import fr.sqq.achatgroupe.application.port.in.GeneratePreparationListUseCase.PreparationItem;
import fr.sqq.achatgroupe.application.port.in.GeneratePreparationListUseCase.PreparationOrder;
import fr.sqq.achatgroupe.infrastructure.in.rest.dto.PreparationItemResponse;
import fr.sqq.achatgroupe.infrastructure.in.rest.dto.PreparationOrderResponse;

import java.util.List;

public class BackofficePreparationRestMapper {

    private BackofficePreparationRestMapper() {
    }

    public static List<PreparationOrderResponse> toResponse(List<PreparationOrder> orders) {
        return orders.stream()
                .map(BackofficePreparationRestMapper::toResponse)
                .toList();
    }

    public static PreparationOrderResponse toResponse(PreparationOrder order) {
        List<PreparationItemResponse> items = order.items().stream()
                .map(item -> new PreparationItemResponse(item.productName(), item.quantity()))
                .toList();
        return new PreparationOrderResponse(order.orderId(), order.orderNumber(), order.customerName(),
                order.timeSlotLabel(), items);
    }
}
