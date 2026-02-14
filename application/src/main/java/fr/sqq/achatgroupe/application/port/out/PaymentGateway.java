package fr.sqq.achatgroupe.application.port.out;

import fr.sqq.achatgroupe.domain.model.order.Order;

import java.util.UUID;

public interface PaymentGateway {

    PaymentSessionResult createCheckoutSession(Order order, String successUrl, String cancelUrl);

    PaymentWebhookResult parseWebhookEvent(String payload, String signature);

    record PaymentSessionResult(String checkoutUrl, String sessionId) {}

    record PaymentWebhookResult(UUID orderId, String externalPaymentId, PaymentWebhookStatus status) {}

    enum PaymentWebhookStatus { SUCCEEDED, FAILED, IGNORED }
}
