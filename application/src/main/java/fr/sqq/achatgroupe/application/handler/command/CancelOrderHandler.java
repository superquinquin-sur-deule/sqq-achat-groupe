package fr.sqq.achatgroupe.application.handler.command;

import fr.sqq.achatgroupe.application.command.CancelOrderCommand;
import fr.sqq.achatgroupe.application.port.out.OrderRepository;
import fr.sqq.achatgroupe.application.service.OrderStockService;
import fr.sqq.achatgroupe.domain.exception.OrderNotFoundException;
import fr.sqq.achatgroupe.domain.model.order.Order;
import fr.sqq.achatgroupe.domain.model.order.OrderStatus;
import fr.sqq.mediator.CommandHandler;
import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.util.Optional;

@ApplicationScoped
public class CancelOrderHandler implements CommandHandler<CancelOrderCommand, Void> {
    private final OrderRepository orderRepository;
    private final OrderStockService orderStockService;

    public CancelOrderHandler(OrderRepository orderRepository, OrderStockService orderStockService) {
        this.orderRepository = orderRepository;
        this.orderStockService = orderStockService;
    }

    @Override
    @Transactional
    public Void handle(CancelOrderCommand command) {

        Order order = findOrder(command)
                .orElseThrow(() -> new OrderNotFoundException(command.orderId()));

        if (order.status() == OrderStatus.CANCELLED) {
            Log.infof("Annulation ignorée : commande %s déjà annulée", command.orderId());
            return null;
        }

        if (order.status() == OrderStatus.PICKED_UP) {
            throw new IllegalStateException("Impossible d'annuler une commande déjà récupérée");
        }

        orderStockService.releaseStock(order);

        order.cancel(command.reason());
        orderRepository.save(order);

        Log.infof("Commande %s annulée — stocks restaurés, créneau libéré", command.orderId());
        return null;
    }

    private Optional<Order> findOrder(CancelOrderCommand command) {
        if (command.venteId() == null) {
            return orderRepository.findOrderById(command.orderId());
        }
        return orderRepository.findOrderByIdAndVenteId(command.orderId(), command.venteId());
    }
}
