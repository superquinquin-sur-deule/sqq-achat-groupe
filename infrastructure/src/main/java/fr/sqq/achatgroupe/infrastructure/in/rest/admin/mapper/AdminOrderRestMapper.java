package fr.sqq.achatgroupe.infrastructure.in.rest.admin.mapper;

import fr.sqq.achatgroupe.application.service.VariantIndex;
import fr.sqq.achatgroupe.domain.model.order.Order;
import fr.sqq.achatgroupe.domain.model.order.OrderItem;
import fr.sqq.achatgroupe.domain.model.planning.TimeSlot;
import fr.sqq.achatgroupe.domain.model.vente.Vente;
import fr.sqq.achatgroupe.infrastructure.in.rest.admin.dto.AdminOrderDetailResponse;
import fr.sqq.achatgroupe.infrastructure.in.rest.admin.dto.AdminOrderResponse;
import org.mapstruct.Mapper;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Mapper(componentModel = "cdi")
public interface AdminOrderRestMapper {

    DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm");
    DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    /**
     * Libellé de retrait : le créneau quand la vente en utilise, sinon la plage de dates
     * configurée sur la vente (une commande sans créneau y est alors tout à fait normale).
     */
    private static String pickupLabel(Order order, Map<Long, TimeSlot> timeSlotsById, Vente vente) {
        TimeSlot slot = order.timeSlotId() != null ? timeSlotsById.get(order.timeSlotId()) : null;
        if (slot != null) {
            return slot.date() + " " + slot.startTime().format(TIME_FORMAT) + "-" + slot.endTime().format(TIME_FORMAT);
        }
        if (vente != null && !vente.usesTimeSlots()) {
            return vente.pickupStartDate().format(DATE_FORMAT) + " – " + vente.pickupEndDate().format(DATE_FORMAT);
        }
        return "Créneau inconnu";
    }

    default AdminOrderResponse toListResponse(Order order, Map<Long, TimeSlot> timeSlotsById, Vente vente) {
        String timeSlotLabel = pickupLabel(order, timeSlotsById, vente);
        return new AdminOrderResponse(
                order.id(),
                order.orderNumber().value(),
                order.customer().firstName(),
                order.customer().lastName(),
                order.customer().email(),
                timeSlotLabel,
                order.totalAmount().amount(),
                order.status().name(),
                order.createdAt(),
                order.cancellationReason()
        );
    }

    default List<AdminOrderResponse> toListResponse(List<Order> orders, Map<Long, TimeSlot> timeSlotsById, Vente vente) {
        return orders.stream()
                .map(order -> toListResponse(order, timeSlotsById, vente))
                .toList();
    }

    default AdminOrderDetailResponse toDetailResponse(Order order, Map<Long, TimeSlot> timeSlotsById, VariantIndex variants) {
        TimeSlot slot = order.timeSlotId() != null ? timeSlotsById.get(order.timeSlotId()) : null;
        AdminOrderDetailResponse.TimeSlotInfo timeSlotInfo = slot != null
                ? new AdminOrderDetailResponse.TimeSlotInfo(slot.date(), slot.startTime(), slot.endTime())
                : null;

        List<AdminOrderDetailResponse.OrderItemInfo> items = order.items().stream()
                .map(item -> toItemResponse(item, variants))
                .toList();

        return new AdminOrderDetailResponse(
                order.id(),
                order.orderNumber().value(),
                order.status().name(),
                order.customer().firstName(),
                order.customer().lastName(),
                order.customer().email(),
                order.customer().phone(),
                timeSlotInfo,
                items,
                order.totalAmount().amount(),
                order.createdAt()
        );
    }

    default AdminOrderDetailResponse.OrderItemInfo toItemResponse(OrderItem item, VariantIndex variants) {
        String productName = variants.displayNameOr(item.variantId(), "Produit #" + item.productId());
        return new AdminOrderDetailResponse.OrderItemInfo(
                productName,
                item.quantity(),
                item.unitPrice().amount(),
                item.subtotal().amount()
        );
    }
}
