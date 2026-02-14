package fr.sqq.achatgroupe.application.handler.query;

import fr.sqq.achatgroupe.application.port.out.OrderRepository;
import fr.sqq.achatgroupe.application.port.out.ProductRepository;
import fr.sqq.achatgroupe.application.query.GenerateSupplierOrderQuery;
import fr.sqq.achatgroupe.application.query.GenerateSupplierOrderQuery.SupplierOrderLine;
import fr.sqq.achatgroupe.domain.model.catalog.Product;
import fr.sqq.achatgroupe.domain.model.order.Order;
import fr.sqq.achatgroupe.domain.model.order.OrderItem;
import fr.sqq.mediator.QueryHandler;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@ApplicationScoped
public class GenerateSupplierOrderHandler implements QueryHandler<GenerateSupplierOrderQuery, List<SupplierOrderLine>> {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    public GenerateSupplierOrderHandler(OrderRepository orderRepository, ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
    }

    @Override
    @Transactional
    public List<SupplierOrderLine> handle(GenerateSupplierOrderQuery query) {
        List<Order> orders = orderRepository.findPaidByVenteId(query.venteId());

        List<Product> products = productRepository.findAllByVenteId(query.venteId());
        Map<Long, Product> productsById = products.stream()
                .collect(Collectors.toMap(Product::id, Function.identity()));

        Map<Long, Integer> quantitiesByProduct = new HashMap<>();
        for (Order order : orders) {
            for (OrderItem item : order.items()) {
                quantitiesByProduct.merge(item.productId(), item.quantity(), Integer::sum);
            }
        }

        return quantitiesByProduct.entrySet().stream()
                .map(entry -> {
                    Product product = productsById.get(entry.getKey());
                    return new SupplierOrderLine(product.name(), product.supplier(), entry.getValue());
                })
                .sorted(Comparator.comparing(SupplierOrderLine::supplier)
                        .thenComparing(SupplierOrderLine::productName))
                .toList();
    }
}
