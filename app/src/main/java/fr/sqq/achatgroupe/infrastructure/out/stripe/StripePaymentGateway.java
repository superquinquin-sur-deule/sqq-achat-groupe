package fr.sqq.achatgroupe.infrastructure.out.stripe;

import com.stripe.Stripe;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import com.stripe.param.checkout.SessionCreateParams;
import fr.sqq.achatgroupe.domain.exception.PaymentSessionCreationException;
import fr.sqq.achatgroupe.domain.exception.PaymentWebhookException;
import fr.sqq.achatgroupe.domain.model.order.Order;
import fr.sqq.achatgroupe.application.port.out.PaymentGateway;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import java.util.UUID;

@ApplicationScoped
public class StripePaymentGateway implements PaymentGateway {

    private static final Logger LOG = Logger.getLogger(StripePaymentGateway.class);

    @ConfigProperty(name = "stripe.api-key")
    String apiKey;

    @ConfigProperty(name = "stripe.webhook-secret")
    String webhookSecret;

    @PostConstruct
    void init() {
        Stripe.apiKey = apiKey;
    }

    @Override
    public PaymentSessionResult createCheckoutSession(Order order, String successUrl, String cancelUrl) {
        long amountInCents = order.totalAmount().movePointRight(2).longValueExact();

        SessionCreateParams params = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl(successUrl)
                .setCancelUrl(cancelUrl)
                .addLineItem(
                        SessionCreateParams.LineItem.builder()
                                .setPriceData(
                                        SessionCreateParams.LineItem.PriceData.builder()
                                                .setCurrency("eur")
                                                .setUnitAmount(amountInCents)
                                                .setProductData(
                                                        SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                .setName("Commande " + order.orderNumber().value())
                                                                .build())
                                                .build())
                                .setQuantity(1L)
                                .build())
                .putMetadata("order_id", order.id().toString())
                .setCustomerEmail(order.customer().email())
                .build();

        try {
            Session session = Session.create(params);
            LOG.infof("Stripe Checkout Session créée pour commande %s : %s", order.id(), session.getId());
            return new PaymentSessionResult(session.getUrl(), session.getId());
        } catch (StripeException e) {
            LOG.errorf(e, "Erreur Stripe lors de la création de la session pour commande %s", order.id());
            throw new PaymentSessionCreationException(order.id());
        }
    }

    @Override
    public PaymentWebhookResult parseWebhookEvent(String payload, String signature) {
        Event event;
        try {
            event = Webhook.constructEvent(payload, signature, webhookSecret);
        } catch (SignatureVerificationException e) {
            throw new PaymentWebhookException("Signature webhook invalide");
        }

        String eventType = event.getType();

        if (!"checkout.session.completed".equals(eventType) && !"checkout.session.expired".equals(eventType)) {
            return new PaymentWebhookResult(null, null, PaymentWebhookStatus.IGNORED);
        }

        Session session = (Session) event.getDataObjectDeserializer()
                .getObject()
                .orElseThrow(() -> new PaymentWebhookException("Impossible de désérialiser la session Stripe"));

        String orderId = session.getMetadata().get("order_id");
        if (orderId == null) {
            throw new PaymentWebhookException("Metadata order_id manquant dans la session Stripe");
        }

        PaymentWebhookStatus status;
        String stripePaymentId = session.getPaymentIntent();

        if ("checkout.session.completed".equals(eventType) && "paid".equals(session.getPaymentStatus())) {
            status = PaymentWebhookStatus.SUCCEEDED;
        } else {
            status = PaymentWebhookStatus.FAILED;
        }

        LOG.infof("Webhook Stripe reçu : type=%s, orderId=%s, paymentIntent=%s, paymentStatus=%s",
                eventType, orderId, stripePaymentId, session.getPaymentStatus());

        try {
            return new PaymentWebhookResult(UUID.fromString(orderId), stripePaymentId, status);
        } catch (IllegalArgumentException e) {
            throw new PaymentWebhookException("Metadata order_id invalide : " + orderId);
        }
    }
}
