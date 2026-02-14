package fr.sqq.achatgroupe.domain.model.payment;

import java.math.BigDecimal;
import java.time.Instant;

public class Payment {

    private final Long id;
    private final Long orderId;
    private final BigDecimal amount;
    private String stripePaymentId;
    private PaymentStatus status;
    private int attempts;
    private final Instant createdAt;
    private Instant updatedAt;
    private int version;

    public Payment(Long id, Long orderId, BigDecimal amount, String stripePaymentId,
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

    public static Payment create(Long orderId, BigDecimal amount) {
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
    public Long orderId() { return orderId; }
    public BigDecimal amount() { return amount; }
    public String stripePaymentId() { return stripePaymentId; }
    public PaymentStatus status() { return status; }
    public int attempts() { return attempts; }
    public Instant createdAt() { return createdAt; }
    public Instant updatedAt() { return updatedAt; }
    public int version() { return version; }
}
