package fr.sqq.achatgroupe.domain.model.order;

import fr.sqq.achatgroupe.domain.exception.OrderAlreadyPaidException;
import fr.sqq.achatgroupe.domain.model.shared.Money;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class Order {

    private final UUID id;
    private final Long venteId;
    private final OrderNumber orderNumber;
    private final CustomerInfo customer;
    private final Long timeSlotId;
    private final List<OrderItem> items;
    private OrderStatus status;
    private final Money totalAmount;
    private final Instant createdAt;

    public Order(UUID id, Long venteId, OrderNumber orderNumber, CustomerInfo customer, Long timeSlotId,
                 List<OrderItem> items, OrderStatus status, Money totalAmount, Instant createdAt) {
        if (orderNumber == null) throw new IllegalArgumentException("Order number must not be null");
        if (customer == null) throw new IllegalArgumentException("Customer must not be null");
        if (timeSlotId == null) throw new IllegalArgumentException("Time slot ID must not be null");
        if (items == null || items.isEmpty()) throw new IllegalArgumentException("Items must not be empty");
        if (status == null) throw new IllegalArgumentException("Status must not be null");
        if (totalAmount == null) throw new IllegalArgumentException("Total amount must not be null");
        if (createdAt == null) throw new IllegalArgumentException("Created at must not be null");
        this.id = id;
        this.venteId = venteId;
        this.orderNumber = orderNumber;
        this.customer = customer;
        this.timeSlotId = timeSlotId;
        this.items = List.copyOf(items);
        this.status = status;
        this.totalAmount = totalAmount;
        this.createdAt = createdAt;
    }

    public static Order create(Long venteId, OrderNumber number, CustomerInfo customer, Long timeSlotId, List<OrderItem> items) {
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("La commande doit contenir au moins un article");
        }
        Money total = items.stream()
                .map(OrderItem::subtotal)
                .reduce(Money.ZERO, Money::add);
        return new Order(UUID.randomUUID(), venteId, number, customer, timeSlotId, items, OrderStatus.PENDING, total, Instant.now());
    }

    public void markAsPaid() {
        if (this.status != OrderStatus.PENDING) {
            throw new OrderAlreadyPaidException(this.id);
        }
        this.status = OrderStatus.PAID;
    }

    public void markAsPickedUp() {
        if (this.status != OrderStatus.PAID) {
            throw new IllegalStateException("Seule une commande payée peut être marquée retirée");
        }
        this.status = OrderStatus.PICKED_UP;
    }

    public void cancel() {
        if (this.status == OrderStatus.PAID || this.status == OrderStatus.PICKED_UP) {
            throw new IllegalStateException("Impossible d'annuler une commande en statut " + this.status);
        }
        this.status = OrderStatus.CANCELLED;
    }

    public void cancelDueToShortage() {
        if (this.status != OrderStatus.PAID) {
            throw new IllegalStateException("Seule une commande payée peut être annulée pour rupture");
        }
        if (!isFullyCancelled()) {
            throw new IllegalStateException("Seule une commande entièrement annulée peut être marquée annulée");
        }
        this.status = OrderStatus.CANCELLED;
    }

    public Money effectiveTotalAmount() {
        return items.stream()
                .map(OrderItem::effectiveSubtotal)
                .reduce(Money.ZERO, Money::add);
    }

    public Money refundAmount() {
        return totalAmount.subtract(effectiveTotalAmount());
    }

    public boolean hasAdjustments() {
        return items.stream().anyMatch(item -> item.cancelledQuantity() > 0);
    }

    public boolean isFullyCancelled() {
        return items.stream().allMatch(OrderItem::isFullyCancelled);
    }

    public UUID id() { return id; }
    public Long venteId() { return venteId; }
    public OrderNumber orderNumber() { return orderNumber; }
    public CustomerInfo customer() { return customer; }
    public Long timeSlotId() { return timeSlotId; }
    public List<OrderItem> items() { return items; }
    public OrderStatus status() { return status; }
    public Money totalAmount() { return totalAmount; }
    public Instant createdAt() { return createdAt; }
}
