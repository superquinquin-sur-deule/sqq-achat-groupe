package fr.sqq.achatgroupe.domain.model.catalog;

import java.util.Objects;

public record ProductId(Long value) {

    public ProductId {
        Objects.requireNonNull(value, "Product id must not be null");
        if (value <= 0) {
            throw new IllegalArgumentException("Product id must be positive");
        }
    }
}
