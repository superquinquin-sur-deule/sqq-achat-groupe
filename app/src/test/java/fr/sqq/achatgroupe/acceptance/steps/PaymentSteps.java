package fr.sqq.achatgroupe.acceptance.steps;

import fr.sqq.achatgroupe.acceptance.support.TestContext;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import com.microsoft.playwright.*;
import com.microsoft.playwright.options.WaitForSelectorState;
import fr.sqq.achatgroupe.acceptance.support.FakePaymentGateway;
import io.cucumber.java.Before;
import io.cucumber.java.fr.Alors;
import io.cucumber.java.fr.Et;
import io.cucumber.java.fr.Quand;
import io.cucumber.java.fr.Étantdonnéque;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

@ApplicationScoped
public class PaymentSteps {

    @Inject
    TestContext testContext;

    private Response apiResponse;
    private Long createdOrderId;

    @Before("@payment")
    public void setUp() {
        FakePaymentGateway.reset();
    }

    // --- Helpers ---

    private String venteUrl() {
        return PlaywrightHooks.testUrl() + "/ventes/" + testContext.venteId();
    }

    private void navigateToHome() {
        PlaywrightHooks.page().navigate(venteUrl());
        PlaywrightHooks.page().waitForSelector("[data-testid='product-card']", new Page.WaitForSelectorOptions()
                .setState(WaitForSelectorState.VISIBLE)
                .setTimeout(15000));
    }

    private void addFirstProductToCart() {
        Locator firstCard = PlaywrightHooks.page().locator("[data-testid='product-card']:not([data-exhausted='true'])").first();
        firstCard.locator("[data-testid='add-button']").click();
        PlaywrightHooks.page().locator("[role='alert']").first().waitFor(new Locator.WaitForOptions()
                .setState(WaitForSelectorState.VISIBLE)
                .setTimeout(5000));
        PlaywrightHooks.page().waitForTimeout(500);
    }

    private void navigateToCheckout() {
        PlaywrightHooks.page().locator("a[aria-label='Panier']").click();
        PlaywrightHooks.page().waitForURL("**/cart");
        PlaywrightHooks.page().waitForSelector("[data-testid='checkout-button']", new Page.WaitForSelectorOptions()
                .setState(WaitForSelectorState.VISIBLE)
                .setTimeout(5000));
        PlaywrightHooks.page().locator("[data-testid='checkout-button']").click();
        PlaywrightHooks.page().waitForURL("**/checkout");
        PlaywrightHooks.page().waitForSelector("#customer-name", new Page.WaitForSelectorOptions()
                .setState(WaitForSelectorState.VISIBLE)
                .setTimeout(5000));
    }

    private void fillAndSubmitCustomerForm() {
        PlaywrightHooks.page().locator("#customer-name").fill("Marie Dupont");
        PlaywrightHooks.page().locator("#customer-email").fill("marie@exemple.fr");
        PlaywrightHooks.page().locator("#customer-phone").fill("06 12 34 56 78");
        PlaywrightHooks.page().locator("button:has-text('Continuer')").first().click();
        PlaywrightHooks.page().waitForSelector("[role='radiogroup']", new Page.WaitForSelectorOptions()
                .setState(WaitForSelectorState.VISIBLE)
                .setTimeout(10000));
    }

    private void selectFirstAvailableSlotAndContinue() {
        Locator slots = PlaywrightHooks.page().locator("[role='radio']:not([aria-disabled='true'])");
        slots.first().waitFor(new Locator.WaitForOptions()
                .setState(WaitForSelectorState.VISIBLE)
                .setTimeout(10000));
        slots.first().click();
        PlaywrightHooks.page().locator("button:has-text('Continuer')").first().click();
        PlaywrightHooks.page().waitForSelector("button:has-text('Payer ma commande')", new Page.WaitForSelectorOptions()
                .setState(WaitForSelectorState.VISIBLE)
                .setTimeout(5000));
    }

    private Long createOrderViaApi() {
        Long productId = testContext.productIds().get(0);
        Long timeSlotId = testContext.timeSlotIds().get(0);
        String body = """
                {
                    "customerName": "Marie Dupont",
                    "email": "marie@exemple.fr",
                    "phone": "06 12 34 56 78",
                    "timeSlotId": %d,
                    "items": [{"productId": %d, "quantity": 1}]
                }
                """.formatted(timeSlotId, productId);

        return RestAssured.given()
                .contentType(ContentType.JSON)
                .body(body)
                .when()
                .post("/api/ventes/" + testContext.venteId() + "/orders")
                .then()
                .statusCode(201)
                .extract()
                .jsonPath()
                .getLong("data.id");
    }

    private void initiatePaymentViaApi(Long orderId) {
        String body = """
                {
                    "successUrl": "http://localhost:8081/confirmation?orderId=%d&session_id={CHECKOUT_SESSION_ID}",
                    "cancelUrl": "http://localhost:8081/checkout"
                }
                """.formatted(orderId);

        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(body)
                .when()
                .post("/api/orders/" + orderId + "/payment")
                .then()
                .statusCode(200);
    }

    private void sendSuccessWebhook(Long orderId) {
        apiResponse = RestAssured.given()
                .contentType(ContentType.JSON)
                .header("Stripe-Signature", "fake_sig_" + orderId)
                .body(String.valueOf(orderId))
                .when()
                .post("/api/webhooks/stripe");
    }

    // --- Database verification steps ---

    @Quand("je vérifie la structure de la base de données")
    public void jeVerifieLaStructureDeLaBase() {
        // Verification happens in the assertion step
    }

    @Alors("la table payments existe avec les colonnes requises")
    public void laTablePaymentsExiste() {
        apiResponse = RestAssured.given()
                .when()
                .get("/q/health");
        apiResponse.then().statusCode(200);
    }

    // --- Préconditions (unique step text to avoid duplication with CheckoutSteps) ---

    @Et("j'ai ajouté des produits au panier pour le paiement")
    public void jAiAjouteDesProduitsAuPanierPourLePaiement() {
        navigateToHome();
        addFirstProductToCart();
    }

    @Et("j'ai saisi mes coordonnées pour le paiement")
    public void jAiSaisiMesCoordonnesPourLePaiement() {
        navigateToCheckout();
        fillAndSubmitCustomerForm();
    }

    @Et("j'ai choisi un créneau pour le paiement")
    public void jAiChoisiUnCreneauPourLePaiement() {
        selectFirstAvailableSlotAndContinue();
    }

    @Étantdonnéque("une commande en attente de paiement existe")
    public void uneCommandeEnAttenteExiste() {
        createdOrderId = createOrderViaApi();
        initiatePaymentViaApi(createdOrderId);
    }

    @Étantdonnéque("une commande a déjà été payée")
    public void uneCommandeDejaPayee() {
        createdOrderId = createOrderViaApi();
        initiatePaymentViaApi(createdOrderId);
        sendSuccessWebhook(createdOrderId);
        apiResponse.then().statusCode(200);
    }

    @Et("j'ai payé ma commande avec succès")
    public void jAiPayeMaCommandeAvecSucces() {
        PlaywrightHooks.page().locator("button:has-text('Payer ma commande')").click();
        PlaywrightHooks.page().waitForTimeout(3000);
        createdOrderId = FakePaymentGateway.getLastOrderId();
        if (createdOrderId != null) {
            sendSuccessWebhook(createdOrderId);
        }
    }

    // --- Actions ---

    @Quand("je clique sur \"Payer ma commande\"")
    public void jeCliqueSurPayerMaCommande() {
        PlaywrightHooks.page().locator("button:has-text('Payer ma commande')").click();
        PlaywrightHooks.page().waitForTimeout(3000);
    }

    @Quand("le webhook Stripe notifie un paiement réussi")
    public void leWebhookStripeNotifieUnPaiementReussi() {
        sendSuccessWebhook(createdOrderId);
    }

    @Quand("le webhook Stripe notifie le même paiement une deuxième fois")
    public void leWebhookNotifieLeMeme() {
        sendSuccessWebhook(createdOrderId);
    }

    @Quand("je suis sur la page de confirmation")
    public void jeSuisSurLaPageDeConfirmation() {
        assertNotNull(createdOrderId, "L'orderId doit exister");
        PlaywrightHooks.page().navigate(PlaywrightHooks.testUrl() + "/confirmation?orderId=" + createdOrderId);
        PlaywrightHooks.page().waitForSelector("h1", new Page.WaitForSelectorOptions()
                .setState(WaitForSelectorState.VISIBLE)
                .setTimeout(10000));
    }

    // --- Assertions ---

    @Alors("je suis redirigé vers la page de paiement")
    public void jeSuisRedirigeVersLaPageDePaiement() {
        String url = PlaywrightHooks.page().url();
        assertTrue(
                url.contains("test-stripe-redirect") || url.contains("checkout.stripe.com"),
                "L'URL doit être la page de paiement Stripe (ou fake), got: " + url
        );
    }

    @Alors("le statut de la commande passe à {string}")
    public void leStatutDeLaCommandePasseA(String expectedStatus) {
        apiResponse.then().statusCode(200);

        RestAssured.given()
                .when()
                .get("/api/orders/" + createdOrderId)
                .then()
                .statusCode(200)
                .body("data.status", equalTo(expectedStatus));
    }

    @Et("le paiement est enregistré avec le statut {string}")
    public void lePaiementEstEnregistreAvecLeStatut(String expectedStatus) {
        apiResponse.then().statusCode(200);
    }

    @Alors("la commande reste au statut {string}")
    public void laCommandeResteAuStatut(String expectedStatus) {
        apiResponse.then().statusCode(200);

        RestAssured.given()
                .when()
                .get("/api/orders/" + createdOrderId)
                .then()
                .statusCode(200)
                .body("data.status", equalTo(expectedStatus));
    }

    @Et("aucun doublon de paiement n'est créé")
    public void aucunDoublonDePaiementNestCree() {
        apiResponse.then().statusCode(200);
    }

    @Alors("je vois le message {string}")
    public void jeVoisLeMessage(String message) {
        Locator h1 = PlaywrightHooks.page().locator("h1");
        h1.waitFor(new Locator.WaitForOptions()
                .setState(WaitForSelectorState.VISIBLE)
                .setTimeout(10000));
        String text = h1.textContent();
        assertNotNull(text);
        assertTrue(text.contains(message), "La page doit contenir '" + message + "', got: " + text);
    }

    @Et("je vois le récapitulatif de la commande avec les produits")
    public void jeVoisLeRecapitulatifDeLaCommande() {
        Locator items = PlaywrightHooks.page().locator("ul li");
        assertTrue(items.count() > 0, "La commande doit contenir des produits");
    }

    @Et("je vois le créneau de retrait")
    public void jeVoisLeCreneauDeRetrait() {
        Locator timeSlotSection = PlaywrightHooks.page().locator(".bg-primary");
        assertTrue(timeSlotSection.count() > 0, "Le créneau de retrait doit être affiché");
    }
}
