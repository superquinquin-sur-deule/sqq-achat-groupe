package fr.sqq.achatgroupe.application.handler.query;

import fr.sqq.achatgroupe.application.port.out.OrderRepository;
import fr.sqq.achatgroupe.application.port.out.ProductRepository;
import fr.sqq.achatgroupe.application.port.out.ReceptionRepository;
import fr.sqq.achatgroupe.application.port.out.RefundRepository;
import fr.sqq.achatgroupe.application.query.GetReceptionStatusQuery;
import fr.sqq.achatgroupe.application.query.GetReceptionStatusQuery.ReceptionLineStatus;
import fr.sqq.achatgroupe.application.query.GetReceptionStatusQuery.ReceptionStatusResult;
import fr.sqq.achatgroupe.application.query.GetReceptionStatusQuery.SupplierReceptionStatus;
import fr.sqq.achatgroupe.domain.model.catalog.Product;
import fr.sqq.achatgroupe.domain.model.order.Order;
import fr.sqq.achatgroupe.domain.model.order.OrderItem;
import fr.sqq.achatgroupe.domain.model.reception.Reception;
import fr.sqq.mediator.QueryHandler;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

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
    private final RefundRepository refundRepository;

    public GetReceptionStatusHandler(ReceptionRepository receptionRepository,
                                     ProductRepository productRepository,
                                     OrderRepository orderRepository,
                                     RefundRepository refundRepository) {
        this.receptionRepository = receptionRepository;
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
        this.refundRepository = refundRepository;
    }

    @Override
    @Transactional
    public ReceptionStatusResult handle(GetReceptionStatusQuery query) {
        List<Product> products = productRepository.findAllByVenteId(query.venteId());
        Map<Long, Product> productsById = products.stream()
                .collect(Collectors.toMap(Product::id, Function.identity()));

        Map<String, List<Product>> productsBySupplier = products.stream()
                .collect(Collectors.groupingBy(Product::supplier));

        List<Reception> receptions = receptionRepository.findAllByVenteId(query.venteId());
        Map<String, Reception> receptionBySupplier = receptions.stream()
                .collect(Collectors.toMap(Reception::supplier, Function.identity()));

        // Compute ordered quantities per product from paid orders
        List<Order> paidOrders = orderRepository.findPaidByVenteId(query.venteId());
        Map<Long, Integer> orderedQuantities = new HashMap<>();
        for (Order order : paidOrders) {
            for (OrderItem item : order.items()) {
                orderedQuantities.merge(item.productId(), item.quantity(), Integer::sum);
            }
        }

        // Only suppliers that have ordered products need a reception
        Set<String> suppliersWithOrders = products.stream()
                .filter(p -> orderedQuantities.containsKey(p.id()))
                .map(Product::supplier)
                .collect(Collectors.toSet());

        List<SupplierReceptionStatus> suppliers = suppliersWithOrders.stream()
                .sorted()
                .map(supplier -> {
                    Reception reception = receptionBySupplier.get(supplier);
                    if (reception != null) {
                        List<ReceptionLineStatus> lines = reception.items().stream()
                                .map(item -> {
                                    Product p = productsById.get(item.productId());
                                    String name = p != null ? p.name() : "Produit #" + item.productId();
                                    return new ReceptionLineStatus(
                                            item.productId(), name,
                                            item.orderedQuantity(), item.receivedQuantity(), item.shortage());
                                })
                                .toList();
                        return new SupplierReceptionStatus(supplier, reception.id(),
                                reception.status().name(), lines);
                    } else {
                        // For pending suppliers, provide product lines with ordered quantities
                        List<ReceptionLineStatus> lines = productsBySupplier.getOrDefault(supplier, List.of()).stream()
                                .filter(p -> orderedQuantities.containsKey(p.id()))
                                .map(p -> new ReceptionLineStatus(
                                        p.id(), p.name(),
                                        orderedQuantities.getOrDefault(p.id(), 0), 0, 0))
                                .toList();
                        return new SupplierReceptionStatus(supplier, null, "PENDING", lines);
                    }
                })
                .toList();

        boolean allReceived = !suppliersWithOrders.isEmpty()
                && suppliersWithOrders.stream().allMatch(receptionBySupplier::containsKey);
        boolean hasRefunds = !refundRepository.findAllByVenteId(query.venteId()).isEmpty();
        return new ReceptionStatusResult(suppliers, allReceived, hasRefunds);
    }
}
