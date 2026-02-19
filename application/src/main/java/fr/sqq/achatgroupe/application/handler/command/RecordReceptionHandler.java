package fr.sqq.achatgroupe.application.handler.command;

import fr.sqq.achatgroupe.application.command.RecordReceptionCommand;
import fr.sqq.achatgroupe.application.command.RecordReceptionCommand.ReceptionLineCommand;
import fr.sqq.achatgroupe.application.port.out.OrderRepository;
import fr.sqq.achatgroupe.application.port.out.ProductRepository;
import fr.sqq.achatgroupe.application.port.out.ReceptionRepository;
import fr.sqq.achatgroupe.application.port.out.RefundRepository;
import fr.sqq.achatgroupe.domain.model.catalog.Product;
import fr.sqq.achatgroupe.domain.model.order.Order;
import fr.sqq.achatgroupe.domain.model.order.OrderItem;
import fr.sqq.achatgroupe.domain.model.reception.Reception;
import fr.sqq.achatgroupe.domain.model.reception.ReceptionItem;
import fr.sqq.mediator.CommandHandler;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import org.jboss.logging.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@ApplicationScoped
public class RecordReceptionHandler implements CommandHandler<RecordReceptionCommand, Reception> {

    private static final Logger LOG = Logger.getLogger(RecordReceptionHandler.class);

    private final ReceptionRepository receptionRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final RefundRepository refundRepository;

    public RecordReceptionHandler(ReceptionRepository receptionRepository,
                                  OrderRepository orderRepository,
                                  ProductRepository productRepository,
                                  RefundRepository refundRepository) {
        this.receptionRepository = receptionRepository;
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.refundRepository = refundRepository;
    }

    @Override
    @Transactional
    public Reception handle(RecordReceptionCommand command) {
        Optional<Reception> existingReception = receptionRepository.findByVenteIdAndSupplier(command.venteId(), command.supplier());

        if (existingReception.isPresent()) {
            if (!refundRepository.findAllByVenteId(command.venteId()).isEmpty()) {
                throw new IllegalStateException("Impossible de modifier les réceptions après le lancement des remboursements");
            }
            receptionRepository.delete(existingReception.get());
            resetAdjustments(command.venteId());
            LOG.infof("Réception existante supprimée pour vente %d, fournisseur %s — mise à jour en cours",
                    command.venteId(), command.supplier());
        }

        List<Order> paidOrders = orderRepository.findPaidByVenteId(command.venteId());
        List<Product> products = productRepository.findAllByVenteId(command.venteId());

        Map<Long, Product> productsById = products.stream()
                .filter(p -> command.supplier().equals(p.supplier()))
                .collect(Collectors.toMap(Product::id, Function.identity()));

        Map<Long, Integer> orderedQuantities = new HashMap<>();
        for (Order order : paidOrders) {
            for (OrderItem item : order.items()) {
                if (productsById.containsKey(item.productId())) {
                    orderedQuantities.merge(item.productId(), item.quantity(), Integer::sum);
                }
            }
        }

        Map<Long, Integer> receivedByProduct = command.lines().stream()
                .collect(Collectors.toMap(ReceptionLineCommand::productId, ReceptionLineCommand::receivedQuantity));

        List<ReceptionItem> items = orderedQuantities.entrySet().stream()
                .map(entry -> ReceptionItem.create(
                        entry.getKey(),
                        entry.getValue(),
                        receivedByProduct.getOrDefault(entry.getKey(), 0)))
                .toList();

        Reception reception = Reception.create(command.venteId(), command.supplier(), items);
        Reception saved = receptionRepository.save(reception);

        LOG.infof("Réception enregistrée pour vente %d, fournisseur %s : %d produits",
                command.venteId(), command.supplier(), items.size());
        return saved;
    }

    private void resetAdjustments(Long venteId) {
        List<Order> paidOrders = orderRepository.findPaidByVenteId(venteId);
        for (Order order : paidOrders) {
            if (order.hasAdjustments()) {
                for (OrderItem item : order.items()) {
                    item.resetCancellation();
                }
                orderRepository.save(order);
            }
        }
    }
}
