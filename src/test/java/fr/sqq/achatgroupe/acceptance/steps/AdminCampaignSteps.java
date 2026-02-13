package fr.sqq.achatgroupe.acceptance.steps;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Route;
import com.microsoft.playwright.options.WaitForSelectorState;
import fr.sqq.achatgroupe.acceptance.support.TestContext;
import io.cucumber.java.fr.Alors;
import io.cucumber.java.fr.Et;
import io.cucumber.java.fr.Quand;
import io.cucumber.java.fr.Étantdonnéque;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import static org.junit.jupiter.api.Assertions.*;

@ApplicationScoped
public class AdminCampaignSteps {

    @Inject
    TestContext testContext;

    private Page page() {
        return PlaywrightHooks.page();
    }

    @Étantdonnéque("la campagne est désactivée")
    public void laCampagneEstDesactivee() {
        // Deactivate campaign via API
        RestAssured.given()
                .contentType(ContentType.JSON)
                .body("{\"venteId\":" + testContext.venteId() + ",\"active\":false}")
                .when()
                .put("/api/admin/campaign")
                .then()
                .statusCode(200);
    }

    @Quand("je navigue vers la page admin campagne")
    public void jeNavigueVersLaPageAdminCampagne() {
        page().route("**/api/admin/me", route -> route.fulfill(new Route.FulfillOptions()
                .setStatus(200)
                .setContentType("application/json")
                .setBody("{\"data\":{\"name\":\"Test Admin\",\"email\":\"admin@test.fr\"}}")));

        page().navigate(PlaywrightHooks.testUrl() + "/admin/campaign");
        page().waitForSelector("[data-testid='campaign-card']", new Page.WaitForSelectorOptions()
                .setState(WaitForSelectorState.VISIBLE)
                .setTimeout(15000));
    }

    @Alors("le toggle \"Période de commande\" est en position OFF")
    public void leToggleEstEnPositionOff() {
        Locator toggle = page().locator("[data-testid='campaign-toggle']");
        toggle.waitFor(new Locator.WaitForOptions()
                .setState(WaitForSelectorState.VISIBLE)
                .setTimeout(5000));
        String ariaChecked = toggle.getAttribute("aria-checked");
        assertEquals("false", ariaChecked, "Le toggle doit être en position OFF (aria-checked=false)");
    }

    @Et("je vois le message de campagne {string}")
    public void jeVoisLeMessageDeCampagne(String expectedMessage) {
        Locator message = page().locator("[data-testid='campaign-status-message']");
        message.waitFor(new Locator.WaitForOptions()
                .setState(WaitForSelectorState.VISIBLE)
                .setTimeout(5000));
        String text = message.textContent();
        assertNotNull(text);
        assertTrue(text.contains(expectedMessage), "Le message doit contenir '" + expectedMessage + "', got: " + text);
    }

    @Et("je bascule le toggle de la campagne")
    public void jeBasculeLeToggle() {
        page().locator("[data-testid='campaign-toggle']").click();
        page().waitForSelector("[role='alert']", new Page.WaitForSelectorOptions()
                .setState(WaitForSelectorState.VISIBLE)
                .setTimeout(10000));
    }

    @Alors("le toggle \"Période de commande\" est en position ON")
    public void leToggleEstEnPositionOn() {
        Locator toggle = page().locator("[data-testid='campaign-toggle']");
        toggle.waitFor(new Locator.WaitForOptions()
                .setState(WaitForSelectorState.VISIBLE)
                .setTimeout(5000));
        String ariaChecked = toggle.getAttribute("aria-checked");
        assertEquals("true", ariaChecked, "Le toggle doit être en position ON (aria-checked=true)");
    }
}
