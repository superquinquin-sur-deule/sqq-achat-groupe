package fr.sqq.achatgroupe.domain.model.catalog;

import fr.sqq.achatgroupe.domain.exception.InsufficientStockException;

import java.math.BigDecimal;

public class Product {

    private final Long id;
    private final Long venteId;
    private final String name;
    private final String description;
    private final BigDecimal price;
    private final String supplier;
    private int stock;
    private final boolean active;

    public Product(Long id, Long venteId, String name, String description, BigDecimal price, String supplier, int stock, boolean active) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Product name must not be blank");
        }
        if (price == null || price.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Product price must be positive");
        }
        if (supplier == null || supplier.isBlank()) {
            throw new IllegalArgumentException("Product supplier must not be blank");
        }
        if (stock < 0) {
            throw new IllegalArgumentException("Product stock must not be negative");
        }
        this.id = id;
        this.venteId = venteId;
        this.name = name;
        this.description = description;
        this.price = price;
        this.supplier = supplier;
        this.stock = stock;
        this.active = active;
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

    public BigDecimal price() {
        return price;
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
}
