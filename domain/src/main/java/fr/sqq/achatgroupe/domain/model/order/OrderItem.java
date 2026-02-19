package fr.sqq.achatgroupe.domain.model.order;

import java.math.BigDecimal;

public class OrderItem {

    private final Long id;
    private final Long productId;
    private final int quantity;
    private final BigDecimal unitPrice;
    private int cancelledQuantity;

    public OrderItem(Long id, Long productId, int quantity, BigDecimal unitPrice, int cancelledQuantity) {
        if (productId == null) throw new IllegalArgumentException("Product id must not be null");
        if (quantity <= 0) throw new IllegalArgumentException("Quantity must be positive");
        if (unitPrice == null || unitPrice.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Unit price must be positive");
        }
        if (cancelledQuantity < 0 || cancelledQuantity > quantity) {
            throw new IllegalArgumentException("Cancelled quantity must be between 0 and quantity");
        }
        this.id = id;
        this.productId = productId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.cancelledQuantity = cancelledQuantity;
    }

    public static OrderItem create(Long productId, int quantity, BigDecimal unitPrice) {
        return new OrderItem(null, productId, quantity, unitPrice, 0);
    }

    public void cancelQuantity(int qty) {
        if (qty <= 0) throw new IllegalArgumentException("La quantité annulée doit être positive");
        if (cancelledQuantity + qty > quantity) {
            throw new IllegalArgumentException("La quantité annulée ne peut pas dépasser la quantité commandée");
        }
        this.cancelledQuantity += qty;
    }

    public int effectiveQuantity() {
        return quantity - cancelledQuantity;
    }

    public BigDecimal effectiveSubtotal() {
        return unitPrice.multiply(BigDecimal.valueOf(effectiveQuantity()));
    }

    public void resetCancellation() {
        this.cancelledQuantity = 0;
    }

    public boolean isFullyCancelled() {
        return cancelledQuantity == quantity;
    }

    public BigDecimal subtotal() {
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }

    public Long id() { return id; }
    public Long productId() { return productId; }
    public int quantity() { return quantity; }
    public BigDecimal unitPrice() { return unitPrice; }
    public int cancelledQuantity() { return cancelledQuantity; }
}
