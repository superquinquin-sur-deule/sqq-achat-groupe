package fr.sqq.achatgroupe.application.handler.command;

import fr.sqq.achatgroupe.application.command.ApplyShortageAdjustmentsCommand;
import fr.sqq.achatgroupe.application.port.out.OrderRepository;
import fr.sqq.achatgroupe.application.port.out.ProductRepository;
import fr.sqq.achatgroupe.application.port.out.ReceptionRepository;
import fr.sqq.achatgroupe.domain.model.catalog.Product;
import fr.sqq.achatgroupe.domain.model.order.Order;
import fr.sqq.achatgroupe.domain.model.order.OrderItem;
import fr.sqq.achatgroupe.domain.model.reception.Reception;
import fr.sqq.achatgroupe.domain.model.reception.ReceptionItem;
import fr.sqq.mediator.CommandHandler;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import org.jboss.logging.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@ApplicationScoped
public class ApplyShortageAdjustmentsHandler implements CommandHandler<ApplyShortageAdjustmentsCommand, Void> {

    private static final Logger LOG = Logger.getLogger(ApplyShortageAdjustmentsHandler.class);

    private final ReceptionRepository receptionRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    public ApplyShortageAdjustmentsHandler(ReceptionRepository receptionRepository,
                                           OrderRepository orderRepository,
                                           ProductRepository productRepository) {
        this.receptionRepository = receptionRepository;
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
    }

    @Override
    @Transactional
    public Void handle(ApplyShortageAdjustmentsCommand command) {
        List<Reception> receptions = receptionRepository.findAllByVenteId(command.venteId());

        List<Order> paidOrders = orderRepository.findPaidByVenteId(command.venteId());
        List<Product> products = productRepository.findAllByVenteId(command.venteId());

        // Only suppliers that have ordered products need a reception
        Map<Long, Integer> orderedQtys = new HashMap<>();
        for (Order order : paidOrders) {
            for (OrderItem item : order.items()) {
                orderedQtys.merge(item.productId(), item.quantity(), Integer::sum);
            }
        }

        Set<String> suppliersWithOrders = products.stream()
                .filter(p -> orderedQtys.containsKey(p.id()))
                .map(Product::supplier)
                .collect(Collectors.toSet());

        Set<String> receivedSuppliers = receptions.stream()
                .map(Reception::supplier)
                .collect(Collectors.toSet());

        if (!receivedSuppliers.containsAll(suppliersWithOrders)) {
            throw new IllegalStateException("Toutes les réceptions doivent être enregistrées avant d'appliquer les ajustements");
        }

        Map<Long, Integer> shortageByProduct = new HashMap<>();
        for (Reception reception : receptions) {
            for (ReceptionItem item : reception.items()) {
                if (item.shortage() > 0) {
                    shortageByProduct.put(item.productId(), item.shortage());
                }
            }
        }

        if (shortageByProduct.isEmpty()) {
            LOG.info("Aucun manque détecté, pas d'ajustement nécessaire");
            return null;
        }

        // Subtract already-cancelled quantities to make idempotent
        for (Order order : paidOrders) {
            for (OrderItem item : order.items()) {
                if (item.cancelledQuantity() > 0 && shortageByProduct.containsKey(item.productId())) {
                    shortageByProduct.merge(item.productId(), -item.cancelledQuantity(), Integer::sum);
                }
            }
        }
        shortageByProduct.values().removeIf(v -> v <= 0);

        if (shortageByProduct.isEmpty()) {
            LOG.info("Ajustements déjà appliqués, aucune action nécessaire");
            return null;
        }

        List<Order> shuffled = new ArrayList<>(paidOrders);
        Collections.shuffle(shuffled);

        for (Map.Entry<Long, Integer> entry : shortageByProduct.entrySet()) {
            Long productId = entry.getKey();
            int remainingShortage = entry.getValue();

            for (Order order : shuffled) {
                if (remainingShortage <= 0) break;

                for (OrderItem item : order.items()) {
                    if (item.productId().equals(productId) && item.effectiveQuantity() > 0) {
                        int toCancel = Math.min(item.effectiveQuantity(), remainingShortage);
                        item.cancelQuantity(toCancel);
                        remainingShortage -= toCancel;
                        break;
                    }
                }
            }
        }

        for (Order order : shuffled) {
            if (order.hasAdjustments()) {
                if (order.isFullyCancelled()) {
                    order.cancel();
                }
                orderRepository.save(order);
            }
        }

        LOG.infof("Ajustements de rupture appliqués pour la vente %d", command.venteId());
        return null;
    }
}
