package fr.sqq.achatgroupe.domain.model.shared;

import java.math.BigDecimal;

public record Money(BigDecimal amount, String currency) {

    public static final Money ZERO = new Money(BigDecimal.ZERO, "EUR");

    public Money {
        if (amount == null) throw new IllegalArgumentException("Amount must not be null");
        if (amount.compareTo(BigDecimal.ZERO) < 0) throw new IllegalArgumentException("Le montant ne peut pas être négatif");
        if (currency == null || currency.isBlank()) throw new IllegalArgumentException("Currency must not be blank");
    }

    public Money add(Money other) {
        if (!this.currency.equals(other.currency)) throw new IllegalArgumentException("Devises incompatibles");
        return new Money(this.amount.add(other.amount), this.currency);
    }
}
