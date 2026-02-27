package fr.sqq.achatgroupe.domain.model.shared;

import java.math.BigDecimal;
import java.math.RoundingMode;

public record Money(BigDecimal amount, String currency) {

    public static final Money ZERO = new Money(BigDecimal.ZERO, "EUR");

    public Money {
        if (amount == null) throw new IllegalArgumentException("Amount must not be null");
        if (amount.compareTo(BigDecimal.ZERO) < 0) throw new IllegalArgumentException("Le montant ne peut pas être négatif");
        if (currency == null || currency.isBlank()) throw new IllegalArgumentException("Currency must not be blank");
    }

    public static Money eur(BigDecimal amount) {
        return new Money(amount, "EUR");
    }

    public Money add(Money other) {
        if (!this.currency.equals(other.currency)) throw new IllegalArgumentException("Devises incompatibles");
        return new Money(this.amount.add(other.amount), this.currency);
    }

    public Money subtract(Money other) {
        if (!this.currency.equals(other.currency)) throw new IllegalArgumentException("Devises incompatibles");
        return new Money(this.amount.subtract(other.amount), this.currency);
    }

    public Money multiply(int quantity) {
        return new Money(this.amount.multiply(BigDecimal.valueOf(quantity)), this.currency);
    }

    public Money multiply(BigDecimal factor) {
        return new Money(this.amount.multiply(factor).setScale(2, RoundingMode.HALF_UP), this.currency);
    }

    public boolean isPositive() {
        return amount.compareTo(BigDecimal.ZERO) > 0;
    }

    public long toCents() {
        return amount.movePointRight(2).longValueExact();
    }
}
