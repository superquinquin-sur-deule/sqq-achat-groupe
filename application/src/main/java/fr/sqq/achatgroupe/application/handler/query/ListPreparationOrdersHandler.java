package fr.sqq.achatgroupe.application.handler.query;

import fr.sqq.achatgroupe.application.port.out.OrderRepository;
import fr.sqq.achatgroupe.application.port.out.ProductRepository;
import fr.sqq.achatgroupe.application.port.out.TimeSlotRepository;
import fr.sqq.achatgroupe.application.query.CursorPage;
import fr.sqq.achatgroupe.application.query.GeneratePreparationListQuery.PreparationItem;
import fr.sqq.achatgroupe.application.query.GeneratePreparationListQuery.PreparationOrder;
import fr.sqq.achatgroupe.application.query.ListPreparationOrdersQuery;
import fr.sqq.achatgroupe.domain.model.catalog.Product;
import fr.sqq.achatgroupe.domain.model.order.Order;
import fr.sqq.achatgroupe.domain.model.planning.TimeSlot;
import fr.sqq.mediator.QueryHandler;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@ApplicationScoped
public class ListPreparationOrdersHandler implements QueryHandler<ListPreparationOrdersQuery, CursorPage<PreparationOrder>> {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final TimeSlotRepository timeSlotRepository;

    public ListPreparationOrdersHandler(OrderRepository orderRepository, ProductRepository productRepository,
                                        TimeSlotRepository timeSlotRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.timeSlotRepository = timeSlotRepository;
    }

    @Override
    @Transactional
    public CursorPage<PreparationOrder> handle(ListPreparationOrdersQuery query) {
        CursorPage<Order> page = orderRepository.findPaidByVenteId(
                query.venteId(), query.pageRequest(), null, query.timeSlotId());

        List<Product> products = productRepository.findAllByVenteId(query.venteId());
        Map<Long, Product> productsById = products.stream()
                .collect(Collectors.toMap(Product::id, Function.identity()));

        List<TimeSlot> timeSlots = timeSlotRepository.findAllByVenteId(query.venteId());
        Map<Long, TimeSlot> timeSlotsById = timeSlots.stream()
                .collect(Collectors.toMap(TimeSlot::id, Function.identity()));

        List<PreparationOrder> enrichedOrders = page.items().stream()
                .map(order -> {
                    TimeSlot slot = timeSlotsById.get(order.timeSlotId());
                    String timeSlotLabel = slot != null
                            ? slot.date() + " " + slot.startTime() + "-" + slot.endTime()
                            : "Créneau inconnu";

                    List<PreparationItem> items = order.items().stream()
                            .map(item -> {
                                Product product = productsById.get(item.productId());
                                String productName = product != null
                                        ? product.name()
                                        : "Produit inconnu (ID: " + item.productId() + ")";
                                String supplier = product != null
                                        ? product.supplier()
                                        : "Fournisseur inconnu";
                                return new PreparationItem(productName, supplier, item.effectiveQuantity(), item.cancelledQuantity());
                            })
                            .sorted(Comparator.comparing(PreparationItem::supplier)
                                    .thenComparing(PreparationItem::productName))
                            .toList();

                    return new PreparationOrder(
                            order.id(),
                            order.orderNumber().value(),
                            order.customer().firstName(),
                            order.customer().lastName(),
                            order.customer().email(),
                            order.customer().phone(),
                            timeSlotLabel,
                            items
                    );
                })
                .toList();

        return new CursorPage<>(enrichedOrders, page.endCursor(), page.hasNext());
    }
}
