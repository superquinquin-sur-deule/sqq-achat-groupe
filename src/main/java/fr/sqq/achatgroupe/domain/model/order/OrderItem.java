package fr.sqq.achatgroupe.domain.model.order;

import java.math.BigDecimal;

public class OrderItem {

    private final Long id;
    private final Long productId;
    private final int quantity;
    private final BigDecimal unitPrice;

    public OrderItem(Long id, Long productId, int quantity, BigDecimal unitPrice) {
        if (productId == null) throw new IllegalArgumentException("Product id must not be null");
        if (quantity <= 0) throw new IllegalArgumentException("Quantity must be positive");
        if (unitPrice == null || unitPrice.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Unit price must be positive");
        }
        this.id = id;
        this.productId = productId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    public static OrderItem create(Long productId, int quantity, BigDecimal unitPrice) {
        return new OrderItem(null, productId, quantity, unitPrice);
    }

    public BigDecimal subtotal() {
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }

    public Long id() { return id; }
    public Long productId() { return productId; }
    public int quantity() { return quantity; }
    public BigDecimal unitPrice() { return unitPrice; }
}
