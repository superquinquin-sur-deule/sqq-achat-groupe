package fr.sqq.achatgroupe.application.handler.query;

import fr.sqq.achatgroupe.application.port.out.OrderRepository;
import fr.sqq.achatgroupe.application.port.out.RefundRepository;
import fr.sqq.achatgroupe.application.query.GetRefundPreviewQuery;
import fr.sqq.achatgroupe.application.query.GetRefundPreviewQuery.RefundOrderItem;
import fr.sqq.achatgroupe.application.query.GetRefundPreviewQuery.RefundPreviewResult;
import fr.sqq.achatgroupe.domain.model.order.Order;
import fr.sqq.achatgroupe.domain.model.payment.Refund;
import fr.sqq.mediator.QueryHandler;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@ApplicationScoped
public class GetRefundPreviewHandler implements QueryHandler<GetRefundPreviewQuery, RefundPreviewResult> {

    private final OrderRepository orderRepository;
    private final RefundRepository refundRepository;

    public GetRefundPreviewHandler(OrderRepository orderRepository, RefundRepository refundRepository) {
        this.orderRepository = orderRepository;
        this.refundRepository = refundRepository;
    }

    @Override
    @Transactional
    public RefundPreviewResult handle(GetRefundPreviewQuery query) {
        List<Order> paidOrders = orderRepository.findPaidByVenteId(query.venteId());
        List<Order> adjustedOrders = paidOrders.stream()
                .filter(o -> o.refundAmount().compareTo(BigDecimal.ZERO) > 0)
                .toList();

        Map<UUID, Refund> refundsByOrderId = refundRepository.findAllByVenteId(query.venteId()).stream()
                .collect(Collectors.toMap(Refund::orderId, r -> r, (a, b) -> b));

        List<RefundOrderItem> orders = adjustedOrders.stream()
                .map(order -> {
                    Refund refund = refundsByOrderId.get(order.id());
                    String refundStatus = refund != null ? refund.status().name() : "PENDING";
                    String customerName = order.customer().firstName() + " " + order.customer().lastName();
                    return new RefundOrderItem(
                            order.id(),
                            order.orderNumber().value(),
                            customerName,
                            order.totalAmount(),
                            order.effectiveTotalAmount(),
                            order.refundAmount(),
                            refundStatus);
                })
                .toList();

        return new RefundPreviewResult(orders);
    }
}
