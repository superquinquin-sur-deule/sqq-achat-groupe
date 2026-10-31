package fr.sqq.achatgroupe.domain.model.catalog;

import fr.sqq.achatgroupe.domain.exception.InsufficientStockException;
import fr.sqq.achatgroupe.domain.model.shared.Money;

public class ProductVariant {

    public static final int MAX_LABEL_LENGTH = 100;

    private final Long id;
    private final Long productId;
    private final String label;
    private final String reference;
    private final Money prixHt;
    private int stock;
    private final Integer colisage;
    private final int position;

    public ProductVariant(Long id, Long productId, String label, String reference, Money prixHt,
                          int stock, Integer colisage, int position) {
        if (label == null) {
            throw new IllegalArgumentException("Variant label must not be null");
        }
        if (label.length() > MAX_LABEL_LENGTH) {
            throw new IllegalArgumentException("Variant label must not exceed " + MAX_LABEL_LENGTH + " characters");
        }
        if (reference == null || reference.isBlank()) {
            throw new IllegalArgumentException("Product reference must not be blank");
        }
        if (prixHt == null || !prixHt.isPositive()) {
            throw new IllegalArgumentException("Product price must be positive");
        }
        if (stock < 0) {
            throw new IllegalArgumentException("Product stock must not be negative");
        }
        if (colisage != null && colisage <= 0) {
            throw new IllegalArgumentException("Product colisage must be positive");
        }
        this.id = id;
        this.productId = productId;
        this.label = label.strip();
        this.reference = reference;
        this.prixHt = prixHt;
        this.stock = stock;
        this.colisage = colisage;
        this.position = position;
    }

    public void decrementStock(int quantity, String displayName) {
        if (quantity <= 0) throw new IllegalArgumentException("La quantité doit être positive");
        if (this.stock < quantity) throw new InsufficientStockException(displayName, this.stock, quantity);
        this.stock -= quantity;
    }

    public void incrementStock(int quantity) {
        if (quantity <= 0) throw new IllegalArgumentException("La quantité doit être positive");
        this.stock += quantity;
    }

    public boolean isAvailable() {
        return stock > 0;
    }

    public boolean isNamed() {
        return !label.isEmpty();
    }

    public Long id() { return id; }
    public Long productId() { return productId; }
    public String label() { return label; }
    public String reference() { return reference; }
    public Money prixHt() { return prixHt; }
    public int stock() { return stock; }
    public Integer colisage() { return colisage; }
    public int position() { return position; }
}
