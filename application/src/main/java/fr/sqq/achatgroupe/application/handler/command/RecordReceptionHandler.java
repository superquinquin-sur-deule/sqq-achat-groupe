package fr.sqq.achatgroupe.application.handler.command;

import fr.sqq.achatgroupe.application.command.RecordReceptionCommand;
import fr.sqq.achatgroupe.application.command.RecordReceptionCommand.ReceptionLineCommand;
import fr.sqq.achatgroupe.application.port.out.OrderRepository;
import fr.sqq.achatgroupe.application.port.out.ProductRepository;
import fr.sqq.achatgroupe.application.port.out.ReceptionRepository;
import fr.sqq.achatgroupe.application.service.VariantIndex;
import fr.sqq.achatgroupe.domain.model.catalog.Product;
import fr.sqq.achatgroupe.domain.model.order.Order;
import fr.sqq.achatgroupe.domain.model.order.OrderItem;
import fr.sqq.achatgroupe.domain.model.reception.Reception;
import fr.sqq.achatgroupe.domain.model.reception.ReceptionItem;
import fr.sqq.mediator.CommandHandler;
import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import org.jboss.logging.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@ApplicationScoped
public class RecordReceptionHandler implements CommandHandler<RecordReceptionCommand, Reception> {

    private static final Logger LOG = Logger.getLogger(RecordReceptionHandler.class);

    private final ReceptionRepository receptionRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    public RecordReceptionHandler(ReceptionRepository receptionRepository,
                                  OrderRepository orderRepository,
                                  ProductRepository productRepository) {
        this.receptionRepository = receptionRepository;
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
    }

    @Override
    @Transactional
    public Reception handle(RecordReceptionCommand command) {
        Log.infof("Recording reception for vente %d, fournisseur %s", command.venteId(), command.supplier());
        Optional<Reception> existingReception = receptionRepository.findByVenteIdAndSupplier(command.venteId(), command.supplier());

        List<Order> confirmedOrders = orderRepository.findConfirmedByVenteId(command.venteId());

        if (existingReception.isPresent()) {
            if (confirmedOrders.stream().anyMatch(Order::hasAdjustments)) {
                throw new IllegalStateException("Impossible de modifier les réceptions après l'application des ajustements");
            }
            receptionRepository.delete(existingReception.get());
            LOG.infof("Réception existante supprimée pour vente %d, fournisseur %s — mise à jour en cours",
                    command.venteId(), command.supplier());
        }

        List<Product> products = productRepository.findAllByVenteId(command.venteId());

        // La réception se fait variante par variante : c'est l'unité réellement commandée.
        VariantIndex supplierVariants = VariantIndex.of(products.stream()
                .filter(p -> command.supplier().equals(p.supplier()))
                .toList());

        Map<Long, Integer> orderedQuantities = new HashMap<>();
        for (Order order : confirmedOrders) {
            for (OrderItem item : order.items()) {
                if (supplierVariants.contains(item.variantId())) {
                    orderedQuantities.merge(item.variantId(), item.quantity(), Integer::sum);
                }
            }
        }

        Map<Long, Integer> receivedByVariant = command.lines().stream()
                .collect(Collectors.toMap(ReceptionLineCommand::variantId, ReceptionLineCommand::receivedQuantity));

        List<ReceptionItem> items = orderedQuantities.entrySet().stream()
                .map(entry -> ReceptionItem.create(
                        supplierVariants.get(entry.getKey()).product().id(),
                        entry.getKey(),
                        entry.getValue(),
                        receivedByVariant.getOrDefault(entry.getKey(), 0)))
                .toList();

        Reception reception = Reception.create(command.venteId(), command.supplier(), items);
        Reception saved = receptionRepository.save(reception);

        LOG.infof("Réception enregistrée pour vente %d, fournisseur %s : %d variantes",
                command.venteId(), command.supplier(), items.size());
        return saved;
    }
}
