package fr.sqq.achatgroupe.acceptance.steps;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.WaitForSelectorState;
import fr.sqq.achatgroupe.acceptance.support.FakePaymentGateway;
import fr.sqq.achatgroupe.acceptance.support.TestContext;
import fr.sqq.achatgroupe.application.port.out.PaymentGateway.PaymentWebhookStatus;
import io.cucumber.java.Before;
import io.cucumber.java.fr.Alors;
import io.cucumber.java.fr.Et;
import io.cucumber.java.fr.Quand;
import io.cucumber.java.fr.Étantdonnéque;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;

import static org.junit.jupiter.api.Assertions.assertTrue;

@QuarkusTest
public class PaymentErrorSteps {

    @Inject
    TestContext testContext;

    private String orderId;

    @Before("@payment-error")
    public void setUp() {
        FakePaymentGateway.reset();
    }

    // --- Helpers (webhooks via REST — tolerated) ---

    private void createOrderAndInitiatePaymentViaBrowser() {
        Page page = PlaywrightHooks.page();
        String venteUrl = PlaywrightHooks.testUrl() + "/ventes/" + testContext.venteId();

        page.navigate(venteUrl);
        page.waitForSelector("[data-testid='product-card']", new Page.WaitForSelectorOptions()
                .setState(WaitForSelectorState.VISIBLE)
                .setTimeout(15000));

        // Add the first available product to the cart
        Locator firstCard = page.locator("[data-testid='product-card']:not([data-exhausted='true'])").first();
        firstCard.locator("[data-testid='add-button']").click();
        page.locator("[role='alert']").first().waitFor(new Locator.WaitForOptions()
                .setState(WaitForSelectorState.VISIBLE)
                .setTimeout(5000));
        page.waitForTimeout(500);

        // Navigate to cart then checkout
        page.locator("a[aria-label='Panier']").click();
        page.waitForURL("**/cart");
        page.waitForSelector("[data-testid='checkout-button']", new Page.WaitForSelectorOptions()
                .setState(WaitForSelectorState.VISIBLE)
                .setTimeout(5000));
        page.locator("[data-testid='checkout-button']").click();
        page.waitForURL("**/checkout");
        page.waitForSelector("#customer-last-name", new Page.WaitForSelectorOptions()
                .setState(WaitForSelectorState.VISIBLE)
                .setTimeout(5000));

        // Fill customer form
        page.locator("#customer-last-name").fill("Dupont");
        page.locator("#customer-first-name").fill("Marie");
        page.locator("#customer-email").fill("marie@exemple.fr");
        page.locator("#customer-phone").fill("06 12 34 56 78");
        page.locator("button:has-text('Continuer')").first().click();
        page.waitForSelector("[role='radiogroup']", new Page.WaitForSelectorOptions()
                .setState(WaitForSelectorState.VISIBLE)
                .setTimeout(10000));

        // Select first available time slot
        Locator slots = page.locator("[role='radio']:not([aria-disabled='true'])");
        slots.first().waitFor(new Locator.WaitForOptions()
                .setState(WaitForSelectorState.VISIBLE)
                .setTimeout(10000));
        slots.first().click();
        page.locator("button:has-text('Continuer')").first().click();
        page.waitForSelector("button:has-text('Payer ma commande')", new Page.WaitForSelectorOptions()
                .setState(WaitForSelectorState.VISIBLE)
                .setTimeout(5000));

        // Click pay — creates the order + initiates payment
        page.locator("button:has-text('Payer ma commande')").click();
        page.waitForTimeout(3000);

        // Capture the order ID from the FakePaymentGateway
        orderId = FakePaymentGateway.getLastOrderId().toString();
    }

    private void sendFailureWebhook() {
        java.util.UUID internalOrderId = FakePaymentGateway.getLastOrderId();
        FakePaymentGateway.setNextWebhookStatus(PaymentWebhookStatus.FAILED);
        RestAssured.given()
                .contentType(ContentType.JSON)
                .header("Stripe-Signature", "fake_sig_" + internalOrderId)
                .body(internalOrderId.toString())
                .when()
                .post("/api/webhooks/stripe")
                .then()
                .statusCode(200);
    }

    private void initiatePaymentViaApi() {
        String body = """
                {
                    "successUrl": "http://localhost:8081/confirmation?orderId=%s&session_id={CHECKOUT_SESSION_ID}",
                    "cancelUrl": "http://localhost:8081/payment-error?orderId=%s"
                }
                """.formatted(orderId, orderId);

        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(body)
                .when()
                .post("/api/orders/" + orderId + "/payment")
                .then()
                .statusCode(200);
    }

    // --- Steps ---

    @Étantdonnéque("une commande avec un premier paiement échoué existe")
    public void uneCommandeAvecUnPremierPaiementEchoueExiste() {
        createOrderAndInitiatePaymentViaBrowser();
        sendFailureWebhook();
    }

    @Étantdonnéque("une commande avec deux paiements échoués existe")
    public void uneCommandeAvecDeuxPaiementsEchouesExiste() {
        createOrderAndInitiatePaymentViaBrowser();

        // First attempt
        sendFailureWebhook();

        // Second attempt
        initiatePaymentViaApi();
        sendFailureWebhook();
    }

    @Quand("je navigue vers la page d'erreur de paiement")
    public void jeNavigueVersLaPageDErreurDePaiement() {
        PlaywrightHooks.page().navigate(PlaywrightHooks.testUrl() + "/payment-error?orderId=" + orderId);
        PlaywrightHooks.page().waitForSelector("[data-testid='payment-error'], [data-testid='payment-cancelled']", new Page.WaitForSelectorOptions()
                .setState(WaitForSelectorState.VISIBLE)
                .setTimeout(15000));
    }

    @Alors("je vois le conteneur d'erreur de paiement")
    public void jeVoisLeConteneurDErreurDePaiement() {
        Locator errorContainer = PlaywrightHooks.page().locator("[data-testid='payment-error']");
        assertTrue(errorContainer.isVisible(), "Le conteneur d'erreur de paiement doit être visible");
    }

    @Et("je vois le titre d'erreur {string}")
    public void jeVoisLeTitreDErreur(String message) {
        Locator h1 = PlaywrightHooks.page().locator("[data-testid='payment-error'] h1");
        assertTrue(h1.isVisible(), "Le titre d'erreur doit être visible");
        assertTrue(h1.textContent().contains(message),
                "Le titre doit contenir '" + message + "', got: " + h1.textContent());
    }

    @Et("je vois le compteur de tentatives {string}")
    public void jeVoisLeCompteurDeTentatives(String expectedText) {
        Locator page = PlaywrightHooks.page().locator("[data-testid='payment-error']");
        assertTrue(page.textContent().contains(expectedText),
                "La page doit contenir '" + expectedText + "', got: " + page.textContent());
    }

    @Et("je clique sur le bouton réessayer")
    public void jeCliqueSurLeBoutonReessayer() {
        Locator retryButton = PlaywrightHooks.page().locator("[data-testid='retry-button']");
        assertTrue(retryButton.isVisible(), "Le bouton réessayer doit être visible");
        retryButton.click();
        PlaywrightHooks.page().waitForTimeout(3000);
    }

    // "je suis redirigé vers la page de paiement" is defined in PaymentSteps

    @Alors("je vois le conteneur de commande annulée")
    public void jeVoisLeConteneurDeCommandeAnnulee() {
        Locator cancelledContainer = PlaywrightHooks.page().locator("[data-testid='payment-cancelled']");
        assertTrue(cancelledContainer.isVisible(), "Le conteneur de commande annulée doit être visible");
    }

    @Et("je vois le titre d'annulation {string}")
    public void jeVoisLeTitreDAnnulation(String message) {
        Locator h1 = PlaywrightHooks.page().locator("[data-testid='payment-cancelled'] h1");
        assertTrue(h1.isVisible(), "Le titre d'annulation doit être visible");
        assertTrue(h1.textContent().contains(message),
                "Le titre doit contenir '" + message + "', got: " + h1.textContent());
    }

    @Et("le bouton réessayer est visible")
    public void leBoutonReessayerEstVisible() {
        Locator retryButton = PlaywrightHooks.page().locator("[data-testid='retry-button']");
        assertTrue(retryButton.isVisible(), "Le bouton réessayer doit être visible");
    }
}
