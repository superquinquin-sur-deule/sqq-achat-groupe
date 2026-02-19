package fr.sqq.achatgroupe.acceptance.support;

import fr.sqq.achatgroupe.domain.model.order.Order;
import fr.sqq.achatgroupe.application.port.out.PaymentGateway;
import io.quarkus.test.Mock;
import jakarta.annotation.Priority;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Alternative;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Mock
@Alternative
@Priority(1)
@ApplicationScoped
public class FakePaymentGateway implements PaymentGateway {

    private static final AtomicLong SESSION_COUNTER = new AtomicLong(0);
    private static final Map<String, UUID> SESSION_TO_ORDER = new ConcurrentHashMap<>();
    private static String lastSessionId;
    private static UUID lastOrderId;
    private static String lastCancelUrl;
    private static PaymentWebhookStatus nextWebhookStatus = PaymentWebhookStatus.SUCCEEDED;

    @Override
    public PaymentSessionResult createCheckoutSession(Order order, String successUrl, String cancelUrl) {
        String sessionId = "cs_test_fake_" + SESSION_COUNTER.incrementAndGet();
        SESSION_TO_ORDER.put(sessionId, order.id());
        lastSessionId = sessionId;
        lastOrderId = order.id();
        lastCancelUrl = cancelUrl;

        String checkoutUrl = "/test-stripe-redirect?session_id=" + sessionId;
        return new PaymentSessionResult(checkoutUrl, sessionId);
    }

    @Override
    public RefundResult createRefund(String stripePaymentIntentId, long amountInCents) {
        return new RefundResult("re_test_fake_" + System.nanoTime(), true);
    }

    @Override
    public PaymentWebhookResult parseWebhookEvent(String payload, String signature) {
        UUID orderId = UUID.fromString(payload);
        String paymentIntentId = "pi_test_fake_" + orderId;

        PaymentWebhookStatus status = nextWebhookStatus;
        return new PaymentWebhookResult(orderId, paymentIntentId, status);
    }

    public static String getLastSessionId() {
        return lastSessionId;
    }

    public static UUID getLastOrderId() {
        return lastOrderId;
    }

    public static String getLastCancelUrl() {
        return lastCancelUrl;
    }

    public static void setNextWebhookStatus(PaymentWebhookStatus status) {
        nextWebhookStatus = status;
    }

    public static void reset() {
        SESSION_TO_ORDER.clear();
        lastSessionId = null;
        lastOrderId = null;
        lastCancelUrl = null;
        nextWebhookStatus = PaymentWebhookStatus.SUCCEEDED;
    }
}
