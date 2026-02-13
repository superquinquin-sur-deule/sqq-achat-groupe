package fr.sqq.achatgroupe.infrastructure.in.rest.dto;

public record PaymentStatusResponse(
        int attempts,
        int maxAttempts,
        String paymentStatus,
        String orderStatus,
        boolean canRetry
) {
}
