package fr.sqq.achatgroupe.application.handler.query;

import fr.sqq.achatgroupe.application.port.out.OrderRepository;
import fr.sqq.achatgroupe.application.port.out.ProductRepository;
import fr.sqq.achatgroupe.application.query.GenerateSupplierOrderQuery;
import fr.sqq.achatgroupe.application.query.GenerateSupplierOrderQuery.SupplierOrderLine;
import fr.sqq.achatgroupe.domain.model.catalog.Product;
import fr.sqq.achatgroupe.domain.model.catalog.ProductVariant;
import fr.sqq.achatgroupe.domain.model.order.CustomerInfo;
import fr.sqq.achatgroupe.domain.model.order.Order;
import fr.sqq.achatgroupe.domain.model.order.OrderItem;
import fr.sqq.achatgroupe.domain.model.order.OrderNumber;
import fr.sqq.achatgroupe.domain.model.shared.Money;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

class GenerateSupplierOrderHandlerTest {

    private static final Long VENTE_ID = 1L;

    private OrderRepository orderRepository;
    private ProductRepository productRepository;
    private GenerateSupplierOrderHandler handler;

    @BeforeEach
    void setUp() {
        orderRepository = Mockito.mock(OrderRepository.class);
        productRepository = Mockito.mock(ProductRepository.class);
        handler = new GenerateSupplierOrderHandler(orderRepository, productRepository);
    }

    /** Produit simple : une variante unique au libellé vide. */
    private static Product simpleProduct(Long productId, Long variantId, String name, String reference,
                                         String supplier, String category, BigDecimal prixHt, Integer colisage) {
        return new Product(productId, VENTE_ID, name, "Bio", BigDecimal.ZERO, supplier, true,
                category, supplier, null, false,
                List.of(new ProductVariant(variantId, productId, "", reference, Money.eur(prixHt), 100, colisage, 0)));
    }

    @Test
    void should_aggregate_quantities_by_product_and_sort_by_supplier_then_name() {
        Product tomatoes = simpleProduct(10L, 110L, "Tomates", "TOM-001", "Ferme A", "Legumes", new BigDecimal("3.50"), null);
        Product carrots = simpleProduct(20L, 120L, "Carottes", "CAR-001", "Ferme A", "Legumes", new BigDecimal("2.00"), null);
        Product cheese = simpleProduct(30L, 130L, "Fromage", "FRO-001", "Ferme B", "Fromage", new BigDecimal("8.00"), null);

        Order order1 = Order.create(VENTE_ID, OrderNumber.generate(),
                new CustomerInfo("Alice", "Dupont", "alice@test.com", "0601020304"), 1L,
                List.of(
                        OrderItem.create(10L, 110L, 3, Money.eur(new BigDecimal("3.50"))),
                        OrderItem.create(30L, 130L, 1, Money.eur(new BigDecimal("8.00")))
                ), null);

        Order order2 = Order.create(VENTE_ID, OrderNumber.generate(),
                new CustomerInfo("Bob", "Martin", "bob@test.com", "0605060708"), 1L,
                List.of(
                        OrderItem.create(10L, 110L, 2, Money.eur(new BigDecimal("3.50"))),
                        OrderItem.create(20L, 120L, 5, Money.eur(new BigDecimal("2.00")))
                ), null);

        when(orderRepository.findConfirmedByVenteId(VENTE_ID)).thenReturn(List.of(order1, order2));
        when(productRepository.findAllByVenteId(VENTE_ID)).thenReturn(List.of(tomatoes, carrots, cheese));

        List<SupplierOrderLine> result = handler.handle(new GenerateSupplierOrderQuery(VENTE_ID));

        assertThat(result).containsExactly(
                new SupplierOrderLine("CAR-001", "Carottes", "Ferme A", "Ferme A", 5, null, null),
                new SupplierOrderLine("TOM-001", "Tomates", "Ferme A", "Ferme A", 5, null, null),
                new SupplierOrderLine("FRO-001", "Fromage", "Ferme B", "Ferme B", 1, null, null)
        );
    }

    @Test
    void should_calculate_nombre_colis_when_colisage_is_set() {
        Product tomatoes = simpleProduct(10L, 110L, "Tomates", "TOM-001", "Ferme A", "Legumes", new BigDecimal("3.50"), 6);

        Order order1 = Order.create(VENTE_ID, OrderNumber.generate(),
                new CustomerInfo("Alice", "Dupont", "alice@test.com", "0601020304"), 1L,
                List.of(OrderItem.create(10L, 110L, 10, Money.eur(new BigDecimal("3.50")))), null);

        when(orderRepository.findConfirmedByVenteId(VENTE_ID)).thenReturn(List.of(order1));
        when(productRepository.findAllByVenteId(VENTE_ID)).thenReturn(List.of(tomatoes));

        List<SupplierOrderLine> result = handler.handle(new GenerateSupplierOrderQuery(VENTE_ID));

        assertThat(result).containsExactly(
                new SupplierOrderLine("TOM-001", "Tomates", "Ferme A", "Ferme A", 10, 6, 2)
        );
    }

    @Test
    void should_emit_one_line_per_variant_with_its_own_reference_and_colisage() {
        Product sapin = new Product(40L, VENTE_ID, "Sapin Nordmann", "Coupe frais", BigDecimal.ZERO,
                "Pepiniere", true, "Decoration", "Pepiniere", null, false,
                List.of(
                        new ProductVariant(140L, 40L, "150cm", "SAP-150", Money.eur(new BigDecimal("29.90")), 20, 5, 0),
                        new ProductVariant(141L, 40L, "200cm", "SAP-200", Money.eur(new BigDecimal("39.90")), 15, 3, 1)
                ));

        Order order = Order.create(VENTE_ID, OrderNumber.generate(),
                new CustomerInfo("Alice", "Dupont", "alice@test.com", "0601020304"), 1L,
                List.of(
                        OrderItem.create(40L, 140L, 7, Money.eur(new BigDecimal("29.90"))),
                        OrderItem.create(40L, 141L, 4, Money.eur(new BigDecimal("39.90")))
                ), null);

        when(orderRepository.findConfirmedByVenteId(VENTE_ID)).thenReturn(List.of(order));
        when(productRepository.findAllByVenteId(VENTE_ID)).thenReturn(List.of(sapin));

        List<SupplierOrderLine> result = handler.handle(new GenerateSupplierOrderQuery(VENTE_ID));

        assertThat(result).containsExactly(
                new SupplierOrderLine("SAP-150", "Sapin Nordmann — 150cm", "Pepiniere", "Pepiniere", 7, 5, 2),
                new SupplierOrderLine("SAP-200", "Sapin Nordmann — 200cm", "Pepiniere", "Pepiniere", 4, 3, 2)
        );
    }
}
