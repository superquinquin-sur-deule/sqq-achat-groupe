package fr.sqq.achatgroupe.infrastructure.out.stripe;

import com.stripe.Stripe;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import com.stripe.param.ProductCreateParams;
import com.stripe.param.RefundCreateParams;
import com.stripe.param.checkout.SessionCreateParams;
import fr.sqq.achatgroupe.application.port.out.PaymentCatalogGateway;
import fr.sqq.achatgroupe.application.port.out.PaymentGateway;
import fr.sqq.achatgroupe.application.port.out.ProductRepository;
import fr.sqq.achatgroupe.domain.exception.PaymentSessionCreationException;
import fr.sqq.achatgroupe.domain.exception.PaymentWebhookException;
import fr.sqq.achatgroupe.domain.model.catalog.Product;
import fr.sqq.achatgroupe.domain.model.catalog.ProductId;
import fr.sqq.achatgroupe.domain.model.order.Order;
import fr.sqq.achatgroupe.domain.model.order.OrderItem;
import io.quarkus.logging.Log;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.math.BigDecimal;
import java.util.UUID;

@ApplicationScoped
public class StripePaymentGateway implements PaymentGateway, PaymentCatalogGateway {
    
    @ConfigProperty(name = "stripe.api-key")
    String apiKey;

    @ConfigProperty(name = "stripe.webhook-secret")
    String webhookSecret;

    @Inject
    ProductRepository productRepository;

    @PostConstruct
    void init() {
        Stripe.apiKey = apiKey;
    }

    @Override
    public PaymentSessionResult createCheckoutSession(Order order, String successUrl, String cancelUrl) {
        SessionCreateParams.Builder paramsBuilder = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setAutomaticTax(SessionCreateParams.AutomaticTax.builder().setEnabled(true).build())
                .setSuccessUrl(successUrl)
                .setCancelUrl(cancelUrl)
                .putMetadata("order_id", order.id().toString())
                .setCustomerEmail(order.customer().email());

        for (OrderItem item : order.items()) {
            SessionCreateParams.LineItem.PriceData.Builder priceDataBuilder =
                    SessionCreateParams.LineItem.PriceData.builder()
                            .setCurrency("eur")
                            .setUnitAmount(item.unitPrice().toCents());

            String stripeProductId = productRepository.findById(new ProductId(item.productId()))
                    .map(Product::stripeProductId)
                    .orElse(null);

            if (stripeProductId != null) {
                priceDataBuilder.setProduct(stripeProductId);
            } else {
                priceDataBuilder.setProductData(
                        SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                .setName("Produit #" + item.productId())
                                .build());
            }

            paramsBuilder.addLineItem(
                    SessionCreateParams.LineItem.builder()
                            .setPriceData(priceDataBuilder.build())
                            .setQuantity((long) item.quantity())
                            .build());
        }

        SessionCreateParams params = paramsBuilder.build();

        try {
            Session session = Session.create(params);
            Log.infof("Stripe Checkout Session créée pour commande %s : %s", order.id(), session.getId());
            return new PaymentSessionResult(session.getUrl(), session.getId());
        } catch (StripeException e) {
            Log.errorf(e, "Erreur Stripe lors de la création de la session pour commande %s", order.id());
            throw new PaymentSessionCreationException(order.id());
        }
    }

    @Override
    public RefundResult createRefund(String stripePaymentIntentId, long amountInCents) {
        RefundCreateParams params = RefundCreateParams.builder()
                .setPaymentIntent(stripePaymentIntentId)
                .setAmount(amountInCents)
                .build();

        try {
            com.stripe.model.Refund stripeRefund = com.stripe.model.Refund.create(params);
            Log.infof("Remboursement Stripe créé : %s pour payment intent %s", stripeRefund.getId(), stripePaymentIntentId);
            return new RefundResult(stripeRefund.getId(), "succeeded".equals(stripeRefund.getStatus()));
        } catch (StripeException e) {
            Log.errorf(e, "Erreur Stripe lors du remboursement pour payment intent %s", stripePaymentIntentId);
            return new RefundResult(null, false);
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

        Log.infof("Webhook Stripe reçu : type=%s, orderId=%s, paymentIntent=%s, paymentStatus=%s",
                eventType, orderId, stripePaymentId, session.getPaymentStatus());

        try {
            return new PaymentWebhookResult(UUID.fromString(orderId), stripePaymentId, status);
        } catch (IllegalArgumentException e) {
            throw new PaymentWebhookException("Metadata order_id invalide : " + orderId);
        }
    }

    @Override
    public String registerProduct(Long productId, String name, String description,
                                  BigDecimal prixHt, BigDecimal tauxTva, BigDecimal prixTtc, String reference,
                                  String stripeTaxCode) {
        long prixTtcInCents = prixTtc.movePointRight(2).longValueExact();

        ProductCreateParams.Builder builder = ProductCreateParams.builder()
                .setName(name)
                .setDefaultPriceData(
                        ProductCreateParams.DefaultPriceData.builder()
                                .setCurrency("eur")
                                .setTaxBehavior(ProductCreateParams.DefaultPriceData.TaxBehavior.INCLUSIVE)
                                .setUnitAmount(prixTtcInCents)
                                .build())
                .putMetadata("product_id", String.valueOf(productId))
                .putMetadata("reference", reference)
                .putMetadata("prix_ht", prixHt.toPlainString())
                .putMetadata("taux_tva", tauxTva.toPlainString())
                .putMetadata("tax_percent", tauxTva.toPlainString());

        if (stripeTaxCode != null) {
            builder.setTaxCode(stripeTaxCode);
        }

        ProductCreateParams params = builder.build();

        try {
            com.stripe.model.Product stripeProduct = com.stripe.model.Product.create(params);
            Log.infof("Produit Stripe créé : %s pour produit local %d", stripeProduct.getId(), productId);
            return stripeProduct.getId();
        } catch (StripeException e) {
            Log.errorf(e, "Erreur Stripe lors de la création du produit %d", productId);
            throw new RuntimeException("Impossible de créer le produit Stripe pour le produit " + productId, e);
        }
    }
}
