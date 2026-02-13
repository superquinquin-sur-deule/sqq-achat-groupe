package fr.sqq.achatgroupe.application.service;

import fr.sqq.achatgroupe.application.port.in.ListOrdersUseCase;
import fr.sqq.achatgroupe.application.port.out.OrderRepository;
import fr.sqq.achatgroupe.domain.model.order.Order;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.util.List;

@ApplicationScoped
public class ListOrders implements ListOrdersUseCase {

    private final OrderRepository orderRepository;

    public ListOrders(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    @Transactional
    public List<Order> listOrders(Long venteId) {
        return orderRepository.findPaidByVenteId(venteId);
    }
}
