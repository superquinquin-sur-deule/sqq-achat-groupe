package fr.sqq.achatgroupe.infrastructure.out.stripe;

import com.stripe.exception.StripeException;
import com.stripe.param.ProductCreateParams;
import fr.sqq.achatgroupe.application.port.out.PaymentCatalogGateway;
import jakarta.enterprise.context.ApplicationScoped;
import org.jboss.logging.Logger;

import java.math.BigDecimal;

@ApplicationScoped
public class StripePaymentCatalogGateway implements PaymentCatalogGateway {

    private static final Logger LOG = Logger.getLogger(StripePaymentCatalogGateway.class);

    @Override
    public String registerProduct(Long productId, String name, String description,
                                  BigDecimal prixHt, BigDecimal tauxTva, BigDecimal prixTtc, String reference) {
        long prixTtcInCents = prixTtc.movePointRight(2).longValueExact();

        ProductCreateParams params = ProductCreateParams.builder()
                .setName(name)
                .setDescription(description)
                .setDefaultPriceData(
                        ProductCreateParams.DefaultPriceData.builder()
                                .setCurrency("eur")
                                .setUnitAmount(prixTtcInCents)
                                .build())
                .putMetadata("product_id", String.valueOf(productId))
                .putMetadata("reference", reference)
                .putMetadata("prix_ht", prixHt.toPlainString())
                .putMetadata("taux_tva", tauxTva.toPlainString())
                .build();

        try {
            com.stripe.model.Product stripeProduct = com.stripe.model.Product.create(params);
            LOG.infof("Produit Stripe créé : %s pour produit local %d", stripeProduct.getId(), productId);
            return stripeProduct.getId();
        } catch (StripeException e) {
            LOG.errorf(e, "Erreur Stripe lors de la création du produit %d", productId);
            throw new RuntimeException("Impossible de créer le produit Stripe pour le produit " + productId, e);
        }
    }
}
