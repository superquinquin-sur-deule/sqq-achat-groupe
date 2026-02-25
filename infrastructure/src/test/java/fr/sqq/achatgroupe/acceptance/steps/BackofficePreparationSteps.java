package fr.sqq.achatgroupe.acceptance.steps;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Route;
import com.microsoft.playwright.options.WaitForSelectorState;
import fr.sqq.achatgroupe.acceptance.support.TestContext;
import fr.sqq.achatgroupe.application.command.ApplyShortageAdjustmentsCommand;
import fr.sqq.mediator.Mediator;
import io.cucumber.java.fr.Alors;
import io.cucumber.java.fr.Et;
import io.cucumber.java.fr.Quand;
import io.cucumber.java.fr.Étantdonnéque;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
public class BackofficePreparationSteps {

    @Inject
    TestContext testContext;

    @Inject
    Mediator mediator;

    private String selectedSlotText;

    private Page page() {
        return PlaywrightHooks.page();
    }

    @Quand("je navigue vers la page listes de préparation")
    public void jeNavigueVersLaPageListesDePreparation() {
        page().route("**/api/admin/me", route -> route.fulfill(new Route.FulfillOptions()
                .setStatus(200)
                .setContentType("application/json")
                .setBody("{\"data\":{\"name\":\"Test Logistique\",\"email\":\"logistique@test.fr\"}}")));

        Long venteId = testContext.venteId();
        page().route("**/api/admin/ventes", route -> route.fulfill(new Route.FulfillOptions()
                .setStatus(200)
                .setContentType("application/json")
                .setBody("{\"data\":[{\"id\":" + venteId + ",\"name\":\"Vente Test\",\"description\":\"Test\",\"status\":\"ACTIVE\",\"startDate\":null,\"endDate\":null,\"createdAt\":\"2026-01-01T00:00:00Z\"}],\"pageInfo\":{\"endCursor\":null,\"hasNext\":false}}")));

        page().navigate(PlaywrightHooks.testUrl() + "/admin/preparation");
        page().waitForSelector("[data-testid='preparation-card'], [data-testid='preparation-empty']",
                new Page.WaitForSelectorOptions()
                        .setState(WaitForSelectorState.VISIBLE)
                        .setTimeout(15000));
    }

    @Alors("je vois le titre préparation {string}")
    public void jeVoisLeTitrePreparation(String title) {
        Locator titleEl = page().locator("[data-testid='preparation-title']");
        assertTrue(titleEl.isVisible(), "Le titre de la page préparation doit être visible");
        assertTrue(titleEl.textContent().contains(title), "Le titre doit contenir '" + title + "'");
    }

    @Et("je vois les fiches de préparation groupées par créneau")
    public void jeVoisLesFichesDePreparationGroupeesParCreneau() {
        Locator sections = page().locator("[data-testid='preparation-slot-section']");
        assertTrue(sections.count() >= 2, "Il doit y avoir au moins 2 sections créneau, trouvé: " + sections.count());

        Locator headers = page().locator("[data-testid='preparation-slot-header']");
        assertTrue(headers.count() >= 2, "Il doit y avoir au moins 2 en-têtes de créneau");
    }

    @Et("chaque fiche contient le numéro de commande, le nom du coopérateur et les produits")
    public void chaqueFicheContientLeNumeroDeCommandeLeNomDuCooperateurEtLesProduits() {
        Locator cards = page().locator("[data-testid='preparation-card']");
        assertTrue(cards.count() >= 3, "Il doit y avoir au moins 3 fiches de préparation, trouvé: " + cards.count());

        // Vérifier la structure de la première fiche
        Locator firstCard = cards.first();

        Locator orderNumber = firstCard.locator("[data-testid='preparation-card-order-number']");
        assertTrue(orderNumber.isVisible(), "Le numéro de commande doit être visible");
        assertTrue(orderNumber.textContent().contains("AG-"), "Le numéro de commande doit contenir 'AG-'");

        Locator customer = firstCard.locator("[data-testid='preparation-card-customer']");
        assertTrue(customer.isVisible(), "Le nom du coopérateur doit être visible");
        assertFalse(customer.textContent().isBlank(), "Le nom du coopérateur ne doit pas être vide");

        Locator items = firstCard.locator("[data-testid='preparation-card-items']");
        assertTrue(items.isVisible(), "Le tableau des produits doit être visible");
        Locator rows = items.locator("tbody tr");
        assertTrue(rows.count() >= 1, "Il doit y avoir au moins 1 produit dans la fiche");

        // Vérifier les valeurs réelles — les noms des coopérateurs doivent apparaître dans les fiches
        String allCardsText = page().locator("[data-testid='preparation-card']").allTextContents().toString();
        assertTrue(allCardsText.contains("DURAND Alice"), "DURAND Alice doit apparaître dans les fiches");
        assertTrue(allCardsText.contains("MARTIN Bob"), "MARTIN Bob doit apparaître dans les fiches");
        assertTrue(allCardsText.contains("PETIT Claire"), "PETIT Claire doit apparaître dans les fiches");

        // Vérifier que les produits du setup sont présents
        assertTrue(allCardsText.contains("Tomates bio"), "Le produit 'Tomates bio' doit apparaître");
        assertTrue(allCardsText.contains("Pain de campagne"), "Le produit 'Pain de campagne' doit apparaître");
    }

    @Alors("les produits sont groupés par fournisseur dans les fiches de préparation")
    public void lesProduitsGroupesParFournisseur() {
        Locator supplierHeaders = page().locator("[data-testid='preparation-supplier-header']");
        assertTrue(supplierHeaders.count() >= 1,
                "Il doit y avoir au moins 1 en-tête fournisseur, trouvé: " + supplierHeaders.count());

        // Verify supplier names appear in the headers
        String allHeaders = supplierHeaders.allTextContents().toString();
        assertTrue(allHeaders.contains("Ferme du Soleil"),
                "Le fournisseur 'Ferme du Soleil' doit apparaître dans les en-têtes");
        assertTrue(allHeaders.contains("Boulangerie Martin"),
                "Le fournisseur 'Boulangerie Martin' doit apparaître dans les en-têtes");
    }

    @Et("je sélectionne un créneau dans le filtre préparation")
    public void jeSelectionneUnCreneauDansLeFiltrePreparation() {
        Locator filter = page().locator("[data-testid='preparation-slot-filter']");
        assertTrue(filter.isVisible(), "Le filtre par créneau doit être visible");

        // Sélectionner la deuxième option (premier créneau réel)
        Locator options = filter.locator("option");
        assertTrue(options.count() >= 2, "Il doit y avoir au moins 2 options dans le filtre");
        String secondOptionValue = options.nth(1).getAttribute("value");
        selectedSlotText = options.nth(1).textContent().trim();
        filter.selectOption(secondOptionValue);
    }

    @Alors("seules les fiches du créneau sélectionné sont affichées")
    public void seulesLesFichesDuCreneauSelectionneSontAffichees() {
        Locator sections = page().locator("[data-testid='preparation-slot-section']");
        assertEquals(1, sections.count(), "Il ne doit y avoir qu'une seule section créneau après filtrage");

        Locator header = page().locator("[data-testid='preparation-slot-header']");
        assertTrue(header.textContent().contains(selectedSlotText),
                "L'en-tête du créneau doit contenir '" + selectedSlotText + "'");
    }

    @Alors("je vois le bouton imprimer des listes de préparation")
    public void jeVoisLeBoutonImprimerDesListesDePreparation() {
        Locator btn = page().locator("[data-testid='preparation-print-btn']");
        assertTrue(btn.isVisible(), "Le bouton Imprimer doit être visible");
        assertTrue(btn.textContent().contains("Imprimer"), "Le bouton doit contenir 'Imprimer'");
    }

    @Quand("je navigue vers la page listes de préparation sans commande")
    public void jeNavigueVersLaPageListesDePreparationSansCommande() {
        Long emptyVenteId = testContext.emptyVenteId();

        page().route("**/api/admin/me", route -> route.fulfill(new Route.FulfillOptions()
                .setStatus(200)
                .setContentType("application/json")
                .setBody("{\"data\":{\"name\":\"Test Logistique\",\"email\":\"logistique@test.fr\"}}")));

        page().route("**/api/admin/ventes", route -> route.fulfill(new Route.FulfillOptions()
                .setStatus(200)
                .setContentType("application/json")
                .setBody("{\"data\":[{\"id\":" + emptyVenteId + ",\"name\":\"Vente Vide\",\"description\":\"Test\",\"status\":\"ACTIVE\",\"startDate\":null,\"endDate\":null,\"createdAt\":\"2026-01-01T00:00:00Z\"}],\"pageInfo\":{\"endCursor\":null,\"hasNext\":false}}")));

        page().navigate(PlaywrightHooks.testUrl() + "/admin/preparation");
        page().waitForSelector("[data-testid='preparation-card'], [data-testid='preparation-empty']",
                new Page.WaitForSelectorOptions()
                        .setState(WaitForSelectorState.VISIBLE)
                        .setTimeout(15000));
    }

    @Alors("je vois le message vide préparation {string}")
    public void jeVoisLeMessageVidePreparation(String message) {
        Locator empty = page().locator("[data-testid='preparation-empty']");
        assertTrue(empty.isVisible(), "L'état vide doit être visible");
        assertTrue(empty.textContent().contains(message), "Le message doit contenir '" + message + "'");
    }

    @Étantdonnéque("les ajustements de rupture sont appliqués")
    public void lesAjustementsDeRuptureSontAppliques() {
        mediator.send(new ApplyShortageAdjustmentsCommand(testContext.venteId()));
    }

    @Alors("je vois les produits manquants sur les fiches de préparation")
    public void jeVoisLesProduitsManquantsSurLesFichesDePreparation() {
        Locator missingSection = page().locator("[data-testid='preparation-card-missing']");
        assertTrue(missingSection.count() >= 1,
                "Il doit y avoir au moins une section 'Produits manquants', trouvé: " + missingSection.count());

        Locator missingItems = page().locator("[data-testid='preparation-missing-item']");
        assertTrue(missingItems.count() >= 1,
                "Il doit y avoir au moins un produit manquant listé, trouvé: " + missingItems.count());

        String missingText = missingSection.first().textContent();
        assertTrue(missingText.contains("Produits manquants"),
                "La section doit contenir le titre 'Produits manquants'");
        assertTrue(missingText.contains("Un remboursement sera émis"),
                "La section doit contenir le message de remboursement");
    }
}
