package fr.sqq.achatgroupe.acceptance.steps;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.options.WaitForSelectorState;
import io.cucumber.java.fr.Alors;
import io.cucumber.java.fr.Et;
import io.cucumber.java.fr.Quand;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;

import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@QuarkusTest
public class InStorePaymentSteps {

    private static final Pattern ORDER_ID_IN_URL = Pattern.compile("orderId=([0-9a-fA-F-]{36})");

    private int lastStatusCode;

    @Alors("le récapitulatif indique que le règlement se fait en magasin")
    public void leRecapitulatifIndiqueLeReglementEnMagasin() {
        Locator notice = PlaywrightHooks.page().locator("[data-testid='in-store-payment-notice']");
        notice.waitFor(new Locator.WaitForOptions()
                .setState(WaitForSelectorState.VISIBLE)
                .setTimeout(5000));
        String text = notice.textContent();
        assertNotNull(text);
        assertTrue(text.contains("en magasin"),
                "Le récapitulatif doit annoncer le règlement en magasin, got: " + text);
    }

    @Et("le bouton de validation affiche {string}")
    public void leBoutonDeValidationAffiche(String label) {
        Locator button = PlaywrightHooks.page().locator("button:has-text('" + label + "')");
        button.first().waitFor(new Locator.WaitForOptions()
                .setState(WaitForSelectorState.VISIBLE)
                .setTimeout(5000));
        assertEquals(1, button.count(), "Le bouton \"" + label + "\" doit être présent une fois");
    }

    @Et("la page de confirmation indique que le règlement se fait en magasin")
    public void laPageDeConfirmationIndiqueLeReglementEnMagasin() {
        leRecapitulatifIndiqueLeReglementEnMagasin();
    }

    @Et("la commande confirmée est en statut {string}")
    public void laCommandeConfirmeeEstEnStatut(String expectedStatus) {
        Matcher matcher = ORDER_ID_IN_URL.matcher(PlaywrightHooks.page().url());
        assertTrue(matcher.find(), "L'URL de confirmation doit porter l'identifiant de commande : "
                + PlaywrightHooks.page().url());

        String status = RestAssured.given()
                .when()
                .get("/api/orders/" + matcher.group(1))
                .then()
                .statusCode(200)
                .extract()
                .path("data.status");

        assertEquals(expectedStatus, status);
    }

    @Quand("j'appelle l'ancienne API d'initiation de paiement")
    public void jAppelleLAncienneApiDInitiationDePaiement() {
        lastStatusCode = RestAssured.given()
                .contentType("application/json")
                .body("{\"successUrl\":\"http://localhost:8081/confirmation\",\"cancelUrl\":\"http://localhost:8081/\"}")
                .when()
                .post("/api/orders/" + UUID.randomUUID() + "/payment")
                .then()
                .extract()
                .statusCode();
    }

    @Alors("l'API répond {int}")
    public void lApiRepond(int expectedStatus) {
        assertEquals(expectedStatus, lastStatusCode);
    }
}
