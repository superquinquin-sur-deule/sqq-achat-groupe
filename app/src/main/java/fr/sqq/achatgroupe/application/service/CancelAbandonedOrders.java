package fr.sqq.achatgroupe.application.service;

import fr.sqq.achatgroupe.application.command.CancelOrderCommand;
import fr.sqq.achatgroupe.domain.model.order.Order;
import fr.sqq.achatgroupe.application.port.out.OrderRepository;
import fr.sqq.mediator.Mediator;
import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

@ApplicationScoped
public class CancelAbandonedOrders {

    private static final Logger LOG = Logger.getLogger(CancelAbandonedOrders.class);

    private final OrderRepository orderRepository;
    private final Mediator mediator;
    private final Duration abandonTimeout;

    public CancelAbandonedOrders(OrderRepository orderRepository, Mediator mediator,
                                  @ConfigProperty(name = "app.order.abandon-timeout", defaultValue = "30m") String abandonTimeout) {
        this.orderRepository = orderRepository;
        this.mediator = mediator;
        this.abandonTimeout = Duration.parse("PT" + abandonTimeout.toUpperCase());
    }

    @Scheduled(every = "5m")
    void cancelAbandoned() {
        Instant cutoff = Instant.now().minus(abandonTimeout);
        List<Order> abandonedOrders = orderRepository.findPendingOrdersBefore(cutoff);

        if (abandonedOrders.isEmpty()) {
            return;
        }

        LOG.infof("Annulation de %d commande(s) abandonnée(s) (timeout: %s)", abandonedOrders.size(), abandonTimeout);

        for (Order order : abandonedOrders) {
            try {
                mediator.send(new CancelOrderCommand(order.id()));
            } catch (Exception e) {
                LOG.errorf(e, "Erreur lors de l'annulation de la commande abandonnée %d", order.id());
            }
        }
    }
}
