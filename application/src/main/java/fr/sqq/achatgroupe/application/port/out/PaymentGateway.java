package fr.sqq.achatgroupe.application.port.out;

import fr.sqq.achatgroupe.domain.model.order.Order;

import java.util.UUID;

public interface PaymentGateway {

    PaymentSessionResult createCheckoutSession(Order order, String successUrl, String cancelUrl);

    PaymentWebhookResult parseWebhookEvent(String payload, String signature);

    RefundResult createRefund(String stripePaymentIntentId, long amountInCents);

    record PaymentSessionResult(String checkoutUrl, String sessionId) {}

    record PaymentWebhookResult(UUID orderId, String externalPaymentId, PaymentWebhookStatus status) {}

    record RefundResult(String stripeRefundId, boolean succeeded) {}

    enum PaymentWebhookStatus { SUCCEEDED, FAILED, IGNORED }
}
