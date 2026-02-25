package fr.sqq.achatgroupe.application.port.out;

import java.math.BigDecimal;

public interface PaymentCatalogGateway {

    String registerProduct(Long productId, String name, String description,
                           BigDecimal prixHt, BigDecimal tauxTva, BigDecimal prixTtc, String reference);
}
