package fr.sqq.achatgroupe.application.handler.query;

import fr.sqq.achatgroupe.application.port.out.OrderRepository;
import fr.sqq.achatgroupe.application.port.out.ProductRepository;
import fr.sqq.achatgroupe.application.query.GenerateSupplierOrderQuery;
import fr.sqq.achatgroupe.application.query.GenerateSupplierOrderQuery.SupplierOrderLine;
import fr.sqq.achatgroupe.domain.model.catalog.Product;
import fr.sqq.achatgroupe.domain.model.order.CustomerInfo;
import fr.sqq.achatgroupe.domain.model.order.Order;
import fr.sqq.achatgroupe.domain.model.order.OrderItem;
import fr.sqq.achatgroupe.domain.model.order.OrderNumber;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

class GenerateSupplierOrderHandlerTest {

    private OrderRepository orderRepository;
    private ProductRepository productRepository;
    private GenerateSupplierOrderHandler handler;

    @BeforeEach
    void setUp() {
        orderRepository = Mockito.mock(OrderRepository.class);
        productRepository = Mockito.mock(ProductRepository.class);
        handler = new GenerateSupplierOrderHandler(orderRepository, productRepository);
    }

    @Test
    void should_aggregate_quantities_by_product_and_sort_by_supplier_then_name() {
        Long venteId = 1L;

        Product tomatoes = new Product(10L, venteId, "Tomates", "Bio", new BigDecimal("3.50"), "Ferme A", 100, true, "TOM-001", "Legumes", "Ferme A", false);
        Product carrots = new Product(20L, venteId, "Carottes", "Bio", new BigDecimal("2.00"), "Ferme A", 100, true, "CAR-001", "Legumes", "Ferme A", false);
        Product cheese = new Product(30L, venteId, "Fromage", "Comté", new BigDecimal("8.00"), "Ferme B", 50, true, "FRO-001", "Fromage", "Ferme B", false);

        Order order1 = Order.create(venteId, OrderNumber.generate(),
                new CustomerInfo("Alice", "Dupont", "alice@test.com", "0601020304"), 1L,
                List.of(
                        OrderItem.create(10L, 3, new BigDecimal("3.50")),
                        OrderItem.create(30L, 1, new BigDecimal("8.00"))
                ));
        order1.markAsPaid();

        Order order2 = Order.create(venteId, OrderNumber.generate(),
                new CustomerInfo("Bob", "Martin", "bob@test.com", "0605060708"), 1L,
                List.of(
                        OrderItem.create(10L, 2, new BigDecimal("3.50")),
                        OrderItem.create(20L, 5, new BigDecimal("2.00"))
                ));
        order2.markAsPaid();

        when(orderRepository.findPaidByVenteId(venteId)).thenReturn(List.of(order1, order2));
        when(productRepository.findAllByVenteId(venteId)).thenReturn(List.of(tomatoes, carrots, cheese));

        List<SupplierOrderLine> result = handler.handle(new GenerateSupplierOrderQuery(venteId));

        assertThat(result).containsExactly(
                new SupplierOrderLine("Carottes", "Ferme A", 5),
                new SupplierOrderLine("Tomates", "Ferme A", 5),
                new SupplierOrderLine("Fromage", "Ferme B", 1)
        );
    }
}
