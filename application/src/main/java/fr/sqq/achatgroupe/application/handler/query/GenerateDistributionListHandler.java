package fr.sqq.achatgroupe.application.handler.query;

import fr.sqq.achatgroupe.application.port.out.OrderRepository;
import fr.sqq.achatgroupe.application.port.out.TimeSlotRepository;
import fr.sqq.achatgroupe.application.query.GenerateDistributionListQuery;
import fr.sqq.achatgroupe.application.query.GenerateDistributionListQuery.DistributionOrder;
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
public class GenerateDistributionListHandler implements QueryHandler<GenerateDistributionListQuery, List<DistributionOrder>> {

    private final OrderRepository orderRepository;
    private final TimeSlotRepository timeSlotRepository;

    public GenerateDistributionListHandler(OrderRepository orderRepository,
                                           TimeSlotRepository timeSlotRepository) {
        this.orderRepository = orderRepository;
        this.timeSlotRepository = timeSlotRepository;
    }

    @Override
    @Transactional
    public List<DistributionOrder> handle(GenerateDistributionListQuery query) {
        List<Order> orders = orderRepository.findPaidByVenteId(query.venteId());

        List<TimeSlot> timeSlots = timeSlotRepository.findAllByVenteId(query.venteId());
        Map<Long, TimeSlot> timeSlotsById = timeSlots.stream()
                .collect(Collectors.toMap(TimeSlot::id, Function.identity()));

        return orders.stream()
                .map(order -> {
                    TimeSlot slot = timeSlotsById.get(order.timeSlotId());
                    String timeSlotLabel = slot != null
                            ? slot.date() + " " + slot.startTime() + "-" + slot.endTime()
                            : "Créneau inconnu";

                    return new DistributionOrder(
                            order.id(),
                            order.orderNumber().value(),
                            order.customer().firstName(),
                            order.customer().lastName(),
                            timeSlotLabel
                    );
                })
                .sorted(Comparator.comparing(DistributionOrder::timeSlotLabel)
                        .thenComparing(DistributionOrder::customerLastName))
                .toList();
    }
}
