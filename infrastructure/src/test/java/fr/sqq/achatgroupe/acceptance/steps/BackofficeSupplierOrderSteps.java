package fr.sqq.achatgroupe.acceptance.steps;

import com.microsoft.playwright.Download;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Route;
import com.microsoft.playwright.options.WaitForSelectorState;
import fr.sqq.achatgroupe.acceptance.support.TestContext;
import io.cucumber.java.fr.Alors;
import io.cucumber.java.fr.Et;
import io.cucumber.java.fr.Quand;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
public class BackofficeSupplierOrderSteps {

    @Inject
    TestContext testContext;

    private Download lastDownload;

    private Page page() {
        return PlaywrightHooks.page();
    }

    @Quand("je navigue vers la page bon fournisseur")
    public void jeNavigueVersLaPageBonFournisseur() {
        page().route("**/api/admin/me", route -> route.fulfill(new Route.FulfillOptions()
                .setStatus(200)
                .setContentType("application/json")
                .setBody("{\"data\":{\"name\":\"Test Logistique\",\"email\":\"logistique@test.fr\"}}")));

        Long venteId = testContext.venteId();
        page().route("**/api/admin/ventes", route -> route.fulfill(new Route.FulfillOptions()
                .setStatus(200)
                .setContentType("application/json")
                .setBody("{\"data\":[{\"id\":" + venteId + ",\"name\":\"Vente Test\",\"description\":\"Test\",\"status\":\"ACTIVE\",\"startDate\":null,\"endDate\":null,\"createdAt\":\"2026-01-01T00:00:00Z\"}],\"pageInfo\":{\"endCursor\":null,\"hasNext\":false}}")));

        page().navigate(PlaywrightHooks.testUrl() + "/admin/supplier-order");
        page().waitForSelector("[data-testid='supplier-order-table'], [data-testid='supplier-order-empty']",
                new Page.WaitForSelectorOptions()
                        .setState(WaitForSelectorState.VISIBLE)
                        .setTimeout(15000));
    }

    @Alors("je vois le titre bon fournisseur {string}")
    public void jeVoisLeTitreBonFournisseur(String title) {
        Locator titleEl = page().locator("[data-testid='supplier-order-title']");
        assertTrue(titleEl.isVisible(), "Le titre du bon fournisseur doit être visible");
        assertTrue(titleEl.textContent().contains(title), "Le titre doit contenir '" + title + "'");
    }

    @Et("je vois les produits groupés par fournisseur avec les quantités totales")
    public void jeVoisLesProduitsGroupesParFournisseurAvecQuantites() {
        Locator table = page().locator("[data-testid='supplier-order-table']");
        assertTrue(table.isVisible(), "Le tableau doit être visible");

        Locator groups = page().locator("[data-testid='supplier-group']");
        assertTrue(groups.count() >= 2, "Il doit y avoir au moins 2 groupes fournisseur, trouvé: " + groups.count());

        Locator headers = page().locator("[data-testid='supplier-group-header']");
        assertTrue(headers.count() >= 2, "Il doit y avoir au moins 2 en-têtes fournisseur");

        Locator rows = page().locator("[data-testid='supplier-order-row']");
        assertTrue(rows.count() >= 3, "Il doit y avoir au moins 3 lignes produit, trouvé: " + rows.count());

        // Vérifier qu'au moins une quantité est affichée
        String firstRowText = rows.first().textContent();
        assertFalse(firstRowText.isBlank(), "La première ligne ne doit pas être vide");
    }

    @Alors("je vois le bouton imprimer du bon fournisseur")
    public void jeVoisLeBoutonImprimerDuBonFournisseur() {
        Locator btn = page().locator("[data-testid='supplier-order-print-btn']");
        assertTrue(btn.isVisible(), "Le bouton Imprimer doit être visible");
        assertTrue(btn.textContent().contains("Imprimer"), "Le bouton doit contenir 'Imprimer'");
    }

    @Et("je vois le bouton exporter Excel du bon fournisseur")
    public void jeVoisLeBoutonExporterExcelDuBonFournisseur() {
        Locator btn = page().locator("[data-testid='supplier-order-export-btn']");
        assertTrue(btn.isVisible(), "Le bouton Exporter Excel doit être visible");
        assertTrue(btn.textContent().contains("Exporter Excel"), "Le bouton doit contenir 'Exporter Excel'");
    }

    @Et("je clique sur exporter Excel du bon fournisseur")
    public void jeCliqueSurExporterExcelDuBonFournisseur() {
        lastDownload = page().waitForDownload(() -> {
            page().locator("[data-testid='supplier-order-export-btn']").click();
        });
    }

    @Alors("un fichier Excel bon fournisseur est téléchargé")
    public void unFichierExcelBonFournisseurEstTelecharge() {
        assertNotNull(lastDownload, "Un téléchargement doit avoir été déclenché");
        String filename = lastDownload.suggestedFilename();
        assertTrue(filename.startsWith("bon-fournisseur-"), "Le fichier doit commencer par 'bon-fournisseur-'");
        assertTrue(filename.endsWith(".xlsx"), "Le fichier doit se terminer par '.xlsx'");
    }

    @Quand("je navigue vers la page bon fournisseur sans commande")
    public void jeNavigueVersLaPageBonFournisseurSansCommande() {
        Long emptyVenteId = testContext.emptyVenteId();

        page().route("**/api/admin/me", route -> route.fulfill(new Route.FulfillOptions()
                .setStatus(200)
                .setContentType("application/json")
                .setBody("{\"data\":{\"name\":\"Test Logistique\",\"email\":\"logistique@test.fr\"}}")));

        page().route("**/api/admin/ventes", route -> route.fulfill(new Route.FulfillOptions()
                .setStatus(200)
                .setContentType("application/json")
                .setBody("{\"data\":[{\"id\":" + emptyVenteId + ",\"name\":\"Vente Vide\",\"description\":\"Test\",\"status\":\"ACTIVE\",\"startDate\":null,\"endDate\":null,\"createdAt\":\"2026-01-01T00:00:00Z\"}],\"pageInfo\":{\"endCursor\":null,\"hasNext\":false}}")));

        page().navigate(PlaywrightHooks.testUrl() + "/admin/supplier-order");
        page().waitForSelector("[data-testid='supplier-order-table'], [data-testid='supplier-order-empty']",
                new Page.WaitForSelectorOptions()
                        .setState(WaitForSelectorState.VISIBLE)
                        .setTimeout(15000));
    }

    @Alors("je vois le message vide bon fournisseur {string}")
    public void jeVoisLeMessageVideBonFournisseur(String message) {
        Locator empty = page().locator("[data-testid='supplier-order-empty']");
        assertTrue(empty.isVisible(), "L'état vide doit être visible");
        assertTrue(empty.textContent().contains(message), "Le message doit contenir '" + message + "'");
    }
}
