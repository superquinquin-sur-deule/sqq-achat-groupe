package fr.sqq.achatgroupe.application.service;

import fr.sqq.achatgroupe.application.port.out.ProductRepository;
import fr.sqq.achatgroupe.application.port.out.TimeSlotRepository;
import fr.sqq.achatgroupe.domain.exception.TimeSlotNotFoundException;
import fr.sqq.achatgroupe.domain.exception.VariantNotFoundException;
import fr.sqq.achatgroupe.domain.model.catalog.Product;
import fr.sqq.achatgroupe.domain.model.catalog.ProductVariant;
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
            Product product = loadProduct(item);
            ProductVariant variant = product.variant(item.variantId());
            variant.decrementStock(item.quantity(), product.displayName(variant));
            productRepository.save(product);
        }
        reserveTimeSlot(order);
    }

    public void releaseStock(Order order) {
        for (OrderItem item : order.items()) {
            Product product = loadProduct(item);
            product.variant(item.variantId()).incrementStock(item.quantity());
            productRepository.save(product);
        }
        releaseTimeSlot(order);
    }

    private Product loadProduct(OrderItem item) {
        return productRepository.findByVariantId(item.variantId())
                .orElseThrow(() -> new VariantNotFoundException(item.variantId()));
    }

    private void reserveTimeSlot(Order order) {
        if (order.timeSlotId() == null) {
            return;
        }
        TimeSlot slot = timeSlotRepository.findSlotById(order.timeSlotId())
                .orElseThrow(() -> new TimeSlotNotFoundException(order.timeSlotId()));
        slot.reserveOnePlace();
        timeSlotRepository.save(slot);
    }

    private void releaseTimeSlot(Order order) {
        if (order.timeSlotId() == null) {
            return;
        }
        TimeSlot slot = timeSlotRepository.findSlotById(order.timeSlotId())
                .orElseThrow(() -> new TimeSlotNotFoundException(order.timeSlotId()));
        slot.releaseOnePlace();
        timeSlotRepository.save(slot);
    }
}
