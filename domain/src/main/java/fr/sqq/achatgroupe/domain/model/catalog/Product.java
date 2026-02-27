package fr.sqq.achatgroupe.domain.model.catalog;

import fr.sqq.achatgroupe.domain.exception.InsufficientStockException;
import fr.sqq.achatgroupe.domain.model.shared.Money;

import java.math.BigDecimal;

public class Product {

    private final Long id;
    private final Long venteId;
    private final String name;
    private final String description;
    private final Money prixHt;
    private final BigDecimal tauxTva;
    private final String supplier;
    private int stock;
    private final boolean active;
    private final String reference;
    private final String category;
    private final String brand;
    private final boolean hasImage;
    private String stripeProductId;

    public Product(Long id, Long venteId, String name, String description, Money prixHt, BigDecimal tauxTva, String supplier, int stock, boolean active,
                   String reference, String category, String brand, boolean hasImage) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Product name must not be blank");
        }
        if (prixHt == null || !prixHt.isPositive()) {
            throw new IllegalArgumentException("Product price must be positive");
        }
        if (tauxTva == null || tauxTva.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Product VAT rate must not be negative");
        }
        if (supplier == null || supplier.isBlank()) {
            throw new IllegalArgumentException("Product supplier must not be blank");
        }
        if (stock < 0) {
            throw new IllegalArgumentException("Product stock must not be negative");
        }
        if (reference == null || reference.isBlank()) {
            throw new IllegalArgumentException("Product reference must not be blank");
        }
        if (category == null || category.isBlank()) {
            throw new IllegalArgumentException("Product category must not be blank");
        }
        if (brand == null || brand.isBlank()) {
            throw new IllegalArgumentException("Product brand must not be blank");
        }
        this.id = id;
        this.venteId = venteId;
        this.name = name;
        this.description = description;
        this.prixHt = prixHt;
        this.tauxTva = tauxTva;
        this.supplier = supplier;
        this.stock = stock;
        this.active = active;
        this.reference = reference;
        this.category = category;
        this.brand = brand;
        this.hasImage = hasImage;
    }

    public void decrementStock(int quantity) {
        if (quantity <= 0) throw new IllegalArgumentException("La quantité doit être positive");
        if (this.stock < quantity) throw new InsufficientStockException(this.name, this.stock, quantity);
        this.stock -= quantity;
    }

    public void incrementStock(int quantity) {
        if (quantity <= 0) throw new IllegalArgumentException("La quantité doit être positive");
        this.stock += quantity;
    }

    public boolean isAvailable() {
        return stock > 0;
    }

    public Money prixTtc() {
        return prixHt.multiply(BigDecimal.ONE.add(tauxTva.divide(BigDecimal.valueOf(100))));
    }

    public Long id() {
        return id;
    }

    public Long venteId() {
        return venteId;
    }

    public String name() {
        return name;
    }

    public String description() {
        return description;
    }

    public Money prixHt() {
        return prixHt;
    }

    public BigDecimal tauxTva() {
        return tauxTva;
    }

    public String supplier() {
        return supplier;
    }

    public int stock() {
        return stock;
    }

    public boolean active() {
        return active;
    }

    public String reference() {
        return reference;
    }

    public String category() {
        return category;
    }

    public String brand() {
        return brand;
    }

    public boolean hasImage() {
        return hasImage;
    }

    public String stripeProductId() {
        return stripeProductId;
    }

    public void assignStripeProductId(String stripeProductId) {
        this.stripeProductId = stripeProductId;
    }
}
