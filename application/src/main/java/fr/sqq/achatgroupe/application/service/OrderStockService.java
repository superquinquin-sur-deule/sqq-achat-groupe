package fr.sqq.achatgroupe.application.service;

import fr.sqq.achatgroupe.application.port.out.ProductRepository;
import fr.sqq.achatgroupe.application.port.out.TimeSlotRepository;
import fr.sqq.achatgroupe.domain.exception.ProductNotFoundException;
import fr.sqq.achatgroupe.domain.exception.TimeSlotNotFoundException;
import fr.sqq.achatgroupe.domain.model.catalog.Product;
import fr.sqq.achatgroupe.domain.model.catalog.ProductId;
import fr.sqq.achatgroupe.domain.model.order.Order;
import fr.sqq.achatgroupe.domain.model.order.OrderItem;
import fr.sqq.achatgroupe.domain.model.planning.TimeSlot;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class OrderStockService {

    private final ProductRepository productRepository;
    private final TimeSlotRepository timeSlotRepository;

    public OrderStockService(ProductRepository productRepository, TimeSlotRepository timeSlotRepository) {
        this.productRepository = productRepository;
        this.timeSlotRepository = timeSlotRepository;
    }

    public void reserveStock(Order order) {
        for (OrderItem item : order.items()) {
            Product product = productRepository.findById(new ProductId(item.productId()))
                    .orElseThrow(() -> new ProductNotFoundException(new ProductId(item.productId())));
            product.decrementStock(item.quantity());
            productRepository.save(product);
        }
        if (order.timeSlotId() != null) {
            TimeSlot slot = timeSlotRepository.findSlotById(order.timeSlotId())
                    .orElseThrow(() -> new TimeSlotNotFoundException(order.timeSlotId()));
            slot.reserveOnePlace();
            timeSlotRepository.save(slot);
        }
    }

    public void releaseStock(Order order) {
        for (OrderItem item : order.items()) {
            Product product = productRepository.findById(new ProductId(item.productId()))
                    .orElseThrow(() -> new ProductNotFoundException(new ProductId(item.productId())));
            product.incrementStock(item.quantity());
            productRepository.save(product);
        }
        if (order.timeSlotId() != null) {
            TimeSlot slot = timeSlotRepository.findSlotById(order.timeSlotId())
                    .orElseThrow(() -> new TimeSlotNotFoundException(order.timeSlotId()));
            slot.releaseOnePlace();
            timeSlotRepository.save(slot);
        }
    }
}
