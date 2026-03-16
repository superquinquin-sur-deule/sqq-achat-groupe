package fr.sqq.achatgroupe.application.handler.command;

import fr.sqq.achatgroupe.application.command.CancelOrderCommand;
import fr.sqq.achatgroupe.application.port.out.OrderRepository;
import fr.sqq.achatgroupe.application.port.out.PaymentRepository;
import fr.sqq.achatgroupe.application.service.OrderStockService;
import fr.sqq.achatgroupe.domain.exception.OrderNotFoundException;
import fr.sqq.achatgroupe.domain.model.order.Order;
import fr.sqq.achatgroupe.domain.model.order.OrderStatus;
import fr.sqq.achatgroupe.domain.model.payment.PaymentStatus;
import fr.sqq.mediator.CommandHandler;
import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class CancelOrderHandler implements CommandHandler<CancelOrderCommand, Void> {
    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final OrderStockService orderStockService;

    public CancelOrderHandler(OrderRepository orderRepository, PaymentRepository paymentRepository,
                              OrderStockService orderStockService) {
        this.orderRepository = orderRepository;
        this.paymentRepository = paymentRepository;
        this.orderStockService = orderStockService;
    }

    @Override
    @Transactional
    public Void handle(CancelOrderCommand command) {

        Order order = orderRepository.findOrderById(command.orderId())
                .orElseThrow(() -> new OrderNotFoundException(command.orderId()));

        if (order.status() == OrderStatus.CANCELLED || order.status() == OrderStatus.PAID) {
            Log.infof("Annulation ignorée : commande %s déjà en statut %s", command.orderId(), order.status());
            return null;
        }

        boolean hasActivePayment = paymentRepository.findByOrderIdForUpdate(order.id())
                .map(payment -> payment.status() != PaymentStatus.FAILED)
                .orElse(false);

        if (hasActivePayment) {
            Log.infof("Annulation ignorée : commande %s a un paiement en cours", command.orderId());
            return null;
        }

        orderStockService.releaseStock(order);

        order.cancel(command.reason());
        orderRepository.save(order);

        Log.infof("Commande %s annulée — stocks restaurés, créneau libéré", command.orderId());
        return null;
    }
}
