package fr.sqq.achatgroupe.acceptance.steps;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.WaitForSelectorState;
import fr.sqq.achatgroupe.acceptance.support.TestContext;
import io.cucumber.java.fr.Alors;
import io.cucumber.java.fr.Et;
import io.cucumber.java.fr.Quand;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import jakarta.inject.Inject;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
public class AdminProductImageSteps {

    @Inject
    TestContext testContext;

    private Long imageProductId;

    private Page page() {
        return PlaywrightHooks.page();
    }

    @Quand("j'uploade une image PNG sur le premier produit via l'API")
    public void jUploadeUneImagePngSurLePremierProduitViaLApi() {
        imageProductId = testContext.productIds().get(0);
        Long venteId = testContext.venteId();

        File imageFile = loadTestResourceFile("images/test-product.png");

        RestAssured.given()
                .multiPart("image", imageFile, "image/png")
                .when()
                .post("/api/admin/ventes/" + venteId + "/products/" + imageProductId + "/image")
                .then()
                .statusCode(200);
    }

    @Alors("l'image est accessible via l'endpoint public")
    public void lImageEstAccessibleViaLEndpointPublic() {
        byte[] imageData = RestAssured.given()
                .when()
                .get("/api/product-images/" + imageProductId)
                .then()
                .statusCode(200)
                .contentType("image/png")
                .extract()
                .asByteArray();

        assertTrue(imageData.length > 0, "L'image retournée ne doit pas être vide");
    }

    @Et("la réponse produit contient l'URL de l'image")
    public void laReponseProduitContientLUrlDeLImage() {
        Long venteId = testContext.venteId();

        String imageUrl = RestAssured.given()
                .when()
                .get("/api/admin/ventes/" + venteId + "/products?size=100")
                .then()
                .statusCode(200)
                .extract()
                .jsonPath()
                .getString("data.find { it.id == " + imageProductId + " }.imageUrl");

        assertNotNull(imageUrl, "Le produit doit avoir une imageUrl");
        assertEquals("/api/product-images/" + imageProductId, imageUrl);
    }

    @Et("j'accède à la page du catalogue")
    public void jAccedeALaPageDuCatalogue() {
        page().navigate(PlaywrightHooks.testUrl() + "/ventes/" + testContext.venteId());
        page().waitForSelector("[data-testid='product-card']", new Page.WaitForSelectorOptions()
                .setState(WaitForSelectorState.VISIBLE)
                .setTimeout(15000));
    }

    @Alors("la carte du produit affiche l'image")
    public void laCarteDuProduitAfficheLImage() {
        Locator cards = page().locator("[data-testid='product-card']");
        assertTrue(cards.count() > 0, "Des cartes produit doivent être visibles");

        Locator productImage = cards.first().locator("img[src='/api/product-images/" + imageProductId + "']");
        assertTrue(productImage.count() > 0,
                "La carte du premier produit doit contenir une balise img avec l'URL de l'image");
    }

    private File loadTestResourceFile(String resourcePath) {
        URL resource = Thread.currentThread().getContextClassLoader().getResource(resourcePath);
        if (resource == null) {
            throw new IllegalArgumentException("Test resource not found: " + resourcePath);
        }
        try {
            return new File(resource.toURI());
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("Invalid resource URI: " + resourcePath, e);
        }
    }
}
