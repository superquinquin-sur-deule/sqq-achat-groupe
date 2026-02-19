package fr.sqq.achatgroupe.domain.model.payment;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public class Refund {

    private final Long id;
    private final UUID orderId;
    private final BigDecimal amount;
    private String stripeRefundId;
    private RefundStatus status;
    private final Instant createdAt;
    private Instant updatedAt;

    public Refund(Long id, UUID orderId, BigDecimal amount, String stripeRefundId,
                  RefundStatus status, Instant createdAt, Instant updatedAt) {
        if (orderId == null) throw new IllegalArgumentException("Order ID must not be null");
        if (amount == null) throw new IllegalArgumentException("Amount must not be null");
        if (status == null) throw new IllegalArgumentException("Status must not be null");
        this.id = id;
        this.orderId = orderId;
        this.amount = amount;
        this.stripeRefundId = stripeRefundId;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Refund create(UUID orderId, BigDecimal amount) {
        Instant now = Instant.now();
        return new Refund(null, orderId, amount, null, RefundStatus.PENDING, now, now);
    }

    public void markAsSucceeded(String stripeRefundId) {
        this.stripeRefundId = stripeRefundId;
        this.status = RefundStatus.SUCCEEDED;
        this.updatedAt = Instant.now();
    }

    public void markAsFailed() {
        this.status = RefundStatus.FAILED;
        this.updatedAt = Instant.now();
    }

    public Long id() { return id; }
    public UUID orderId() { return orderId; }
    public BigDecimal amount() { return amount; }
    public String stripeRefundId() { return stripeRefundId; }
    public RefundStatus status() { return status; }
    public Instant createdAt() { return createdAt; }
    public Instant updatedAt() { return updatedAt; }
}
