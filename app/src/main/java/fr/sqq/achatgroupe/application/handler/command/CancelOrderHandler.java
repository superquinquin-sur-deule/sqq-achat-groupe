package fr.sqq.achatgroupe.application.handler.command;

import fr.sqq.achatgroupe.application.command.CancelOrderCommand;
import fr.sqq.achatgroupe.application.port.out.OrderRepository;
import fr.sqq.achatgroupe.application.port.out.ProductRepository;
import fr.sqq.achatgroupe.application.port.out.TimeSlotRepository;
import fr.sqq.achatgroupe.domain.exception.OrderNotFoundException;
import fr.sqq.achatgroupe.domain.exception.ProductNotFoundException;
import fr.sqq.achatgroupe.domain.exception.TimeSlotNotFoundException;
import fr.sqq.achatgroupe.domain.model.catalog.Product;
import fr.sqq.achatgroupe.domain.model.catalog.ProductId;
import fr.sqq.achatgroupe.domain.model.order.Order;
import fr.sqq.achatgroupe.domain.model.order.OrderItem;
import fr.sqq.achatgroupe.domain.model.order.OrderStatus;
import fr.sqq.achatgroupe.domain.model.planning.TimeSlot;
import fr.sqq.mediator.CommandHandler;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import org.jboss.logging.Logger;

@ApplicationScoped
public class CancelOrderHandler implements CommandHandler<CancelOrderCommand, Void> {

    private static final Logger LOG = Logger.getLogger(CancelOrderHandler.class);

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final TimeSlotRepository timeSlotRepository;

    public CancelOrderHandler(OrderRepository orderRepository, ProductRepository productRepository,
                              TimeSlotRepository timeSlotRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.timeSlotRepository = timeSlotRepository;
    }

    @Override
    @Transactional
    public Void handle(CancelOrderCommand command) {
        Order order = orderRepository.findOrderById(command.orderId())
                .orElseThrow(() -> new OrderNotFoundException(command.orderId()));

        if (order.status() == OrderStatus.CANCELLED || order.status() == OrderStatus.PAID) {
            LOG.infof("Annulation ignorée : commande %s déjà en statut %s", command.orderId(), order.status());
            return null;
        }

        for (OrderItem item : order.items()) {
            Product product = productRepository.findById(new ProductId(item.productId()))
                    .orElseThrow(() -> new ProductNotFoundException(new ProductId(item.productId())));
            product.incrementStock(item.quantity());
            productRepository.save(product);
        }

        TimeSlot slot = timeSlotRepository.findSlotById(order.timeSlotId())
                .orElseThrow(() -> new TimeSlotNotFoundException(order.timeSlotId()));
        slot.releaseOnePlace();
        timeSlotRepository.save(slot);

        order.cancel();
        orderRepository.save(order);

        LOG.infof("Commande %s annulée — stocks restaurés, créneau libéré", command.orderId());
        return null;
    }
}
