package fr.sqq.achatgroupe.acceptance.steps;

import fr.sqq.achatgroupe.acceptance.support.TestContext;
import fr.sqq.achatgroupe.application.port.out.ProductRepository;
import fr.sqq.achatgroupe.domain.model.catalog.Product;
import fr.sqq.achatgroupe.domain.model.catalog.ProductId;
import io.cucumber.java.fr.Alors;
import io.cucumber.java.fr.Quand;
import io.cucumber.java.fr.Étantdonnéque;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
public class StripeProductIdPreservationSteps {

    @Inject
    TestContext testContext;

    @Inject
    ProductRepository productRepository;

    private String originalStripeProductId;

    @Étantdonnéque("le premier produit possède un stripeProductId en base")
    public void lePremierProduitPossedeUnStripeProductIdEnBase() {
        Long productId = testContext.productIds().get(0);
        Product product = productRepository.findById(new ProductId(productId))
                .orElseThrow(() -> new AssertionError("Le produit " + productId + " n'existe pas en base"));

        originalStripeProductId = product.stripeProductId();
        assertNotNull(originalStripeProductId, "Le produit doit avoir un stripeProductId après création");
        assertFalse(originalStripeProductId.isBlank(), "Le stripeProductId ne doit pas être vide");
    }

    @Quand("je modifie le premier produit via l'API")
    public void jeModifieLePremierProduitViaLApi() {
        Long productId = testContext.productIds().get(0);
        Long venteId = testContext.venteId();

        RestAssured.given()
                .contentType(ContentType.JSON)
                .body("""
                        {
                            "name": "Tomates bio modifiées",
                            "description": "Description mise à jour",
                            "prixHt": 3.32,
                            "tauxTva": 5.50,
                            "supplier": "Ferme du Soleil",
                            "stock": 25,
                            "active": true,
                            "reference": "TOM-001",
                            "category": "Légumes",
                            "brand": "Ferme du Soleil",
                            "colisage": 6
                        }
                        """)
                .when()
                .put("/api/admin/ventes/" + venteId + "/products/" + productId)
                .then()
                .statusCode(200);
    }

    @Alors("le stripeProductId du premier produit est inchangé en base")
    public void leStripeProductIdDuPremierProduitEstInchangeEnBase() {
        Long productId = testContext.productIds().get(0);
        Product product = productRepository.findById(new ProductId(productId))
                .orElseThrow(() -> new AssertionError("Le produit " + productId + " n'existe pas en base"));

        assertEquals(originalStripeProductId, product.stripeProductId(),
                "Le stripeProductId doit être préservé après l'opération");
    }

}
