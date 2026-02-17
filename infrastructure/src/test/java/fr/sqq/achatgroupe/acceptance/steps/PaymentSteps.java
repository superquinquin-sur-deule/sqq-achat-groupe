package fr.sqq.achatgroupe.acceptance.steps;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.WaitForSelectorState;
import fr.sqq.achatgroupe.acceptance.support.FakePaymentGateway;
import fr.sqq.achatgroupe.acceptance.support.TestContext;
import io.cucumber.java.Before;
import io.cucumber.java.fr.Alors;
import io.cucumber.java.fr.Et;
import io.cucumber.java.fr.Quand;
import io.cucumber.java.fr.Étantdonnéque;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import jakarta.inject.Inject;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@QuarkusTest
public class PaymentSteps {

    @Inject
    TestContext testContext;

    private Response apiResponse;
    private String createdOrderId;

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
        PlaywrightHooks.page().waitForSelector("#customer-last-name", new Page.WaitForSelectorOptions()
                .setState(WaitForSelectorState.VISIBLE)
                .setTimeout(5000));
    }

    private void fillAndSubmitCustomerForm() {
        PlaywrightHooks.page().locator("#customer-last-name").fill("Dupont");
        PlaywrightHooks.page().locator("#customer-first-name").fill("Marie");
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

    private void createOrderViaBrowser() {
        navigateToHome();
        addFirstProductToCart();
        navigateToCheckout();
        fillAndSubmitCustomerForm();
        selectFirstAvailableSlotAndContinue();

        // Click pay — creates the order + initiates payment
        PlaywrightHooks.page().locator("button:has-text('Payer ma commande')").click();
        PlaywrightHooks.page().waitForTimeout(3000);

        createdOrderId = FakePaymentGateway.getLastOrderId().toString();
    }

    private void sendSuccessWebhook() {
        java.util.UUID internalOrderId = FakePaymentGateway.getLastOrderId();
        apiResponse = RestAssured.given()
                .contentType(ContentType.JSON)
                .header("Stripe-Signature", "fake_sig_" + internalOrderId)
                .body(internalOrderId.toString())
                .when()
                .post("/api/webhooks/stripe");
    }

    // --- Préconditions ---

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
        createOrderViaBrowser();
    }

    @Étantdonnéque("une commande a déjà été payée")
    public void uneCommandeDejaPayee() {
        createOrderViaBrowser();
        sendSuccessWebhook();
        apiResponse.then().statusCode(200);
    }

    @Et("j'ai payé ma commande avec succès")
    public void jAiPayeMaCommandeAvecSucces() {
        PlaywrightHooks.page().locator("button:has-text('Payer ma commande')").click();
        PlaywrightHooks.page().waitForTimeout(3000);
        if (FakePaymentGateway.getLastOrderId() != null) {
            sendSuccessWebhook();
        }
        createdOrderId = FakePaymentGateway.getLastOrderId().toString();
    }

    // --- Actions ---

    @Quand("je clique sur \"Payer ma commande\"")
    public void jeCliqueSurPayerMaCommande() {
        PlaywrightHooks.page().locator("button:has-text('Payer ma commande')").click();
        PlaywrightHooks.page().waitForTimeout(3000);
    }

    @Quand("le webhook Stripe notifie un paiement réussi")
    public void leWebhookStripeNotifieUnPaiementReussi() {
        sendSuccessWebhook();
    }

    @Quand("le webhook Stripe notifie le même paiement une deuxième fois")
    public void leWebhookNotifieLeMeme() {
        sendSuccessWebhook();
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

    @Alors("la page de confirmation affiche le statut payé")
    public void laPageDeConfirmationAfficheLeStatutPaye() {
        apiResponse.then().statusCode(200);
        assertNotNull(createdOrderId, "L'orderId doit exister");
        PlaywrightHooks.page().navigate(PlaywrightHooks.testUrl() + "/confirmation?orderId=" + createdOrderId);
        PlaywrightHooks.page().waitForSelector("h1", new Page.WaitForSelectorOptions()
                .setState(WaitForSelectorState.VISIBLE)
                .setTimeout(10000));
        Locator h1 = PlaywrightHooks.page().locator("h1");
        String text = h1.textContent();
        assertTrue(text.contains("Merci"), "La page de confirmation doit afficher un message de succès, got: " + text);
    }

    @Alors("la page de confirmation reste accessible")
    public void laPageDeConfirmationResteAccessible() {
        apiResponse.then().statusCode(200);
        assertNotNull(createdOrderId, "L'orderId doit exister");
        PlaywrightHooks.page().navigate(PlaywrightHooks.testUrl() + "/confirmation?orderId=" + createdOrderId);
        PlaywrightHooks.page().waitForSelector("h1", new Page.WaitForSelectorOptions()
                .setState(WaitForSelectorState.VISIBLE)
                .setTimeout(10000));
        Locator h1 = PlaywrightHooks.page().locator("h1");
        String text = h1.textContent();
        assertTrue(text.contains("Merci"), "La page de confirmation doit rester accessible, got: " + text);
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
