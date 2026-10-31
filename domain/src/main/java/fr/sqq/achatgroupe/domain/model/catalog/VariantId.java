package fr.sqq.achatgroupe.domain.model.catalog;

import java.util.Objects;

public record VariantId(Long value) {

    public VariantId {
        Objects.requireNonNull(value, "Variant id must not be null");
        if (value <= 0) {
            throw new IllegalArgumentException("Variant id must be positive");
        }
    }
}
