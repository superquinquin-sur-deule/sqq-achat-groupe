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
import jakarta.inject.Inject;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
public class AdminProductSteps {

    @Inject
    TestContext testContext;

    private int initialProductCount;

    private Page page() {
        return PlaywrightHooks.page();
    }

    private void mockAdminAuth() {
        page().route("**/api/admin/me", route -> route.fulfill(new Route.FulfillOptions()
                .setStatus(200)
                .setContentType("application/json")
                .setBody("{\"data\":{\"name\":\"Test Admin\",\"email\":\"admin@test.fr\"}}")));
    }

    private void mockVentesList() {
        Long venteId = testContext.venteId();
        page().route("**/api/admin/ventes", route -> {
            if (route.request().url().contains("/ventes/")) {
                route.resume();
            } else {
                route.fulfill(new Route.FulfillOptions()
                        .setStatus(200)
                        .setContentType("application/json")
                        .setBody("{\"data\":[{\"id\":" + venteId + ",\"name\":\"Vente Test\",\"description\":\"Test\",\"status\":\"ACTIVE\",\"startDate\":null,\"endDate\":null,\"createdAt\":\"2026-01-01T00:00:00Z\"}],\"pageInfo\":{\"endCursor\":null,\"hasNext\":false}}"));
            }
        });
    }

    // --- Helper: create order via browser flow ---

    private void createOrderViaBrowser() {
        String venteUrl = PlaywrightHooks.testUrl() + "/ventes/" + testContext.venteId();

        page().navigate(venteUrl);
        page().waitForSelector("[data-testid='product-card']", new Page.WaitForSelectorOptions()
                .setState(WaitForSelectorState.VISIBLE)
                .setTimeout(15000));

        // Add the first available product to the cart
        Locator firstCard = page().locator("[data-testid='product-card']:not([data-exhausted='true'])").first();
        firstCard.locator("[data-testid='add-button']").click();
        page().locator("[role='alert']").first().waitFor(new Locator.WaitForOptions()
                .setState(WaitForSelectorState.VISIBLE)
                .setTimeout(5000));
        page().waitForTimeout(500);

        // Navigate to cart then checkout
        page().locator("a[aria-label='Panier']").click();
        page().waitForURL("**/cart");
        page().waitForSelector("[data-testid='checkout-button']", new Page.WaitForSelectorOptions()
                .setState(WaitForSelectorState.VISIBLE)
                .setTimeout(5000));
        page().locator("[data-testid='checkout-button']").click();
        page().waitForURL("**/checkout");
        page().waitForSelector("#customer-last-name", new Page.WaitForSelectorOptions()
                .setState(WaitForSelectorState.VISIBLE)
                .setTimeout(5000));

        // Fill customer form
        page().locator("#customer-last-name").fill("Dupont");
        page().locator("#customer-first-name").fill("Marie");
        page().locator("#customer-email").fill("marie@exemple.fr");
        page().locator("#customer-phone").fill("06 12 34 56 78");
        page().locator("button:has-text('Continuer')").first().click();
        page().waitForSelector("[role='radiogroup']", new Page.WaitForSelectorOptions()
                .setState(WaitForSelectorState.VISIBLE)
                .setTimeout(10000));

        // Select first available time slot
        Locator slots = page().locator("[role='radio']:not([aria-disabled='true'])");
        slots.first().waitFor(new Locator.WaitForOptions()
                .setState(WaitForSelectorState.VISIBLE)
                .setTimeout(10000));
        slots.first().click();
        page().locator("button:has-text('Continuer')").first().click();
        page().waitForSelector("button:has-text('Payer ma commande')", new Page.WaitForSelectorOptions()
                .setState(WaitForSelectorState.VISIBLE)
                .setTimeout(5000));

        // Click pay (creates the order server-side)
        page().locator("button:has-text('Payer ma commande')").click();
        page().waitForTimeout(3000);
    }

    // --- Steps ---

    @Étantdonnéque("une commande existe sur la vente via le navigateur")
    public void uneCommandeExisteSurLaVenteViaLeNavigateur() {
        createOrderViaBrowser();
    }

    @Quand("je navigue vers la page admin des produits")
    public void jeNavigueVersLaPageAdminDesProduits() {
        mockAdminAuth();
        mockVentesList();

        page().navigate(PlaywrightHooks.testUrl() + "/admin/products");
        page().waitForSelector("[data-testid='product-table'], [data-testid='empty-state']", new Page.WaitForSelectorOptions()
                .setState(WaitForSelectorState.VISIBLE)
                .setTimeout(15000));
    }

    @Alors("je vois le tableau des produits avec des lignes")
    public void jeVoisLeTableauDesProduitsAvecDesLignes() {
        Locator table = page().locator("[data-testid='product-table']");
        assertTrue(table.isVisible(), "Le tableau des produits doit être visible");
        Locator rows = page().locator("[data-testid='product-row']");
        assertTrue(rows.count() > 0, "Le tableau doit contenir au moins une ligne de produit");
    }

    @Et("je clique sur le bouton ajouter un produit")
    public void jeCliqueSurLeBoutonAjouterUnProduit() {
        page().locator("[data-testid='add-product-btn']").click();
        page().waitForSelector("[data-testid='product-form']", new Page.WaitForSelectorOptions()
                .setState(WaitForSelectorState.VISIBLE)
                .setTimeout(5000));
    }

    @Et("je remplis le formulaire produit avec les valeurs")
    public void jeRemplisLeFormulaireProduit(io.cucumber.datatable.DataTable dataTable) {
        Map<String, String> values = dataTable.asMap(String.class, String.class);
        page().locator("#product-name").fill(values.get("nom"));
        page().locator("#product-prix-ht").fill(values.get("prix HT"));
        page().locator("#product-taux-tva").fill(values.get("TVA"));
        page().locator("#product-supplier").fill(values.get("fournisseur"));
        page().locator("#product-stock").fill(values.get("stock"));
        page().locator("#product-reference").fill(values.get("référence"));
        page().locator("#product-category").fill(values.get("catégorie"));
        page().locator("#product-brand").fill(values.get("marque"));
    }

    @Et("je soumets le formulaire produit")
    public void jeSoumetsLeFormulaireProduit() {
        page().locator("button:has-text('Créer le produit')").click();
        page().waitForSelector("[role='alert']", new Page.WaitForSelectorOptions()
                .setState(WaitForSelectorState.VISIBLE)
                .setTimeout(10000));
    }

    @Alors("je vois un toast de succès")
    public void jeVoisUnToastDeSucces() {
        Locator toast = page().locator("[role='alert']").first();
        assertTrue(toast.isVisible(), "Un toast de succès doit être visible");
    }

    @Et("le produit {string} apparaît dans le tableau")
    public void leProduitApparaitDansLeTableau(String productName) {
        page().waitForTimeout(1000);
        Locator table = page().locator("[data-testid='product-table']");
        assertTrue(table.textContent().contains(productName),
                "Le tableau doit contenir le produit '" + productName + "'");
    }

    @Et("je clique sur le bouton modifier du premier produit")
    public void jeCliqueSurLeBoutonModifierDuPremierProduit() {
        Locator firstRow = page().locator("[data-testid='product-row']").first();
        firstRow.locator("button:has-text('Modifier')").click();
        page().waitForSelector("[data-testid='product-form']", new Page.WaitForSelectorOptions()
                .setState(WaitForSelectorState.VISIBLE)
                .setTimeout(5000));
    }

    @Et("je modifie le nom du produit en {string}")
    public void jeModifieLeNomDuProduitEn(String name) {
        page().locator("#product-name").fill(name);
    }

    @Et("je soumets le formulaire produit en modification")
    public void jeSoumetsLeFormulaireProduitEnModification() {
        page().locator("button:has-text('Enregistrer les modifications')").click();
        page().waitForSelector("[role='alert']", new Page.WaitForSelectorOptions()
                .setState(WaitForSelectorState.VISIBLE)
                .setTimeout(10000));
    }

    @Et("je compte le nombre de produits dans le tableau")
    public void jeCompteLeNombreDeProduitsDansLeTableau() {
        initialProductCount = page().locator("[data-testid='product-row']").count();
    }

    @Et("je clique sur le bouton supprimer du premier produit")
    public void jeCliqueSurLeBoutonSupprimerDuPremierProduit() {
        Locator firstRow = page().locator("[data-testid='product-row']").first();
        firstRow.locator("button:has-text('Supprimer')").click();
    }

    @Et("je confirme la suppression")
    public void jeConfirmeLaSuppression() {
        // After clicking delete, confirmation buttons appear
        Locator firstRow = page().locator("[data-testid='product-row']").first();
        firstRow.locator("button:has-text('Supprimer')").click();
        page().waitForSelector("[role='alert']", new Page.WaitForSelectorOptions()
                .setState(WaitForSelectorState.VISIBLE)
                .setTimeout(10000));
    }

    @Alors("le tableau contient un produit de moins")
    public void leTableauContientUnProduitDeMoins() {
        page().waitForTimeout(1000);
        int currentCount = page().locator("[data-testid='product-row']").count();
        assertEquals(initialProductCount - 1, currentCount,
                "Le tableau doit contenir un produit de moins");
    }

    @Et("je soumets le formulaire produit vide")
    public void jeSoumetsLeFormulaireProduitVide() {
        // Clear the default stock value
        page().locator("#product-stock").fill("");
        page().locator("button:has-text('Créer le produit')").click();
        page().waitForTimeout(500);
    }

    @Alors("je vois des messages d'erreur de validation")
    public void jeVoisDesMessagesDErreurDeValidation() {
        Locator errors = page().locator("[role='alert']");
        assertTrue(errors.count() > 0, "Des messages d'erreur de validation doivent être visibles");
    }

    @Alors("je vois le message d'avertissement commandes existantes")
    public void jeVoisLeMessageDAvertissementCommandesExistantes() {
        Locator warning = page().locator("text=Cette vente contient des commandes");
        assertTrue(warning.isVisible(), "Le message d'avertissement commandes existantes doit être visible");
    }

    @Alors("le bouton modifier n'est pas visible")
    public void leBoutonModifierNEstPasVisible() {
        Locator firstRow = page().locator("[data-testid='product-row']").first();
        Locator modifyBtn = firstRow.locator("button:has-text('Modifier')");
        assertEquals(0, modifyBtn.count(), "Le bouton Modifier ne doit pas être visible quand des commandes existent");
    }

    @Et("je clique sur le bouton désactiver du premier produit")
    public void jeCliqueSurLeBoutonDesactiverDuPremierProduit() {
        Locator firstRow = page().locator("[data-testid='product-row']").first();
        firstRow.locator("button:has-text('Désactiver')").click();
        page().waitForSelector("[role='alert']", new Page.WaitForSelectorOptions()
                .setState(WaitForSelectorState.VISIBLE)
                .setTimeout(10000));
    }

    @Alors("le bouton supprimer n'est pas visible")
    public void leBoutonSupprimerNEstPasVisible() {
        Locator firstRow = page().locator("[data-testid='product-row']").first();
        Locator deleteBtn = firstRow.locator("button:has-text('Supprimer')");
        assertEquals(0, deleteBtn.count(), "Le bouton Supprimer ne doit pas être visible quand des commandes existent");
    }

    @Et("je clique sur le bouton supprimer de la première vente")
    public void jeCliqueSurLeBoutonSupprimerDeLaPremiereVente() {
        Locator firstRow = page().locator("[data-testid='vente-row']").first();
        firstRow.locator("button:has-text('Supprimer')").click();
    }

    @Et("je confirme la suppression de la vente")
    public void jeConfirmeLaSuppressionDeLaVente() {
        Locator firstRow = page().locator("[data-testid='vente-row']").first();
        firstRow.locator("button:has-text('Supprimer')").click();
        page().waitForSelector("[role='alert']", new Page.WaitForSelectorOptions()
                .setState(WaitForSelectorState.VISIBLE)
                .setTimeout(10000));
    }

    @Alors("je vois un toast d'erreur")
    public void jeVoisUnToastDErreur() {
        Locator toast = page().locator("[role='alert']").first();
        assertTrue(toast.isVisible(), "Un toast d'erreur doit être visible");
    }

    @Alors("le bouton supprimer de la vente n'est pas visible")
    public void leBoutonSupprimerDeLaVenteNEstPasVisible() {
        Locator firstRow = page().locator("[data-testid='vente-row']").first();
        Locator deleteBtn = firstRow.locator("button:has-text('Supprimer')");
        assertEquals(0, deleteBtn.count(), "Le bouton Supprimer de la vente ne doit pas être visible quand des commandes existent");
    }
}
