package fr.sqq.achatgroupe.domain.model.order;

import java.util.Objects;

public record OrderId(Long value) {

    public OrderId {
        Objects.requireNonNull(value, "Order id must not be null");
    }
}
