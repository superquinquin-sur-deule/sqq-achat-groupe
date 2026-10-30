package fr.sqq.achatgroupe.acceptance.steps;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Route;
import com.microsoft.playwright.options.WaitForSelectorState;
import fr.sqq.achatgroupe.acceptance.support.TestContext;
import io.cucumber.java.fr.Alors;
import io.cucumber.java.fr.Et;
import io.cucumber.java.fr.Quand;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import jakarta.inject.Inject;

import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@QuarkusTest
public class VenteSansCreneauSteps {

    @Inject
    TestContext testContext;

    private Response lastResponse;

    private Page page() {
        return PlaywrightHooks.page();
    }

    // --- Helpers ---

    /** La sidenav admin lit la liste des ventes : le mock doit porter le mode de retrait. */
    private void mockAdminApis() {
        page().route("**/api/admin/me", route -> route.fulfill(new Route.FulfillOptions()
                .setStatus(200)
                .setContentType("application/json")
                .setBody("{\"data\":{\"name\":\"Test Admin\",\"email\":\"admin@test.fr\"}}")));

        String venteJson = """
                {"data":[{"id":%d,"name":"Vente Sans Créneau","description":"Test","status":"ACTIVE",\
                "startDate":null,"endDate":null,"pickupMode":"DATE_RANGE",\
                "pickupStartDate":"%s","pickupEndDate":"%s",\
                "createdAt":"2026-01-01T00:00:00Z","hasOrders":false}],\
                "pageInfo":{"endCursor":null,"hasNext":false}}"""
                .formatted(testContext.venteId(), testContext.pickupStartDate(), testContext.pickupEndDate());

        page().route("**/api/admin/ventes", route -> route.fulfill(new Route.FulfillOptions()
                .setStatus(200)
                .setContentType("application/json")
                .setBody(venteJson)));
    }

    private Locator stepper() {
        return page().locator("nav[aria-label='Progression de la commande']");
    }

    private String orderBodyWithoutTimeSlot() {
        return """
                {
                    "customerFirstName": "Marie",
                    "customerLastName": "Dupont",
                    "email": "marie@exemple.fr",
                    "phone": "06 12 34 56 78",
                    "items": [{"productId": %d, "quantity": 1}]
                }
                """.formatted(testContext.productIds().get(0));
    }

    // --- Admin : formulaire vente ---

    @Et("je remplis le formulaire vente sans créneau avec le nom {string}")
    public void jeRemplisLeFormulaireVenteSansCreneau(String name) {
        page().locator("[data-testid='vente-name-input']").fill(name);
        page().locator("[data-testid='vente-pickup-mode-select']").selectOption("DATE_RANGE");
        page().waitForSelector("[data-testid='vente-pickup-start-input']", new Page.WaitForSelectorOptions()
                .setState(WaitForSelectorState.VISIBLE)
                .setTimeout(5000));
        page().locator("[data-testid='vente-pickup-start-input']").fill(LocalDate.now().plusDays(30).toString());
        page().locator("[data-testid='vente-pickup-end-input']").fill(LocalDate.now().plusDays(33).toString());
    }

    // --- Admin : page créneaux ---

    @Quand("je navigue vers la page admin créneaux de la vente sans créneau")
    public void jeNavigueVersLaPageAdminCreneaux() {
        mockAdminApis();
        page().navigate(PlaywrightHooks.testUrl() + "/admin/timeslots");
        page().waitForSelector("[data-testid='timeslots-date-range-notice']", new Page.WaitForSelectorOptions()
                .setState(WaitForSelectorState.VISIBLE)
                .setTimeout(15000));
    }

    @Alors("je vois le message expliquant que la vente fonctionne en plage de dates")
    public void jeVoisLeMessagePlageDeDates() {
        Locator notice = page().locator("[data-testid='timeslots-date-range-notice']");
        assertTrue(notice.isVisible(), "L'encart d'explication doit être visible");
        String text = notice.textContent();
        assertNotNull(text);
        assertTrue(text.contains("plage de dates"), "L'encart doit mentionner la plage de dates, trouvé: " + text);

        assertEquals(0, page().locator("[data-testid='add-timeslot-btn']").count(),
                "Le bouton d'ajout de créneau ne doit pas être affiché");
    }

    @Et("le lien Créneaux de la sidenav est désactivé")
    public void leLienCreneauxEstDesactive() {
        Locator link = page().locator("[data-testid='sidenav-timeslots']");
        assertTrue(link.isVisible(), "Le lien Créneaux doit rester visible");
        assertEquals("true", link.getAttribute("aria-disabled"),
                "Le lien Créneaux doit être marqué désactivé");
    }

    // --- Coop : tunnel de commande ---

    @Et("j'ai des produits dans mon panier de la vente sans créneau")
    public void jAiDesProduitsDansMonPanier() {
        page().navigate(PlaywrightHooks.testUrl() + "/ventes/" + testContext.venteId());
        page().waitForSelector("[data-testid='product-card']", new Page.WaitForSelectorOptions()
                .setState(WaitForSelectorState.VISIBLE)
                .setTimeout(15000));
        page().locator("[data-testid='product-card']:not([data-exhausted='true'])").first()
                .locator("[data-testid='add-button']").click();
        page().locator("[role='alert']").first().waitFor(new Locator.WaitForOptions()
                .setState(WaitForSelectorState.VISIBLE)
                .setTimeout(5000));
    }

    @Quand("je navigue vers la page de la vente sans créneau")
    public void jeNavigueVersLaPageDeLaVente() {
        page().navigate(PlaywrightHooks.testUrl() + "/ventes/" + testContext.venteId());
        page().waitForSelector("[data-testid='hero-banner']", new Page.WaitForSelectorOptions()
                .setState(WaitForSelectorState.VISIBLE)
                .setTimeout(15000));
    }

    @Alors("le bandeau affiche la plage de retrait sous \"Récupère-la\"")
    public void leBandeauAfficheLaPlageDeRetrait() {
        Locator range = page().locator("[data-testid='hero-pickup-range']");
        range.waitFor(new Locator.WaitForOptions()
                .setState(WaitForSelectorState.VISIBLE)
                .setTimeout(10000));
        String text = range.textContent();
        assertNotNull(text);
        assertFalse(text.isBlank(), "La plage de retrait ne doit pas être vide dans le bandeau");

        // Le bandeau ne doit plus parler de réservation de créneau.
        String banner = page().locator("[data-testid='vente-info-card']").textContent();
        assertNotNull(banner);
        assertTrue(banner.contains("sans réservation de créneau"),
                "Le bandeau doit indiquer qu'aucun créneau n'est à réserver, trouvé: " + banner);
    }

    @Quand("je navigue vers le checkout de la vente sans créneau")
    public void jeNavigueVersLeCheckout() {
        page().locator("a[aria-label='Panier']").click();
        page().waitForURL("**/cart");
        page().locator("[data-testid='checkout-button']").click();
        page().waitForURL("**/checkout");
        page().waitForSelector("#customer-last-name", new Page.WaitForSelectorOptions()
                .setState(WaitForSelectorState.VISIBLE)
                .setTimeout(10000));
    }

    @Alors("le Stepper ne comporte que 3 étapes")
    public void leStepperNeComporteQue3Etapes() {
        stepper().waitFor(new Locator.WaitForOptions()
                .setState(WaitForSelectorState.VISIBLE)
                .setTimeout(5000));
        int steps = stepper().locator("div.rounded-full").count();
        assertEquals(3, steps, "Le stepper doit comporter 3 étapes, trouvé: " + steps);
    }

    @Et("l'étape \"Créneau\" n'est pas affichée")
    public void letapeCreneauNestPasAffichee() {
        String text = stepper().textContent();
        assertNotNull(text);
        assertFalse(text.contains("Créneau"), "Le stepper ne doit pas mentionner l'étape Créneau, trouvé: " + text);
    }

    @Et("je remplis mes coordonnées et je continue")
    public void jeRemplisMesCoordonneesEtJeContinue() {
        page().locator("#customer-last-name").fill("Dupont");
        page().locator("#customer-first-name").fill("Marie");
        page().locator("#customer-email").fill("marie@exemple.fr");
        page().locator("#customer-phone").fill("06 12 34 56 78");
        page().locator("button:has-text('Continuer')").first().click();
    }

    @Alors("je vois le récapitulatif avec la plage de retrait")
    public void jeVoisLeRecapitulatifAvecLaPlage() {
        Locator range = page().locator("[data-testid='order-summary-pickup-range']");
        range.waitFor(new Locator.WaitForOptions()
                .setState(WaitForSelectorState.VISIBLE)
                .setTimeout(10000));
        String text = range.textContent();
        assertNotNull(text);
        assertTrue(text.contains("Retrait"), "Le récapitulatif doit annoncer le retrait, trouvé: " + text);

        // Aucune sélection de créneau n'est proposée dans ce mode.
        assertEquals(0, page().locator("[role='radiogroup']").count(),
                "Aucun sélecteur de créneau ne doit être affiché");
    }

    @Quand("je confirme ma commande sans créneau")
    public void jeConfirmeMaCommande() {
        page().locator("button:has-text('Valider ma commande')").click();
    }

    @Et("la confirmation affiche la plage de retrait")
    public void laConfirmationAfficheLaPlage() {
        Locator range = page().locator("[data-testid='confirmation-pickup-range']");
        range.waitFor(new Locator.WaitForOptions()
                .setState(WaitForSelectorState.VISIBLE)
                .setTimeout(10000));
        String text = range.textContent();
        assertNotNull(text);
        assertTrue(text.contains("Retrait"), "La confirmation doit afficher la plage de retrait, trouvé: " + text);
    }

    // --- API ---

    @Quand("je poste une commande sans créneau")
    public void jePosteUneCommandeSansCreneau() {
        lastResponse = RestAssured.given()
                .contentType(ContentType.JSON)
                .body(orderBodyWithoutTimeSlot())
                .when()
                .post("/api/ventes/" + testContext.venteId() + "/orders");
    }

    @Alors("la commande est créée et n'a aucun créneau")
    public void laCommandeEstCreeeSansCreneau() {
        lastResponse.then().statusCode(201);
        String orderId = lastResponse.jsonPath().getString("data.id");

        RestAssured.given()
                .when()
                .get("/api/orders/" + orderId)
                .then()
                .statusCode(200)
                .body("data.status", org.hamcrest.Matchers.equalTo("CONFIRMED"))
                .body("data.timeSlot", org.hamcrest.Matchers.nullValue())
                .body("data.pickupMode", org.hamcrest.Matchers.equalTo("DATE_RANGE"))
                .body("data.pickupStartDate", org.hamcrest.Matchers.equalTo(testContext.pickupStartDate().toString()))
                .body("data.pickupEndDate", org.hamcrest.Matchers.equalTo(testContext.pickupEndDate().toString()));
    }

    @Alors("la commande est refusée avec le statut {int}")
    public void laCommandeEstRefusee(int status) {
        lastResponse.then().statusCode(status);
    }

    @Et("une commande a été passée sur la vente sans créneau")
    public void uneCommandeAEtePassee() {
        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(orderBodyWithoutTimeSlot())
                .when()
                .post("/api/ventes/" + testContext.venteId() + "/orders")
                .then()
                .statusCode(201);
    }

    @Quand("je bascule la vente sans créneau en mode créneaux")
    public void jeBasculeEnModeCreneaux() {
        Instant startDate = Instant.now().minus(1, ChronoUnit.DAYS);
        Instant endDate = Instant.now().plus(60, ChronoUnit.DAYS);
        lastResponse = RestAssured.given()
                .contentType(ContentType.JSON)
                .body("""
                        {
                            "name": "Vente Sans Créneau",
                            "description": "Bascule interdite",
                            "startDate": "%s",
                            "endDate": "%s",
                            "pickupMode": "TIME_SLOTS"
                        }
                        """.formatted(startDate, endDate))
                .when()
                .put("/api/admin/ventes/" + testContext.venteId());
    }

    @Alors("la modification est refusée avec le statut {int}")
    public void laModificationEstRefusee(int status) {
        lastResponse.then().statusCode(status);
    }
}
