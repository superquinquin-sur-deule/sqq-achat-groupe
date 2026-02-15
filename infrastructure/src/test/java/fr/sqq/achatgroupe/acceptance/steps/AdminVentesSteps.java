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
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@QuarkusTest
public class AdminVentesSteps {

    @Inject
    TestContext testContext;

    private Page page() {
        return PlaywrightHooks.page();
    }

    @Étantdonnéque("la vente est désactivée")
    public void laVenteEstDesactivee() {
        RestAssured.given()
                .contentType(ContentType.JSON)
                .when()
                .put("/api/admin/ventes/" + testContext.venteId() + "/deactivate")
                .then()
                .statusCode(200);
    }

    @Quand("je navigue vers la page admin ventes")
    public void jeNavigueVersLaPageAdminVentes() {
        page().route("**/api/admin/me", route -> route.fulfill(new Route.FulfillOptions()
                .setStatus(200)
                .setContentType("application/json")
                .setBody("{\"data\":{\"name\":\"Test Admin\",\"email\":\"admin@test.fr\"}}")));

        page().navigate(PlaywrightHooks.testUrl() + "/admin/ventes");
        page().waitForSelector("[data-testid='vente-table'], [data-testid='empty-state']", new Page.WaitForSelectorOptions()
                .setState(WaitForSelectorState.VISIBLE)
                .setTimeout(15000));
    }

    @Alors("je vois le tableau des ventes")
    public void jeVoisLeTableauDesVentes() {
        Locator table = page().locator("[data-testid='vente-table']");
        assertTrue(table.isVisible(), "Le tableau des ventes doit être visible");
    }

    @Et("chaque ligne affiche nom, dates et statut")
    public void chaqueLigneAfficheNomDatesEtStatut() {
        Locator rows = page().locator("[data-testid='vente-row']");
        assertTrue(rows.count() >= 1, "Il doit y avoir au moins 1 vente, trouvé: " + rows.count());

        Locator firstRow = rows.first();
        Locator cells = firstRow.locator("td");
        assertTrue(cells.count() >= 5, "Chaque ligne doit avoir au moins 5 cellules");
        assertFalse(cells.nth(0).textContent().isBlank(), "Le nom ne doit pas être vide");

        Locator badge = firstRow.locator("[data-testid='vente-status-badge']");
        assertTrue(badge.isVisible(), "Le badge de statut doit être visible");
    }

    @Et("je clique sur \"Créer une vente\"")
    public void jeCliqueSurCreerUneVente() {
        page().locator("[data-testid='add-vente-btn']").click();
        page().waitForSelector("[data-testid='vente-form']", new Page.WaitForSelectorOptions()
                .setState(WaitForSelectorState.VISIBLE)
                .setTimeout(5000));
    }

    @Et("je remplis le formulaire vente avec le nom {string}")
    public void jeRemplisLeFormulaireVenteAvecLeNom(String name) {
        page().locator("[data-testid='vente-name-input']").fill(name);
    }

    @Et("je clique sur le bouton \"Enregistrer\" du formulaire vente")
    public void jeCliqueSurEnregistrerDuFormulaireVente() {
        page().locator("[data-testid='vente-submit-btn']").click();
        page().waitForSelector("[role='alert']", new Page.WaitForSelectorOptions()
                .setState(WaitForSelectorState.VISIBLE)
                .setTimeout(10000));
    }

    @Et("je clique sur le bouton désactiver de la première vente")
    public void jeCliqueSurDesactiverDeLaPremiereVente() {
        Locator firstRow = page().locator("[data-testid='vente-row']").first();
        Locator toggleBtn = firstRow.locator("[data-testid='vente-toggle-btn']");
        assertTrue(toggleBtn.textContent().contains("Désactiver"), "Le bouton doit afficher 'Désactiver'");
        toggleBtn.click();
        page().waitForSelector("[role='alert']", new Page.WaitForSelectorOptions()
                .setState(WaitForSelectorState.VISIBLE)
                .setTimeout(10000));
    }

    @Et("je clique sur le bouton activer de la première vente")
    public void jeCliqueSurActiverDeLaPremiereVente() {
        Locator firstRow = page().locator("[data-testid='vente-row']").first();
        Locator toggleBtn = firstRow.locator("[data-testid='vente-toggle-btn']");
        assertTrue(toggleBtn.textContent().contains("Activer"), "Le bouton doit afficher 'Activer'");
        toggleBtn.click();
        page().waitForSelector("[role='alert']", new Page.WaitForSelectorOptions()
                .setState(WaitForSelectorState.VISIBLE)
                .setTimeout(10000));
    }

    @Alors("le sélecteur de vente est visible dans la sidenav")
    public void leSelecteurDeVenteEstVisibleDansLaSidenav() {
        Locator selector = page().locator("[data-testid='sidenav-vente-selector']");
        assertTrue(selector.isVisible(), "Le sélecteur de vente doit être visible dans la sidenav");
    }
}
