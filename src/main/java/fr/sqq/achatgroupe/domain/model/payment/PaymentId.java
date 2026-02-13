package fr.sqq.achatgroupe.domain.model.payment;

import java.util.Objects;

public record PaymentId(Long value) {

    public PaymentId {
        Objects.requireNonNull(value, "Payment id must not be null");
    }
}
