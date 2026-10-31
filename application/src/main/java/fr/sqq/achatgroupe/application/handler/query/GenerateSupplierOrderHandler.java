package fr.sqq.achatgroupe.application.handler.query;

import fr.sqq.achatgroupe.application.port.out.OrderRepository;
import fr.sqq.achatgroupe.application.port.out.ProductRepository;
import fr.sqq.achatgroupe.application.query.GenerateSupplierOrderQuery;
import fr.sqq.achatgroupe.application.query.GenerateSupplierOrderQuery.SupplierOrderLine;
import fr.sqq.achatgroupe.application.service.VariantIndex;
import fr.sqq.achatgroupe.domain.model.catalog.Product;
import fr.sqq.achatgroupe.domain.model.catalog.ProductVariant;
import fr.sqq.achatgroupe.domain.model.order.Order;
import fr.sqq.achatgroupe.domain.model.order.OrderItem;
import fr.sqq.mediator.QueryHandler;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        List<Order> orders = orderRepository.findConfirmedByVenteId(query.venteId());
        VariantIndex variants = VariantIndex.of(productRepository.findAllByVenteId(query.venteId()));

        // Une ligne de bon fournisseur par variante : c'est elle qui porte la référence et le colisage.
        Map<Long, Integer> quantitiesByVariant = new HashMap<>();
        for (Order order : orders) {
            for (OrderItem item : order.items()) {
                quantitiesByVariant.merge(item.variantId(), item.quantity(), Integer::sum);
            }
        }

        return quantitiesByVariant.entrySet().stream()
                .map(entry -> {
                    VariantIndex.Entry indexed = variants.get(entry.getKey());
                    if (indexed == null) {
                        return null;
                    }
                    Product product = indexed.product();
                    ProductVariant variant = indexed.variant();
                    int qty = entry.getValue();
                    Integer colisage = variant.colisage();
                    Integer nombreColis = colisage != null ? (int) Math.ceil((double) qty / colisage) : null;
                    return new SupplierOrderLine(variant.reference(), product.displayName(variant), product.brand(),
                            product.supplier(), qty, colisage, nombreColis);
                })
                .filter(line -> line != null)
                .sorted(Comparator.comparing(SupplierOrderLine::supplier)
                        .thenComparing(SupplierOrderLine::productName))
                .toList();
    }
}
