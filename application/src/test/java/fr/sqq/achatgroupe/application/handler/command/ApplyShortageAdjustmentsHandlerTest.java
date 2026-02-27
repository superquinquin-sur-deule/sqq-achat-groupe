package fr.sqq.achatgroupe.application.handler.command;

import fr.sqq.achatgroupe.application.command.ApplyShortageAdjustmentsCommand;
import fr.sqq.achatgroupe.application.port.out.OrderRepository;
import fr.sqq.achatgroupe.application.port.out.ProductRepository;
import fr.sqq.achatgroupe.application.port.out.ReceptionRepository;
import fr.sqq.achatgroupe.domain.model.catalog.Product;
import fr.sqq.achatgroupe.domain.model.order.CustomerInfo;
import fr.sqq.achatgroupe.domain.model.order.Order;
import fr.sqq.achatgroupe.domain.model.order.OrderItem;
import fr.sqq.achatgroupe.domain.model.order.OrderNumber;
import fr.sqq.achatgroupe.domain.model.reception.Reception;
import fr.sqq.achatgroupe.domain.model.reception.ReceptionItem;
import fr.sqq.achatgroupe.domain.model.shared.Money;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ApplyShortageAdjustmentsHandlerTest {

    private ReceptionRepository receptionRepository;
    private OrderRepository orderRepository;
    private ProductRepository productRepository;
    private ApplyShortageAdjustmentsHandler handler;

    private static final Long VENTE_ID = 1L;
    private static final Long PRODUCT_A = 10L;
    private static final Long PRODUCT_B = 20L;

    @BeforeEach
    void setUp() {
        receptionRepository = Mockito.mock(ReceptionRepository.class);
        orderRepository = Mockito.mock(OrderRepository.class);
        productRepository = Mockito.mock(ProductRepository.class);
        handler = new ApplyShortageAdjustmentsHandler(receptionRepository, orderRepository, productRepository);
    }

    @Test
    void should_be_idempotent_when_called_twice() {
        Product productA = new Product(PRODUCT_A, VENTE_ID, "Tomates", "Bio", Money.eur(new BigDecimal("3.50")), BigDecimal.ZERO, "Ferme A", 100, true, "TOM-001", "Legumes", "Ferme A", false);

        // Order with 4x Product A
        Order order = Order.create(VENTE_ID, OrderNumber.generate(),
                new CustomerInfo("Alice", "Dupont", "alice@test.com", "0601020304"), 1L,
                List.of(OrderItem.create(PRODUCT_A, 4, Money.eur(new BigDecimal("3.50")))));
        order.markAsPaid();

        // Reception: ordered 4, received 3 → shortage = 1
        Reception reception = Reception.create(VENTE_ID, "Ferme A",
                List.of(ReceptionItem.create(PRODUCT_A, 4, 3)));

        when(receptionRepository.findAllByVenteId(VENTE_ID)).thenReturn(List.of(reception));
        when(orderRepository.findPaidByVenteId(VENTE_ID)).thenReturn(List.of(order));
        when(productRepository.findAllByVenteId(VENTE_ID)).thenReturn(List.of(productA));
        when(orderRepository.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));

        // First call: should cancel 1 unit
        handler.handle(new ApplyShortageAdjustmentsCommand(VENTE_ID));

        assertThat(order.items().get(0).cancelledQuantity()).isEqualTo(1);
        assertThat(order.items().get(0).effectiveQuantity()).isEqualTo(3);

        // Second call: should be a no-op
        handler.handle(new ApplyShortageAdjustmentsCommand(VENTE_ID));

        assertThat(order.items().get(0).cancelledQuantity()).isEqualTo(1);
        assertThat(order.items().get(0).effectiveQuantity()).isEqualTo(3);
    }

    @Test
    void should_not_save_orders_when_already_adjusted() {
        Product productA = new Product(PRODUCT_A, VENTE_ID, "Tomates", "Bio", Money.eur(new BigDecimal("3.50")), BigDecimal.ZERO, "Ferme A", 100, true, "TOM-001", "Legumes", "Ferme A", false);

        // Order already adjusted: 4 ordered, 1 cancelled
        OrderItem adjustedItem = new OrderItem(1L, PRODUCT_A, 4, Money.eur(new BigDecimal("3.50")), 1);
        Order order = new Order(java.util.UUID.randomUUID(), VENTE_ID, OrderNumber.generate(),
                new CustomerInfo("Alice", "Dupont", "alice@test.com", "0601020304"), 1L,
                List.of(adjustedItem), fr.sqq.achatgroupe.domain.model.order.OrderStatus.PAID,
                Money.eur(new BigDecimal("14.00")), java.time.Instant.now());

        Reception reception = Reception.create(VENTE_ID, "Ferme A",
                List.of(ReceptionItem.create(PRODUCT_A, 4, 3)));

        when(receptionRepository.findAllByVenteId(VENTE_ID)).thenReturn(List.of(reception));
        when(orderRepository.findPaidByVenteId(VENTE_ID)).thenReturn(List.of(order));
        when(productRepository.findAllByVenteId(VENTE_ID)).thenReturn(List.of(productA));

        handler.handle(new ApplyShortageAdjustmentsCommand(VENTE_ID));

        // Should not save anything since adjustments are already applied
        verify(orderRepository, never()).save(any());
    }

    @Test
    void should_apply_partial_remaining_shortage() {
        Product productA = new Product(PRODUCT_A, VENTE_ID, "Tomates", "Bio", Money.eur(new BigDecimal("3.50")), BigDecimal.ZERO, "Ferme A", 100, true, "TOM-001", "Legumes", "Ferme A", false);

        // Two orders with Product A
        Order order1 = Order.create(VENTE_ID, OrderNumber.generate(),
                new CustomerInfo("Alice", "Dupont", "alice@test.com", "0601020304"), 1L,
                List.of(OrderItem.create(PRODUCT_A, 3, Money.eur(new BigDecimal("3.50")))));
        order1.markAsPaid();

        // Order2 already has 1 cancelled (from a previous partial run)
        OrderItem partiallyAdjustedItem = new OrderItem(2L, PRODUCT_A, 2, Money.eur(new BigDecimal("3.50")), 1);
        Order order2 = new Order(java.util.UUID.randomUUID(), VENTE_ID, OrderNumber.generate(),
                new CustomerInfo("Bob", "Martin", "bob@test.com", "0605060708"), 1L,
                List.of(partiallyAdjustedItem), fr.sqq.achatgroupe.domain.model.order.OrderStatus.PAID,
                Money.eur(new BigDecimal("7.00")), java.time.Instant.now());

        // Shortage = 3, already cancelled = 1, remaining = 2
        Reception reception = Reception.create(VENTE_ID, "Ferme A",
                List.of(ReceptionItem.create(PRODUCT_A, 5, 2)));

        when(receptionRepository.findAllByVenteId(VENTE_ID)).thenReturn(List.of(reception));
        when(orderRepository.findPaidByVenteId(VENTE_ID)).thenReturn(List.of(order1, order2));
        when(productRepository.findAllByVenteId(VENTE_ID)).thenReturn(List.of(productA));
        when(orderRepository.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));

        handler.handle(new ApplyShortageAdjustmentsCommand(VENTE_ID));

        // Total cancelled across both orders should equal the shortage (3)
        int totalCancelled = order1.items().get(0).cancelledQuantity()
                + order2.items().get(0).cancelledQuantity();
        assertThat(totalCancelled).isEqualTo(3);
    }
}
