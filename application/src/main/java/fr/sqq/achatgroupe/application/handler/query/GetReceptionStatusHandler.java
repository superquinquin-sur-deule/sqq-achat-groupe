package fr.sqq.achatgroupe.application.handler.query;

import fr.sqq.achatgroupe.application.port.out.OrderRepository;
import fr.sqq.achatgroupe.application.port.out.ProductRepository;
import fr.sqq.achatgroupe.application.port.out.ReceptionRepository;
import fr.sqq.achatgroupe.application.query.GetReceptionStatusQuery;
import fr.sqq.achatgroupe.application.query.GetReceptionStatusQuery.ReceptionLineStatus;
import fr.sqq.achatgroupe.application.query.GetReceptionStatusQuery.ReceptionStatusResult;
import fr.sqq.achatgroupe.application.query.GetReceptionStatusQuery.SupplierReceptionStatus;
import fr.sqq.achatgroupe.application.service.VariantIndex;
import fr.sqq.achatgroupe.domain.model.catalog.Product;
import fr.sqq.achatgroupe.domain.model.catalog.ProductVariant;
import fr.sqq.achatgroupe.domain.model.order.Order;
import fr.sqq.achatgroupe.domain.model.order.OrderItem;
import fr.sqq.achatgroupe.domain.model.reception.Reception;
import fr.sqq.mediator.QueryHandler;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@ApplicationScoped
public class GetReceptionStatusHandler implements QueryHandler<GetReceptionStatusQuery, ReceptionStatusResult> {

    private final ReceptionRepository receptionRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;

    public GetReceptionStatusHandler(ReceptionRepository receptionRepository,
                                     ProductRepository productRepository,
                                     OrderRepository orderRepository) {
        this.receptionRepository = receptionRepository;
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
    }

    @Override
    @Transactional
    public ReceptionStatusResult handle(GetReceptionStatusQuery query) {
        List<Product> products = productRepository.findAllByVenteId(query.venteId());
        VariantIndex variants = VariantIndex.of(products);

        Map<String, List<Product>> productsBySupplier = products.stream()
                .collect(Collectors.groupingBy(Product::supplier));

        List<Reception> receptions = receptionRepository.findAllByVenteId(query.venteId());
        Map<String, Reception> receptionBySupplier = receptions.stream()
                .collect(Collectors.toMap(Reception::supplier, Function.identity()));

        // Quantités commandées par variante : c'est l'unité réceptionnée.
        List<Order> confirmedOrders = orderRepository.findConfirmedByVenteId(query.venteId());
        Map<Long, Integer> orderedQuantities = new HashMap<>();
        for (Order order : confirmedOrders) {
            for (OrderItem item : order.items()) {
                orderedQuantities.merge(item.variantId(), item.quantity(), Integer::sum);
            }
        }

        // Seuls les fournisseurs dont une variante a été commandée doivent être réceptionnés.
        Set<String> suppliersWithOrders = products.stream()
                .filter(p -> p.variants().stream().anyMatch(v -> orderedQuantities.containsKey(v.id())))
                .map(Product::supplier)
                .collect(Collectors.toSet());

        List<SupplierReceptionStatus> suppliers = suppliersWithOrders.stream()
                .sorted()
                .map(supplier -> {
                    Reception reception = receptionBySupplier.get(supplier);
                    if (reception != null) {
                        List<ReceptionLineStatus> lines = reception.items().stream()
                                .map(item -> new ReceptionLineStatus(
                                        item.variantId(),
                                        variants.displayNameOr(item.variantId(), "Produit #" + item.productId()),
                                        item.orderedQuantity(), item.receivedQuantity(), item.shortage()))
                                .toList();
                        return new SupplierReceptionStatus(supplier, reception.id(),
                                reception.status().name(), lines);
                    }

                    // Fournisseur en attente : on liste les variantes commandées avec leur quantité.
                    List<ReceptionLineStatus> lines = new ArrayList<>();
                    for (Product product : productsBySupplier.getOrDefault(supplier, List.of())) {
                        for (ProductVariant variant : product.variants()) {
                            Integer ordered = orderedQuantities.get(variant.id());
                            if (ordered != null) {
                                lines.add(new ReceptionLineStatus(variant.id(),
                                        product.displayName(variant), ordered, 0, 0));
                            }
                        }
                    }
                    return new SupplierReceptionStatus(supplier, null, "PENDING", lines);
                })
                .toList();

        boolean allReceived = !suppliersWithOrders.isEmpty()
                && suppliersWithOrders.stream().allMatch(receptionBySupplier::containsKey);
        boolean hasAdjustments = confirmedOrders.stream().anyMatch(Order::hasAdjustments);
        return new ReceptionStatusResult(suppliers, allReceived, hasAdjustments);
    }
}
