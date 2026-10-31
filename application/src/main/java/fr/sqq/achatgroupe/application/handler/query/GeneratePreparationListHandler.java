package fr.sqq.achatgroupe.application.handler.query;

import fr.sqq.achatgroupe.application.port.out.OrderRepository;
import fr.sqq.achatgroupe.application.port.out.ProductRepository;
import fr.sqq.achatgroupe.application.port.out.TimeSlotRepository;
import fr.sqq.achatgroupe.application.query.GeneratePreparationListQuery;
import fr.sqq.achatgroupe.application.query.GeneratePreparationListQuery.PreparationItem;
import fr.sqq.achatgroupe.application.query.GeneratePreparationListQuery.PreparationOrder;
import fr.sqq.achatgroupe.application.service.VariantIndex;
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
public class GeneratePreparationListHandler implements QueryHandler<GeneratePreparationListQuery, List<PreparationOrder>> {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final TimeSlotRepository timeSlotRepository;

    public GeneratePreparationListHandler(OrderRepository orderRepository, ProductRepository productRepository,
                                          TimeSlotRepository timeSlotRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.timeSlotRepository = timeSlotRepository;
    }

    @Override
    @Transactional
    public List<PreparationOrder> handle(GeneratePreparationListQuery query) {
        List<Order> orders = orderRepository.findConfirmedByVenteId(query.venteId());

        VariantIndex variants = VariantIndex.of(productRepository.findAllByVenteId(query.venteId()));

        List<TimeSlot> timeSlots = timeSlotRepository.findAllByVenteId(query.venteId());
        Map<Long, TimeSlot> timeSlotsById = timeSlots.stream()
                .collect(Collectors.toMap(TimeSlot::id, Function.identity()));

        return orders.stream()
                .map(order -> {
                    TimeSlot slot = timeSlotsById.get(order.timeSlotId());
                    String timeSlotLabel = slot != null
                            ? slot.date() + " " + slot.startTime() + "-" + slot.endTime()
                            : "Créneau inconnu";

                    List<PreparationItem> items = order.items().stream()
                            .map(item -> {
                                VariantIndex.Entry indexed = variants.get(item.variantId());
                                String productName = indexed != null
                                        ? indexed.displayName()
                                        : "Produit inconnu (ID: " + item.productId() + ")";
                                String supplier = indexed != null
                                        ? indexed.product().supplier()
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
                .sorted(Comparator.comparing(PreparationOrder::timeSlotLabel)
                        .thenComparing(PreparationOrder::customerLastName))
                .toList();
    }
}
