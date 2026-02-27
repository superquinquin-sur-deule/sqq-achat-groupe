package fr.sqq.achatgroupe.domain.model.payment;

import fr.sqq.achatgroupe.domain.model.shared.Money;

import java.time.Instant;
import java.util.UUID;

public class Payment {

    private final Long id;
    private final UUID orderId;
    private final Money amount;
    private String stripePaymentId;
    private PaymentStatus status;
    private int attempts;
    private final Instant createdAt;
    private Instant updatedAt;
    private int version;

    public Payment(Long id, UUID orderId, Money amount, String stripePaymentId,
                   PaymentStatus status, int attempts, Instant createdAt, Instant updatedAt, int version) {
        if (orderId == null) throw new IllegalArgumentException("Order ID must not be null");
        if (amount == null) throw new IllegalArgumentException("Amount must not be null");
        if (status == null) throw new IllegalArgumentException("Status must not be null");
        this.id = id;
        this.orderId = orderId;
        this.amount = amount;
        this.stripePaymentId = stripePaymentId;
        this.status = status;
        this.attempts = attempts;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.version = version;
    }

    public static Payment create(UUID orderId, Money amount) {
        Instant now = Instant.now();
        return new Payment(null, orderId, amount, null, PaymentStatus.PENDING, 0, now, now, 0);
    }

    public void recordAttempt() {
        this.attempts++;
        this.updatedAt = Instant.now();
    }

    public void markAsSucceeded(String stripePaymentId) {
        if (this.status == PaymentStatus.SUCCEEDED) {
            return; // Idempotent
        }
        this.stripePaymentId = stripePaymentId;
        this.status = PaymentStatus.SUCCEEDED;
        this.updatedAt = Instant.now();
    }

    public void markAsFailed() {
        this.status = PaymentStatus.FAILED;
        this.updatedAt = Instant.now();
    }

    public boolean isAlreadySucceeded() {
        return this.status == PaymentStatus.SUCCEEDED;
    }

    public Long id() { return id; }
    public UUID orderId() { return orderId; }
    public Money amount() { return amount; }
    public String stripePaymentId() { return stripePaymentId; }
    public PaymentStatus status() { return status; }
    public int attempts() { return attempts; }
    public Instant createdAt() { return createdAt; }
    public Instant updatedAt() { return updatedAt; }
    public int version() { return version; }
}
