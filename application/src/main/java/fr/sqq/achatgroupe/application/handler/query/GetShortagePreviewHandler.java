package fr.sqq.achatgroupe.application.handler.query;

import fr.sqq.achatgroupe.application.port.out.OrderRepository;
import fr.sqq.achatgroupe.application.port.out.ProductRepository;
import fr.sqq.achatgroupe.application.port.out.ReceptionRepository;
import fr.sqq.achatgroupe.application.query.GetShortagePreviewQuery;
import fr.sqq.achatgroupe.application.query.GetShortagePreviewQuery.ShortageItem;
import fr.sqq.achatgroupe.application.query.GetShortagePreviewQuery.ShortagePreviewResult;
import fr.sqq.achatgroupe.domain.model.catalog.Product;
import fr.sqq.achatgroupe.domain.model.order.Order;
import fr.sqq.achatgroupe.domain.model.order.OrderItem;
import fr.sqq.achatgroupe.domain.model.reception.Reception;
import fr.sqq.achatgroupe.domain.model.reception.ReceptionItem;
import fr.sqq.mediator.QueryHandler;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@ApplicationScoped
public class GetShortagePreviewHandler implements QueryHandler<GetShortagePreviewQuery, ShortagePreviewResult> {

    private final ReceptionRepository receptionRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    public GetShortagePreviewHandler(ReceptionRepository receptionRepository,
                                     OrderRepository orderRepository,
                                     ProductRepository productRepository) {
        this.receptionRepository = receptionRepository;
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
    }

    @Override
    @Transactional
    public ShortagePreviewResult handle(GetShortagePreviewQuery query) {
        List<Reception> receptions = receptionRepository.findAllByVenteId(query.venteId());
        List<Product> products = productRepository.findAllByVenteId(query.venteId());
        Map<Long, Product> productsById = products.stream()
                .collect(Collectors.toMap(Product::id, Function.identity()));

        Map<Long, ReceptionItem> shortageItems = receptions.stream()
                .flatMap(r -> r.items().stream())
                .filter(item -> item.shortage() > 0)
                .collect(Collectors.toMap(ReceptionItem::productId, Function.identity()));

        if (shortageItems.isEmpty()) {
            return new ShortagePreviewResult(List.of());
        }

        List<Order> paidOrders = orderRepository.findPaidByVenteId(query.venteId());

        Map<Long, Long> affectedOrderCounts = shortageItems.keySet().stream()
                .collect(Collectors.toMap(
                        productId -> productId,
                        productId -> paidOrders.stream()
                                .filter(order -> order.items().stream()
                                        .anyMatch(item -> item.productId().equals(productId)))
                                .count()));

        List<ShortageItem> items = shortageItems.entrySet().stream()
                .map(entry -> {
                    Long productId = entry.getKey();
                    ReceptionItem ri = entry.getValue();
                    Product product = productsById.get(productId);
                    String name = product != null ? product.name() : "Produit #" + productId;
                    String supplier = product != null ? product.supplier() : "Inconnu";
                    return new ShortageItem(productId, name, supplier,
                            ri.orderedQuantity(), ri.receivedQuantity(), ri.shortage(),
                            affectedOrderCounts.getOrDefault(productId, 0L).intValue());
                })
                .toList();

        return new ShortagePreviewResult(items);
    }
}
