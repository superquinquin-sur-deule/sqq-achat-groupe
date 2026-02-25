package fr.sqq.achatgroupe.acceptance.support;

import fr.sqq.achatgroupe.application.port.out.PaymentCatalogGateway;
import io.quarkus.test.Mock;
import jakarta.annotation.Priority;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Alternative;

import java.math.BigDecimal;

@Mock
@Alternative
@Priority(1)
@ApplicationScoped
public class FakePaymentCatalogGateway implements PaymentCatalogGateway {

    @Override
    public String registerProduct(Long productId, String name, String description,
                                  BigDecimal prixHt, BigDecimal tauxTva, BigDecimal prixTtc, String reference) {
        return "prod_test_fake_" + productId;
    }
}
